package com.spydnel.backpacks.common.menu;

import com.spydnel.backpacks.registry.BPMenus;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.world.Container;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;

public class BackpackMenu extends AbstractContainerMenu {
    private final Container container;
    public final int slotCount;
    public final int rows;

    public BackpackMenu(int windowId, Inventory playerInventory, Container container) {
        super(BPMenus.BACKPACK_MENU.get(), windowId);
        this.container = container;
        this.slotCount = container.getContainerSize();
        this.rows = Math.max(1, (int) Math.ceil(slotCount / 9.0));
        container.startOpen(playerInventory.player);

        layoutBackpackSlots();
        layoutPlayerInventorySlots(playerInventory);
    }

    public BackpackMenu(int windowId, Inventory playerInventory, RegistryFriendlyByteBuf buf) {
        this(windowId, playerInventory, new SimpleContainer(buf.readVarInt()));
    }

    private void layoutBackpackSlots() {
        int index = 0;
        for (int row = 0; row < rows; row++) {
            int slotsInRow = Math.min(9, slotCount - row * 9);
            int rowWidth = slotsInRow * 18;
            int offset = (162 - rowWidth) / 2;
            int startX = 7 + offset + 1;
            int y = 18 + row * 18;
            for (int col = 0; col < slotsInRow; col++) {
                int x = startX + col * 18;
                this.addSlot(new Slot(container, index, x, y));
                index++;
            }
        }
    }

    private void layoutPlayerInventorySlots(Inventory playerInventory) {
        int invY = 18 + rows * 18 + 14;
        for (int row = 0; row < 3; row++) {
            for (int col = 0; col < 9; col++) {
                this.addSlot(new Slot(playerInventory, col + row * 9 + 9, 8 + col * 18, invY + row * 18));
            }
        }
        int hotbarY = invY + 58;
        for (int col = 0; col < 9; col++) {
            this.addSlot(new Slot(playerInventory, col, 8 + col * 18, hotbarY));
        }
    }

    @Override
    public ItemStack quickMoveStack(Player player, int index) {
        ItemStack copy = ItemStack.EMPTY;
        Slot slot = this.slots.get(index);

        if (slot != null && slot.hasItem()) {
            ItemStack stackInSlot = slot.getItem();
            copy = stackInSlot.copy();

            if (index < slotCount) {
                if (!this.moveItemStackTo(stackInSlot, slotCount, this.slots.size(), true)) {
                    return ItemStack.EMPTY;
                }
            } else {
                if (!this.moveItemStackTo(stackInSlot, 0, slotCount, false)) {
                    return ItemStack.EMPTY;
                }
            }

            if (stackInSlot.isEmpty()) {
                slot.set(ItemStack.EMPTY);
            } else {
                slot.setChanged();
            }
        }

        return copy;
    }

    @Override
    public void removed(Player player) {
        super.removed(player);
        this.container.stopOpen(player);
    }

    @Override
    public boolean stillValid(Player player) {
        return container.stillValid(player);
    }
}
