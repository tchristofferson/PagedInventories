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

    public PagedInventory createPagedInventory(ItemStack nextButton, ItemStack previousButton, ItemStack closeButton) {
        return new PagedInventory(registrar, nextButton, previousButton, closeButton);
    }

    public InventoryRegistrar getRegistrar() {
        return registrar;
    }

}
