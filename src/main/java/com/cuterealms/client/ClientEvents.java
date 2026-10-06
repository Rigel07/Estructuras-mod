package com.cuterealms.client;

import com.cuterealms.CuteRealmsMod;
import com.cuterealms.ModRegistry;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = CuteRealmsMod.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public class ClientEvents {

    @SubscribeEvent
    public static void registerLayers(EntityRenderersEvent.RegisterLayerDefinitions event) {
        for (ChibiModel.Style style : ChibiModel.Style.values()) {
            event.registerLayerDefinition(style.layer(), () -> ChibiModel.createBodyLayer(style));
        }
    }

    @SubscribeEvent
    public static void registerRenderers(EntityRenderersEvent.RegisterRenderers event) {
        event.registerEntityRenderer(ModRegistry.MOCHI.get(),
                context -> new MerchantRenderer(context, ChibiModel.Style.MOCHI, "mochi"));
        event.registerEntityRenderer(ModRegistry.ESTRELLIN.get(),
                context -> new MerchantRenderer(context, ChibiModel.Style.STAR, "estrellin"));
        event.registerEntityRenderer(ModRegistry.TULI.get(),
                context -> new MerchantRenderer(context, ChibiModel.Style.TULI, "tuli"));
        event.registerEntityRenderer(ModRegistry.LUMI.get(), LumiRenderer::new);
        event.registerEntityRenderer(ModRegistry.POMPOM.get(), PompomRenderer::new);
    }
}
