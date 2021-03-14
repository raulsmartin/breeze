package me.raulsmartin.breeze.blocks;

import me.raulsmartin.breeze.registry.BreezeRegistry;
import me.raulsmartin.breeze.types.BreezeType;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.DirectionalBlock;
import net.minecraft.block.IWaterLoggable;
import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.Fluids;
import net.minecraft.fluid.IFluidState;
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
import net.minecraft.util.text.ITextComponent;
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
        super(Block.Properties.from(parent).func_226896_b_());
        this.setDefaultState(this.getDefaultState()
                .with(FACING, Direction.NORTH).with(DOUBLE, Boolean.FALSE).with(WATERLOGGED, Boolean.FALSE));
        this.parent = parent;
        this.type = type;
    }

    @Override
    @Nonnull
    @OnlyIn(Dist.CLIENT)
    public ITextComponent getNameTextComponent() {
        return new TranslationTextComponent(getTranslationKey(), BreezeRegistry.ENGLISH.translateKey(parent.getTranslationKey()));
    }

    @Override
    @Nonnull
    public String getTranslationKey() {
        return "block.breeze.breeze_" + type.getName();
    }

    @Override
    public boolean func_220074_n(BlockState state) {
        return !state.get(DOUBLE);
    }

    @Override
    protected void fillStateContainer(StateContainer.Builder<Block, BlockState> builder) {
        builder.add(FACING, DOUBLE, WATERLOGGED);
    }

    @Override
    @Nonnull
    public VoxelShape getShape(BlockState state, IBlockReader worldIn, BlockPos pos, ISelectionContext context) {
        if (state.get(DOUBLE))
            return VoxelShapes.fullCube();

        switch (state.get(FACING)) {
            case DOWN:
                return Block.makeCuboidShape(0.0D, 8.0D, 0.0D, 16.0D, 16.0D, 16.0D);
            case UP:
                return Block.makeCuboidShape(0.0D, 0.0D, 0.0D, 16.0D, 8.0D, 16.0D);
            case EAST:
                return Block.makeCuboidShape(0.0D, 0.0D, 0.0D, 8.0D, 16.0D, 16.0D);
            case WEST:
                return Block.makeCuboidShape(8.0D, 0.0D, 0.0D, 16.0D, 16.0D, 16.0D);
            case SOUTH:
                return Block.makeCuboidShape(0.0D, 0.0D, 0.0D, 16.0D, 16.0D, 8.0D);
            case NORTH:
            default:
                return Block.makeCuboidShape(0.0D, 0.0D, 8.0D, 16.0D, 16.0D, 16.0D);
        }
    }

    @Override
    @Nullable
    public BlockState getStateForPlacement(BlockItemUseContext context) {
        BlockPos blockpos = context.getPos();
        BlockState blockstate = context.getWorld().getBlockState(blockpos);
        if (blockstate.getBlock() == this)
            return blockstate.with(DOUBLE, Boolean.TRUE).with(WATERLOGGED, Boolean.FALSE);

        IFluidState ifluidstate = context.getWorld().getFluidState(blockpos);
        return this.getDefaultState().with(FACING, context.getNearestLookingDirection().getOpposite())
                .with(WATERLOGGED, ifluidstate.getFluid() == Fluids.WATER);
    }

    @Override
    public boolean isReplaceable(BlockState state, BlockItemUseContext useContext) {
        ItemStack itemstack = useContext.getItem();
        Direction facing = state.get(FACING);
        if (state.get(DOUBLE) || itemstack.getItem() != this.asItem()) return false;
        if (useContext.replacingClickedOnBlock()) {
            boolean positiveX = useContext.getHitVec().x - (double) useContext.getPos().getX() > 0.75D;
            boolean positiveY = useContext.getHitVec().y - (double) useContext.getPos().getY() > 0.5D;
            boolean positiveZ = useContext.getHitVec().z - (double) useContext.getPos().getZ() > 0.75D;
            Direction direction = useContext.getFace();
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
    public IFluidState getFluidState(BlockState state) {
        return state.get(WATERLOGGED) ? Fluids.WATER.getStillFluidState(false) : super.getFluidState(state);
    }

    @Override
    public boolean receiveFluid(IWorld worldIn, BlockPos pos, BlockState state, IFluidState fluidStateIn) {
        return !state.get(DOUBLE) && IWaterLoggable.super.receiveFluid(worldIn, pos, state, fluidStateIn);
    }

    @Override
    public boolean canContainFluid(IBlockReader worldIn, BlockPos pos, BlockState state, Fluid fluidIn) {
        return !state.get(DOUBLE) && IWaterLoggable.super.canContainFluid(worldIn, pos, state, fluidIn);
    }

    @Override
    @Nonnull
    public BlockState updatePostPlacement(BlockState stateIn, Direction facing, BlockState facingState, IWorld worldIn, BlockPos currentPos, BlockPos facingPos) {
        if (stateIn.get(WATERLOGGED))
            worldIn.getPendingFluidTicks().scheduleTick(currentPos, Fluids.WATER, Fluids.WATER.getTickRate(worldIn));

        return super.updatePostPlacement(stateIn, facing, facingState, worldIn, currentPos, facingPos);
    }

    @Override
    public boolean allowsMovement(BlockState state, IBlockReader worldIn, BlockPos pos, PathType type) {
        return type == PathType.WATER && worldIn.getFluidState(pos).isTagged(FluidTags.WATER);
    }

    @Override
    @Nonnull
    public BlockState rotate(BlockState state, Rotation rot) {
        return state.with(FACING, rot.rotate(state.get(FACING)));
    }

    @Override
    @Nonnull
    public BlockState mirror(BlockState state, Mirror mirrorIn) {
        return state.rotate(mirrorIn.toRotation(state.get(FACING)));
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public float getAmbientOcclusionLightValue(BlockState state, IBlockReader worldIn, BlockPos pos) {
        return 1.0F;
    }

    @Override
    public boolean propagatesSkylightDown(BlockState state, IBlockReader reader, BlockPos pos) {
        return true;
    }

    @Override
    public boolean func_229869_c_(BlockState state, IBlockReader reader, BlockPos pos) {
        return false;
    }

    @Override
    public boolean isNormalCube(BlockState state, IBlockReader worldIn, BlockPos pos) {
        return false;
    }
}