package com.tchristofferson.pagedinventories.navigationitems;

import com.tchristofferson.pagedinventories.NavigationType;
import org.bukkit.inventory.ItemStack;

public final class PreviousNavigationItem extends NavigationItemCloneable {

    public PreviousNavigationItem(ItemStack itemStack) {
        this(itemStack, 0);
    }

    public PreviousNavigationItem(ItemStack itemStack, int slot) {
        super(itemStack, slot);
    }

    @Override
    public NavigationType getNavigationType() {
        return NavigationType.PREVIOUS;
    }

    @Override
    public NavigationItem clone() {
        return new PreviousNavigationItem(getItemStack().clone());
    }
}
