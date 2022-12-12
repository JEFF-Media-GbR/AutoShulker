package de.jeff_media.autoshulker.listeners;

import de.jeff_media.autoshulker.ItemStackFactory;
import de.jeff_media.autoshulker.Main;
import de.jeff_media.autoshulker.config.Permissions;
import de.jeff_media.autoshulker.enums.ShulkerType;
import de.jeff_media.autoshulker.nbt.NBTTags;
import de.jeff_media.autoshulker.utils.InventoryUtils;
import de.jeff_media.autoshulker.utils.ShulkerUtils;
import com.jeff_media.jefflib.PDCUtils;
import de.jeff_media.morepersistentdatatypes.DataType;
import org.bukkit.Material;
import org.bukkit.block.ShulkerBox;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.inventory.PrepareItemCraftEvent;
import org.bukkit.inventory.CraftingInventory;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BlockStateMeta;
import org.bukkit.inventory.meta.ItemMeta;
import org.javatuples.Pair;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;

public class CraftingListener implements @NotNull Listener {

    private final Main main;

    public CraftingListener() {
        main= Main.getInstance();
    }

    @EventHandler
    public void onDupe(InventoryClickEvent event) {
        if(event.getView().getTopInventory().getType() == InventoryType.WORKBENCH) {
            CraftingInventory inv = (CraftingInventory) event.getView().getTopInventory();
            if(inv.getResult() != null) {
                if (ShulkerUtils.isAutoShulkerBox(inv.getResult())) {
                    if(event.getCurrentItem() != null && !event.getCurrentItem().getType().isAir()) {
                        if(ShulkerUtils.isAutoShulkerBox(event.getCurrentItem())) {
                            if(hasMoreThanOneIngredient(inv.getMatrix())) event.setCancelled(true);
                        }
                    }
                }
            }
        }
    }

    private static boolean hasMoreThanOneIngredient(ItemStack[] items) {
        for(ItemStack item : items) {
            if(item == null) continue;
            if(item.getAmount()>1) return true;
        }
        return false;
    }

    @EventHandler
    public void onCraft(InventoryClickEvent event) {
        if(event.getSlotType() != InventoryType.SlotType.RESULT) return;
        if(event.getClickedInventory() == null) return;
        if(event.getClickedInventory().getType() != InventoryType.CRAFTING) return;
        CraftingInventory inv = (CraftingInventory) event.getClickedInventory();
        if(!ShulkerUtils.isAutoShulkerBox(inv.getResult())) return;
        for(ItemStack item : inv.getMatrix()) {
            if(item==null) continue;
            if(item.getAmount()>1) event.setCancelled(true);
        }
    }

    @EventHandler
    public void turnShulkerToRegular(PrepareItemCraftEvent event) {
        ItemStack oldShulkerItemStack = null;
        for(ItemStack item : event.getInventory().getMatrix()) {
            if(item == null) continue;
            if(oldShulkerItemStack == null) {
                if(ShulkerUtils.isAutoShulkerBox(item) && item.getAmount()==1) {
                    oldShulkerItemStack = item;
                } else {
                    return;
                }
            } else {
                return;
            }
        }
        if(oldShulkerItemStack==null) return;

        ItemStack newShulkerItemStack = new ItemStack(oldShulkerItemStack.getType());
        BlockStateMeta newShulkerItemMeta = (BlockStateMeta) newShulkerItemStack.getItemMeta();
        ShulkerBox newShulkerBox = (ShulkerBox) newShulkerItemMeta.getBlockState();
        Inventory newInventory = newShulkerBox.getInventory();
        BlockStateMeta oldShulkerItemMeta = (BlockStateMeta) oldShulkerItemStack.getItemMeta();
        ShulkerBox oldShulkerBox = (ShulkerBox) oldShulkerItemMeta.getBlockState();
        Inventory oldInventory = oldShulkerBox.getInventory();

        for(ItemStack item : oldInventory) {
            if(item == null || item.getAmount() == 0 || ShulkerUtils.isPaper(item)) {
                continue;
            }
            newInventory.addItem(item);
        }
        newShulkerItemMeta.setBlockState(newShulkerBox);
        newShulkerItemStack.setItemMeta(newShulkerItemMeta);
        event.getInventory().setResult(newShulkerItemStack);
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {

    }

    @EventHandler
    public void onCraftPrepare(PrepareItemCraftEvent event) {
        List<ItemStack> matrix = new LinkedList<>(Arrays.asList(event.getInventory().getMatrix()));
        for(ItemStack item : matrix) {
            if(InventoryUtils.isNullItem(item)) continue;
            if(item.getAmount()!=1) return;
        }

        ItemStack shulker = getShulkerFromMatrix(matrix);
        if(shulker==null) return;
        matrix.remove(shulker);
        shulker = shulker.clone();

        Pair<ItemStack,ShulkerType> shulkerTypePair = getShulkerTypeFromMatrix(matrix);
        if(shulkerTypePair==null) return;
        if(shulkerTypePair.getValue0() != null) {
            matrix.remove(shulkerTypePair.getValue0());
        }
        ShulkerType shulkerType = shulkerTypePair.getValue1();
        if(!event.getView().getPlayer().hasPermission(Permissions.CRAFT+shulkerType.name().toLowerCase())) return;

        HashSet<Material> newMaterials = getMaterialsFromMatrix(matrix);
        if(newMaterials==null) {
            if (!ShulkerUtils.isAutoShulkerBox(shulker)) {
                return;
            } else {
                Inventory shulkerInventory = ShulkerUtils.getShulkerInventory(shulker);
                ItemStack oldPaper = PDCUtils.get(shulker,NBTTags.PAPER, DataType.ITEM_STACK); //shulkerInventory.getItem(ShulkerUtils.PAPER_SLOT);
                ShulkerType oldType = ShulkerUtils.getShulkerTypeFromPaper(oldPaper);
                if(oldType == shulkerType) return;
            }

        }

        Inventory shulkerInventory = ShulkerUtils.getShulkerInventory(shulker);

        // This already is a AutoShulker Box, meaning it already contains a paper at the right slot
        if(ShulkerUtils.isAutoShulkerBox(shulker)) {
            ItemStack oldPaper = PDCUtils.get(shulker, NBTTags.PAPER, DataType.ITEM_STACK); //shulkerInventory.getItem(ShulkerUtils.PAPER_SLOT);
            HashSet<Material> oldMaterials = ShulkerUtils.getMaterialSetFromPaper(oldPaper);
            removeDuplicateMaterials(oldMaterials, newMaterials);
            if(newMaterials==null) newMaterials = new HashSet<>();
            newMaterials.addAll(oldMaterials);
        } /*else {
            if(!InventoryUtils.freeSlot(shulkerInventory, ShulkerUtils.PAPER_SLOT)) {
                return;
            }
        }*/
        ItemStack newPaper = ItemStackFactory.getPaperItem(newMaterials,shulkerType);
        //shulkerInventory.setItem(ShulkerUtils.PAPER_SLOT,newPaper);
        ItemStackFactory.applyPaperMeta(shulker,newMaterials,shulkerType);
        ShulkerUtils.setShulkerInventory(shulker,shulkerInventory.getContents());
        PDCUtils.set(shulker, NBTTags.PAPER, DataType.ITEM_STACK, newPaper);
        event.getInventory().setResult(shulker);

    }

    private void removeDuplicateMaterials(HashSet<Material> oldMaterials, HashSet<Material> newMaterials) {
        if(newMaterials==null) newMaterials = new HashSet<>();
        Iterator<Material> it = newMaterials.iterator();
        while(it.hasNext()) {
            Material mat = it.next();
            if(oldMaterials.contains(mat)) {
                it.remove();
                oldMaterials.remove(mat);
            }
        }
    }

    private @Nullable Pair<ItemStack, ShulkerType> getShulkerTypeFromMatrix(List<ItemStack> matrix) {
        Pair<ItemStack, ShulkerType> result = null;
        for(ItemStack item : matrix) {
            if(InventoryUtils.isNullItem(item)) continue;
            for(ShulkerType shulkerType : ShulkerType.values()) {
                if (item.getType() == ItemStackFactory.getSpecialItem(shulkerType)) {
                    if (result != null) return null;
                    result = new Pair<>(item, shulkerType);
                }
            }
        }
        return result;
    }

    private @Nullable ItemStack getShulkerFromMatrix(List<ItemStack> matrix) {
        ItemStack shulker = null;
        for(ItemStack item : matrix) {
            if(InventoryUtils.isNullItem(item)) continue;
            if(ShulkerUtils.isShulkerBox(item)) {
                if(shulker != null) return null;
                shulker = item;
            }
        }
        return shulker;
    }

    /*private @Nullable ItemStack getPaperFromMatrix(List<ItemStack> matrix) {
        ItemStack paper = null;
        for(ItemStack item : matrix) {
            if(InventoryUtils.isNullItem(item)) continue;
            if(item.getType()==ItemStackFactory.getPaperMaterial()) {
                if(paper != null) return null;
                paper = item;
            }
        }
        return paper;
    }*/

    private @Nullable HashSet<Material> getMaterialsFromMatrix(List<ItemStack> matrix) {
        HashSet<Material> materialSet = new HashSet<>();
        for(ItemStack item : matrix) {
            if(InventoryUtils.isNullItem(item)) continue;
            if(materialSet.contains(item.getType())) {
                return null;
            } else {
                materialSet.add(item.getType());
            }
        }
        if(materialSet.size()==0) return null;
        return materialSet;
    }
}
