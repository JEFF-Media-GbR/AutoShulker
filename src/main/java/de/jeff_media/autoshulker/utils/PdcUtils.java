package de.jeff_media.autoshulker.utils;

import de.jeff_media.autoshulker.Main;
import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataHolder;
import org.bukkit.persistence.PersistentDataType;

import java.util.Objects;

/**
 * Persistent-data helpers used by AutoShulker.
 *
 * <p>This contains the small subset of the previous shared library's PDC utilities that the
 * plugin needs. Item stacks require special handling because their PDC lives
 * on their item meta, while entities and blocks expose a PDC directly.</p>
 */
public final class PdcUtils {

    private PdcUtils() {
    }

    public static <T, Z> void set(PersistentDataHolder holder, String key,
                                  PersistentDataType<T, Z> type, Z value) {
        holder.getPersistentDataContainer().set(getKey(key), type, value);
    }

    public static <T, Z> Z get(PersistentDataHolder holder, String key,
                               PersistentDataType<T, Z> type) {
        return holder.getPersistentDataContainer().get(getKey(key), type);
    }

    public static <T, Z> boolean has(PersistentDataHolder holder, String key,
                                     PersistentDataType<T, Z> type) {
        return holder.getPersistentDataContainer().has(getKey(key), type);
    }

    public static <T, Z> void set(ItemStack item, String key,
                                  PersistentDataType<T, Z> type, Z value) {
        ItemMeta meta = getOrCreateMeta(item);
        meta.getPersistentDataContainer().set(getKey(key), type, value);
        item.setItemMeta(meta);
    }

    public static <T, Z> Z get(ItemStack item, String key,
                               PersistentDataType<T, Z> type) {
        ItemMeta meta = item.getItemMeta();
        return meta == null ? null : meta.getPersistentDataContainer().get(getKey(key), type);
    }

    public static <T, Z> boolean has(ItemStack item, String key,
                                     PersistentDataType<T, Z> type) {
        ItemMeta meta = item.getItemMeta();
        return meta != null && meta.getPersistentDataContainer().has(getKey(key), type);
    }

    private static ItemMeta getOrCreateMeta(ItemStack item) {
        ItemMeta meta = item.getItemMeta();
        return Objects.requireNonNull(meta == null
                ? Bukkit.getItemFactory().getItemMeta(item.getType())
                : meta, "Item type does not support item meta: " + item.getType());
    }

    private static NamespacedKey getKey(String key) {
        return new NamespacedKey(Main.getInstance(), key);
    }
}
