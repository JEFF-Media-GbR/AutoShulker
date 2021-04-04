package de.jeff_media.autoshulker.listeners;

import de.jeff_media.autoshulker.Main;
import de.jeff_media.autoshulker.config.Config;
import de.jeff_media.autoshulker.enums.ShulkerType;
import de.jeff_media.autoshulker.utils.InventoryUtils;
import de.jeff_media.autoshulker.utils.ShulkerUtils;
import org.bukkit.block.ShulkerBox;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

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

    @EventHandler(priority = EventPriority.LOW)
    public void onDiscard(InventoryCloseEvent event) {

        if(!main.getConfig().getBoolean(Config.GARBAGEBOX_DESTROYS_ALL_ITEMS)) return;

        Inventory inv = event.getInventory();

        if(inv.getSize() <= ShulkerUtils.PAPER_SLOT) return;
        if(!ShulkerUtils.isPaper(inv.getItem(ShulkerUtils.PAPER_SLOT))) return;
        ItemStack paper = inv.getItem(ShulkerUtils.PAPER_SLOT);
        ShulkerType shulkerType = ShulkerUtils.getShulkerTypeFromPaper(paper);
        if(shulkerType != ShulkerType.GARBAGEBOX) return;


        for(ItemStack item : inv.getContents()) {
            if(item==null) continue;
            if(item.equals(paper)) continue;
            item.setAmount(0);
        }

    }
}
