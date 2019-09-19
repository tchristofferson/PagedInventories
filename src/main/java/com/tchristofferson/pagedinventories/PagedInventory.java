package com.tchristofferson.pagedinventories;

import com.google.common.base.Preconditions;
import com.tchristofferson.pagedinventories.handlers.PagedInventoryClickHandler;
import com.tchristofferson.pagedinventories.handlers.PagedInventoryCloseHandler;
import com.tchristofferson.pagedinventories.handlers.PagedInventorySwitchPageHandler;
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
    private final Map<NavigationType, ItemStack> navigation;

    private final List<PagedInventoryClickHandler> clickHandlers;
    private final List<PagedInventoryCloseHandler> closeHandlers;
    private final List<PagedInventorySwitchPageHandler> switchHandlers;

    protected PagedInventory(InventoryRegistrar registrar, ItemStack nextButton, ItemStack previousButton, ItemStack closeButton) {
        this.registrar = registrar;
        pages = new ArrayList<>();

        navigation = new HashMap<>(3);
        navigation.put(NavigationType.NEXT, nextButton);
        navigation.put(NavigationType.PREVIOUS, previousButton);
        navigation.put(NavigationType.CLOSE, closeButton);

        clickHandlers = new ArrayList<>();
        closeHandlers = new ArrayList<>();
        switchHandlers = new ArrayList<>();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void addClickHandler(PagedInventoryClickHandler handler) {
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
    public void addCloseHandler(PagedInventoryCloseHandler handler) {
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
    public void addSwitchHandler(PagedInventorySwitchPageHandler handler) {
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
                    player.getOpenInventory(), player, PagedInventorySwitchPageHandler.PageAction.NEXT, index);
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
                    player.getOpenInventory(), player, PagedInventorySwitchPageHandler.PageAction.PREVIOUS, index);
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

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean contains(Inventory inventory) {
        return pages.contains(inventory);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Inventory getPage(int page) {
        return pages.get(page);
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
    public Map<NavigationType, ItemStack> getNavigation() {
        return new HashMap<>(navigation);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void updateNavigation(NavigationType navigationType, ItemStack newButton) {
        if (navigationType == null)
            throw new NullPointerException("NavigationType cannot be null");

        navigation.put(navigationType, newButton);

        if (pages.isEmpty())
            return;

        pages.forEach(inventory -> inventory.setItem(InventoryUtil.getNavigationSlot(navigationType, inventory.getSize()), newButton));
    }

    /**
     * {@inheritDoc}
     */
    @Override
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
                PagedInventoryCloseHandler.Handler handler = new PagedInventoryCloseHandler.Handler(viewer.getOpenInventory(), (Player) viewer);
                viewer.closeInventory();
                callCloseHandlers(handler);
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
        if (!obj.getClass().equals(PagedInventory.class))
            return false;

        PagedInventory inv = (PagedInventory) obj;
        return registrar.equals(inv.registrar) && pages.equals(inv.pages) && navigation.equals(inv.navigation);
    }

}
