package com.tchristofferson.pagedinventories.navigationitems;

import com.tchristofferson.pagedinventories.NavigationType;
import org.bukkit.inventory.ItemStack;

public abstract class NavigationItem {

    private final ItemStack itemStack;

    NavigationItem(ItemStack itemStack) {
        this.itemStack = itemStack;
    }

    public final ItemStack getItemStack() {
        return itemStack;
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
