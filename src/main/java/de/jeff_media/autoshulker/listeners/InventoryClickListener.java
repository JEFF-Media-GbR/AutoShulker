package de.jeff_media.autoshulker.listeners;

import de.jeff_media.autoshulker.Main;
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
        Inventory inv = event.getInventory();

        if(inv.getSize() <= ShulkerUtils.PAPER_SLOT) return;
        System.out.println(1);
        if(!ShulkerUtils.isPaper(inv.getItem(ShulkerUtils.PAPER_SLOT))) return;
        System.out.println(2);
        ItemStack paper = inv.getItem(ShulkerUtils.PAPER_SLOT);
        ShulkerType shulkerType = ShulkerUtils.getShulkerTypeFromPaper(paper);
        if(shulkerType != ShulkerType.GARBAGE_BOX) return;
        System.out.println(3);


        for(ItemStack item : inv.getContents()) {
            if(item==null) continue;
            System.out.println(4);
            if(item.equals(paper)) continue;
            item.setAmount(0);
        }

    }
}
