package com.tchristofferson.pagedinventories;

import com.tchristofferson.pagedinventories.handlers.*;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;

import java.util.*;

/**
 * This is a registrar for open PagedInventories
 */
public class InventoryRegistrar {

    private final Map<UUID, Inventory> registrar;//Player's with an IPagedInventory open and the specified page
    private final Map<UUID, IPagedInventory> pagedInventoryRegistrar;//Player's with an IPagedInventory open
    private final List<UUID> switchingPages;//Players that are currently switching pages

    private final List<PagedInventoryClickHandler> globalClickHandlers;
    private final List<PagedInventoryCloseHandler> globalCloseHandlers;
    private final List<PagedInventorySwitchPageHandler> globalSwitchHandlers;

    InventoryRegistrar() {
        registrar = new HashMap<>();
        pagedInventoryRegistrar = new HashMap<>();
        switchingPages = new ArrayList<>();

        globalClickHandlers = new ArrayList<>(3);
        globalCloseHandlers = new ArrayList<>(3);
        globalSwitchHandlers = new ArrayList<>(3);
    }

    /**'
     * Registers that a player is switching between pages in a {@link IPagedInventory}
     * @param player The player switching between pages
     */
    public void registerSwitch(Player player) {
        switchingPages.add(player.getUniqueId());
    }

    boolean unregisterSwitch(Player player) {
        return switchingPages.remove(player.getUniqueId());
    }

    /**
     * Register that a player has an {@link IPagedInventory} open
     * Automatically called when using {@link IPagedInventory#open(Player)} or {@link IPagedInventory#open(Player, int)}
     * Will unregister when the player closes the IPagedInventory
     * @param player The player that has opened the {@link IPagedInventory}
     * @param pagedInventory The {@link IPagedInventory} the player is opening / has opened
     * @param inventory The page opened by the player
     */
    public void register(Player player, IPagedInventory pagedInventory, Inventory inventory) {
        UUID uuid = player.getUniqueId();
        registrar.put(uuid, inventory);
        pagedInventoryRegistrar.putIfAbsent(uuid, pagedInventory);
    }

    void unregister(Player player) {
        registrar.remove(player.getUniqueId());
        pagedInventoryRegistrar.remove(player.getUniqueId());
    }

    /**
     * This will get all the inventories that are open AND are a part of a {@link IPagedInventory}
     * @return a {@link Map} with the player's uuid as the key and the open inventory as the value
     */
    public Map<UUID, Inventory> getOpenInventories() {
        return new HashMap<>(registrar);
    }

    /**
     * This will get all the paged inventories that are open
     * @return a {@link Map} with the player's uuid as the key and the open {@link IPagedInventory} as the value
     */
    public Map<UUID, IPagedInventory> getOpenPagedInventories() {
        return new HashMap<>(pagedInventoryRegistrar);
    }

    /**
     * Add a global click handler
     * Whenever any paged inventory gets clicked the specified {@link PagedInventoryClickHandler#handle(PagedInventoryClickHandler.ClickHandler)} will be called
     * The global handler is called before the paged inventory specific handler(s)
     * @param handler The handler
     */
    public void addGlobalHandler(PagedInventoryClickHandler handler) {
        globalClickHandlers.add(handler);
    }

    /**
     * Add a global close handler
     * Whenever any paged inventory gets closed the specified {@link PagedInventoryCloseHandler#handle(PagedInventoryCloseHandler.CloseHandler)} will be called
     * The global handler is called before the paged inventory specific handler(s)
     * @param handler The handler
     */
    public void addGlobalHandler(PagedInventoryCloseHandler handler) {
        globalCloseHandlers.add(handler);
    }

    /**
     * Add a global switch page handler
     * Whenever any player switches pages the specified {@link PagedInventorySwitchPageHandler#handle(PagedInventorySwitchPageHandler.SwitchHandler)} will be called
     * The global handler is called before the paged inventory specific handler(s)
     * @param handler The handler
     */
    public void addGlobalHandler(PagedInventorySwitchPageHandler handler) {
        globalSwitchHandlers.add(handler);
    }

    void callGlobalClickHandlers(PagedInventoryClickHandler.ClickHandler clickHandler) {
        globalClickHandlers.forEach(pagedInventoryGlobalClickHandler -> pagedInventoryGlobalClickHandler.handle(clickHandler));
    }

    /**
     * Clear global click handlers
     * To clear all global handlers see {@link InventoryRegistrar#clearAllGlobalHandlers()}
     */
    public void clearGlobalClickHandlers() {
        globalClickHandlers.clear();
    }

    void callGlobalCloseHandlers(PagedInventoryCloseHandler.CloseHandler closeHandler) {
        globalCloseHandlers.forEach(pagedInventoryGlobalHandler -> pagedInventoryGlobalHandler.handle(closeHandler));
    }

    /**
     * Clear global close handlers
     * To clear all global handlers see {@link InventoryRegistrar#clearAllGlobalHandlers()}
     */
    public void clearGlobalCloseHandlers() {
        globalCloseHandlers.clear();
    }

    void callGlobalSwitchHandlers(PagedInventorySwitchPageHandler.SwitchHandler switchHandler) {
        globalSwitchHandlers.forEach(pagedInventoryGlobalSwitchHandler -> pagedInventoryGlobalSwitchHandler.handle(switchHandler));
    }

    /**
     * Clear global switch page handlers
     * To clear all global handlers see {@link InventoryRegistrar#clearAllGlobalHandlers()}
     */
    public void clearGlobalSwitchHandlers() {
        globalSwitchHandlers.clear();
    }

    public void clearAllGlobalHandlers() {
        clearGlobalClickHandlers();
        clearGlobalCloseHandlers();
        clearGlobalSwitchHandlers();
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this)
            return true;
        if (!obj.getClass().equals(InventoryRegistrar.class))
            return false;

        InventoryRegistrar inventoryRegistrar = (InventoryRegistrar) obj;
        return registrar.equals(inventoryRegistrar.registrar)
                && pagedInventoryRegistrar.equals(inventoryRegistrar.pagedInventoryRegistrar)
                && switchingPages.equals(inventoryRegistrar.switchingPages);
    }

}
