package com.spydnel.backpacks.client.models.variants;

import com.google.common.collect.ImmutableList;
import com.spydnel.backpacks.client.models.BackpackModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.resources.ResourceLocation;

import java.util.List;

public class FullTonedModel extends BackpackModel {

    private static List<ResourceLocation> createTextures() {
        ImmutableList.Builder<ResourceLocation> builder = ImmutableList.builder();
        //BLOCK
        builder.add(getFromString("textures/entity/backpack.png"));
        builder.add(getFromString("textures/entity/full_overlay.png"));
        builder.add(getFromString("textures/entity/full_base.png"));

        //BODY
        builder.add(getFromString("textures/model/backpack.png"));
        builder.add(getFromString("textures/model/full_overlay.png"));
        return builder.build();
    }

    public FullTonedModel(ModelPart root) {
        super(root);
        this.textures = createTextures();
    }
}
