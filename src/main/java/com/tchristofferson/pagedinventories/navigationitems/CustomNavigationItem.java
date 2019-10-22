package com.tchristofferson.pagedinventories.navigationitems;

import com.tchristofferson.pagedinventories.NavigationType;
import com.tchristofferson.pagedinventories.handlers.PagedInventoryCustomNavigationHandler;
import org.bukkit.inventory.ItemStack;

public abstract class CustomNavigationItem extends NavigationItem {

    public CustomNavigationItem(ItemStack itemStack) {
        super(itemStack);
    }

    @Override
    public final NavigationType getNavigationType() {
        return NavigationType.CUSTOM;
    }

    public abstract void handleClick(PagedInventoryCustomNavigationHandler handler);
}
