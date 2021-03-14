package de.jeff_media.autoshulker.listeners;

import de.jeff_media.autoshulker.ItemStackFactory;
import de.jeff_media.autoshulker.Main;
import de.jeff_media.autoshulker.utils.InventoryUtils;
import de.jeff_media.autoshulker.utils.ShulkerUtils;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.PrepareItemCraftEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;

public class CraftingListener implements @NotNull Listener {

    private final Main main;

    public CraftingListener() {
        main= Main.getInstance();
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

        ItemStack paper = getPaperFromMatrix(matrix);
        if(paper==null) return;
        matrix.remove(paper);

        HashSet<Material> newMaterials = getMaterialsFromMatrix(matrix);
        if(newMaterials==null) return;

        Inventory shulkerInventory = ShulkerUtils.getShulkerInventory(shulker);

        // This already is a AutoShulker Box, meaning it already contains a paper at the right slot
        if(ShulkerUtils.isAutoShulkerBox(shulker)) {
            ItemStack oldPaper = shulkerInventory.getItem(ShulkerUtils.PAPER_SLOT);
            HashSet<Material> oldMaterials = ShulkerUtils.getMaterialSetFromPaper(oldPaper);
            newMaterials.addAll(oldMaterials);
        } else {
            if(!InventoryUtils.freeSlot(shulkerInventory, ShulkerUtils.PAPER_SLOT)) {
                return;
            }
        }
        ItemStack newPaper = ItemStackFactory.getPaperItem(newMaterials);
        shulkerInventory.setItem(ShulkerUtils.PAPER_SLOT,newPaper);
        ItemStackFactory.applyNameAndLore(shulker,newMaterials);
        ShulkerUtils.setShulkerInventory(shulker,shulkerInventory.getContents());
        event.getInventory().setResult(shulker);

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

    private @Nullable ItemStack getPaperFromMatrix(List<ItemStack> matrix) {
        ItemStack paper = null;
        for(ItemStack item : matrix) {
            if(InventoryUtils.isNullItem(item)) continue;
            if(item.getType()==ItemStackFactory.getPaperMaterial()) {
                if(paper != null) return null;
                paper = item;
            }
        }
        return paper;
    }

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
