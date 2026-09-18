package de.jeff_media.autoshulker.listeners;

import de.jeff_media.autoshulker.ItemStackFactory;
import de.jeff_media.autoshulker.Main;
import de.jeff_media.autoshulker.config.Messages;
import de.jeff_media.autoshulker.data.PickupResult;
import de.jeff_media.autoshulker.enums.ShulkerType;
import de.jeff_media.autoshulker.nbt.NBTTags;
import de.jeff_media.autoshulker.utils.InventoryUtils;
import de.jeff_media.autoshulker.utils.ShulkerUtils;
import de.jeff_media.autoshulker.utils.PdcUtils;
import com.jeff_media.morepersistentdatatypes.DataType;
import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityPickupItemEvent;
import org.bukkit.event.player.PlayerPickupItemEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashSet;

public class PickUpListener implements Listener {

    private static final Main main = Main.getInstance();

    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void onPickUpAutoShulker(EntityPickupItemEvent event) {
        //TODO: Apply NBT to picked up AutoShulkers
        onPickupAutoShulker(event.getItem(), event.getItem(), event.getItem());
    }

    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void onPickUpAutoShulker(PlayerPickupItemEvent event) {
        onPickupAutoShulker(event.getItem(), event.getItem(), event.getItem());
    }

    private void onPickupAutoShulker(Item event, Item item1, Item item2) {
        //TODO: Apply NBT to picked up AutoShulkers
        if(ShulkerUtils.isAutoShulkerBox(event.getItemStack())) {
            main.debug("pickup event");
            ItemStack shulker = item1.getItemStack().clone();
            Inventory shulkerInventory = ShulkerUtils.getShulkerInventory(shulker);
            ItemStack paper = PdcUtils.get(shulker, NBTTags.PAPER, DataType.ITEM_STACK);
            ShulkerType shulkerType = ShulkerUtils.getShulkerTypeFromPaper(paper);
            HashSet<Material> materials = ShulkerUtils.getMaterialSetFromPaper(paper);
            ItemStack[] items = shulkerInventory.getContents();
            ItemStackFactory.applyPaperMeta(shulker,materials,shulkerType);
            ShulkerUtils.setShulkerInventory(shulker, items);
            PdcUtils.set(shulker, NBTTags.PAPER, DataType.ITEM_STACK, paper);
            item2.setItemStack(shulker);
        }
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onPickUpItemMonitor(EntityPickupItemEvent event) {
        if(main.eventPriority == EventPriority.MONITOR) {
            main.debug("### EntityPickupItemEvent @ MONITOR");
            onPickUpItem(event);
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onPickUpItemHighest(EntityPickupItemEvent event) {
        if(main.eventPriority == EventPriority.HIGHEST) {
            main.debug("### EntityPickupItemEvent @ HIGHEST");
            onPickUpItem(event);
        }
    }

    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void onPickUpItemHigh(EntityPickupItemEvent event) {
        if(main.eventPriority == EventPriority.HIGH) {
            main.debug("### EntityPickupItemEvent @ HIGH");
            onPickUpItem(event);
        }
    }

    @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
    public void onPickUpItemNormal(EntityPickupItemEvent event) {
        if(main.eventPriority == EventPriority.NORMAL) {
            main.debug("### EntityPickupItemEvent @ NORMAL");
            onPickUpItem(event);
        }
    }

    @EventHandler(priority = EventPriority.LOW, ignoreCancelled = true)
    public void onPickUpItemLow(EntityPickupItemEvent event) {
        if(main.eventPriority == EventPriority.LOW) {
            main.debug("### EntityPickupItemEvent @ LOW");
            onPickUpItem(event);
        }
    }

    @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
    public void onPickUpItemLowest(EntityPickupItemEvent event) {
        if(main.eventPriority == EventPriority.LOWEST) {
            main.debug("### EntityPickupItemEvent @ LOWEST");
            onPickUpItem(event);
        }
    }

    public void onPickUpItem(EntityPickupItemEvent event) {
        onPickUpItem(event.getEntity(),event.getItem().getItemStack(), event.getItem(), event);
    }

    public static void onPickUpItem(Entity entity, ItemStack itemstack, Item item, @Nullable EntityPickupItemEvent event) {
        //main.debug("PickupItemEvent Item: "+event.getItem());
        //main.debug("PickupItemEvent Item#ItemStack: " + event.getItem().getItemStack());
        if(!(entity instanceof Player)) return;
        Player player = (Player) entity;

        //System.out.println("Pickup:");
        //System.out.println("  Item: " + item);
        //System.out.println("  ItemStack: " + itemstack);

        //ItemStack item = event.getItem().getItemStack();

        @NotNull PickupResult pickupResult = ShulkerUtils.tryToAddToInventory(player,itemstack);

        // Nothing has been stored
        if(pickupResult.getLeftoverItemStack() != null && pickupResult.getLeftoverItemStack().equals(itemstack)) {
            return;
        }

        if(pickupResult.getLeftoverItemStack()==null) {
            // Nothing
        } else {
            for(ItemStack leftover2 : player.getInventory().addItem(pickupResult.getLeftoverItemStack()).values()) {
                if(InventoryUtils.isNullItem(leftover2)) continue;
                Item newDrop = item.getWorld().dropItem(item.getLocation(), leftover2);
                newDrop.setVelocity(new Vector());
            }
        }
        if(event != null) {
            event.setCancelled(true);
        }
        ItemStack remainingItem = event.getItem().getItemStack().clone();
        remainingItem.setAmount(event.getRemaining());
        if(InventoryUtils.isNullItem(remainingItem)) {
            event.getItem().remove();
        } else {
            event.getItem().setItemStack(remainingItem);
        }
        main.soundUtils.playPickupSound(player);
        Messages.showActionBarMessage(player,main.messages.getActionbarMessage(pickupResult));
        // TODO: Drop overflow
    }

}
