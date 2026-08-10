package com.spydnel.backpacks.client.gui;

import com.spydnel.backpacks.common.menu.BackpackMenu;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;

public class BackpackScreen extends AbstractContainerScreen<BackpackMenu> {
    private static final ResourceLocation TEXTURE =
            ResourceLocation.withDefaultNamespace("textures/gui/container/generic_54.png");

    public BackpackScreen(BackpackMenu menu, Inventory playerInventory, Component title) {
        super(menu, playerInventory, title);
        this.imageWidth = 176;
        this.imageHeight = 17 + menu.rows * 18 + 96;
        this.inventoryLabelY = this.imageHeight - 94;
    }

    @Override
    protected void renderBg(GuiGraphics guiGraphics, float partialTick, int mouseX, int mouseY) {
        int x = (this.width - this.imageWidth) / 2;
        int y = (this.height - this.imageHeight) / 2;
        int topHeight = 17 + this.menu.rows * 18;
        guiGraphics.blit(TEXTURE, x, y, 0, 0, this.imageWidth, topHeight);
        guiGraphics.blit(TEXTURE, x, y + topHeight, 0, 126, this.imageWidth, 96);
    }
}
