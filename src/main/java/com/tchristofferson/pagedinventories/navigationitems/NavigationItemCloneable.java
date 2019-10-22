package com.tchristofferson.pagedinventories.navigationitems;

import org.bukkit.inventory.ItemStack;

public abstract class NavigationItemCloneable extends NavigationItem implements Cloneable {

    NavigationItemCloneable(ItemStack itemStack) {
        super(itemStack);
    }

    @Override
    public abstract NavigationItem clone();

}
