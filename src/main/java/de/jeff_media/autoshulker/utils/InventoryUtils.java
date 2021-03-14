package de.jeff_media.autoshulker.utils;

import org.bukkit.Material;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

public class InventoryUtils {

    public static boolean freeSlot(Inventory inventory, int slot) {
        if(isNullItem(inventory.getItem(slot))) {
            return true;
        }
        for(int i = 0; i < inventory.getSize(); i++) {
            if(i != slot && isNullItem(inventory.getItem(i))) {
                inventory.setItem(i,inventory.getItem(slot));
                inventory.setItem(slot,null);
                return true;
            }
        }
        return false;
    }

    public static boolean isNullItem(ItemStack item) {
        if(item==null) return true;
        if(item.getAmount()==0) return true;
        return item.getType()== Material.AIR;
    }
}
