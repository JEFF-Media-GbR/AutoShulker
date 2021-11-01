package de.jeff_media.autoshulker.nbt;

import de.jeff_media.autoshulker.Main;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Method;
import java.util.Objects;

public class NBTHandler {

    public static void applyNBT(@NotNull ItemStack item, String key, String value) {
        Objects.requireNonNull(item,"item must not be null");
        ItemMeta meta = item.hasItemMeta() ? item.getItemMeta() : Bukkit.getItemFactory().getItemMeta(item.getType());
        PersistentDataContainer pdc = meta.getPersistentDataContainer();
        NamespacedKey namespacedKey = new NamespacedKey(Main.getInstance(), key);
        pdc.set(namespacedKey, PersistentDataType.STRING, value);
        item.setItemMeta(meta);
    }

    public static boolean hasNBT(@NotNull ItemStack item, String key) {
        Objects.requireNonNull(item,"item must not be null");
        if(!item.hasItemMeta()) return false;
        ItemMeta meta = item.getItemMeta();
        PersistentDataContainer pdc = meta.getPersistentDataContainer();
        return pdc.has(new NamespacedKey(Main.getInstance(),key),PersistentDataType.STRING);
    }

    public static @Nullable String getNBT(@NotNull ItemStack item, String key) {
        Objects.requireNonNull(item,"item must not be null");
        if(!item.hasItemMeta()) return null;
        ItemMeta meta = item.getItemMeta();
        PersistentDataContainer pdc = meta.getPersistentDataContainer();
        Main main = Main.getInstance();
        NamespacedKey namespacedKey = new NamespacedKey(main,key);
        if(pdc.has(namespacedKey, PersistentDataType.STRING)) {
            return pdc.get(namespacedKey, PersistentDataType.STRING);
        }
        return null;
    }

}
