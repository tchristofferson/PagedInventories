package com.tchristofferson.pagedinventories.handlers;

import com.tchristofferson.pagedinventories.IPagedInventory;
import org.bukkit.event.inventory.InventoryClickEvent;

public abstract class PagedInventoryGlobalClickHandler extends PagedInventoryClickHandler {

    public static class Handler extends PagedInventoryClickHandler.Handler {

        private final IPagedInventory iPagedInventory;

        public Handler(IPagedInventory iPagedInventory, InventoryClickEvent event) {
            super(event);
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
