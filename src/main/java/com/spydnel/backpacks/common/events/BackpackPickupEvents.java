package com.spydnel.backpacks.common.events;

import com.spydnel.backpacks.Backpacks;
import com.spydnel.backpacks.common.blocks.BackpackBlockEntity;
import com.spydnel.backpacks.common.items.BackpackItem;
import com.spydnel.backpacks.registry.BPBlocks;
import com.spydnel.backpacks.registry.BPItems;
import com.spydnel.backpacks.registry.BPSounds;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.particles.*;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundSource;
import net.minecraft.stats.Stats;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.*;
import net.minecraft.world.item.component.ItemContainerContents;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.minecraft.world.InteractionResult;
import net.neoforged.neoforge.common.util.TriState;
import net.neoforged.neoforge.event.entity.player.ItemEntityPickupEvent;
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent;

import java.util.Objects;

import static com.spydnel.backpacks.common.blocks.BackpackBlock.FACING;
import static com.spydnel.backpacks.common.blocks.BackpackBlock.WATERLOGGED;

@SuppressWarnings("unused")
@EventBusSubscriber(modid = Backpacks.MODID)
public class BackpackPickupEvents {

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public static void onRightClickBlock (PlayerInteractEvent.RightClickBlock event) {



        Level level = event.getLevel();
        Player player = event.getEntity();
        InteractionHand hand = event.getHand();
        BlockPos pos = event.getPos();
        Block block = level.getBlockState(pos).getBlock();
        BlockEntity blockEntity = level.getBlockEntity(pos);
        BlockHitResult blockHitResult = event.getHitVec();

        ItemStack heldItem = event.getItemStack();
        ItemStack chestSlotItem = player.getItemBySlot(EquipmentSlot.CHEST);

        boolean hasBackpack = chestSlotItem.is(BPItems.BACKPACK);
        boolean hasChestPlate = !chestSlotItem.isEmpty();
        boolean isAbove = (pos.above().getY() > player.getEyeY());

        //PICKUP
        if (player.isShiftKeyDown() && !hasChestPlate && block == BPBlocks.BACKPACK.get() && blockEntity != null) {

            ItemStack itemstack = new ItemStack(BPBlocks.BACKPACK);
            itemstack.applyComponents(blockEntity.collectComponents());
            player.setItemSlot(EquipmentSlot.CHEST, itemstack);
            addParticles(level, pos);

            if (!level.isClientSide) {
                level.removeBlockEntity(pos);
                level.removeBlock(pos, false);
            }

            event.setCancellationResult(InteractionResult.sidedSuccess(level.isClientSide));
            event.setCanceled(true);
        }

        //PLACEMENT
        if (
                player.isShiftKeyDown() &&
                hand == InteractionHand.MAIN_HAND &&
                heldItem.isEmpty() &&
                hasBackpack &&
                event.getFace() == Direction.UP
        ) {

            if (!level.getBlockState(pos).useWithoutItem(level, player, blockHitResult).consumesAction()) {
                BlockPlaceContext context = new BlockPlaceContext(player, hand, chestSlotItem, blockHitResult);
                BlockState state = BPBlocks.BACKPACK.get().getStateForPlacement(context);
                ((BackpackItem)chestSlotItem.getItem()).place(context);
                chestSlotItem.shrink(1);
            }

            event.setCancellationResult(InteractionResult.sidedSuccess(level.isClientSide));
            event.setCanceled(true);
        }
    }

    //ARMOR SWAPPING
    @SubscribeEvent
    public static void onRightClickItem(PlayerInteractEvent.RightClickItem event) {
        Item item = event.getItemStack().getItem();
        EquipmentSlot slot = null;

        if (item instanceof ArmorItem) { slot = ((ArmorItem)item).getEquipmentSlot(); }
        if (item instanceof Equipable) { slot = ((Equipable)item).getEquipmentSlot(); }

        if (slot == EquipmentSlot.CHEST && event.getEntity().getItemBySlot(EquipmentSlot.CHEST).is(BPItems.BACKPACK)) {
            event.setCancellationResult(InteractionResult.FAIL);
            event.setCanceled(true);
        }
    }

    //ITEM PICKUP
    @SubscribeEvent
    public static void  onItemEntityPickup(ItemEntityPickupEvent.Pre event) {
        ItemEntity itemEntity = event.getItemEntity();
        ItemStack itemStack = itemEntity.getItem();
        boolean hasContainer = itemStack.has(DataComponents.CONTAINER);
        boolean isEmpty = Objects.equals(itemStack.get(DataComponents.CONTAINER), ItemContainerContents.EMPTY);

        if (itemStack.is(BPItems.BACKPACK) && hasContainer && !isEmpty) {
            Player player = event.getPlayer();
            if (player.getItemBySlot(EquipmentSlot.CHEST).isEmpty() && !itemEntity.hasPickUpDelay()) {
                player.setItemSlot(EquipmentSlot.CHEST, itemStack);
                player.take(itemEntity, 1);
                itemEntity.discard();
                player.awardStat(Stats.ITEM_PICKED_UP.get(itemStack.getItem()), 1);
                player.onItemPickup(itemEntity);
            }
            event.setCanPickup(TriState.FALSE);
        }
    }

    private static void addParticles(Level level, BlockPos pos) {
        for (int i = 0; i < 4; i++) {
            level.addParticle(ParticleTypes.DUST_PLUME, pos.getX() + 0.5, pos.getY(), pos.getZ() + 0.5, 0, 0,0);
        }
    }
}
