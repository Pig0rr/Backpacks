package com.spydnel.backpacks.common.config;

import net.neoforged.neoforge.common.ModConfigSpec;

public class BackpackConfig {
    public static final ModConfigSpec.Builder BUILDER = new ModConfigSpec.Builder();

    private static boolean isValidSlotCount(Object value) {
        if (!(value instanceof Integer)) return false;
        int v = (Integer) value;
        if (v < 1 || v > 54) return false;
        int lastRow = v % 9;
        return lastRow == 0 || lastRow % 2 == 1;
    }

    public static final ModConfigSpec.ConfigValue<Integer> BACKPACK_SLOTS = BUILDER
            .comment(
                "Number of slots in the backpack. Range: 1 to 54.",
                "The last (partial) row must contain either 9 slots (full row) or an odd number (1, 3, 5, 7) to center correctly.",
                "Valid examples: 1,3,5,7,9,10,12,14,16,18,19,21... Invalid examples: 2,4,6,8,11,13,15,17,20...",
                "If you set an invalid number, it will fall back to the default (27)."
            )
            .define("backpackSlots", 27, BackpackConfig::isValidSlotCount);

    public static final ModConfigSpec SPEC = BUILDER.build();
}
