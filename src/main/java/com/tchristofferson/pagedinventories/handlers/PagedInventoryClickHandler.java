package com.tchristofferson.pagedinventories.handlers;

import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;

public abstract class PagedInventoryClickHandler extends PagedInventoryHandler {

    public abstract void handle(PagedInventoryClickHandler.Handler handler);

    public static class Handler extends PagedInventoryHandler.Handler {

        private final InventoryClickEvent event;

        public Handler(InventoryClickEvent event) {
            super(event.getView(), (Player) event.getWhoClicked());
            this.event = event;
        }

        @Override
        public boolean equals(Object obj) {
            if (obj == this)
                return true;
            if (!(obj instanceof Handler))
                return false;

            Handler handler = (Handler) obj;
            return super.equals(obj) && event.equals(handler.event);
        }

        public InventoryClickEvent getEvent() {
            return event;
        }

    }

}
