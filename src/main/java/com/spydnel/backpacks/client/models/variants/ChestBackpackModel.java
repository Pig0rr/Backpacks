package com.spydnel.backpacks.client.models.variants;

import com.google.common.collect.ImmutableList;
import com.spydnel.backpacks.client.models.BackpackModel;
import com.spydnel.backpacks.registry.BPLayers;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.*;
import net.minecraft.resources.ResourceLocation;

import java.util.List;

public class ChestBackpackModel extends BackpackModel {

    private static List<ResourceLocation> createTextures() {
        ImmutableList.Builder<ResourceLocation> builder = ImmutableList.builder();
        //BLOCK
        builder.add(getFromString("textures/entity/chest_backpack.png"));
        builder.add(getFromString("textures/entity/chest_backpack_overlay.png"));
        builder.add(getFromString("textures/entity/chest_backpack_base.png"));

        //BODY
        builder.add(getFromString("textures/model/chest_backpack.png"));
        builder.add(getFromString("textures/model/chest_backpack_overlay.png"));
        return builder.build();
    }

    public static ModelLayerLocation getBodyLayer() {
        return BPLayers.CHEST_BACKPACK;
    }

    public static ModelLayerLocation getBlockLayer() {
        return BPLayers.CHEST_BACKPACK_BLOCK;
    }

    //public final ModelPart root;

    public ChestBackpackModel(ModelPart root) {
        super(root);
        this.root = root;
        this.textures = createTextures();
    }

    //BLOCK MODEL
    public static LayerDefinition createBlockLayer() {
        MeshDefinition meshdefinition = new MeshDefinition();
        PartDefinition partdefinition = meshdefinition.getRoot();

        PartDefinition base = partdefinition.addOrReplaceChild("base", CubeListBuilder.create().texOffs(0, 0).addBox(-5.0F, -7.0F, -4.0F, 10.0F, 7.0F, 8.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 24.0F, 0.0F));

        PartDefinition lid = base.addOrReplaceChild("lid", CubeListBuilder.create().texOffs(0, 16).addBox(-5.0F, -5.0F, 0.0F, 10.0F, 5.0F, 8.0F, new CubeDeformation(0.0F))
                .texOffs(0, 29).addBox(-5.5F, -4.0F, -0.5F, 11.0F, 6.0F, 9.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, -7.0F, -4.0F));

        PartDefinition cube_r1 = lid.addOrReplaceChild("cube_r1", CubeListBuilder.create().texOffs(36, 25).addBox(-2.0F, 0.0F, -1.0F, 4.0F, 3.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, -1.0F, 8.0F, 0.1745F, 0.0F, 0.0F));

        return LayerDefinition.create(meshdefinition, 64, 64);
    }

    //PLAYER MODEL
    public static LayerDefinition createBodyLayer() {
        MeshDefinition meshdefinition = new MeshDefinition();
        PartDefinition partdefinition = meshdefinition.getRoot();

        PartDefinition base = partdefinition.addOrReplaceChild("base", CubeListBuilder.create().texOffs(0, 0).addBox(-7.0F, -10.0F, -4.5F, 8.0F, 12.0F, 4.0F, new CubeDeformation(0.6F))
                .texOffs(0, 16).addBox(-7.0F, -6.0F, -0.5F, 8.0F, 6.0F, 5.0F, new CubeDeformation(0.0F)), PartPose.offset(3.0F, 10.0F, 2.5F));

        PartDefinition lid = base.addOrReplaceChild("lid", CubeListBuilder.create().texOffs(24, 0).addBox(-4.0F, -4.0F, -1.0F, 8.0F, 4.0F, 5.0F, new CubeDeformation(0.0F))
                .texOffs(26, 19).addBox(-4.5F, -3.0F, -1.5F, 9.0F, 2.0F, 6.0F, new CubeDeformation(0.0F)), PartPose.offset(-3.0F, -6.0F, 0.5F));

        PartDefinition cube_r1 = lid.addOrReplaceChild("cube_r1", CubeListBuilder.create().texOffs(50, 5).addBox(-2.5F, 0.0F, -1.0F, 4.0F, 3.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.5F, -1.0F, 4.0F, 0.1745F, 0.0F, 0.0F));

        return LayerDefinition.create(meshdefinition, 64, 64);
    }
}
