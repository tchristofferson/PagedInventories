package com.tchristofferson.pagedinventories.navigationitems;

import com.tchristofferson.pagedinventories.NavigationType;
import org.bukkit.inventory.ItemStack;

public final class CloseNavigationItem extends NavigationItemCloneable {

    public CloseNavigationItem(ItemStack itemStack) {
        super(itemStack);
    }

    @Override
    public NavigationType getNavigationType() {
        return NavigationType.CLOSE;
    }

    @Override
    public CloseNavigationItem clone() {
        return new CloseNavigationItem(getItemStack().clone());
    }
}
