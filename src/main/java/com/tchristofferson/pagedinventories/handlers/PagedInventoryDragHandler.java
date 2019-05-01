package com.tchristofferson.pagedinventories.handlers;

import com.tchristofferson.pagedinventories.PagedInventory;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryDragEvent;

import java.util.Objects;

public abstract class PagedInventoryDragHandler extends PagedInventoryHandler {

    public abstract void handle(PagedInventoryDragHandler.Handler handler);

    public static class Handler extends PagedInventoryHandler.Handler {

        private final InventoryDragEvent event;

        public Handler(PagedInventory pagedInventory, InventoryDragEvent event) {
            super(pagedInventory, event.getView(), (Player) event.getWhoClicked());
            this.event = event;
        }

        @Override
        public boolean equals(Object obj) {
            if (obj == this)
                return true;
            if (!(obj instanceof Handler))
                return false;

            Handler handler = (Handler) obj;
            return super.equals(handler) && event.equals(handler.event);
        }

        @Override
        public int hashCode() {
            return Objects.hash(getPagedInventory(), getInventoryView(), getPlayer().getUniqueId(), event);
        }

        public InventoryDragEvent getEvent() {
            return event;
        }

    }

}
