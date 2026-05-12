package com.spydnel.backpacks.client.rendering;

import com.google.common.collect.ImmutableMap;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.datafixers.util.Pair;
import com.mojang.math.Axis;
import com.spydnel.backpacks.client.models.BackpackModel;
import com.spydnel.backpacks.client.models.variants.ChestBackpackModel;
import com.spydnel.backpacks.client.models.variants.FullTonedModel;
import com.spydnel.backpacks.common.blocks.BackpackBlock;
import com.spydnel.backpacks.common.blocks.BackpackBlockEntity;
import net.irisshaders.iris.shaderpack.materialmap.NamespacedId;
import net.irisshaders.iris.shaderpack.materialmap.WorldRenderingSettings;
import net.irisshaders.iris.uniforms.CapturedRenderingState;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.FastColor;
import net.minecraft.util.Mth;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.fml.ModList;

import java.util.Map;

@OnlyIn(Dist.CLIENT)
public class BackpackBlockRenderer implements BlockEntityRenderer<BackpackBlockEntity> {

    public static Map<String, BackpackModel> createVariants(BlockEntityRendererProvider.Context context) {
        ImmutableMap.Builder<String, BackpackModel> builder = ImmutableMap.builder();
        builder.put("default", new BackpackModel(context.bakeLayer(BackpackModel.getBlockLayer())));
        builder.put("chest", new ChestBackpackModel(context.bakeLayer(ChestBackpackModel.getBlockLayer())));
        builder.put("full_toned", new FullTonedModel(context.bakeLayer(BackpackModel.getBlockLayer())));
        return builder.build();
    }

    public final Map<String, BackpackModel> variants;

    private ModelPart base;
    private ModelPart lid;

    private ResourceLocation texture;
    private ResourceLocation overlayTexture;
    private ResourceLocation baseTexture;

    BackpackModel model;

    public BackpackBlockRenderer(BlockEntityRendererProvider.Context context) {
        this.variants = createVariants(context);
    }


    @Override
    public void render(BackpackBlockEntity blockEntity, float partialTick, PoseStack poseStack, MultiBufferSource buffer, int packedLight, int packedOverlay) {

        if (ModList.get().isLoaded("vanity")) {
            Pair<ResourceLocation, String> design = blockEntity.style != null ? blockEntity.style : null;
            if (design == null) {
                model = variants.get("default");
            } else {
                String path = design.getSecond();
                model = variants.getOrDefault(path, variants.get("default"));
            }
        } else {
            model = variants.get("default");
        }

        this.base = model.getRoot().getChild("base");
        this.lid = base.getChild("lid");

        texture = model.getTexture(0);
        overlayTexture = model.getTexture(1);
        baseTexture =model.getTexture(2);

        poseStack.pushPose();
        boolean isFloating = blockEntity.getBlockState().getValue(BackpackBlock.FLOATING);
        float dir = ((Direction)blockEntity.getBlockState().getValue(BackpackBlock.FACING)).toYRot();
        float lidRot = 0;
        float baseRotX = 0;
        float baseRotZ = 0;
        float basePosY = 24;
        float baseScaleY = 0;
        poseStack.translate(0.5F, 0.5F, 0.5F);
        poseStack.mulPose(Axis.YP.rotationDegrees(-dir));
        poseStack.scale(1.0F, -1.0F, -1.0F);
        poseStack.translate(0.0F, isFloating ? -0.8F : -1.0F, 0.0F);


        if (blockEntity.open && blockEntity.openTicks < 10) {
            float t = ((float)blockEntity.openTicks + partialTick);
            lidRot = (float) Math.pow(2, -1 * t) * Mth.sin((t - 0.75F) * 0.7F) +1;
        } else if (blockEntity.openTicks == 10){
            lidRot = 1;
        } else if (blockEntity.openTicks > 0){
            float t = ((float)blockEntity.openTicks - partialTick);
            lidRot = (float) -Math.pow(2, t -10) * Mth.sin((t - 10.75F) * 0.7F);
        }

        if (blockEntity.placeTicks <= 3 && blockEntity.newlyPlaced) {
            float t = ((float)blockEntity.placeTicks + partialTick) / 4;
            basePosY = t * t * 4 +20;
        }
        if (blockEntity.placeTicks <= 7 && blockEntity.newlyPlaced) {
            float t = ((float)blockEntity.placeTicks + partialTick) / 8;
            baseRotX = Mth.sin(t * 10) * 0.1F * (1 - t);
            baseRotZ = Mth.cos(t * 10) * 0.1F * (1 - t);
        }

        if (isFloating) {
            float t = blockEntity.floatTicks + partialTick;
            basePosY += Mth.sin((t + 20) * Mth.DEG_TO_RAD * 4) * 0.75F;
            baseRotX += Mth.sin(t * Mth.DEG_TO_RAD * 4) * 0.02F;
            baseRotZ += Mth.cos(t * Mth.DEG_TO_RAD * 4) * 0.02F;
        }

        this.lid.xRot = lidRot * 1.5F;
        this.base.xRot = baseRotX;
        this.base.zRot = baseRotZ;
        this.base.y = basePosY;

        renderBaseLayer(blockEntity, poseStack, buffer, packedLight, packedOverlay);
        if (blockEntity.getColor() != 0) {
            renderColoredLayer(blockEntity, poseStack, buffer, packedLight, packedOverlay);
        }
        poseStack.popPose();

    }

    private void renderBaseLayer(BackpackBlockEntity blockEntity, PoseStack poseStack, MultiBufferSource buffer, int packedLight, int packedOverlay) {

        ResourceLocation location = blockEntity.getColor() == 0 ? texture : baseTexture;
        VertexConsumer vertexConsumer = buffer.getBuffer(RenderType.entityCutoutNoCull(location));
        this.base.render(poseStack, vertexConsumer, packedLight, packedOverlay);
    }

    private void renderColoredLayer(BackpackBlockEntity blockEntity, PoseStack poseStack, MultiBufferSource buffer, int packedLight, int packedOverlay) {
        int i = FastColor.ARGB32.opaque(blockEntity.getColor());
        if (FastColor.ARGB32.alpha(i) == 0) {
            return;
        }
        ResourceLocation location = overlayTexture;

        if (ModList.get().isLoaded("iris")) {
            irisCompatStuff(location);
        }

        VertexConsumer vertexConsumer = buffer.getBuffer(RenderType.entityCutoutNoCull(overlayTexture));
        this.base.render(poseStack, vertexConsumer, packedLight, packedOverlay, FastColor.ARGB32.opaque(i));
        //poseStack.popPose();
    }

    private void irisCompatStuff(ResourceLocation location) {
        if (WorldRenderingSettings.INSTANCE.getItemIds() != null) {
            CapturedRenderingState.INSTANCE.setCurrentRenderedItem(WorldRenderingSettings.INSTANCE.getItemIds().applyAsInt(new NamespacedId(location.getNamespace(), location.getPath())));
        }
    }


}
