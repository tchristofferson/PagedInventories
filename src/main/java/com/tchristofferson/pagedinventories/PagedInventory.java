package com.tchristofferson.pagedinventories;

import com.google.common.base.Preconditions;
import com.tchristofferson.pagedinventories.handlers.PagedInventoryCloseHandler;
import com.tchristofferson.pagedinventories.handlers.PagedInventorySwitchPageHandler;
import com.tchristofferson.pagedinventories.utils.InventoryUtil;
import org.bukkit.Bukkit;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.*;

public class PagedInventory implements Iterable<Inventory> {

    private static final int MIN_INV_SIZE = 18;

    private final InventoryRegistrar registrar;
    private final List<Inventory> pages;
    private final Map<NavigationType, ItemStack> navigation;

    PagedInventory(InventoryRegistrar registrar, ItemStack nextButton, ItemStack previousButton, ItemStack closeButton) {
        this.registrar = registrar;
        pages = new ArrayList<>();

        navigation = new HashMap<>(3);
        navigation.put(NavigationType.NEXT, nextButton);
        navigation.put(NavigationType.PREVIOUS, previousButton);
        navigation.put(NavigationType.CLOSE, closeButton);
    }

    /**
     * Opens the next inventory of this paged inventory
     * @param player The player opening the inventory
     * @param currentlyOpen The inventory currently open
     * @return {@code true} if successful, {@code false} otherwise
     */
    public boolean openNext(Player player, Inventory currentlyOpen) {
        int index = pages.indexOf(currentlyOpen);
        if (index == -1 || pages.size() - 1 == index)
            return false;

        registrar.registerSwitch(player);
        boolean success = open(player, index + 1);

        if (success) {
            PagedInventorySwitchPageHandler.Handler handler = new PagedInventorySwitchPageHandler.Handler(
                    this, player.getOpenInventory(), player, PagedInventorySwitchPageHandler.PageAction.NEXT, index);
            registrar.getSwitchHandlers().forEach(pagedInventorySwitchPageHandler -> pagedInventorySwitchPageHandler.handle(handler));
        }

        return success;
    }

    /**
     * Opens the previous inventory of this paged inventory
     * @param player The player opening the inventory
     * @param currentlyOpen The inventory currently open
     * @return {@code true} if successful, {@code false} otherwise
     */
    public boolean openPrevious(Player player, Inventory currentlyOpen) {
        int index = pages.indexOf(currentlyOpen);
        if (index <= 0)
            return false;

        registrar.registerSwitch(player);
        boolean success = open(player, index - 1);

        if (success) {
            PagedInventorySwitchPageHandler.Handler handler = new PagedInventorySwitchPageHandler.Handler(
                    this, player.getOpenInventory(), player, PagedInventorySwitchPageHandler.PageAction.PREVIOUS, index);
            registrar.getSwitchHandlers().forEach(pagedInventorySwitchPageHandler -> pagedInventorySwitchPageHandler.handle(handler));
        }

        return success;
    }

    /**
     * Opens the first page of this paged inventory
     * @param player The player opening the paged inventory
     * @return {@code true} if successful, {@code false} otherwise
     */
    public boolean open(Player player) {
        if (pages.isEmpty())
            return false;
        return open(player, 0);
    }

    /**
     * Opens the page at the given index of this paged inventory, index starts at 0
     * @param player The player opening the inventory
     * @param index The index of the inventory to be opened
     * @return {@code true} if successful, {@code false} otherwise
     */
    public boolean open(Player player, int index) {
        if (pages.size() - 1 < index || index < 0)
            return false;
        Inventory inventory = pages.get(index);
        registrar.register(player, this, inventory);
        player.openInventory(inventory);
        return true;
    }

    /**
     * Determine if this paged inventory contains the given inventory
     * @param inventory The inventory to check for
     * @return {@code true} if this paged inventory contains this inventory, {@code false} otherwise
     */
    public boolean contains(Inventory inventory) {
        return pages.contains(inventory);
    }

    /**
     * Add a new inventory to the end of this paged inventory
     * @param contents The content to add to the inventory. The key is the slot and the value is the item stack
     * @param title The title for the inventory
     * @param size The size of the inventory
     */
    public void addPage(Map<Integer, ItemStack> contents, String title, final int size) {
        Preconditions.checkArgument(size >= MIN_INV_SIZE, "Inventory size must be >= " + MIN_INV_SIZE);
        Inventory inventory = Bukkit.createInventory(null, size, title);

        //Adding items to inventory
        contents.forEach((slot, itemStack) -> {
            if (InventoryUtil.isValidSlot(slot, size))
                inventory.setItem(slot, itemStack);
        });

        addPage(inventory, true);
    }

    /**
     * Add an inventory to the end of this paged inventory
     * @param inventory The inventory to add
     */
    public void addPage(Inventory inventory) {
        addPage(inventory, false);
    }

    private void addPage(Inventory inventory, boolean skipContentCheck) {
        Preconditions.checkArgument(inventory.getSize() >= MIN_INV_SIZE, "Inventory size must be >= " + MIN_INV_SIZE);
        Preconditions.checkState(!pages.contains(inventory), "Cannot add duplicate inventory");

        if (!skipContentCheck && inventory.getContents().length != 0) {
            for (int i = inventory.getSize() - 9; i < inventory.getSize(); i++) {
                ItemStack itemStack = inventory.getItem(i);
                if (itemStack != null && !itemStack.getType().name().endsWith("AIR"))
                    itemStack.setAmount(0);
            }
        }

        if (!pages.isEmpty()) {
            inventory.setItem(InventoryUtil.getNavigationSlot(NavigationType.PREVIOUS, inventory.getSize()), navigation.get(NavigationType.PREVIOUS));
            Inventory secondToLast = pages.get(pages.size() - 1);
            secondToLast.setItem(InventoryUtil.getNavigationSlot(NavigationType.NEXT, secondToLast.getSize()), navigation.get(NavigationType.NEXT));
        }

        inventory.setItem(InventoryUtil.getNavigationSlot(NavigationType.CLOSE, inventory.getSize()), navigation.get(NavigationType.CLOSE));
        pages.add(inventory);
    }

    /**
     * Remove the page at the given index of this paged inventory, index starts at 0
     * @param index The index at which the inventory you want to remove is located
     * @return The inventory removed
     */
    public Inventory removePage(int index) {
        Preconditions.checkArgument(index >= 0 && index <= pages.size() - 1);
        if (pages.size() - 1 == index && pages.size() > 1) {//Is last page
            Inventory inv = pages.get(pages.size() - 2);
            inv.getItem(InventoryUtil.getNavigationSlot(NavigationType.NEXT, inv.getSize())).setAmount(0);
            disperseViewers(pages.get(pages.size() - 1).getViewers(), pages.size() - 2);
        } else if (index == 0 && pages.size() > 1) {//Is first page
            Inventory inv = pages.get(1);
            inv.getItem(InventoryUtil.getNavigationSlot(NavigationType.PREVIOUS, inv.getSize())).setAmount(0);
            disperseViewers(pages.get(0).getViewers(), 1);
        } else if (pages.size() > 1) {//Is between first and last page
            Inventory inv = pages.get(index);
            disperseViewers(inv.getViewers(), index + 1);
        } else {//Is only page
            Inventory inv = pages.get(0);
            pages.remove(0);
           disperseViewers(inv.getViewers(), null);

            return inv;
        }

        return pages.remove(index);
    }

    /**
     * Remove an inventory from this paged inventory
     * @param inventory The inventory to remove
     * @return The inventory removed or {@code null} if the inventory isn't in this paged inventory
     */
    public Inventory removePage(Inventory inventory) {
        int index = pages.indexOf(inventory);
        return removePage(index);
    }

    /**
     * Get the navigation buttons of this paged inventory
     * @return A Map of the navigation where the key is the {@link NavigationType} and the value is the item stack representing the button
     */
    public Map<NavigationType, ItemStack> getNavigation() {
        return new HashMap<>(navigation);
    }

    /**
     * Update a navigation button of this paged inventory
     * @param navigationType The type of button
     * @param newButton The item stack to replace the specified button
     */
    public void updateNavigation(NavigationType navigationType, ItemStack newButton) {
        if (navigationType == null)
            throw new NullPointerException("NavigationType cannot be null");

        navigation.put(navigationType, newButton);

        if (pages.isEmpty())
            return;

        pages.forEach(inventory -> inventory.setItem(InventoryUtil.getNavigationSlot(navigationType, inventory.getSize()), newButton));
    }

    /**
     * Update the navigation buttons of this paged inventory
     * @param nextButton The new next button
     * @param previousButton The new previous button
     * @param closeButton The new close button
     */
    public void updateNavigation(ItemStack nextButton, ItemStack previousButton, ItemStack closeButton) {
        navigation.put(NavigationType.NEXT, nextButton);
        navigation.put(NavigationType.PREVIOUS, previousButton);
        navigation.put(NavigationType.CLOSE, closeButton);

        if (pages.isEmpty())
            return;

        pages.forEach(inventory -> {
            int invSize = inventory.getSize();
            int next = InventoryUtil.getNavigationSlot(NavigationType.NEXT, invSize);
            int previous = InventoryUtil.getNavigationSlot(NavigationType.PREVIOUS, invSize);
            int close = InventoryUtil.getNavigationSlot(NavigationType.CLOSE, invSize);
            inventory.setItem(next, nextButton);
            inventory.setItem(previous, previousButton);
            inventory.setItem(close, closeButton);
        });
    }

    private void disperseViewers(List<HumanEntity> viewers, Integer fallbackIndex) {
        viewers = new ArrayList<>(viewers);
        if (viewers.isEmpty())
            return;

        if (fallbackIndex == null) {
            viewers.forEach(viewer -> {
                PagedInventoryCloseHandler.Handler handler = new PagedInventoryCloseHandler.Handler(this, viewer.getOpenInventory(), (Player) viewer);
                viewer.closeInventory();
                registrar.getCloseHandlers().forEach(pagedInventoryCloseHandler -> pagedInventoryCloseHandler.handle(handler));
            });

            return;
        }

        viewers.forEach(viewer -> open((Player) viewer, fallbackIndex));
    }

    @Override
    public Iterator<Inventory> iterator() {
        return new ArrayList<>(pages).iterator();
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this)
            return true;
        if (!(obj instanceof PagedInventory))
            return false;

        PagedInventory inv = (PagedInventory) obj;
        return registrar.equals(inv.registrar) && pages.equals(inv.pages) && navigation.equals(inv.navigation);
    }

    @Override
    public int hashCode() {
        return Objects.hash(registrar, pages, navigation);
    }

}
