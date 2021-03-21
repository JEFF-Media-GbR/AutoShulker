package de.jeff_media.autoshulker.utils;

import com.google.common.base.Enums;
import com.google.gson.Gson;
import de.jeff_media.autoshulker.config.Permissions;
import de.jeff_media.autoshulker.data.PickupResult;
import de.jeff_media.autoshulker.enums.ShulkerType;
import de.jeff_media.autoshulker.nbt.NBTHandler;
import de.jeff_media.autoshulker.nbt.NBTTags;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.block.ShulkerBox;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BlockStateMeta;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;

public class ShulkerUtils {

    public static final int PAPER_SLOT = 0;

    public static void setMaterialSetToPaper(ItemStack paper, HashSet<Material> materialSet) {
        HashSet<String> names = new HashSet<>();
        for(Material mat : materialSet) {
            names.add(mat.name());
        }
        String materialsAsJson = new Gson().toJson(names);
        NBTHandler.applyNBT(paper,NBTTags.MATERIALS,materialsAsJson);
    }

    public static void addMaterialToPaper(ItemStack paper, Material mat) {
        HashSet<Material> mats = getMaterialSetFromPaper(paper);
        mats.add(mat);
        setMaterialSetToPaper(paper,mats);
    }

    public static @Nullable HashSet<Material> getMaterialSetFromPaper(ItemStack paper) {
        HashSet<Material> materialSet = new HashSet<>();
        String materialsAsJson = NBTHandler.getNBT(paper, NBTTags.MATERIALS);
        if(materialsAsJson==null) return materialSet;
        HashSet<String> names = new Gson().fromJson(materialsAsJson,HashSet.class);
        if(names == null) return materialSet;
        for(String name : names) {
            Material mat = Enums.getIfPresent(Material.class,name).orNull();
            if(mat != null) materialSet.add(mat);
        }
        return materialSet;
    }

    public static @Nullable ShulkerType getShulkerTypeFromPaper(ItemStack paper) {
        String typeAsString = NBTHandler.getNBT(paper, NBTTags.SHULKER_TYPE);
        if(typeAsString == null) return null;
        return Enums.getIfPresent(ShulkerType.class, typeAsString).orNull();
    }

    public static boolean isPaper(ItemStack item) {
        if(InventoryUtils.isNullItem(item)) return false;
        return NBTHandler.hasNBT(item,NBTTags.SHULKER_TYPE);
    }

    public static boolean isShulkerBox(ItemStack item) {
        if(InventoryUtils.isNullItem(item)) return false;
        if(item.getItemMeta() instanceof BlockStateMeta){
            BlockStateMeta blockStateMeta = (BlockStateMeta)item.getItemMeta();
            if(blockStateMeta.getBlockState() instanceof ShulkerBox){
                return true;
            }
        }
        return false;
    }

    public static Inventory getShulkerInventory(ItemStack item) {
        BlockStateMeta blockStateMeta = (BlockStateMeta) item.getItemMeta();
        ShulkerBox shulker = (ShulkerBox) blockStateMeta.getBlockState();
        return shulker.getInventory();
    }

    public static boolean isAutoShulkerBox(ItemStack item) {
        if(!isShulkerBox(item)) return false;
        Inventory shulkerInventory = getShulkerInventory(item);
        return isPaper(shulkerInventory.getItem(PAPER_SLOT));
    }

    public static ItemStack[] getAutoShulkerBoxes(Inventory inventory) {
        HashSet<ItemStack> boxes = new HashSet<>();
        for(ItemStack item : inventory.getContents()) {
            if(isAutoShulkerBox(item)) {
                boxes.add(item);
            }
        }
        return boxes.toArray(new ItemStack[boxes.size()]);
    }

    public static @Nullable ItemStack addToShulkerBox(ItemStack item, ItemStack shulker) {
        BlockStateMeta blockStateMeta = (BlockStateMeta) shulker.getItemMeta();
        ShulkerBox shulkerBox = (ShulkerBox) blockStateMeta.getBlockState();
        HashMap<Integer,ItemStack> overflow = shulkerBox.getInventory().addItem(item);
        blockStateMeta.setBlockState(shulkerBox);
        shulker.setItemMeta(blockStateMeta);
        if(overflow.size()==0) return null;
        return overflow.get(0);
    }

    public static void setShulkerInventory(ItemStack shulker, ItemStack[] items) {
        BlockStateMeta blockStateMeta = (BlockStateMeta) shulker.getItemMeta();
        ShulkerBox shulkerBox = (ShulkerBox) blockStateMeta.getBlockState();
        shulkerBox.getInventory().clear();
        shulkerBox.getInventory().setContents(items);
        blockStateMeta.setBlockState(shulkerBox);
        shulker.setItemMeta(blockStateMeta);
    }

    public static String getColorFromShulker(Material shulker) {
        if(shulker.name().equals("SHULKER_BOX")) return "DEFAULT";
        return shulker.name().replace("_SHULKER_BOX","");
    }

    public static @NotNull PickupResult tryToAddToInventory(Player player, ItemStack itemStack) {
        Inventory shulkerInventory = player.getInventory();
        ItemStack[] boxes = getAutoShulkerBoxes(shulkerInventory);
        ItemStack originalItemStack = itemStack.clone();
        if(boxes.length==0) {
            return new PickupResult(originalItemStack,itemStack,0,0);
        }

        int discarded = 0;
        int collected = 0;

        for(ItemStack box : boxes) {
            if(itemStack==null) break;
            ItemStack paper = getShulkerInventory(box).getItem(PAPER_SLOT);
            ShulkerType shulkerType = ShulkerUtils.getShulkerTypeFromPaper(paper);
            for(Material material : getMaterialSetFromPaper(paper)) {
                if(itemStack == null) break;
                if(!player.hasPermission(Permissions.PICKUP_WITH_NODE + shulkerType.name().toLowerCase())) break;
                if(material == itemStack.getType()) {
                    switch(shulkerType) {
                        case GARBAGE_BOX:
                            discarded += itemStack.getAmount();
                            itemStack = null;
                            break;
                        case AUTO_SHULKER:
                        default:
                            collected += itemStack.getAmount();
                            itemStack = addToShulkerBox(itemStack,box);
                            if(itemStack!=null) {
                                collected -= itemStack.getAmount();
                            }
                    }
                }
            }
        }
        return new PickupResult(originalItemStack,itemStack,collected,discarded);
    }
}
