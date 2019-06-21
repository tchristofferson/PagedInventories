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

    private final List<PagedInventoryClickHandler> clickHandlers;
    private final List<PagedInventoryCloseHandler> closeHandlers;
    private final List<PagedInventorySwitchPageHandler> switchHandlers;

    InventoryRegistrar() {
        registrar = new HashMap<>();
        pagedInventoryRegistrar = new HashMap<>();
        switchingPages = new ArrayList<>();

        clickHandlers = new ArrayList<>();
        closeHandlers = new ArrayList<>();
        switchHandlers = new ArrayList<>();
    }

    /**
     * This will add a new click handler, {@link PagedInventoryClickHandler}
     * @param pagedInventoryClickHandler The handler
     */
    public void addClickHandler(PagedInventoryClickHandler pagedInventoryClickHandler) {
        clickHandlers.add(pagedInventoryClickHandler);
    }

    List<PagedInventoryClickHandler> getClickHandlers() {
        return new ArrayList<>(clickHandlers);
    }

    /**
     * This will add a new close handler, {@link PagedInventoryCloseHandler}
     * @param pagedInventoryCloseHandler The handler
     */
    public void addCloseHandler(PagedInventoryCloseHandler pagedInventoryCloseHandler) {
        closeHandlers.add(pagedInventoryCloseHandler);
    }

    List<PagedInventoryCloseHandler> getCloseHandlers() {
        return new ArrayList<>(closeHandlers);
    }

    /**
     * This will add a new switch handler, {@link PagedInventorySwitchPageHandler}
     * @param pagedInventorySwitchPageHandler The handler
     */
    public void addSwitchHandler(PagedInventorySwitchPageHandler pagedInventorySwitchPageHandler) {
        switchHandlers.add(pagedInventorySwitchPageHandler);
    }

    List<PagedInventorySwitchPageHandler> getSwitchHandlers() {
        return new ArrayList<>(switchHandlers);
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
        if (!(obj instanceof InventoryRegistrar))
            return false;

        InventoryRegistrar inventoryRegistrar = (InventoryRegistrar) obj;
        return registrar.equals(inventoryRegistrar.registrar)
                && pagedInventoryRegistrar.equals(inventoryRegistrar.pagedInventoryRegistrar)
                && clickHandlers.equals(inventoryRegistrar.clickHandlers)
                && closeHandlers.equals(inventoryRegistrar.closeHandlers)
                && switchHandlers.equals(inventoryRegistrar.switchHandlers);
    }

    @Override
    public int hashCode() {
        return Objects.hash(registrar, pagedInventoryRegistrar, clickHandlers, closeHandlers, switchHandlers);
    }

}
