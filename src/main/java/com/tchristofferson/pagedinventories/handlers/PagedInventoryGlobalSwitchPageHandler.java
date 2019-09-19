package com.tchristofferson.pagedinventories.handlers;

import com.tchristofferson.pagedinventories.IPagedInventory;
import org.bukkit.entity.Player;
import org.bukkit.inventory.InventoryView;

public abstract class PagedInventoryGlobalSwitchPageHandler extends PagedInventorySwitchPageHandler {

    public static class Handler extends PagedInventorySwitchPageHandler.Handler {

        private final IPagedInventory iPagedInventory;

        public Handler(IPagedInventory iPagedInventory, InventoryView inventoryView, Player player, PageAction pageAction, int indexFrom) {
            super(inventoryView, player, pageAction, indexFrom);
            this.iPagedInventory = iPagedInventory;
        }

        public IPagedInventory getiPagedInventory() {
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
