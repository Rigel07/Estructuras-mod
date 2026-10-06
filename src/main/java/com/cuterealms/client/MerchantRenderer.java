package com.cuterealms.client;

import com.cuterealms.CuteRealmsMod;
import com.cuterealms.entity.CuteMerchantEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.resources.ResourceLocation;

public class MerchantRenderer extends MobRenderer<CuteMerchantEntity, ChibiModel<CuteMerchantEntity>> {
    private final ResourceLocation texture;

    public MerchantRenderer(EntityRendererProvider.Context context, ChibiModel.Style style, String textureName) {
        super(context, new ChibiModel<>(context.bakeLayer(style.layer()), style), 0.4F);
        this.texture = new ResourceLocation(CuteRealmsMod.MOD_ID, "textures/entity/" + textureName + ".png");
    }

    @Override
    public ResourceLocation getTextureLocation(CuteMerchantEntity entity) {
        return texture;
    }

    @Override
    protected void scale(CuteMerchantEntity entity, PoseStack poseStack, float partialTick) {
        poseStack.scale(0.85F, 0.85F, 0.85F);
    }
}
