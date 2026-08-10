package com.spydnel.backpacks.client;

import com.spydnel.backpacks.Backpacks;
import com.spydnel.backpacks.client.gui.BackpackScreen;
import com.spydnel.backpacks.registry.BPMenus;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.RegisterMenuScreensEvent;

@EventBusSubscriber(modid = Backpacks.MODID, value = Dist.CLIENT)
public class ClientEvents {
    @SubscribeEvent
    public static void registerScreens(RegisterMenuScreensEvent event) {
        event.register(BPMenus.BACKPACK_MENU.get(), BackpackScreen::new);
    }
}
