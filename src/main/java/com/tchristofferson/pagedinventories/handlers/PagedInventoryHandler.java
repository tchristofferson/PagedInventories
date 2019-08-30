package com.tchristofferson.pagedinventories.handlers;

import com.tchristofferson.pagedinventories.IPagedInventory;
import org.bukkit.entity.Player;
import org.bukkit.inventory.InventoryView;

public abstract class PagedInventoryHandler {

    PagedInventoryHandler() {
    }

    public static class Handler {

        private final IPagedInventory pagedInventory;
        private final InventoryView inventoryView;
        private final Player player;

        Handler(IPagedInventory pagedInventory, InventoryView inventoryView, Player player) {
            this.pagedInventory = pagedInventory;
            this.inventoryView = inventoryView;
            this.player = player;
        }

        public IPagedInventory getPagedInventory() {
            return pagedInventory;
        }

        public InventoryView getInventoryView() {
            return inventoryView;
        }

        public Player getPlayer() {
            return player;
        }

        @Override
        public boolean equals(Object obj) {
            if (obj == this)
                return true;
            if (!(obj instanceof Handler))
                return false;

            Handler handler = (Handler) obj;
            return pagedInventory.equals(handler.pagedInventory)
                    && inventoryView.equals(handler.inventoryView)
                    && player.getUniqueId().equals(handler.player.getUniqueId());
        }

    }

    @Override
    public boolean equals(Object obj) {
        return obj == this;
    }

}
