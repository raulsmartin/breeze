package me.raulsmartin.breeze.items;

import me.raulsmartin.breeze.registry.BreezeRegistry;
import net.minecraft.block.Block;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;

import javax.annotation.Nonnull;

public class BreezeItem extends BlockItem {

    private final Block parent;

    public BreezeItem(Block block, Block parent) {
        super(block, new Item.Properties().tab(BreezeRegistry.BREEZE_TAB));
        this.parent = parent;
    }

    @Override
    @Nonnull
    public ITextComponent getName(@Nonnull ItemStack stack) {
        return new TranslationTextComponent(this.getDescriptionId(stack), new TranslationTextComponent(parent.getDescriptionId()));
    }
}