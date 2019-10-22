package com.tchristofferson.pagedinventories.navigationitems;

import com.tchristofferson.pagedinventories.NavigationType;
import org.bukkit.inventory.ItemStack;

public final class NextNavigationItem extends NavigationItemCloneable {

    public NextNavigationItem(ItemStack itemStack) {
        super(itemStack);
    }

    @Override
    public NavigationType getNavigationType() {
        return NavigationType.NEXT;
    }

    @Override
    public NextNavigationItem clone() {
        return new NextNavigationItem(getItemStack().clone());
    }
}
