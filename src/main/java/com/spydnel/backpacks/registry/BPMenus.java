package com.spydnel.backpacks.registry;

import com.spydnel.backpacks.Backpacks;
import com.spydnel.backpacks.common.menu.BackpackMenu;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.inventory.MenuType;
import net.neoforged.neoforge.common.extensions.IMenuTypeExtension;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Supplier;

public class BPMenus {
    public static final DeferredRegister<MenuType<?>> MENU_TYPES = DeferredRegister.create(Registries.MENU, Backpacks.MODID);

    public static final Supplier<MenuType<BackpackMenu>> BACKPACK_MENU = MENU_TYPES.register(
            "backpack_menu",
            () -> IMenuTypeExtension.create(BackpackMenu::new));
}
