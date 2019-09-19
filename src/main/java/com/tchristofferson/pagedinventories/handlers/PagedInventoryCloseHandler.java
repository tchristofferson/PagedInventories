package com.tchristofferson.pagedinventories.handlers;

import org.bukkit.entity.Player;
import org.bukkit.inventory.InventoryView;

public abstract class PagedInventoryCloseHandler extends PagedInventoryHandler {

    public abstract void handle(PagedInventoryCloseHandler.Handler handler);

    public static class Handler extends PagedInventoryHandler.Handler {

        public Handler(InventoryView inventoryView, Player player) {
            super(inventoryView, player);
        }

    }

}
