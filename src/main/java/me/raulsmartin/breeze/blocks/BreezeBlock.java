package me.raulsmartin.breeze.blocks;

import me.raulsmartin.breeze.types.BreezeType;
import net.minecraft.block.*;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.resources.Language;
import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.FluidState;
import net.minecraft.fluid.Fluids;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.item.ItemStack;
import net.minecraft.pathfinding.PathType;
import net.minecraft.state.BooleanProperty;
import net.minecraft.state.DirectionProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.Direction;
import net.minecraft.util.Mirror;
import net.minecraft.util.Rotation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.math.shapes.VoxelShapes;
import net.minecraft.util.text.IFormattableTextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorld;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class BreezeBlock extends Block implements IWaterLoggable {
    public static final DirectionProperty FACING = DirectionalBlock.FACING;
    public static final BooleanProperty DOUBLE = BooleanProperty.create("double");
    public static final BooleanProperty WATERLOGGED = BlockStateProperties.WATERLOGGED;

    private final Block parent;
    private final BreezeType type;

    public BreezeBlock(Block parent, BreezeType type) {
        super(AbstractBlock.Properties.of(parent.defaultBlockState().getMaterial(), parent.defaultMaterialColor())
                .strength(parent.defaultBlockState().getDestroySpeed(null, null),
                        parent.getExplosionResistance(null, null, null, null))
                .sound(parent.getSoundType(parent.defaultBlockState(), null, null, null))
                .noOcclusion());
        this.registerDefaultState(this.stateDefinition.any()
                .setValue(FACING, Direction.NORTH).setValue(DOUBLE, Boolean.FALSE).setValue(WATERLOGGED, Boolean.FALSE));
        this.parent = parent;
        this.type = type;
    }

    @Override
    @Nonnull
    @OnlyIn(Dist.CLIENT)
    public IFormattableTextComponent getName() {
        return new TranslationTextComponent(getDescriptionId(), new TranslationTextComponent(parent.getDescriptionId()));
    }

    @Override
    @Nonnull
    public String getDescriptionId() {
        return "block.breeze.breeze_" + type.getSerializedName();
    }

    @Override
    public boolean useShapeForLightOcclusion(BlockState state) {
        return !state.getValue(DOUBLE);
    }

    @Override
    protected void createBlockStateDefinition(StateContainer.Builder<Block, BlockState> builder) {
        builder.add(FACING, DOUBLE, WATERLOGGED);
    }

    @Override
    @Nonnull
    public VoxelShape getShape(BlockState state, IBlockReader worldIn, BlockPos pos, ISelectionContext context) {
        if (state.getValue(DOUBLE))
            return VoxelShapes.block();

        switch (state.getValue(FACING)) {
            case DOWN:
                return Block.box(0.0D, 8.0D, 0.0D, 16.0D, 16.0D, 16.0D);
            case UP:
                return Block.box(0.0D, 0.0D, 0.0D, 16.0D, 8.0D, 16.0D);
            case EAST:
                return Block.box(0.0D, 0.0D, 0.0D, 8.0D, 16.0D, 16.0D);
            case WEST:
                return Block.box(8.0D, 0.0D, 0.0D, 16.0D, 16.0D, 16.0D);
            case SOUTH:
                return Block.box(0.0D, 0.0D, 0.0D, 16.0D, 16.0D, 8.0D);
            case NORTH:
            default:
                return Block.box(0.0D, 0.0D, 8.0D, 16.0D, 16.0D, 16.0D);
        }
    }

    @Override
    @Nullable
    public BlockState getStateForPlacement(BlockItemUseContext context) {
        BlockPos blockpos = context.getClickedPos();
        BlockState blockstate = context.getLevel().getBlockState(blockpos);
        if (blockstate.getBlock() == this)
            return blockstate.setValue(DOUBLE, Boolean.TRUE).setValue(WATERLOGGED, Boolean.FALSE);

        FluidState fluidstate = context.getLevel().getFluidState(blockpos);
        return this.defaultBlockState().setValue(FACING, context.getNearestLookingDirection().getOpposite())
                .setValue(WATERLOGGED, fluidstate.getType() == Fluids.WATER);
    }

    @Override
    public boolean canBeReplaced(BlockState state, BlockItemUseContext useContext) {
        ItemStack itemstack = useContext.getItemInHand();
        Direction facing = state.getValue(FACING);
        if (state.getValue(DOUBLE) || itemstack.getItem() != this.asItem()) return false;
        if (useContext.replacingClickedOnBlock()) {
            boolean positiveX = useContext.getClickLocation().x - (double) useContext.getClickedPos().getX() > 0.75D;
            boolean positiveY = useContext.getClickLocation().y - (double) useContext.getClickedPos().getY() > 0.5D;
            boolean positiveZ = useContext.getClickLocation().z - (double) useContext.getClickedPos().getZ() > 0.75D;
            Direction direction = useContext.getClickedFace();
            switch (facing) {
                case UP:
                    return direction == Direction.UP || positiveY && direction.getAxis().isHorizontal();
                case DOWN:
                    return direction == Direction.DOWN || !positiveY && direction.getAxis().isHorizontal();
                case SOUTH:
                    return direction == Direction.SOUTH || positiveZ && direction.getAxis().isVertical();
                case NORTH:
                    return direction == Direction.NORTH || !positiveZ && direction.getAxis().isVertical();
                case EAST:
                    return direction == Direction.EAST || positiveX && direction.getAxis().isVertical();
                case WEST:
                    return direction == Direction.WEST || !positiveX && direction.getAxis().isVertical();
            }
        }
        return true;
    }

    @Override
    @Nonnull
    public FluidState getFluidState(BlockState state) {
        return state.getValue(WATERLOGGED) ? Fluids.WATER.getSource(false) : super.getFluidState(state);
    }

    @Override
    public boolean placeLiquid(IWorld worldIn, BlockPos pos, BlockState state, FluidState fluidStateIn) {
        return !state.getValue(DOUBLE) && IWaterLoggable.super.placeLiquid(worldIn, pos, state, fluidStateIn);
    }

    @Override
    public boolean canPlaceLiquid(IBlockReader worldIn, BlockPos pos, BlockState state, Fluid fluidIn) {
        return !state.getValue(DOUBLE) && IWaterLoggable.super.canPlaceLiquid(worldIn, pos, state, fluidIn);
    }

    @Override
    @Nonnull
    public BlockState updateShape(BlockState stateIn, Direction facing, BlockState facingState, IWorld worldIn, BlockPos currentPos, BlockPos facingPos) {
        if (stateIn.getValue(WATERLOGGED))
            worldIn.getLiquidTicks().scheduleTick(currentPos, Fluids.WATER, Fluids.WATER.getTickDelay(worldIn));

        return super.updateShape(stateIn, facing, facingState, worldIn, currentPos, facingPos);
    }

    @Override
    public boolean isPathfindable(BlockState state, IBlockReader worldIn, BlockPos pos, PathType type) {
        return type == PathType.WATER && worldIn.getFluidState(pos).is(FluidTags.WATER);
    }

    @Override
    @Nonnull
    public BlockState rotate(BlockState state, Rotation rot) {
        return state.setValue(FACING, rot.rotate(state.getValue(FACING)));
    }

    @Override
    @Nonnull
    public BlockState mirror(BlockState state, Mirror mirrorIn) {
        return state.rotate(mirrorIn.getRotation(state.getValue(FACING)));
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public float getShadeBrightness(BlockState state, IBlockReader worldIn, BlockPos pos) {
        return 1.0F;
    }

    @Override
    public boolean propagatesSkylightDown(BlockState state, IBlockReader reader, BlockPos pos) {
        return true;
    }
}