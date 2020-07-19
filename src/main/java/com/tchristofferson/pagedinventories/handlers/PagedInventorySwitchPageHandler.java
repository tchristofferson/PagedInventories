package com.tchristofferson.pagedinventories.handlers;

import com.tchristofferson.pagedinventories.IPagedInventory;
import org.bukkit.entity.Player;
import org.bukkit.inventory.InventoryView;

public abstract class PagedInventorySwitchPageHandler extends PagedInventoryHandler {

    public abstract void handle(SwitchHandler switchHandler);

    public static class SwitchHandler extends PagedInventoryHandler.Handler {

        private final PageAction pageAction;
        private final int indexFrom;

        public SwitchHandler(IPagedInventory iPagedInventory, InventoryView inventoryView, Player player, PageAction pageAction, int indexFrom) {
            super(iPagedInventory, inventoryView, player);
            this.pageAction = pageAction;
            this.indexFrom = indexFrom;
        }

        @Override
        public boolean equals(Object obj) {
            if (obj == this)
                return true;
            if (!(obj instanceof SwitchHandler))
                return false;

            SwitchHandler switchHandler = (SwitchHandler) obj;
            return super.equals(switchHandler)
                    && pageAction == switchHandler.pageAction
                    && indexFrom == switchHandler.indexFrom;
        }

        public PageAction getPageAction() {
            return pageAction;
        }

        public int getIndexFrom() {
            return indexFrom;
        }

        public int getIndexTo() {
            return pageAction == PageAction.NEXT ? indexFrom + 1 : indexFrom - 1;
        }

    }

    public enum PageAction {
        NEXT,
        PREVIOUS
    }

}
