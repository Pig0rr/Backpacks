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
    private static final int PANEL_COLOR = 0xFFC6C6C6;

    public BackpackScreen(BackpackMenu menu, Inventory playerInventory, Component title) {
        super(menu, playerInventory, title);
        this.imageWidth = 176;
        this.imageHeight = 114 + menu.rows * 18;
        this.inventoryLabelY = this.imageHeight - 94;
    }

    @Override
    protected void renderBg(GuiGraphics guiGraphics, float partialTick, int mouseX, int mouseY) {
        int x = (this.width - this.imageWidth) / 2;
        int y = (this.height - this.imageHeight) / 2;
        int topHeight = 17 + this.menu.rows * 18;

        guiGraphics.blit(TEXTURE, x, y, 0, 0, this.imageWidth, 17);

        for (int row = 0; row < this.menu.rows; row++) {
            int slotsInRow = Math.min(9, this.menu.slotCount - row * 9);
            int rowY = y + 17 + row * 18;
            int rowWidth = 7 + slotsInRow * 18 + 7;
            int rowX = x + (this.imageWidth - rowWidth) / 2;

            guiGraphics.fill(x + 1, rowY, x + this.imageWidth - 1, rowY + 18, PANEL_COLOR);

            guiGraphics.blit(TEXTURE, rowX, rowY, 0, 17, 7, 18);
            rowX += 7;

            for (int col = 0; col < slotsInRow; col++) {
                guiGraphics.blit(TEXTURE, rowX, rowY, 7, 17, 18, 18);
                rowX += 18;
            }

            guiGraphics.blit(TEXTURE, rowX, rowY, 169, 17, 7, 18);
        }

        guiGraphics.blit(TEXTURE, x, y + topHeight, 0, 126, this.imageWidth, 96);
    }
}
