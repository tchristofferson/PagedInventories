package com.tchristofferson.pagedinventories.handlers;

import com.tchristofferson.pagedinventories.IPagedInventory;
import org.bukkit.event.inventory.InventoryClickEvent;

public class PagedInventoryCustomNavigationHandler extends PagedInventoryClickHandler.Handler {

    public PagedInventoryCustomNavigationHandler(IPagedInventory iPagedInventory, InventoryClickEvent event) {
        super(iPagedInventory, event);
    }

}
