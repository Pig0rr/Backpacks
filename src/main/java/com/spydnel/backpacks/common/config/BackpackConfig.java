package com.spydnel.backpacks.common.config;

import net.neoforged.neoforge.common.ModConfigSpec;

public class BackpackConfig {
    public static final ModConfigSpec.Builder BUILDER = new ModConfigSpec.Builder();

    public static final ModConfigSpec.IntValue BACKPACK_SLOTS = BUILDER
            .comment("Number of slots in the backpack. Range: 1 to 54.")
            .defineInRange("backpackSlots", 27, 1, 54);

    public static final ModConfigSpec SPEC = BUILDER.build();
}
