package com.tchristofferson.pagedinventories;

import com.tchristofferson.pagedinventories.handlers.PagedInventoryClickHandler;
import com.tchristofferson.pagedinventories.handlers.PagedInventoryCloseHandler;
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

import java.util.Map;

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
        if (!registrar.getOpenInventories().containsKey(player.getUniqueId()))
            return;

        event.setCancelled(true);
        IPagedInventory pagedInventory = registrar.getOpenPagedInventories().get(player.getUniqueId());
        ItemStack clicked = event.getCurrentItem();
        Map<NavigationType, ItemStack> navigation = pagedInventory.getNavigation();

        if (clicked != null && navigation.containsValue(clicked)) {
            Inventory inventory = event.getClickedInventory();

            if (navigation.get(NavigationType.NEXT).equals(clicked)) {
                Bukkit.getScheduler().runTask(plugin, () -> pagedInventory.openNext(player, inventory));
            } else if (navigation.get(NavigationType.PREVIOUS).equals(clicked)) {
                Bukkit.getScheduler().runTask(plugin, () -> pagedInventory.openPrevious(player, inventory));
            } else {
                Bukkit.getScheduler().runTask(plugin, player::closeInventory);
            }

            return;
        }

        PagedInventoryClickHandler.Handler globalHandler = new PagedInventoryClickHandler.Handler(pagedInventory, event);
        registrar.callGlobalClickHandlers(globalHandler);
        pagedInventory.callClickHandlers(globalHandler);
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onInventoryCloseEvent(InventoryCloseEvent event) {
        Player player = (Player) event.getPlayer();
        if (!registrar.getOpenInventories().containsKey(player.getUniqueId()))
            return;

        if (!registrar.unregisterSwitch(player)) {
            registrar.unregister(player);
            IPagedInventory iPagedInventory = registrar.getOpenPagedInventories().get(player.getUniqueId());
            PagedInventoryCloseHandler.Handler handler = new PagedInventoryCloseHandler.Handler(iPagedInventory, event.getView(), player);
            registrar.callGlobalCloseHandlers(handler);
            iPagedInventory.callCloseHandlers(handler);
        }
    }

}
