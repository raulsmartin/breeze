package me.raulsmartin.breeze;

import me.raulsmartin.breeze.registry.BreezeRegistry;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

@Mod(Reference.MOD_ID)
public class BreezeMod {
    public BreezeMod() {
        BreezeRegistry.BLOCKS.register(FMLJavaModLoadingContext.get().getModEventBus());
        BreezeRegistry.ITEMS.register(FMLJavaModLoadingContext.get().getModEventBus());
    }
}