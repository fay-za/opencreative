/*
 * OpenCreative+, Minecraft plugin.
 * (C) 2022-2026, McChicken Studio, mcchickenstudio@gmail.com
 *
 * OpenCreative+ is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * OpenCreative+ is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <https://www.gnu.org/licenses/>.
 */

package ua.mcchickenstudio.opencreative.menus;

import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

import static ua.mcchickenstudio.opencreative.utils.ItemUtils.itemEquals;

/**
 * <h1>AbstractListMenu</h1>
 * This class represents a AbstractMenu that has scrollable list
 * of elements. It creates pages and arrows to change current page.
 *
 * @see ua.mcchickenstudio.opencreative.menus.AbstractMenu
 */
public abstract class AbstractListMenu<T> extends AbstractMenu {

    protected final List<T> elements = new ArrayList<>();
    private final Player player;
    private final int[] charmsBarSlots;
    private final int[] decorationSlots;
    private final int[] elementsSlots;
    private int currentPage = 1;

    public AbstractListMenu(Player player, String title, PlacementLayout layout) {
        this(player, title, layout.getElementsSlots(), layout.getCharmsBarSlots(), layout.getDecorationSlots());
    }

    public AbstractListMenu(Player player, String title,
                            int[] elementsSlots, int[] charmsBarSlots,
                            int[] decorationSlots) {
        super(6, title);
        this.player = player;
        this.elementsSlots = elementsSlots;
        this.charmsBarSlots = charmsBarSlots;
        this.decorationSlots = decorationSlots;
    }

    public int getCurrentPage() {
        return currentPage;
    }

    public void setCurrentPage(int currentPage) {
        this.currentPage = currentPage;
    }

    protected abstract ItemStack getElementIcon(T element);

    protected abstract void fillOtherItems();

    protected abstract void onCharmsBarClick(InventoryClickEvent event);

    protected abstract void onElementClick(InventoryClickEvent event);

    protected abstract List<T> getElements();

    protected void fillDecorationItems() {
        for (int slot : decorationSlots) {
            setItem(slot, DECORATION_PANE_ITEM);
        }
    }

    protected void fillElements(int page) {
        fillEmpty();
        if (!elements.isEmpty()) {
            List<T> content = getElementsFromPage(page);
            int slot = 0;
            for (T object : content) {
                setItem(elementsSlots[slot], getElementIcon(object));
                slot++;
            }
        }
    }

    protected void fillEmpty() {
        for (int slot : elementsSlots) {
            setItem(slot, AIR_ITEM);
        }
    }

    protected List<T> getElementsFromPage(int page) {
        if (page < 1 || page > getPages()) {
            page = 1;
        }
        int fromIndex = (page - 1) * elementsSlots.length;
        int toIndex = Math.min(elements.size(), (page) * elementsSlots.length);
        return elements.subList(fromIndex, toIndex);
    }

    public final int getPages() {
        return (elements.size() + elementsSlots.length - 1) / elementsSlots.length;
    }

    @Override
    public void fillItems(Player player) {
        elements.addAll(getElements());
        fillDecorationItems();
        fillElements(getCurrentPage());
        fillOtherItems();
    }

    @Override
    public void onClick(@NotNull InventoryClickEvent event) {
        if (!isPlayerClicked(event) || !isClickedInMenuSlots(event)) {
            event.setCancelled(true);
            return;
        }
        if (isElementClicked(event.getSlot()) && isNotEmpty(event.getCurrentItem())) {
            onElementClick(event);
        } else if (itemEquals(event.getCurrentItem(), DECORATION_ITEM)) {
            event.setCancelled(true);
        } else if (isCharmsBarClicked(event.getSlot()) && isNotEmpty(event.getCurrentItem()) && !event.getCurrentItem().equals(DECORATION_ITEM)) {
            onCharmsBarClick(event);
        } else {
            event.setCancelled(true);
        }
    }

    protected void nextPage() {
        setCurrentPage(getNextPage());
        fillElements(getCurrentPage());
    }

    protected void previousPage() {
        setCurrentPage(getPreviousPage());
        fillElements(getCurrentPage());
    }

    protected Player getPlayer() {
        return player;
    }

    protected int getPreviousPage() {
        int previousPage = (getCurrentPage() - 1);
        int maxPagesAmount = getPages();
        if (previousPage > maxPagesAmount || previousPage < 1) {
            previousPage = 1;
        }
        return previousPage;
    }

    protected int getNextPage() {
        int nextPage = getCurrentPage() + 1;
        int maxPagesAmount = getPages();
        if (nextPage > maxPagesAmount || nextPage < 1) {
            nextPage = 1;
        }
        return nextPage;
    }

    private boolean isCharmsBarClicked(int clickedSlot) {
        for (int itemSlot : charmsBarSlots) {
            if (itemSlot == clickedSlot) {
                return true;
            }
        }
        return false;
    }

    private boolean isElementClicked(int clickedSlot) {
        for (int itemSlot : elementsSlots) {
            if (itemSlot == clickedSlot) {
                return true;
            }
        }
        return false;
    }

    protected int[] getElementsSlots() {
        return elementsSlots;
    }

    protected int[] getCharmsBarSlots() {
        return charmsBarSlots;
    }

    protected int[] getDecorationSlots() {
        return decorationSlots;
    }

    public enum PlacementLayout {
        LEFT_CHARMS_BAR(
                new int[]{12, 13, 14, 15, 16, 21, 22, 23, 24, 25, 30, 31, 32, 33, 34, 39, 40, 41, 42, 43},
                new int[]{0, 9, 18, 27, 36, 45},
                new int[]{1, 10, 19, 28, 37, 46}),
        BOTTOM_CHARMS_BAR(
                new int[]{10, 11, 12, 13, 14, 15, 16, 19, 20, 21, 22, 23, 24, 25, 28, 29, 30, 31, 32, 33, 34},
                new int[]{45, 46, 47, 48, 49, 50, 51, 52, 53},
                new int[]{36, 37, 38, 39, 40, 41, 42, 43, 44});

        private final int[] elementsSlots;
        private final int[] charmsBarSlots;
        private final int[] decorationSlots;

        PlacementLayout(int[] elementsSlots, int[] charmsBarSlots, int[] decorationSlots) {
            this.elementsSlots = elementsSlots;
            this.charmsBarSlots = charmsBarSlots;
            this.decorationSlots = decorationSlots;
        }

        public int[] getCharmsBarSlots() {
            return charmsBarSlots;
        }

        public int[] getElementsSlots() {
            return elementsSlots;
        }

        public int[] getDecorationSlots() {
            return decorationSlots;
        }
    }
}
