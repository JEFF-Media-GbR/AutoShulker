package de.jeff_media.autoshulker.utils;

import com.google.common.base.Enums;
import com.google.gson.Gson;
import de.jeff_media.autoshulker.Main;
import de.jeff_media.autoshulker.config.Permissions;
import de.jeff_media.autoshulker.data.PickupResult;
import de.jeff_media.autoshulker.enums.ShulkerType;
import de.jeff_media.autoshulker.nbt.NBTHandler;
import de.jeff_media.autoshulker.nbt.NBTTags;
import de.jeff_media.jefflib.PDCUtils;
import de.jeff_media.morepersistentdatatypes.DataType;
import org.apache.commons.lang.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.block.ShulkerBox;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BlockStateMeta;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;

public class ShulkerUtils {

    private static final Main main = Main.getInstance();

    private static final HashSet<ItemStack> migratedShulkers = new HashSet<>();

    public static final int PAPER_SLOT = 0;

    public static void setMaterialSetToPaper(ItemStack paper, HashSet<Material> materialSet) {
        HashSet<String> names = new HashSet<>();
        for(Material mat : materialSet) {
            names.add(mat.name());
        }
        String materialsAsJson = StringUtils.join(names,",");
        NBTHandler.applyNBT(paper,NBTTags.MATERIALS,materialsAsJson);
    }

    public static void addMaterialToPaper(ItemStack paper, Material mat) {
        HashSet<Material> mats = getMaterialSetFromPaper(paper);
        mats.add(mat);
        setMaterialSetToPaper(paper,mats);
    }

    public static void migrateFromJson(ItemStack paper, ItemStack box) {
        if(paper == null || box == null) {
            return;
        }
        if(migratedShulkers.contains(box) || migratedShulkers.contains(paper)) {
            return;
        }
        migratedShulkers.add(box);
        migratedShulkers.add(paper);
        String materialsAsJson = NBTHandler.getNBT(paper, NBTTags.MATERIALS);
        if(!materialsAsJson.startsWith("[")) {
            return;
        }
        Inventory inventory = getShulkerInventory(box);
        Main.getInstance().getLogger().info("Migrating old JSON data storage to plaintext...");
        Main.getInstance().getLogger().info("Old JSON data: " + materialsAsJson);
        materialsAsJson = materialsAsJson.replaceAll("\\[","");
        materialsAsJson = materialsAsJson.replaceAll("]","");
        materialsAsJson = materialsAsJson.replaceAll("\"","");
        ItemMeta meta = paper.getItemMeta();
        PersistentDataContainer pdc = meta.getPersistentDataContainer();
        pdc.remove(new NamespacedKey(Main.getInstance(),NBTTags.MATERIALS));
        paper.setItemMeta(meta);
        NBTHandler.applyNBT(paper, NBTTags.MATERIALS,materialsAsJson);
        //Main.getInstance().getLogger().info("New plaintext data: " + materialsAsJson);
        Main.getInstance().getLogger().info("New CSV data : " + paper.getItemMeta().getPersistentDataContainer().get(new NamespacedKey(Main.getInstance(),NBTTags.MATERIALS), PersistentDataType.STRING));
        //inventory.setItem(PAPER_SLOT, paper);
        PDCUtils.set(box, NBTTags.PAPER, DataType.ITEM_STACK, paper);
        setShulkerInventory(box,inventory.getContents());
    }

    public static @Nullable HashSet<Material> getMaterialSetFromPaper(ItemStack paper) {
        HashSet<Material> materialSet = new HashSet<>();
        String materialsAsJson = NBTHandler.getNBT(paper, NBTTags.MATERIALS);
        if(materialsAsJson==null) return materialSet;
        //HashSet<String> names = new Gson().fromJson(materialsAsJson,HashSet.class);
        HashSet<String> names = new HashSet<>(Arrays.asList(materialsAsJson.split(",")));
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
        return Enums.getIfPresent(ShulkerType.class, typeAsString.replace("_","")).orNull();
    }

    public static boolean isPaper(ItemStack item) {
        if(InventoryUtils.isNullItem(item)) return false;
        return NBTHandler.hasNBT(item,NBTTags.SHULKER_TYPE);
    }

    public static boolean isShulkerBox(ItemStack item) {
        if(InventoryUtils.isNullItem(item)) return false;
        // 1.17 fix
        return item.getType().name().endsWith("SHULKER_BOX");
        /*if(item.getItemMeta() instanceof BlockStateMeta){
            BlockStateMeta blockStateMeta = (BlockStateMeta)item.getItemMeta();
            if(blockStateMeta.getBlockState() instanceof ShulkerBox){
                return true;
            }
        }
        return false;*/
    }

    public static Inventory getShulkerInventory(ItemStack item) {
        BlockStateMeta blockStateMeta = (BlockStateMeta) item.getItemMeta();
        ShulkerBox shulker = (ShulkerBox) blockStateMeta.getBlockState();
        return shulker.getInventory();
    }

    public static boolean isAutoShulkerBox(ItemStack item) {
        if(!isShulkerBox(item)) return false;
        if(!item.hasItemMeta()) return false;
        main.debug("Checking whether " + item + " is an autoshulker...");
        if(PDCUtils.has(item,NBTTags.PAPER, DataType.ITEM_STACK)) {
            main.debug("  Yes: PDC");
            return true;
        }
        Inventory shulkerInventory = getShulkerInventory(item.clone());
        if(isPaper(shulkerInventory.getItem(PAPER_SLOT))) {
            main.debug("is paper");
            ItemStack paper = shulkerInventory.getItem(PAPER_SLOT);
            PDCUtils.set(item, NBTTags.PAPER,DataType.ITEM_STACK, paper);

            // Removal start
            BlockStateMeta blockStateMeta = (BlockStateMeta) item.getItemMeta();
            ShulkerBox shulkerBox = (ShulkerBox) blockStateMeta.getBlockState();
            shulkerBox.getInventory().setItem(PAPER_SLOT, null);
            blockStateMeta.setBlockState(shulkerBox);
            item.setItemMeta(blockStateMeta);
            // Removal end

            main.debug("  Yes: Book");

            return true;
        }
        main.debug("  No");
        return false;
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
        //main.debug(1);
        for(ItemStack item : items) {
            //main.debug(2);
            if(isPaper(item)) {
                //main.debug("Correcting JSON in setShulkerInventory");
                migrateFromJson(item, shulker);
            }
        }
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
        ItemStack[] boxes = getAutoShulkerBoxes(player.getInventory());
        ItemStack originalItemStack = itemStack.clone();
        if(boxes.length==0) {
            return new PickupResult(originalItemStack,itemStack,0,0);
        }

        int discarded = 0;
        int collected = 0;

        for(ItemStack box : boxes) {
            if(itemStack==null) break;
            ItemStack paper = PDCUtils.get(box, NBTTags.PAPER, DataType.ITEM_STACK);
            migrateFromJson(paper, box);
            ShulkerType shulkerType = ShulkerUtils.getShulkerTypeFromPaper(paper);
            if(!player.hasPermission(Permissions.USE + shulkerType.name().toLowerCase())) break;
            for(Material material : getMaterialSetFromPaper(paper)) {
                if(itemStack == null) break;
                if(material == itemStack.getType()) {
                    switch(shulkerType) {
                        case GARBAGEBOX:
                            discarded += itemStack.getAmount();
                            itemStack = null;
                            break;
                        case AUTOSHULKER:
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
