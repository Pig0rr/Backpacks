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

        guiGraphics.blit(TEXTURE, x, y, 0, 0, this.imageWidth, topHeight);

        for (int row = 0; row < this.menu.rows; row++) {
            int slotsInRow = Math.min(9, this.menu.slotCount - row * 9);
            int hiddenCols = 9 - slotsInRow;
            int rowY = y + 17 + row * 18;

            if (hiddenCols % 2 == 0) {
                int hiddenLeft = hiddenCols / 2;
                int hiddenRight = hiddenCols - hiddenLeft;
                if (hiddenLeft > 0) {
                    guiGraphics.fill(x + 7, rowY, x + 7 + hiddenLeft * 18, rowY + 18, PANEL_COLOR);
                }
                if (hiddenRight > 0) {
                    guiGraphics.fill(x + 7 + (9 - hiddenRight) * 18, rowY, x + 7 + 9 * 18, rowY + 18, PANEL_COLOR);
                }
            } else {
                guiGraphics.fill(x + 7, rowY, x + 7 + 162, rowY + 18, PANEL_COLOR);

                int offset = (162 - slotsInRow * 18) / 2;
                int rowX = x + 7 + offset;
                for (int col = 0; col < slotsInRow; col++) {
                    guiGraphics.blit(TEXTURE, rowX, rowY, 7, 17, 18, 18);
                    rowX += 18;
                }
            }
        }

        guiGraphics.blit(TEXTURE, x, y + topHeight, 0, 126, this.imageWidth, 96);
    }
}
