package com.tchristofferson.pagedinventories.handlers;

import com.tchristofferson.pagedinventories.IPagedInventory;
import org.bukkit.entity.Player;
import org.bukkit.inventory.InventoryView;

public abstract class PagedInventoryCloseHandler extends PagedInventoryHandler {

    public abstract void handle(PagedInventoryCloseHandler.Handler handler);

    public static class Handler extends PagedInventoryHandler.Handler {

        public Handler(IPagedInventory pagedInventory, InventoryView inventoryView, Player player) {
            super(pagedInventory, inventoryView, player);
        }

    }

}
