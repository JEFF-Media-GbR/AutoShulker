package de.jeff_media.autoshulker.listeners;

import de.jeff_media.autoshulker.ItemStackFactory;
import de.jeff_media.autoshulker.Main;
import de.jeff_media.autoshulker.enums.ShulkerType;
import de.jeff_media.autoshulker.nbt.NBTTags;
import de.jeff_media.autoshulker.utils.ShulkerUtils;
import de.jeff_media.customblockdata.CustomBlockData;
import com.jeff_media.jefflib.PDCUtils;
import de.jeff_media.morepersistentdatatypes.DataType;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Item;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockDropItemEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.ItemSpawnEvent;
import org.bukkit.event.inventory.InventoryPickupItemEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.HashSet;

public class ShulkerBlockListener implements Listener {

    private static final Main main = Main.getInstance();

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onShulkerPlace(BlockPlaceEvent event) {
        if(!ShulkerUtils.isAutoShulkerBox(event.getItemInHand())) return;
        ItemStack paper = PDCUtils.get(event.getItemInHand(), NBTTags.PAPER, DataType.ITEM_STACK);
        CustomBlockData cbd = new CustomBlockData(event.getBlockPlaced(), main);
        cbd.set(new NamespacedKey(main, NBTTags.PAPER), DataType.ITEM_STACK, paper);
    }

    @EventHandler(priority = EventPriority.LOW, ignoreCancelled = true)
    public void onShulkerBreak(BlockDropItemEvent event) {
        CustomBlockData cbd = new CustomBlockData(event.getBlock(), main);
        if(!cbd.has(new NamespacedKey(main, NBTTags.PAPER), DataType.ITEM_STACK)) return;
        ItemStack paper = cbd.get(new NamespacedKey(main, NBTTags.PAPER), DataType.ITEM_STACK);
        cbd.clear();
        assert paper != null;
        for(Item item : event.getItems()) {
            if(ShulkerUtils.isShulkerBox(item.getItemStack())) {
                ItemStack itemStack = item.getItemStack();
                PDCUtils.set(itemStack, NBTTags.PAPER, DataType.ITEM_STACK, paper);
                item.setItemStack(itemStack);
                PDCUtils.set(item, NBTTags.PAPER, DataType.ITEM_STACK, paper);
                //ItemStackFactory.applyPaperMeta(item.getItemStack(),ShulkerUtils.getMaterialSetFromPaper(paper),ShulkerUtils.getShulkerTypeFromPaper(paper));

                // Debug start

                if(ShulkerUtils.isAutoShulkerBox(item.getItemStack())) {
                    main.debug("pickup event");
                    ItemStack shulker = item.getItemStack().clone();
                    Inventory shulkerInventory = ShulkerUtils.getShulkerInventory(shulker);
                    ItemStack paper2 = PDCUtils.get(shulker, NBTTags.PAPER, DataType.ITEM_STACK);
                    ShulkerType shulkerType = ShulkerUtils.getShulkerTypeFromPaper(paper2);
                    HashSet<Material> materials = ShulkerUtils.getMaterialSetFromPaper(paper2);
                    ItemStack[] items = shulkerInventory.getContents();
                    ItemStackFactory.applyPaperMeta(shulker,materials,shulkerType);
                    ShulkerUtils.setShulkerInventory(shulker, items);
                    PDCUtils.set(shulker, NBTTags.PAPER, DataType.ITEM_STACK, paper2);
                    item.setItemStack(shulker);
                }

                // Debug end



                return;
            }
        }
    }

    @EventHandler
    public void onHopper(InventoryPickupItemEvent event) {
        if(!ShulkerUtils.isShulkerBox(event.getItem().getItemStack())) return;
        if(ShulkerUtils.isAutoShulkerBox(event.getItem().getItemStack())) {
            ItemStack shulker = event.getItem().getItemStack().clone();
            Inventory shulkerInventory = ShulkerUtils.getShulkerInventory(shulker);
            ItemStack paper = PDCUtils.get(shulker, NBTTags.PAPER, DataType.ITEM_STACK);
            ShulkerType shulkerType = ShulkerUtils.getShulkerTypeFromPaper(paper);
            HashSet<Material> materials = ShulkerUtils.getMaterialSetFromPaper(paper);
            ItemStack[] items = shulkerInventory.getContents();
            ItemStackFactory.applyPaperMeta(shulker,materials,shulkerType);
            ShulkerUtils.setShulkerInventory(shulker, items);
            PDCUtils.set(shulker, NBTTags.PAPER, DataType.ITEM_STACK, paper);
            event.getItem().setItemStack(shulker);
        }
    }
}
