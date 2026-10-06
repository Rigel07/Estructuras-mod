package com.cuterealms.client;

import com.cuterealms.CuteRealmsMod;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.TamableAnimal;

/**
 * Modelo "chibi" compartido por todos los mobs del mod (cabezota, cuerpo pequeño, bracitos y patitas).
 * Cada estilo activa sus complementos: orejas, colita, alas, antena con estrella o gorro de cocinero.
 * Textura 64x64 con la misma distribución de UV para todos.
 */
public class ChibiModel<T extends LivingEntity> extends EntityModel<T> {

    public enum Style {
        //     id        orejas(0 no,1 oso,2 conejo) cola   alas   antena  gorro  cuerpo ancho/alto/fondo
        MOCHI("mochi", 0, false, false, false, true, 8, 6, 6),
        STAR("star", 0, false, false, true, false, 6, 6, 4),
        LUMI("lumi", 0, false, true, false, false, 5, 6, 4),
        POMPOM("pompom", 2, true, false, false, false, 6, 5, 5),
        TULI("tuli", 1, false, false, false, false, 7, 6, 5);

        final String id;
        final int ears;
        final boolean tail;
        final boolean wings;
        final boolean antenna;
        final boolean hat;
        final int bw;
        final int bh;
        final int bd;

        Style(String id, int ears, boolean tail, boolean wings, boolean antenna, boolean hat, int bw, int bh, int bd) {
            this.id = id;
            this.ears = ears;
            this.tail = tail;
            this.wings = wings;
            this.antenna = antenna;
            this.hat = hat;
            this.bw = bw;
            this.bh = bh;
            this.bd = bd;
        }

        public ModelLayerLocation layer() {
            return new ModelLayerLocation(new ResourceLocation(CuteRealmsMod.MOD_ID, "chibi_" + id), "main");
        }
    }

    private final Style style;
    private final ModelPart root;
    private final ModelPart head;
    private final ModelPart earL;
    private final ModelPart earR;
    private final ModelPart armR;
    private final ModelPart armL;
    private final ModelPart legR;
    private final ModelPart legL;
    private final ModelPart tail;
    private final ModelPart wingL;
    private final ModelPart wingR;

    public ChibiModel(ModelPart root, Style style) {
        this.root = root;
        this.style = style;
        this.head = root.getChild("head");
        this.earL = head.getChild("ear_l");
        this.earR = head.getChild("ear_r");
        this.armR = root.getChild("arm_r");
        this.armL = root.getChild("arm_l");
        this.legR = root.getChild("leg_r");
        this.legL = root.getChild("leg_l");
        this.tail = root.getChild("tail");
        this.wingL = root.getChild("wing_l");
        this.wingR = root.getChild("wing_r");
        this.earL.visible = style.ears != 0;
        this.earR.visible = style.ears != 0;
        this.tail.visible = style.tail;
        this.wingL.visible = style.wings;
        this.wingR.visible = style.wings;
        head.getChild("antenna").visible = style.antenna;
        head.getChild("hat").visible = style.hat;
    }

    public static LayerDefinition createBodyLayer(Style s) {
        MeshDefinition mesh = new MeshDefinition();
        PartDefinition root = mesh.getRoot();
        float bw = s.bw;
        float bh = s.bh;
        float bd = s.bd;
        float bodyTop = 20.0F - bh;

        root.addOrReplaceChild("body",
                CubeListBuilder.create().texOffs(0, 16).addBox(-bw / 2.0F, 0.0F, -bd / 2.0F, bw, bh, bd),
                PartPose.offset(0.0F, bodyTop, 0.0F));

        PartDefinition head = root.addOrReplaceChild("head",
                CubeListBuilder.create().texOffs(0, 0).addBox(-4.0F, -8.0F, -4.0F, 8.0F, 8.0F, 8.0F),
                PartPose.offset(0.0F, bodyTop, 0.0F));

        float ew = s.ears == 2 ? 2.0F : 3.0F;
        float eh = s.ears == 2 ? 6.0F : 3.0F;
        CubeListBuilder ear = CubeListBuilder.create().texOffs(0, 32).addBox(-ew / 2.0F, -eh, -0.5F, ew, eh, 1.0F);
        head.addOrReplaceChild("ear_l", ear, PartPose.offsetAndRotation(2.6F, -7.6F, 0.0F, 0.0F, 0.0F, 0.15F));
        head.addOrReplaceChild("ear_r", ear, PartPose.offsetAndRotation(-2.6F, -7.6F, 0.0F, 0.0F, 0.0F, -0.15F));

        PartDefinition antenna = head.addOrReplaceChild("antenna",
                CubeListBuilder.create().texOffs(40, 32).addBox(-0.5F, -3.0F, -0.5F, 1.0F, 3.0F, 1.0F),
                PartPose.offset(0.0F, -8.0F, 0.0F));
        antenna.addOrReplaceChild("star",
                CubeListBuilder.create().texOffs(48, 32).addBox(-2.0F, -4.0F, -0.5F, 4.0F, 4.0F, 1.0F),
                PartPose.offset(0.0F, -3.0F, 0.0F));

        PartDefinition hat = head.addOrReplaceChild("hat",
                CubeListBuilder.create().texOffs(0, 48).addBox(-4.0F, -3.0F, -4.0F, 8.0F, 3.0F, 8.0F),
                PartPose.offset(0.0F, -8.0F, 0.0F));
        hat.addOrReplaceChild("puff",
                CubeListBuilder.create().texOffs(32, 48).addBox(-3.0F, -3.0F, -3.0F, 6.0F, 3.0F, 6.0F),
                PartPose.offset(0.0F, -3.0F, 0.0F));

        float armX = bw / 2.0F + 1.0F;
        CubeListBuilder arm = CubeListBuilder.create().texOffs(32, 16).addBox(-1.0F, -1.0F, -1.0F, 2.0F, 5.0F, 2.0F);
        root.addOrReplaceChild("arm_r", arm, PartPose.offset(-armX, bodyTop + 1.5F, 0.0F));
        root.addOrReplaceChild("arm_l", arm, PartPose.offset(armX, bodyTop + 1.5F, 0.0F));

        float legX = Math.max(1.5F, bw / 2.0F - 1.5F);
        CubeListBuilder leg = CubeListBuilder.create().texOffs(48, 16).addBox(-1.0F, 0.0F, -1.0F, 2.0F, 4.0F, 2.0F);
        root.addOrReplaceChild("leg_r", leg, PartPose.offset(-legX, 20.0F, 0.0F));
        root.addOrReplaceChild("leg_l", leg, PartPose.offset(legX, 20.0F, 0.0F));

        root.addOrReplaceChild("tail",
                CubeListBuilder.create().texOffs(10, 32).addBox(-1.5F, -1.5F, 0.0F, 3.0F, 3.0F, 3.0F),
                PartPose.offset(0.0F, 18.0F, bd / 2.0F - 0.5F));

        root.addOrReplaceChild("wing_l",
                CubeListBuilder.create().texOffs(24, 32).addBox(0.0F, 0.0F, 0.0F, 6.0F, 8.0F, 1.0F),
                PartPose.offset(1.0F, bodyTop + 1.0F, bd / 2.0F));
        root.addOrReplaceChild("wing_r",
                CubeListBuilder.create().texOffs(24, 32).addBox(-6.0F, 0.0F, 0.0F, 6.0F, 8.0F, 1.0F),
                PartPose.offset(-1.0F, bodyTop + 1.0F, bd / 2.0F));

        return LayerDefinition.create(mesh, 64, 64);
    }

    @Override
    public void setupAnim(T entity, float limbSwing, float limbSwingAmount, float ageInTicks,
                          float netHeadYaw, float headPitch) {
        boolean sitting = entity instanceof TamableAnimal tamable && tamable.isInSittingPose();

        root.y = sitting ? 3.0F : 0.0F;
        head.yRot = netHeadYaw * Mth.DEG_TO_RAD;
        head.xRot = headPitch * Mth.DEG_TO_RAD;

        float swing = Mth.cos(limbSwing * 0.6662F) * 1.2F * limbSwingAmount;

        if (style.wings) {
            // Vuela: se mece en el aire, patitas colgando y alitas aleteando.
            root.y = Mth.sin(ageInTicks * 0.12F) * 1.5F - 2.0F;
            legR.xRot = 0.25F + Mth.sin(ageInTicks * 0.1F) * 0.06F;
            legL.xRot = 0.25F - Mth.sin(ageInTicks * 0.1F) * 0.06F;
            armR.xRot = -0.3F;
            armL.xRot = -0.3F;
            float flap = Mth.sin(ageInTicks * 1.2F) * 0.7F;
            wingL.zRot = flap;
            wingR.zRot = -flap;
        } else if (sitting) {
            legR.xRot = -1.4F;
            legL.xRot = -1.4F;
            armR.xRot = -0.4F;
            armL.xRot = -0.4F;
        } else {
            legR.xRot = swing;
            legL.xRot = -swing;
            armR.xRot = -swing * 0.8F;
            armL.xRot = swing * 0.8F;
        }
        armR.zRot = 0.08F + Mth.sin(ageInTicks * 0.09F) * 0.03F;
        armL.zRot = -0.08F - Mth.sin(ageInTicks * 0.09F) * 0.03F;

        if (style.ears != 0) {
            float flop = style.ears == 2 ? limbSwingAmount * 0.35F : 0.0F;
            float wiggle = Mth.sin(ageInTicks * 0.08F) * 0.04F;
            earL.zRot = 0.15F + wiggle + flop;
            earR.zRot = -0.15F - wiggle - flop;
        }
        if (style.tail) {
            tail.yRot = Mth.sin(ageInTicks * 0.2F) * 0.35F;
        }
    }

    @Override
    public void renderToBuffer(PoseStack poseStack, VertexConsumer buffer, int packedLight, int packedOverlay,
                               float red, float green, float blue, float alpha) {
        root.render(poseStack, buffer, packedLight, packedOverlay, red, green, blue, alpha);
    }
}
