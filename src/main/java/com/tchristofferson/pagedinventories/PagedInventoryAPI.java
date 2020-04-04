package com.tchristofferson.pagedinventories;

import com.tchristofferson.pagedinventories.navigationitems.NavigationItem;
import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;

import java.util.Map;

public class PagedInventoryAPI {

    private final InventoryRegistrar registrar;

    public PagedInventoryAPI(Plugin plugin) {
        this.registrar = new InventoryRegistrar();
        Bukkit.getPluginManager().registerEvents(new PagedInventoryListener(plugin, registrar), plugin);
    }

    public IPagedInventory createPagedInventory(NavigationRow navigationRow) {
        return new PagedInventory(registrar, navigationRow);
    }

    /**
     * Create a new {@link IPagedInventory}
     * @param navigation The navigation for the paged inventory. Must contain next, previous, and close buttons
     * @return The newly created {@link IPagedInventory}
     * @deprecated use {@link PagedInventoryAPI#createPagedInventory(NavigationRow)}
     */
    @Deprecated
    public IPagedInventory createPagedInventory(Map<Integer, NavigationItem> navigation) {
        return new PagedInventory(registrar, navigation);
    }

    /**
     * Gets the inventory registrar for this API instance
     * @return The {@link InventoryRegistrar}
     */
    public InventoryRegistrar getRegistrar() {
        return registrar;
    }

}
