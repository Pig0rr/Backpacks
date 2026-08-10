package com.spydnel.backpacks.common.config;

import net.neoforged.neoforge.common.ModConfigSpec;

public class BackpackConfig {
    public static final ModConfigSpec.Builder BUILDER = new ModConfigSpec.Builder();

    public static final ModConfigSpec.IntValue BACKPACK_SLOTS = BUILDER
            .comment("Number of slots in the backpack. Any number from 1 to 90 works, does not need to be a multiple of 9.")
            .defineInRange("backpackSlots", 27, 1, 90);

    public static final ModConfigSpec SPEC = BUILDER.build();
}
