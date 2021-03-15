package me.raulsmartin.breeze.registry;

import me.raulsmartin.breeze.Reference;
import me.raulsmartin.breeze.blocks.BreezeBlock;
import me.raulsmartin.breeze.items.BreezeItem;
import me.raulsmartin.breeze.types.BreezeType;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

import javax.annotation.Nonnull;
import java.util.Arrays;
import java.util.List;

public class BreezeRegistry {
    public static final List<Block> PARENT_BLOCK_LIST = Arrays.asList(Blocks.ACACIA_LOG, Blocks.ACACIA_WOOD, Blocks.STONE, Blocks.TERRACOTTA, Blocks.WHITE_CONCRETE);

    public static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(ForgeRegistries.BLOCKS, Reference.MOD_ID);
    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, Reference.MOD_ID);

    public static final ItemGroup BREEZE_TAB = new ItemGroup("breeze_tab") {
        @Override
        @Nonnull
        @OnlyIn(Dist.CLIENT)
        public ItemStack makeIcon() {
            return ITEMS.getEntries().stream().findAny().map(itemRegistryObject ->
                    new ItemStack(itemRegistryObject.get())).orElseGet(Items.ACACIA_LOG::getDefaultInstance);
        }

        @Override
        public boolean hasSearchBar() {
            return true;
        }
    }.setBackgroundSuffix("item_search.png");

    static {
        PARENT_BLOCK_LIST.forEach(parent -> Arrays.stream(BreezeType.values()).forEach(type -> {
            if (parent.getRegistryName() == null) return;

            Block block = new BreezeBlock(parent, type);
            BLOCKS.register(parent.getRegistryName().getPath() + "_breeze_" + type.getSerializedName(), () -> block);
            ITEMS.register(parent.getRegistryName().getPath() + "_breeze_" + type.getSerializedName(), () ->
                    new BreezeItem(block, parent));
        }));
    }
}