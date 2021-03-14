package de.jeff_media.autoshulker;

import com.google.common.base.Enums;
import de.jeff_media.autoshulker.config.Config;
import de.jeff_media.autoshulker.utils.ShulkerUtils;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

public class ItemStackFactory {

    public static Material getPaperMaterial() {
        return Enums.getIfPresent(Material.class,Main.getInstance().getConfig().getString(Config.ITEM_MATERIAL)).or(Material.BOOK);
    }

    public static void applyNameAndLore(ItemStack item, HashSet<Material> materialSet) {
        Main main = Main.getInstance();
        String displayName = main.getConfig().getString(Config.ITEM_NAME);
        List<String> lore = new ArrayList<>();
        for(String line : main.getConfig().getString(Config.ITEM_LORE).split("\n")) {
            lore.add(line);
        }
        for(Material mat : materialSet) {
            lore.add(String.format(main.getConfig().getString(Config.ITEM_LORE_LINE),mat.name()));
        }
        ItemMeta meta = Bukkit.getItemFactory().getItemMeta(item.getType());
        meta.setDisplayName(displayName);
        meta.setLore(lore);

        item.setItemMeta(meta);
        ShulkerUtils.setMaterialSetToPaper(item,materialSet);
    }

    public static ItemStack getPaperItem(HashSet<Material> materialSet) {
        Material material = getPaperMaterial();
        ItemStack item = new ItemStack(material,1);
        applyNameAndLore(item,materialSet);
        return item;
    }

}
