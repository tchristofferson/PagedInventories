package com.tchristofferson.pagedinventories;

import com.tchristofferson.pagedinventories.handlers.*;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;

import java.util.*;

public class InventoryRegistrar {

    private final Map<UUID, Inventory> registrar;
    private final Map<UUID, PagedInventory> pagedInventoryRegistrar;

    private final List<PagedInventoryClickHandler> clickHandlers;
    private final List<PagedInventoryCloseHandler> closeHandlers;
    private final List<PagedInventoryDragHandler> dragHandlers;
    private final List<PagedInventorySwitchPageHandler> switchHandlers;

    InventoryRegistrar() {
        registrar = new HashMap<>();
        pagedInventoryRegistrar = new HashMap<>();

        clickHandlers = new ArrayList<>();
        closeHandlers = new ArrayList<>();
        dragHandlers = new ArrayList<>();
        switchHandlers = new ArrayList<>();
    }

    public void addClickHandler(PagedInventoryClickHandler pagedInventoryClickHandler) {
        clickHandlers.add(pagedInventoryClickHandler);
    }

    public void addCloseHandler(PagedInventoryCloseHandler pagedInventoryCloseHandler) {
        closeHandlers.add(pagedInventoryCloseHandler);
    }

    public void addDragHandler(PagedInventoryDragHandler pagedInventoryDragHandler) {
        dragHandlers.add(pagedInventoryDragHandler);
    }

    public void addSwitchHandler(PagedInventorySwitchPageHandler pagedInventorySwitchPageHandler) {
        switchHandlers.add(pagedInventorySwitchPageHandler);
    }

    List<PagedInventorySwitchPageHandler> getSwitchHandlers() {
        return new ArrayList<>(switchHandlers);
    }

    void register(Player player, PagedInventory pagedInventory, Inventory inventory) {
        registrar.put(player.getUniqueId(), inventory);
        pagedInventoryRegistrar.putIfAbsent(player.getUniqueId(), pagedInventory);
    }

    void unregister(Player player) {
        registrar.remove(player.getUniqueId());
        pagedInventoryRegistrar.remove(player.getUniqueId());
    }

    public Map<UUID, Inventory> getOpenInventories() {
        return new HashMap<>(registrar);
    }

    public Map<UUID, PagedInventory> getOpenPagedInventories() {
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
                && dragHandlers.equals(inventoryRegistrar.dragHandlers)
                && switchHandlers.equals(inventoryRegistrar.switchHandlers);
    }

    @Override
    public int hashCode() {
        return Objects.hash(registrar, pagedInventoryRegistrar, clickHandlers, closeHandlers, dragHandlers, switchHandlers);
    }

}
