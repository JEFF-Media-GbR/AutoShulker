package de.jeff_media.autoshulker.listeners;

import de.jeff_media.autoshulker.Main;
import de.jeff_media.autoshulker.utils.InventoryUtils;
import de.jeff_media.autoshulker.utils.ShulkerUtils;
import org.bukkit.block.ShulkerBox;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;

public class InventoryClickListener implements Listener {

    private final Main main;

    public InventoryClickListener() {
        this.main = Main.getInstance();
    }

    @EventHandler
    public void onPaperClickEvent(InventoryClickEvent event) {
        if(InventoryUtils.isNullItem(event.getCurrentItem())) return;
        if(event.getClickedInventory() != null && event.getClickedInventory().getHolder() instanceof ShulkerBox) {
            if(event.getSlot() == ShulkerUtils.PAPER_SLOT) {
                if(ShulkerUtils.isPaper(event.getCurrentItem())) {
                    event.setCancelled(true);
                }
            }
        }
    }
}
