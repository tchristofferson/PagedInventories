package com.tchristofferson.pagedinventories;

import com.google.common.base.Preconditions;
import com.tchristofferson.pagedinventories.handlers.PagedInventorySwitchPageHandler;
import com.tchristofferson.pagedinventories.utils.InventoryUtil;
import org.bukkit.Bukkit;
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

    public boolean openNext(Player player, Inventory currentlyOpen) {
        if (!pages.contains(currentlyOpen))
            return false;
        int index = pages.indexOf(currentlyOpen);
        if (pages.size() - 1 == index)
            return false;
        boolean success = open(player, index + 1);

        if (success) {
            PagedInventorySwitchPageHandler.Handler handler = new PagedInventorySwitchPageHandler.Handler(
                    this, player.getOpenInventory(), player, PagedInventorySwitchPageHandler.PageAction.NEXT, index);
            registrar.getSwitchHandlers().forEach(pagedInventorySwitchPageHandler -> pagedInventorySwitchPageHandler.handle(handler));
        }

        return success;
    }

    public boolean openPrevious(Player player, Inventory currentlyOpen) {
        if (!pages.contains(currentlyOpen))
            return false;
        int index = pages.indexOf(currentlyOpen);
        if (index == 0)
            return false;
        boolean success = open(player, index - 1);

        if (success) {
            PagedInventorySwitchPageHandler.Handler handler = new PagedInventorySwitchPageHandler.Handler(
                    this, player.getOpenInventory(), player, PagedInventorySwitchPageHandler.PageAction.PREVIOUS, index);
            registrar.getSwitchHandlers().forEach(pagedInventorySwitchPageHandler -> pagedInventorySwitchPageHandler.handle(handler));
        }

        return success;
    }

    public boolean open(Player player) {
        if (pages.isEmpty())
            return false;
        return open(player, 0);
    }

    public boolean open(Player player, int index) {
        if (pages.size() - 1 < index || index < 0)
            return false;
        Inventory inventory = pages.get(index);
        registrar.register(player, this, inventory);
        player.openInventory(inventory);
        return true;
    }

    public boolean contains(Inventory inventory) {
        return pages.contains(inventory);
    }

    public void addPage(Map<Integer, ItemStack> contents, String title, final int size) {
        Preconditions.checkArgument(size >= MIN_INV_SIZE, "Inventory size must be >= " + MIN_INV_SIZE);
        Inventory inventory = Bukkit.createInventory(null, size, title);

        for (int i = 0; i < size - 9; i++) {
            ItemStack itemStack = contents.get(i);
            if (itemStack != null && !InventoryUtil.isValidSlot(i, size))
                inventory.setItem(i, itemStack);
        }

    }

    public void addPage(Inventory inventory) {
        Preconditions.checkArgument(inventory.getSize() >= MIN_INV_SIZE);
        for (int i = inventory.getSize() - 9; i < inventory.getSize(); i++) {
            ItemStack itemStack = inventory.getItem(i);
            if (itemStack != null && !itemStack.getType().name().endsWith("AIR"))
                itemStack.setAmount(0);
        }

        if (!pages.isEmpty()) {
            inventory.setItem(InventoryUtil.getNavigationSlot(NavigationType.PREVIOUS, inventory.getSize()), navigation.get(NavigationType.PREVIOUS));
            Inventory secondToLast = pages.get(pages.size() - 1);
            secondToLast.setItem(InventoryUtil.getNavigationSlot(NavigationType.NEXT, secondToLast.getSize()), navigation.get(NavigationType.NEXT));
        }

        inventory.setItem(InventoryUtil.getNavigationSlot(NavigationType.CLOSE, inventory.getSize()), navigation.get(NavigationType.CLOSE));
    }

    public Inventory removePage(int index) {
        Preconditions.checkArgument(index >= 0);
        if (pages.size() - 1 == index && pages.size() >= 2) {
            Inventory inv = pages.get(pages.size() - 2);
            inv.getItem(InventoryUtil.getNavigationSlot(NavigationType.NEXT, inv.getSize())).setAmount(0);
        }

        return pages.remove(index);
    }

    public boolean removePage(Inventory inventory) {
        int index = pages.indexOf(inventory);
        if (index < 0)
            return false;
        removePage(index);
        return true;
    }

    public Map<NavigationType, ItemStack> getNavigation() {
        return new HashMap<>(navigation);
    }

    public void updateNavigation(NavigationType navigationType, ItemStack newButton) {
        if (navigationType == null)
            throw new NullPointerException("NavigationType cannot be null");

        navigation.put(navigationType, newButton);

        if (pages.isEmpty())
            return;

        pages.forEach(inventory -> inventory.setItem(InventoryUtil.getNavigationSlot(navigationType, inventory.getSize()), newButton));
    }

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
