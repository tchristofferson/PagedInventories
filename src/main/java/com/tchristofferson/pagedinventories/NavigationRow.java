package com.tchristofferson.pagedinventories;

import com.google.common.base.Preconditions;
import com.tchristofferson.pagedinventories.navigationitems.CloseNavigationItem;
import com.tchristofferson.pagedinventories.navigationitems.NavigationItem;
import com.tchristofferson.pagedinventories.navigationitems.NextNavigationItem;
import com.tchristofferson.pagedinventories.navigationitems.PreviousNavigationItem;

/**
 * Represents the navigation row (bottom row) of an IPagedInventory
 */
public class NavigationRow {

    private final NavigationItem[] navigation;

    public NavigationRow(NextNavigationItem next, PreviousNavigationItem previous, CloseNavigationItem close, NavigationItem ... others) {
        Preconditions.checkArgument(next != null && previous != null && close != null, "navigation cannot be null");
        navigation = new NavigationItem[9];

        for (NavigationItem navigationItem : others) {
            navigation[navigationItem.getSlot()] = navigationItem;
        }

        navigation[next.getSlot()] = next;
        navigation[previous.getSlot()] = previous;
        navigation[close.getSlot()] = close;
    }

    /**
     * Get the navigation item for the specified slot
     * @param slot The slot for the navigation item, must be 0 - 8
     * @return The navigation item
     */
    public NavigationItem get(int slot) {
        checkSlot(slot);
        return navigation[slot];
    }

    /**
     * Set a navigation item for the specified slot
     * @param slot The slot for the new navigation item, must be 0 - 8
     * @param navigationItem The new navigation item
     */
    public void set(int slot, NavigationItem navigationItem) {
        checkSlot(slot);
        Preconditions.checkArgument(navigation[slot] == null || navigation[slot].getNavigationType() == NavigationType.CUSTOM,
                "Can only set custom navigation buttons");
        Preconditions.checkArgument(navigationItem == null || navigationItem.getNavigationType() == NavigationType.CUSTOM,
                "Cannot change non-custom navigation items");

        navigation[slot] = navigationItem;
    }

    NavigationItem getNavigationItem(NavigationType type) {
        for (NavigationItem navigationItem : navigation) {
            if (navigationItem != null && navigationItem.getNavigationType() == type)
                return navigationItem;
        }

        return null;
    }

    private void checkSlot(int slot) {
        Preconditions.checkArgument(slot >= 0 && slot <= 8, "slot must be 0 - 8");
    }
}
