package de.jeff_media.autoshulker.listeners;

import de.jeff_media.autoshulker.Main;
import de.jeff_media.autoshulker.config.Messages;
import de.jeff_media.autoshulker.utils.InventoryUtils;
import de.jeff_media.autoshulker.utils.ShulkerUtils;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityPickupItemEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;

public class PickUpListener implements Listener {

    private final Main main;

    public PickUpListener() {
        main = Main.getInstance();
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onPickUpItem(EntityPickupItemEvent event) {
        if(!(event.getEntity() instanceof Player)) return;
        Player player = (Player) event.getEntity();

        ItemStack item = event.getItem().getItemStack();

        ItemStack leftover = ShulkerUtils.tryToAddToInventory(player.getInventory(),item);

        // Nothing has been stored
        if(leftover != null && leftover.equals(event.getItem().getItemStack())) {
            return;
        }

        if(leftover==null) {
            // Nothing
        } else {
            for(ItemStack leftover2 : player.getInventory().addItem(leftover).values()) {
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
        Messages.showActionBarMessage(player,main.messages.getActionbarMessage(item));
        // TODO: Drop overflow
    }

}
