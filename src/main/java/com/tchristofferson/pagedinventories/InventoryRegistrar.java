package com.tchristofferson.pagedinventories;

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

    InventoryRegistrar() {
        registrar = new HashMap<>();
        pagedInventoryRegistrar = new HashMap<>();
        switchingPages = new ArrayList<>();
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
