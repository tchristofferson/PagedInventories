package com.tchristofferson.pagedinventories.handlers;

import com.tchristofferson.pagedinventories.IPagedInventory;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.ItemStack;

public abstract class PagedInventoryClickHandler extends PagedInventoryHandler {

    public abstract void handle(PagedInventoryClickHandler.Handler handler);

    public static class Handler extends PagedInventoryHandler.Handler {

        private final InventoryClickEvent event;

        public Handler(IPagedInventory iPagedInventory, InventoryClickEvent event) {
            super(iPagedInventory, event.getView(), (Player) event.getWhoClicked());
            this.event = event;
        }

        public InventoryAction getAction() {
            return event.getAction();
        }

        public ClickType getClick() {
            return event.getClick();
        }

        public ItemStack getCurrentItem() {
            return event.getCurrentItem();
        }

        public ItemStack getCursor() {
            return event.getCursor();
        }

        public int getHotbarButton() {
            return event.getHotbarButton();
        }

        public int getRawSlot() {
            return event.getRawSlot();
        }

        public int getSlot() {
            return event.getSlot();
        }

        public InventoryType.SlotType getSlotType() {
            return event.getSlotType();
        }

        public boolean isLeftClick() {
            return event.isLeftClick();
        }

        public boolean isRightClick() {
            return event.isRightClick();
        }

        public boolean isShiftClick() {
            return event.isShiftClick();
        }

        public void setCurrentItem(ItemStack itemStack) {
            event.setCurrentItem(itemStack);
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
    }

}
