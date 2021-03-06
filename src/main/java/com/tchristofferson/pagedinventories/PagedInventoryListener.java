package com.tchristofferson.pagedinventories;

import com.tchristofferson.pagedinventories.handlers.PagedInventoryClickHandler;
import com.tchristofferson.pagedinventories.handlers.PagedInventoryCloseHandler;
import com.tchristofferson.pagedinventories.handlers.PagedInventoryCustomNavigationHandler;
import com.tchristofferson.pagedinventories.navigationitems.CustomNavigationItem;
import com.tchristofferson.pagedinventories.navigationitems.NavigationItem;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;

class PagedInventoryListener implements Listener {

    private final Plugin plugin;
    private final InventoryRegistrar registrar;

    PagedInventoryListener(Plugin plugin, InventoryRegistrar registrar) {
        this.plugin = plugin;
        this.registrar = registrar;
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onInventoryClickEvent(InventoryClickEvent event) {
        Player player = (Player) event.getWhoClicked();
        Inventory inventory = event.getClickedInventory();
        if (inventory == null || !registrar.getOpenInventories().containsKey(player.getUniqueId()))
            return;

        event.setCancelled(true);
        IPagedInventory pagedInventory = registrar.getOpenPagedInventories().get(player.getUniqueId());
        ItemStack clicked = event.getCurrentItem();

        if (clicked != null) {
            NavigationItem navigationItem;

            if (event.getSlot() < inventory.getSize() - 9)
                navigationItem = null;
            else
                navigationItem = pagedInventory.getNavigationItem(event.getSlot() - (inventory.getSize() - 9));

            if (navigationItem != null) {
                //Handlers automatically called inside of paged inventory methods
                if (navigationItem.getNavigationType() == NavigationType.NEXT) {
                    Bukkit.getScheduler().runTask(plugin, () -> pagedInventory.openNext(player, inventory));
                } else if (navigationItem.getNavigationType() == NavigationType.PREVIOUS) {
                    Bukkit.getScheduler().runTask(plugin, () -> pagedInventory.openPrevious(player, inventory));
                } else if (navigationItem.getNavigationType() == NavigationType.CLOSE) {
                    Bukkit.getScheduler().runTask(plugin, player::closeInventory);
                } else {
                    CustomNavigationItem customNavigationItem = (CustomNavigationItem) navigationItem;
                    customNavigationItem.handleClick(new PagedInventoryCustomNavigationHandler(pagedInventory, event));
                }

                return;
            }
        }

        PagedInventoryClickHandler.ClickHandler globalClickHandler = new PagedInventoryClickHandler.ClickHandler(pagedInventory, event);
        registrar.callGlobalClickHandlers(globalClickHandler);
        pagedInventory.callClickHandlers(globalClickHandler);
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onInventoryCloseEvent(InventoryCloseEvent event) {
        Player player = (Player) event.getPlayer();
        if (!registrar.getOpenInventories().containsKey(player.getUniqueId()))
            return;

        if (!registrar.unregisterSwitch(player)) {
            IPagedInventory iPagedInventory = registrar.getOpenPagedInventories().get(player.getUniqueId());
            registrar.unregister(player);
            PagedInventoryCloseHandler.CloseHandler closeHandler = new PagedInventoryCloseHandler.CloseHandler(iPagedInventory, event.getView(), player);
            registrar.callGlobalCloseHandlers(closeHandler);
            iPagedInventory.callCloseHandlers(closeHandler);
        }
    }

}
