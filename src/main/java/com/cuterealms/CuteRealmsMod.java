package com.cuterealms;

import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

@Mod(CuteRealmsMod.MOD_ID)
public class CuteRealmsMod {
    public static final String MOD_ID = "cute_realms";

    public CuteRealmsMod() {
        IEventBus modBus = FMLJavaModLoadingContext.get().getModEventBus();
        ModRegistry.register(modBus);
    }
}
