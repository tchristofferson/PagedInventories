package com.tchristofferson.pagedinventories;

import com.google.common.base.Preconditions;
import org.bukkit.Material;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

public final class PageModifier {

    private final Inventory inventory;
    //Is the value for the first slot in bottom row
    private final int maxIterableSlot;

    public PageModifier(Inventory inventory) {
        this.inventory = inventory;
        this.maxIterableSlot = inventory.getSize() - 9;
    }

    public ItemStack getItem(int slot) {
        checkSlot(slot);
        return inventory.getItem(slot);
    }

    public void setItem(int slot, ItemStack itemStack) {
        checkSlot(slot);
        inventory.setItem(slot, itemStack);
    }

    public boolean containsAtLeast(Material material, int amount) {
        int count = 0;

        for (int i = 0; i < maxIterableSlot; i++) {
            ItemStack itemStack = inventory.getItem(i);

            if (itemStack == null)
                continue;

            if (itemStack.getType() == material)
                count += itemStack.getAmount();
        }

        return count >= amount;
    }

    public boolean containsAtLeast(ItemStack itemStack, int amount) {
        int count = 0;

        for (int i = 0; i < maxIterableSlot; i++) {
            ItemStack stack = inventory.getItem(i);

            if (stack == null)
                continue;

            if (itemStack.isSimilar(stack))
                count += stack.getAmount();
        }

        return count >= amount;
    }

    /**
     * Returns the size of the inventory - 9 to exclude the navigation row
     * @return The size of the backed inventory
     */
    public int getSize() {
        return inventory.getSize() - 9;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (!(obj instanceof PageModifier))
            return false;

        PageModifier pageModifier = (PageModifier) obj;
        return pageModifier.maxIterableSlot == maxIterableSlot && pageModifier.inventory.equals(inventory);
    }

    private void checkSlot(int slot) {
        Preconditions.checkArgument(slot < maxIterableSlot, "Cannot get or modify navigation items this way!");
    }
}
