package com.spydnel.backpacks.client.rendering;

import com.google.common.collect.ImmutableMap;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.datafixers.util.Pair;
import com.mojang.math.Axis;
import com.spydnel.backpacks.client.models.BackpackModel;
import com.spydnel.backpacks.client.models.variants.ChestBackpackModel;
import com.spydnel.backpacks.client.models.variants.FullTonedModel;
import com.spydnel.backpacks.registry.BPItems;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.geom.EntityModelSet;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.FastColor;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.DyedItemColor;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.fml.ModList;
import org.figuramc.figura.avatar.Avatar;
import org.figuramc.figura.avatar.AvatarManager;
import org.figuramc.figura.model.ParentType;
import tech.thatgravyboat.vanity.common.item.DesignHelper;

import java.util.Map;

import static com.spydnel.backpacks.registry.BPDataAttatchments.OPEN_COUNT;
import static com.spydnel.backpacks.registry.BPDataAttatchments.OPEN_TICKS;

@OnlyIn(Dist.CLIENT)
public class BackpackLayer<T extends LivingEntity, M extends HumanoidModel<T>> extends RenderLayer<T, M>{

    public static Map<String, BackpackModel> createVariants(EntityModelSet entityModelSet) {
        ImmutableMap.Builder<String, BackpackModel> builder = ImmutableMap.builder();
        builder.put("default", new BackpackModel(entityModelSet.bakeLayer(BackpackModel.getBodyLayer())));
        builder.put("chest", new ChestBackpackModel(entityModelSet.bakeLayer(ChestBackpackModel.getBodyLayer())));
        builder.put("full_toned", new FullTonedModel(entityModelSet.bakeLayer(BackpackModel.getBodyLayer())));
        return builder.build();
    }

    public final Map<String, BackpackModel> variants;

    private final ModelPart parentBody;
    private ModelPart root;

    private ResourceLocation texture;
    private ResourceLocation overlayTexture;

    public BackpackLayer(RenderLayerParent renderer, EntityModelSet entityModelSet) {
        super(renderer);

        this.variants = createVariants(entityModelSet);

        this.parentBody = this.getParentBody(renderer);
    }

    public void render(PoseStack poseStack, MultiBufferSource buffer, int packedLight, T livingEntity, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float headYaw, float headPitch) {
        ItemStack itemStack = livingEntity.getItemBySlot(EquipmentSlot.CHEST);

        if (shouldRender(itemStack, livingEntity)) {
            BackpackModel model;

            //VANITY STUFF
            if (ModList.get().isLoaded("vanity")) {
                Pair<ResourceLocation, String> design = DesignHelper.getStyle(itemStack) != null ? DesignHelper.getStyle(itemStack) : null;
                if (design == null) {
                    model = variants.get("default");
                } else {
                    String type = design.getSecond();
                    model = variants.getOrDefault(type, variants.get("default"));
                }
            } else {
                model = variants.get("default");
            }

            root = model.getRoot();
            texture = model.getTexture(3);
            overlayTexture = model.getTexture(4);

            if (ModList.get().isLoaded("figura")) {
                figuraCompatStuff(poseStack, buffer, packedLight, livingEntity, partialTicks, itemStack, this);
            } else {
                renderBaseLayer(poseStack, buffer, packedLight, livingEntity, partialTicks, itemStack, true);
            }
        }
    }

    private void renderBaseLayer(PoseStack poseStack, MultiBufferSource buffer, int packedLight, T livingEntity, float partialTicks, ItemStack itemStack, boolean copyPose) {
        poseStack.pushPose();
        float lidRot = 0;
        boolean isOpen = livingEntity.getData(OPEN_COUNT) > 0;
        int openTicks = livingEntity.getData(OPEN_TICKS);

        if (isOpen && openTicks < 10) {
            float t = ((float)openTicks + partialTicks);
            lidRot = (float) Math.pow(2, -1 * t) * Mth.sin((t - 0.75F) * 0.5F) +1;
        } else if (openTicks == 10){
            lidRot = 1;
        } else if (openTicks > 0){
            float t = ((float)openTicks - partialTicks);
            lidRot = (float) -Math.pow(2, t -10) * Mth.sin((t - 10.75F) * 0.5F);
        }

        this.root.getChild("base").getChild("lid").xRot = lidRot;
        if (copyPose) { this.root.copyFrom(parentBody); }
        VertexConsumer vertexConsumer = ItemRenderer.getArmorFoilBuffer(buffer, RenderType.armorCutoutNoCull(texture), itemStack.hasFoil());
        this.root.render(poseStack, vertexConsumer, packedLight, OverlayTexture.NO_OVERLAY);
        renderColoredLayer(poseStack, buffer, packedLight, itemStack);
        poseStack.popPose();
    }

    private void renderColoredLayer(PoseStack poseStack, MultiBufferSource buffer, int packedLight, ItemStack itemStack) {
        int i = DyedItemColor.getOrDefault(itemStack, 0);
        if (FastColor.ARGB32.alpha(i) == 0) {
            return;
        }

        VertexConsumer vertexConsumer = ItemRenderer.getArmorFoilBuffer(buffer, RenderType.armorCutoutNoCull(overlayTexture), itemStack.hasFoil());

        this.root.render(poseStack, vertexConsumer, packedLight, OverlayTexture.NO_OVERLAY, FastColor.ARGB32.opaque(i));
    }

    public boolean shouldRender(ItemStack stack, T entity) {
        return stack.getItem() == BPItems.BACKPACK.asItem();
    }

    protected ModelPart getParentBody(RenderLayerParent<T, HumanoidModel<T>> renderer) {
        return this.getParentModel().body;
    }

    private void figuraCompatStuff(PoseStack poseStack, MultiBufferSource buffer, int packedLight, T livingEntity, float partialTicks, ItemStack itemStack, BackpackLayer backpackLayer) {
        Avatar avatar = AvatarManager.getAvatar(livingEntity);
        if (avatar != null) {
            boolean shouldRender = (avatar.luaRuntime != null && avatar.luaRuntime.vanilla_model.CHESTPLATE.getVisible() != null) ? avatar.luaRuntime.vanilla_model.CHESTPLATE.getVisible() : true;
            boolean render = avatar.pivotPartRender(ParentType.ChestplatePivot, (stack) -> {
                stack.scale(16.0F, 16.0F, 16.0F);
                stack.mulPose(Axis.XP.rotationDegrees(180.0F));
                stack.mulPose(Axis.YP.rotationDegrees(180.0F));
                renderBaseLayer(stack, buffer, packedLight, livingEntity, partialTicks, itemStack, false);
            });
            if (!render && shouldRender) { renderBaseLayer(poseStack, buffer, packedLight, livingEntity, partialTicks, itemStack, true); }
        } else {
            renderBaseLayer(poseStack, buffer, packedLight, livingEntity, partialTicks, itemStack, true);
        }
    }
}
