package com.tchristofferson.pagedinventories;

import com.tchristofferson.pagedinventories.handlers.PagedInventoryClickHandler;
import com.tchristofferson.pagedinventories.handlers.PagedInventoryCloseHandler;
import com.tchristofferson.pagedinventories.handlers.PagedInventorySwitchPageHandler;
import com.tchristofferson.pagedinventories.navigationitems.NavigationItem;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.Map;
import java.util.UUID;

public interface IPagedInventory {

    int MIN_INV_SIZE = 18;

    /**
     * Add a click handler
     * @param handler The handler
     */
    void addHandler(PagedInventoryClickHandler handler);

    /**
     * Call the click handlers. This method is called automatically when a player clicks a paged inventory
     * @param clickHandler The handler
     */
    void callClickHandlers(PagedInventoryClickHandler.ClickHandler clickHandler);

    /**
     * Clears all click handlers
     * To clear all handlers see {@link IPagedInventory#clearAllHandlers()}
     */
    void clearClickHandlers();

    /**
     * Add a close handler
     * @param handler The handler
     */
    void addHandler(PagedInventoryCloseHandler handler);

    /**
     * Call the close handlers. This method is called automatically when a player clicks a paged inventory
     * @param closeHandler The handler
     */
    void callCloseHandlers(PagedInventoryCloseHandler.CloseHandler closeHandler);

    /**
     * Clears all close handlers
     * To clear all handlers see {@link IPagedInventory#clearAllHandlers()}
     */
    void clearCloseHandlers();

    /**
     * Add a switch page handler
     * @param handler The handler
     */
    void addHandler(PagedInventorySwitchPageHandler handler);

    /**
     * Call the switch page handlers. This method is called automatically when a player clicks a paged inventory
     * @param switchHandler The handler
     */
    void callSwitchHandlers(PagedInventorySwitchPageHandler.SwitchHandler switchHandler);

    /**
     * Clears all switch page handlers
     * To clear all handlers see {@link IPagedInventory#clearAllHandlers()}
     */
    void clearSwitchHandlers();

    /**
     * Clears all handlers (click, close, switch)
     */
    void clearAllHandlers();

    /**
     * Get a page modifier for the page at the specified index
     * @param index The page modifier for the page at this index
     * @return The page modifier for the page at the specified index
     */
    PageModifier getPageModifier(int index);

    /**
     * Opens the first page of this paged inventory
     * @param player The player opening the paged inventory
     * @return {@code true} if successful, {@code false} otherwise
     */
    boolean open(Player player);

    /**
     * Opens the page at the given index of this paged inventory, index starts at 0
     * @param player The player opening the inventory
     * @param index The index of the inventory to be opened
     * @return {@code true} if successful, {@code false} otherwise
     */
    boolean open(Player player, int index);

    /**
     * Opens the next inventory of this paged inventory
     * @param player The player opening the inventory
     * @param currentlyOpen The inventory currently open
     * @return {@code true} if successful, {@code false} otherwise
     */
    boolean openNext(Player player, Inventory currentlyOpen);

    /**
     * Opens the previous inventory of this paged inventory
     * @param player The player opening the inventory
     * @param currentlyOpen The inventory currently open
     * @return {@code true} if successful, {@code false} otherwise
     */
    boolean openPrevious(Player player, Inventory currentlyOpen);

    /**
     * Determine if this paged inventory contains the given inventory
     * @param inventory The inventory to check for
     * @return {@code true} if this paged inventory contains this inventory, {@code false} otherwise
     */
    default boolean contains(Inventory inventory) {
        return indexOf(inventory) != -1;
    }

    /**
     * Get the index of the specified page
     * @param inventory The inventory to get the index of
     * @return The index, or -1 if it is not found
     */
    int indexOf(Inventory inventory);

    /**
     * Add an inventory to the end of this paged inventory
     * @param inventory The inventory to add
     */
    void addPage(Inventory inventory);

    /**
     * Add a new inventory to the end of this paged inventory
     * @param contents The content to add to the inventory. The key is the slot and the value is the item stack
     * @param title The title for the inventory
     * @param size The size of the inventory
     */
    void addPage(Map<Integer, ItemStack> contents, String title, int size);

    /**
     * Remove an inventory from this paged inventory
     * @param inventory The inventory to remove
     * @return {@code true} if the inventory was successfully removed, {@code false} otherwise
     */
    boolean removePage(Inventory inventory);

    /**
     * Remove the page at the given index of this paged inventory, index starts at 0
     * @param index The index at which the inventory you want to remove is located
     * @return The inventory removed
     */
    Inventory removePage(int index);

    /**
     * Get the navigation buttons of this paged inventory
     * @return A Map of the navigation where the key is the {@link NavigationType} and the value is the item stack representing the button
     * @deprecated use {@link IPagedInventory#getNavigationRow()}
     */
    @Deprecated
    Map<Integer, NavigationItem> getNavigation();

    /**
     * Get the navigation row
     * @return The navigation row
     */
    NavigationRow getNavigationRow();

    /**
     * Get the navigation item at the specified slot
     * @param slot The slot with the navigation item. Use a slot from 0 to 8
     * @return The {@link NavigationItem} at the specified slot in the navigation row
     */
    NavigationItem getNavigationItem(int slot);

    /**
     * Get the slot of the specified navigation item
     * @param navigationItem The {@link NavigationItem} to get the slot of
     * @return The slot, from 0 to 8, in the navigation row, or -1 if it wasn't found
     */
    int getNavigationItem(NavigationItem navigationItem);

    /**
     * Update a navigation button of this paged inventory
     * @param slot The slot in the bottom bar of the page (0-8)
     * @param navigationItem The item to replace the item in the specified slot
     * @deprecated use {@link IPagedInventory#getNavigationRow()} and {@link NavigationRow#set(int, NavigationItem)}
     */
    @Deprecated
    void setNavigation(Integer slot, NavigationItem navigationItem);

    /**
     * Get the UUIDs of the players viewing one of the pages in the paged inventory.
     * The key is the player's UUID, the value is the index of the page they are viewing.
     * @return a map of player UUIDs and the index of the page they are viewing
     */
    Map<UUID, Integer> getViewers();

    /**
     * Get how many inventory pages there are
     * @return How many inventory pages there are
     */
    int getSize();

}
