package de.jeff_media.autoshulker;

import com.google.common.base.Enums;
import de.jeff_media.autoshulker.config.Config;
import de.jeff_media.autoshulker.enums.ShulkerType;
import de.jeff_media.autoshulker.nbt.NBTHandler;
import de.jeff_media.autoshulker.nbt.NBTTags;
import de.jeff_media.autoshulker.utils.ShulkerUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.Nullable;
import de.jeff_media.jefflib.WordUtils;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

public class ItemStackFactory {

    /*public static Material getPaperMaterial() {
        return
    }*/

    public static @Nullable Material getSpecialItem(ShulkerType shulkerType) {
        switch(shulkerType) {
            case GARBAGEBOX:
                return Enums.getIfPresent(Material.class,Main.getInstance().getConfig().getString(Config.ITEM_MATERIAL_GARBAGE).toUpperCase()).or(Material.LAVA_BUCKET);
            case AUTOSHULKER:
            default:
                return Enums.getIfPresent(Material.class,Main.getInstance().getConfig().getString(Config.ITEM_MATERIAL).toUpperCase()).or(Material.BOOK);
        }
    }

    public static void applyPaperMeta(ItemStack item, HashSet<Material> materialSet, ShulkerType shulkerType) {
        Main main = Main.getInstance();
        String displayName = ChatColor.translateAlternateColorCodes('&',main.getConfig().getString(shulkerType.getConfigName()));
        List<String> lore = new ArrayList<>();
        for(String line : ChatColor.translateAlternateColorCodes('&',main.getConfig().getString(shulkerType.getConfigLore())).split("\n")) {
            lore.add(line);
        }
        for(Material mat : materialSet) {
            lore.add(String.format(ChatColor.translateAlternateColorCodes('&',main.getConfig().getString(Config.ITEM_LORE_LINE)),WordUtils.getNiceMaterialName(mat)));
        }
        ItemMeta meta = Bukkit.getItemFactory().getItemMeta(item.getType());
        meta.setDisplayName(displayName);
        meta.setLore(lore);

        item.setItemMeta(meta);
        ShulkerUtils.setMaterialSetToPaper(item,materialSet);
        NBTHandler.applyNBT(item, NBTTags.SHULKER_TYPE,shulkerType.name());
    }

    public static ItemStack getPaperItem(HashSet<Material> materialSet, ShulkerType shulkerType) {
        Material material = getSpecialItem(shulkerType);
        ItemStack item = new ItemStack(material,1);
        applyPaperMeta(item,materialSet, shulkerType);
        return item;
    }

}
