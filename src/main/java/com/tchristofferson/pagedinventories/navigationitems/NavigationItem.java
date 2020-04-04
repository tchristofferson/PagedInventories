package com.tchristofferson.pagedinventories.navigationitems;

import com.google.common.base.Preconditions;
import com.tchristofferson.pagedinventories.NavigationType;
import org.bukkit.inventory.ItemStack;

public abstract class NavigationItem {

    private final ItemStack itemStack;
    private final int slot;

    NavigationItem(ItemStack itemStack, int slot) {
        Preconditions.checkArgument(slot <= 8 && slot >= 0, "Invalid slot ranges. Must be 0 - 8");
        this.itemStack = itemStack;
        this.slot = slot;
    }

    public final ItemStack getItemStack() {
        return itemStack;
    }

    public final int getSlot() {
        return slot;
    }

    public abstract NavigationType getNavigationType();

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (!(obj instanceof NavigationItem))
            return false;

        NavigationItem navigationItem = (NavigationItem) obj;
        return navigationItem.itemStack.equals(itemStack) && navigationItem.getNavigationType() == getNavigationType();
    }
}
