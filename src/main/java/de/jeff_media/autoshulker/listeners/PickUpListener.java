package de.jeff_media.autoshulker.listeners;

import de.jeff_media.autoshulker.ItemStackFactory;
import de.jeff_media.autoshulker.Main;
import de.jeff_media.autoshulker.config.Messages;
import de.jeff_media.autoshulker.data.PickupResult;
import de.jeff_media.autoshulker.enums.ShulkerType;
import de.jeff_media.autoshulker.utils.InventoryUtils;
import de.jeff_media.autoshulker.utils.ShulkerUtils;
import org.bukkit.Material;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityPickupItemEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;

import java.util.HashSet;

public class PickUpListener implements Listener {

    private final Main main;

    public PickUpListener() {
        main = Main.getInstance();
    }

    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void onPickUpAutoShulker(EntityPickupItemEvent event) {
        //TODO: Apply NBT to picked up AutoShulkers
        if(ShulkerUtils.isAutoShulkerBox(event.getItem().getItemStack())) {
            ItemStack shulker = event.getItem().getItemStack().clone();
            Inventory shulkerInventory = ShulkerUtils.getShulkerInventory(shulker);
            ItemStack paper = shulkerInventory.getItem(ShulkerUtils.PAPER_SLOT);
            ShulkerType shulkerType = ShulkerUtils.getShulkerTypeFromPaper(paper);
            HashSet<Material> materials = ShulkerUtils.getMaterialSetFromPaper(paper);
            ItemStack[] items = shulkerInventory.getContents();
            ItemStackFactory.applyPaperMeta(shulker,materials,shulkerType);
            ShulkerUtils.setShulkerInventory(shulker, items);
            event.getItem().setItemStack(shulker);
        }
    }


    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onPickUpItem(EntityPickupItemEvent event) {
        if(!(event.getEntity() instanceof Player)) return;
        Player player = (Player) event.getEntity();

        ItemStack item = event.getItem().getItemStack();

        @NotNull PickupResult pickupResult = ShulkerUtils.tryToAddToInventory(player.getInventory(),item);

        // Nothing has been stored
        if(pickupResult.getLeftoverItemStack() != null && pickupResult.getLeftoverItemStack().equals(event.getItem().getItemStack())) {
            return;
        }

        if(pickupResult.getLeftoverItemStack()==null) {
            // Nothing
        } else {
            for(ItemStack leftover2 : player.getInventory().addItem(pickupResult.getLeftoverItemStack()).values()) {
                if(InventoryUtils.isNullItem(leftover2)) continue;
                Item newDrop = event.getItem().getWorld().dropItem(event.getItem().getLocation(), leftover2);
                newDrop.setVelocity(new Vector());
            }
        }
        event.setCancelled(true);
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
