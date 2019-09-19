package com.tchristofferson.pagedinventories.handlers;

import com.tchristofferson.pagedinventories.IPagedInventory;
import org.bukkit.entity.Player;
import org.bukkit.inventory.InventoryView;

public abstract class PagedInventoryGlobalCloseHandler extends PagedInventoryCloseHandler {

    public static class Handler extends PagedInventoryCloseHandler.Handler {

        private final IPagedInventory iPagedInventory;

        public Handler(IPagedInventory iPagedInventory, InventoryView inventoryView, Player player) {
            super(inventoryView, player);
            this.iPagedInventory = iPagedInventory;
        }

        public IPagedInventory getPagedInventory() {
            return iPagedInventory;
        }

        @Override
        public boolean equals(Object obj) {
            return super.equals(obj)
                    && obj.getClass().equals(Handler.class)
                    && iPagedInventory.equals(((Handler) obj).iPagedInventory);
        }
    }

}
