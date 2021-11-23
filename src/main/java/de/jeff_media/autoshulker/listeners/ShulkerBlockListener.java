package de.jeff_media.autoshulker.listeners;

import de.jeff_media.autoshulker.Main;
import de.jeff_media.autoshulker.nbt.NBTTags;
import de.jeff_media.autoshulker.utils.ShulkerUtils;
import de.jeff_media.customblockdata.CustomBlockData;
import de.jeff_media.jefflib.PDCUtils;
import de.jeff_media.morepersistentdatatypes.DataType;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Item;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockDropItemEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BlockStateMeta;

public class ShulkerBlockListener implements Listener {

    private static final Main main = Main.getInstance();

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onShulkerPlace(BlockPlaceEvent event) {
        main.debug("BlockPlaceEvent");
        main.debug(event.getItemInHand());
        if(!ShulkerUtils.isAutoShulkerBox(event.getItemInHand())) {
            main.debug(event.getItemInHand());
            return;
        }
        main.debug(1);
        ItemStack paper = PDCUtils.get(event.getItemInHand(), NBTTags.PAPER, DataType.ITEM_STACK);
        main.debug(2);
        CustomBlockData cbd = new CustomBlockData(event.getBlockPlaced(), main);
        main.debug(3);
        cbd.set(new NamespacedKey(main, NBTTags.PAPER), DataType.ITEM_STACK, paper);
        main.debug(4);
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onShulkerBreak(BlockDropItemEvent event) {
        main.debug("BlockDropItemEvent");
        CustomBlockData cbd = new CustomBlockData(event.getBlock(), main);
        main.debug(1);
        if(!cbd.has(new NamespacedKey(main, NBTTags.PAPER), DataType.ITEM_STACK)) return;
        main.debug(2);
        ItemStack paper = cbd.get(new NamespacedKey(main, NBTTags.PAPER), DataType.ITEM_STACK);
        main.debug(3);
        cbd.remove(new NamespacedKey(main, NBTTags.PAPER));
        main.debug(4);
        cbd.clear();
        main.debug(5);
        assert paper != null;
        for(Item item : event.getItems()) {
            System.out.println("Item1^: " + item.getItemStack());
            main.debug(6);
            if(ShulkerUtils.isShulkerBox(item.getItemStack())) {
                main.debug(7);
                ItemStack itemStack = item.getItemStack();
                for(ItemStack content : ShulkerUtils.getShulkerInventory(itemStack)) {
                    System.out.println("Shulker contains: ");
                }
                PDCUtils.set(itemStack, NBTTags.PAPER, DataType.ITEM_STACK, paper);
                item.setItemStack(itemStack);
                PDCUtils.set(item, NBTTags.PAPER, DataType.ITEM_STACK, paper);
                System.out.println("Item2^: " + item.getItemStack());

                return;
            }
        }
    }
}
