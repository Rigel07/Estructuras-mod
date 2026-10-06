package com.cuterealms;

import com.cuterealms.entity.CuteMerchantEntity;
import com.cuterealms.entity.LumiEntity;
import com.cuterealms.entity.PompomEntity;
import net.minecraftforge.event.entity.EntityAttributeCreationEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = CuteRealmsMod.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class ModEvents {

    @SubscribeEvent
    public static void onAttributes(EntityAttributeCreationEvent event) {
        event.put(ModRegistry.MOCHI.get(), CuteMerchantEntity.createAttributes().build());
        event.put(ModRegistry.ESTRELLIN.get(), CuteMerchantEntity.createAttributes().build());
        event.put(ModRegistry.TULI.get(), CuteMerchantEntity.createAttributes().build());
        event.put(ModRegistry.LUMI.get(), LumiEntity.createAttributes().build());
        event.put(ModRegistry.POMPOM.get(), PompomEntity.createAttributes().build());
    }
}
