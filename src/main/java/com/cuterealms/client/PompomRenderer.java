package com.cuterealms.client;

import com.cuterealms.CuteRealmsMod;
import com.cuterealms.entity.PompomEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.resources.ResourceLocation;

public class PompomRenderer extends MobRenderer<PompomEntity, ChibiModel<PompomEntity>> {
    private static final ResourceLocation[] TEXTURES = new ResourceLocation[PompomEntity.VARIANTS];

    static {
        for (int i = 0; i < TEXTURES.length; i++) {
            TEXTURES[i] = new ResourceLocation(CuteRealmsMod.MOD_ID, "textures/entity/pompom_" + i + ".png");
        }
    }

    public PompomRenderer(EntityRendererProvider.Context context) {
        super(context, new ChibiModel<>(context.bakeLayer(ChibiModel.Style.POMPOM.layer()), ChibiModel.Style.POMPOM), 0.35F);
    }

    @Override
    public ResourceLocation getTextureLocation(PompomEntity entity) {
        return TEXTURES[Math.floorMod(entity.getVariant(), TEXTURES.length)];
    }

    @Override
    protected void scale(PompomEntity entity, PoseStack poseStack, float partialTick) {
        float size = entity.isBaby() ? 0.5F : 0.8F;
        poseStack.scale(size, size, size);
    }
}
