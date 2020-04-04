package com.tchristofferson.pagedinventories;

import com.google.common.base.Preconditions;
import com.tchristofferson.pagedinventories.handlers.*;
import com.tchristofferson.pagedinventories.navigationitems.*;
import com.tchristofferson.pagedinventories.utils.InventoryUtil;
import org.bukkit.Bukkit;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.*;

public class PagedInventory implements IPagedInventory {

    private final InventoryRegistrar registrar;
    private final List<Inventory> pages;
    private final NavigationRow navigationRow;

    private final List<PagedInventoryClickHandler> clickHandlers;
    private final List<PagedInventoryCloseHandler> closeHandlers;
    private final List<PagedInventorySwitchPageHandler> switchHandlers;

    protected PagedInventory(InventoryRegistrar registrar, NavigationRow navigationRow) {
        this.registrar = registrar;
        this.pages = new ArrayList<>();
        this.clickHandlers = new ArrayList<>(3);
        this.closeHandlers = new ArrayList<>(3);
        this.switchHandlers = new ArrayList<>(3);
        this.navigationRow = navigationRow;
    }

    @Deprecated
    protected PagedInventory(InventoryRegistrar registrar, Map<Integer, NavigationItem> navigation) {
        this(registrar, getFromMap(navigation));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void addHandler(PagedInventoryClickHandler handler) {
        clickHandlers.add(handler);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void callClickHandlers(PagedInventoryClickHandler.Handler handler) {
        clickHandlers.forEach(pagedInventoryClickHandler -> pagedInventoryClickHandler.handle(handler));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void clearClickHandlers() {
        clickHandlers.clear();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void addHandler(PagedInventoryCloseHandler handler) {
        closeHandlers.add(handler);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void callCloseHandlers(PagedInventoryCloseHandler.Handler handler) {
        closeHandlers.forEach(pagedInventoryCloseHandler -> pagedInventoryCloseHandler.handle(handler));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void clearCloseHandlers() {
        closeHandlers.clear();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void addHandler(PagedInventorySwitchPageHandler handler) {
        switchHandlers.add(handler);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void callSwitchHandlers(PagedInventorySwitchPageHandler.Handler handler) {
        switchHandlers.forEach(pagedInventorySwitchPageHandler -> pagedInventorySwitchPageHandler.handle(handler));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void clearSwitchHandlers() {
        switchHandlers.clear();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void clearAllHandlers() {
        clearClickHandlers();
        clearCloseHandlers();
        clearSwitchHandlers();
    }

    @Override
    public PageModifier getPageModifier(int index) {
        return new PageModifier(pages.get(index));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean openNext(Player player, Inventory currentlyOpen) {
        int index = pages.indexOf(currentlyOpen);
        if (index == -1 || pages.size() - 1 == index)
            return false;

        registrar.registerSwitch(player);
        boolean success = open(player, index + 1);

        if (success) {
            PagedInventorySwitchPageHandler.Handler handler = new PagedInventorySwitchPageHandler.Handler(
                    this, player.getOpenInventory(), player, PagedInventorySwitchPageHandler.PageAction.NEXT, index);
            registrar.callGlobalSwitchHandlers(handler);
            callSwitchHandlers(handler);
        }

        return success;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean openPrevious(Player player, Inventory currentlyOpen) {
        int index = pages.indexOf(currentlyOpen);
        if (index <= 0)
            return false;

        registrar.registerSwitch(player);
        boolean success = open(player, index - 1);

        if (success) {
            PagedInventorySwitchPageHandler.Handler handler = new PagedInventorySwitchPageHandler.Handler(
                    this, player.getOpenInventory(), player, PagedInventorySwitchPageHandler.PageAction.PREVIOUS, index);
            registrar.callGlobalSwitchHandlers(handler);
            callSwitchHandlers(handler);
        }

        return success;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean open(Player player) {
        if (pages.isEmpty())
            return false;
        return open(player, 0);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean open(Player player, int index) {
        if (pages.size() - 1 < index || index < 0)
            return false;
        Inventory inventory = pages.get(index);
        player.openInventory(inventory);
        registrar.register(player, this, inventory);
        return true;
    }

    @Override
    public int indexOf(Inventory inventory) {
        return pages.indexOf(inventory);
    }

    /**
     * {@inheritDoc}
     */
    @Override
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
     * {@inheritDoc}
     */
    @Override
    public void addPage(Inventory inventory) {
        addPage(inventory, false);
    }

    private void addPage(Inventory inventory, boolean skipContentCheck) {
        Preconditions.checkArgument(inventory.getSize() >= MIN_INV_SIZE, "Inventory size must be >= " + MIN_INV_SIZE);
        Preconditions.checkState(!pages.contains(inventory), "Cannot add duplicate inventory");

        if (!skipContentCheck && inventory.getContents().length != 0) {
            for (int i = inventory.getSize() - 9; i < inventory.getSize(); i++) {
                ItemStack itemStack = inventory.getItem(i);
                if (itemStack != null)
                    itemStack.setAmount(0);
            }
        }

        if (!pages.isEmpty()) {
            NavigationItem previousItem = navigationRow.getNavigationItem(NavigationType.PREVIOUS);
            NavigationItem nextItem = navigationRow.getNavigationItem(NavigationType.NEXT);

            inventory.setItem(previousItem.getSlot() + inventory.getSize() - 9, previousItem.getItemStack());
            Inventory currentLast = pages.get(pages.size() - 1);
            currentLast.setItem(nextItem.getSlot() + inventory.getSize() - 9, nextItem.getItemStack());
        }

        NavigationItem closeItem = navigationRow.getNavigationItem(NavigationType.CLOSE);
        inventory.setItem(closeItem.getSlot() + inventory.getSize() - 9, closeItem.getItemStack());
        pages.add(inventory);
    }

    /**
     * {@inheritDoc}
     */
    @Override
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
     * {@inheritDoc}
     */
    @Override
    public boolean removePage(Inventory inventory) {
        int index = pages.indexOf(inventory);

        if (index == -1) {
            return false;
        }

        removePage(index);
        return true;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Map<Integer, NavigationItem> getNavigation() {
        Map<Integer, NavigationItem> nav = new HashMap<>(9);

        for (int i = 0; i < 9; i++) {
            NavigationItem navigationItem = navigationRow.get(i);

            if (navigationItem != null)
                nav.put(i, navigationItem);
        }

        return nav;
    }

    @Override
    public NavigationRow getNavigationRow() {
        return navigationRow;
    }

    @Override
    public NavigationItem getNavigationItem(int slot) {
        return navigationRow.get(slot);
    }

    @Override
    public int getNavigationItem(NavigationItem navigationItem) {
        for (int i = 0; i < 9; i++) {
            if (navigationItem.equals(navigationRow.get(i)))
                return i;
        }

        return -1;
    }


    /**
     * {@inheritDoc}
     */
    @Override
    public void setNavigation(Integer slot, NavigationItem navigationItem) {
        navigationRow.set(slot, navigationItem);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int getSize() {
        return pages.size();
    }

    private void disperseViewers(List<HumanEntity> viewers, Integer fallbackIndex) {
        viewers = new ArrayList<>(viewers);
        if (viewers.isEmpty())
            return;

        if (fallbackIndex == null) {
            viewers.forEach(viewer -> {
                PagedInventoryCloseHandler.Handler handler = new PagedInventoryCloseHandler.Handler(this, viewer.getOpenInventory(), ((Player) viewer));
                viewer.closeInventory();
                registrar.callGlobalCloseHandlers(handler);
                callCloseHandlers(handler);
            });

            return;
        }

        viewers.forEach(viewer -> open((Player) viewer, fallbackIndex));
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this)
            return true;
        if (!obj.getClass().equals(PagedInventory.class))
            return false;

        PagedInventory inv = (PagedInventory) obj;

        return registrar.equals(inv.registrar)
                && pages.equals(inv.pages)
                && navigationRow.equals(inv.navigationRow)
                && clickHandlers.equals(inv.clickHandlers)
                && closeHandlers.equals(inv.closeHandlers)
                && switchHandlers.equals(inv.switchHandlers);
    }

    private static NavigationRow getFromMap(Map<Integer, NavigationItem> navigation) {
        NextNavigationItem nextItem = null;
        PreviousNavigationItem previousItem = null;
        CloseNavigationItem closeItem = null;
        List<NavigationItem> navigationItems = new ArrayList<>(9);

        for (Map.Entry<Integer, NavigationItem> entry : navigation.entrySet()) {
            switch (entry.getValue().getNavigationType()) {
                case NEXT:
                    nextItem = (NextNavigationItem) entry.getValue();
                    break;
                case PREVIOUS:
                    previousItem = (PreviousNavigationItem) entry.getValue();
                    break;
                case CLOSE:
                    closeItem = (CloseNavigationItem) entry.getValue();
                default:
                    navigationItems.add(entry.getValue());
            }
        }

        return new NavigationRow(nextItem, previousItem, closeItem, navigationItems.toArray(new NavigationItem[0]));
    }
}
