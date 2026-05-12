package com.spydnel.backpacks.client.models;

import com.google.common.collect.ImmutableList;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.spydnel.backpacks.Backpacks;
import com.spydnel.backpacks.registry.BPLayers;
import net.minecraft.client.model.Model;
import net.minecraft.client.model.geom.EntityModelSet;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.*;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;

import java.util.List;
import java.util.function.Function;

public class BackpackModel extends Model {

    protected static ResourceLocation getFromString(String s) {
        return ResourceLocation.fromNamespaceAndPath(Backpacks.MODID, s);
    }

    private static List<ResourceLocation> createTextures() {
        ImmutableList.Builder<ResourceLocation> builder = ImmutableList.builder();
        //BLOCK
        builder.add(getFromString("textures/entity/backpack.png"));
        builder.add(getFromString("textures/entity/backpack_overlay.png"));
        builder.add(getFromString("textures/entity/backpack_base.png"));

        //BODY
        builder.add(getFromString("textures/model/backpack.png"));
        builder.add(getFromString("textures/model/backpack_overlay.png"));
        return builder.build();
    }

    protected List<ResourceLocation> textures;

    public final ResourceLocation getTexture(int i) {
        return this.textures.get(i);
    }

    public static ModelLayerLocation getBodyLayer() {
        return BPLayers.BACKPACK;
    }

    public static ModelLayerLocation getBlockLayer() {
        return BPLayers.BACKPACK_BLOCK;
    }

    public ModelPart getRoot() { return this.root; }

    protected ModelPart root;
    //public final ModelPart base;
    //public final ModelPart lid;

    public BackpackModel(ModelPart root) {
        super(RenderType::entityCutoutNoCull);
        this.root = root;
        this.textures = createTextures();
    }

    //BLOCK MODEL
    public static LayerDefinition createBlockLayer() {
        MeshDefinition meshdefinition = new MeshDefinition();
        PartDefinition partdefinition = meshdefinition.getRoot();

        PartDefinition base = partdefinition.addOrReplaceChild("base", CubeListBuilder.create().texOffs(0, 0).addBox(-5.0F, -11.0F, -4.0F, 10.0F, 11.0F, 8.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 24.0F, 0.0F));

        PartDefinition lid = base.addOrReplaceChild("lid", CubeListBuilder.create().texOffs(0, 19).addBox(-5.5F, -2.0F, -0.5F, 11.0F, 5.0F, 9.0F, new CubeDeformation(0.0F))
                .texOffs(-9, 33).addBox(-5.5F, 1.0F, -0.5F, 11.0F, 0.0F, 9.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, -11.0F, -4.0F));

        return LayerDefinition.create(meshdefinition, 64, 64);
    }

    //PLAYER MODEL
    public static LayerDefinition createBodyLayer() {
        MeshDefinition meshdefinition = new MeshDefinition();
        PartDefinition partdefinition = meshdefinition.getRoot();

        PartDefinition base = partdefinition.addOrReplaceChild("base", CubeListBuilder.create().texOffs(40, 0).addBox(-7.0F, -10.0F, -4.5F, 8.0F, 12.0F, 4.0F, new CubeDeformation(0.6F))
                .texOffs(0, 0).addBox(-7.0F, -9.0F, -1.0F, 8.0F, 9.0F, 6.0F, new CubeDeformation(0.0F)), PartPose.offset(3.0F, 10.0F, 2.5F));

        PartDefinition lid = base.addOrReplaceChild("lid", CubeListBuilder.create().texOffs(0, 15).addBox(-4.5F, -1.0F, -2.0F, 9.0F, 4.0F, 7.0F, new CubeDeformation(0.0F))
                .texOffs(32, 18).addBox(-4.5F, 0.0F, -2.0F, 9.0F, 1.0F, 7.0F, new CubeDeformation(0.0F)), PartPose.offset(-3.0F, -9.0F, 0.5F));

        return LayerDefinition.create(meshdefinition, 64, 64);
    }

    public void renderToBuffer(PoseStack poseStack, VertexConsumer buffer, int packedLight, int packedOverlay, int color) {
        this.root.render(poseStack, buffer, packedLight, packedOverlay, color);
    }
}
