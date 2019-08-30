package com.tchristofferson.pagedinventories;

import com.tchristofferson.pagedinventories.handlers.*;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;

import java.util.*;

/**
 * This is a registrar for open PagedInventories
 */
public class InventoryRegistrar {

    private final Map<UUID, Inventory> registrar;
    private final Map<UUID, IPagedInventory> pagedInventoryRegistrar;
    private final List<UUID> switchingPages;

    private final Map<IPagedInventory, List<PagedInventoryClickHandler>> clickHandlers;
    private final Map<IPagedInventory, List<PagedInventoryCloseHandler>> closeHandlers;
    private final Map<IPagedInventory, List<PagedInventorySwitchPageHandler>> switchHandlers;

    InventoryRegistrar() {
        registrar = new HashMap<>();
        pagedInventoryRegistrar = new HashMap<>();
        switchingPages = new ArrayList<>();

        clickHandlers = new HashMap<>();
        closeHandlers = new HashMap<>();
        switchHandlers = new HashMap<>();
    }

    /**
     * This will add a new click handler for the specified IPagedInventory, {@link PagedInventoryClickHandler}
     * @param iPagedInventory The IPagedInventory you want the handler to work with
     * @param pagedInventoryClickHandler The handler
     */
    public void addClickHandler(IPagedInventory iPagedInventory, PagedInventoryClickHandler pagedInventoryClickHandler) {
        List<PagedInventoryClickHandler> handlers = clickHandlers.get(iPagedInventory);

        if (handlers == null) {
            handlers = new ArrayList<>();
            handlers.add(pagedInventoryClickHandler);
            clickHandlers.put(iPagedInventory, handlers);
            return;
        }

        handlers.add(pagedInventoryClickHandler);
    }

    void callClickHandlers(PagedInventoryClickHandler.Handler handler) {
        List<PagedInventoryClickHandler> handlers = clickHandlers.get(handler.getPagedInventory());

        if (handlers == null || clickHandlers.isEmpty())
            return;

        handlers.forEach(pagedInventoryClickHandler -> pagedInventoryClickHandler.handle(handler));
    }

    /**
     * This will add a new close handler for the specified IPagedInventory, {@link PagedInventoryCloseHandler}
     * @param iPagedInventory The IPagedInventory you want the handler to work with
     * @param pagedInventoryCloseHandler The handler
     */
    public void addCloseHandler(IPagedInventory iPagedInventory, PagedInventoryCloseHandler pagedInventoryCloseHandler) {
        List<PagedInventoryCloseHandler> handlers = closeHandlers.get(iPagedInventory);

        if (handlers == null) {
            handlers = new ArrayList<>();
            handlers.add(pagedInventoryCloseHandler);
            closeHandlers.put(iPagedInventory, handlers);
            return;
        }

        handlers.add(pagedInventoryCloseHandler);
    }

    void callCloseHandlers(PagedInventoryCloseHandler.Handler handler) {
        List<PagedInventoryCloseHandler> handlers = closeHandlers.get(handler.getPagedInventory());

        if (handlers == null || handlers.isEmpty())
            return;

        handlers.forEach(pagedInventoryCloseHandler -> pagedInventoryCloseHandler.handle(handler));
    }

    /**
     * This will add a new switch handler for the specified IPagedInventory, {@link PagedInventorySwitchPageHandler}
     * @param iPagedInventory The IPagedInventory you want the handler to work with
     * @param pagedInventorySwitchPageHandler The handler
     */
    public void addSwitchHandler(IPagedInventory iPagedInventory, PagedInventorySwitchPageHandler pagedInventorySwitchPageHandler) {
        List<PagedInventorySwitchPageHandler> handlers = switchHandlers.get(iPagedInventory);

        if (handlers == null) {
            handlers = new ArrayList<>();
            handlers.add(pagedInventorySwitchPageHandler);
            switchHandlers.put(iPagedInventory, handlers);
            return;
        }

        handlers.add(pagedInventorySwitchPageHandler);
    }

    void callSwitchHandlers(PagedInventorySwitchPageHandler.Handler handler) {
        List<PagedInventorySwitchPageHandler> handlers = switchHandlers.get(handler.getPagedInventory());

        if (handlers == null || handlers.isEmpty())
            return;

        handlers.forEach(pagedInventorySwitchPageHandler -> pagedInventorySwitchPageHandler.handle(handler));
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
     * Call this method when opening a {@link IPagedInventory}
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

    @Override
    public boolean equals(Object obj) {
        if (obj == this)
            return true;
        if (!obj.getClass().equals(InventoryRegistrar.class))
            return false;

        InventoryRegistrar inventoryRegistrar = (InventoryRegistrar) obj;
        return registrar.equals(inventoryRegistrar.registrar)
                && pagedInventoryRegistrar.equals(inventoryRegistrar.pagedInventoryRegistrar)
                && clickHandlers.equals(inventoryRegistrar.clickHandlers)
                && closeHandlers.equals(inventoryRegistrar.closeHandlers)
                && switchHandlers.equals(inventoryRegistrar.switchHandlers);
    }

}
