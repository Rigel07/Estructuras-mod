package com.cuterealms.client;

import com.cuterealms.CuteRealmsMod;
import com.cuterealms.entity.LumiEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.resources.ResourceLocation;

public class LumiRenderer extends MobRenderer<LumiEntity, ChibiModel<LumiEntity>> {
    private static final ResourceLocation TEXTURE =
            new ResourceLocation(CuteRealmsMod.MOD_ID, "textures/entity/lumi.png");

    public LumiRenderer(EntityRendererProvider.Context context) {
        super(context, new ChibiModel<>(context.bakeLayer(ChibiModel.Style.LUMI.layer()), ChibiModel.Style.LUMI), 0.2F);
    }

    @Override
    public ResourceLocation getTextureLocation(LumiEntity entity) {
        return TEXTURE;
    }

    @Override
    protected void scale(LumiEntity entity, PoseStack poseStack, float partialTick) {
        poseStack.scale(0.7F, 0.7F, 0.7F);
    }
}
