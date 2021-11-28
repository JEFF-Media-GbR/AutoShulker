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

public class ShulkerBlockListener implements Listener {

    private static final Main main = Main.getInstance();

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onShulkerPlace(BlockPlaceEvent event) {
        if(!ShulkerUtils.isAutoShulkerBox(event.getItemInHand())) return;
        ItemStack paper = PDCUtils.get(event.getItemInHand(), NBTTags.PAPER, DataType.ITEM_STACK);
        CustomBlockData cbd = new CustomBlockData(event.getBlockPlaced(), main);
        cbd.set(new NamespacedKey(main, NBTTags.PAPER), DataType.ITEM_STACK, paper);
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
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
                return;
            }
        }
    }
}
