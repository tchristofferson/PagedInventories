package com.tchristofferson.pagedinventories;

import org.bukkit.Bukkit;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;

public class PagedInventoryAPI {

    private final InventoryRegistrar registrar;

    public PagedInventoryAPI(Plugin plugin) {
        this.registrar = new InventoryRegistrar();
        Bukkit.getPluginManager().registerEvents(new PagedInventoryListener(plugin, registrar), plugin);
    }

    /**
     * Creates a new {@link PagedInventory}
     * @param nextButton The next button
     * @param previousButton The previous button
     * @param closeButton The close button
     * @return The newly created {@link PagedInventory}
     */
    public PagedInventory createPagedInventory(ItemStack nextButton, ItemStack previousButton, ItemStack closeButton) {
        return new PagedInventory(registrar, nextButton, previousButton, closeButton);
    }

    /**
     * Gets the inventory registrar for this API instance
     * @return The {@link InventoryRegistrar}
     */
    public InventoryRegistrar getRegistrar() {
        return registrar;
    }

}
