package de.jeff_media.autoshulker.data;

import org.bukkit.inventory.ItemStack;

public class PickupResult {

    private int collected, discarded;
    private ItemStack leftover, original;

    public PickupResult(ItemStack original, ItemStack leftover, int collected, int discarded) {
        this.leftover = leftover;
        this.collected = collected;
        this.discarded = discarded;
        this.original=original;
    }

    public int getCollected() {
        return collected;
    }

    public int getDiscarded() {
        return discarded;
    }

    public ItemStack getLeftoverItemStack() {
        return leftover;
    }

    public ItemStack getOriginalItemStack() {
        return original;
    }

}
