package com.tchristofferson.pagedinventories.handlers;

import com.tchristofferson.pagedinventories.IPagedInventory;
import org.bukkit.entity.Player;
import org.bukkit.inventory.InventoryView;

import java.util.Objects;

public abstract class PagedInventorySwitchPageHandler extends PagedInventoryHandler {

    public abstract void handle(PagedInventorySwitchPageHandler.Handler handler);

    public static class Handler extends PagedInventoryHandler.Handler {

        private final PageAction pageAction;
        private final int indexFrom;

        public Handler(IPagedInventory pagedInventory, InventoryView inventoryView, Player player, PageAction pageAction, int indexFrom) {
            super(pagedInventory, inventoryView, player);
            this.pageAction = pageAction;
            this.indexFrom = indexFrom;
        }

        @Override
        public boolean equals(Object obj) {
            if (obj == this)
                return true;
            if (!obj.getClass().equals(Handler.class))
                return false;

            Handler handler = (Handler) obj;
            return super.equals(handler)
                    && pageAction == handler.pageAction
                    && indexFrom == handler.indexFrom;
        }

        @Override
        public int hashCode() {
            return Objects.hash(getPagedInventory(), getInventoryView(), getPlayer().getUniqueId(), pageAction, indexFrom);
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
