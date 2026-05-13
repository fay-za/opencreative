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
import ua.mcchickenstudio.opencreative.settings.Sounds;

import java.util.List;
import java.util.function.Predicate;

import static ua.mcchickenstudio.opencreative.utils.ItemUtils.itemEquals;

/**
 * <h1>ListBrowserMenu</h1>
 * This class represents a menu that stores
 * list of elements and has arrows to change pages.
 *
 * @param <T> type of elements, that will be stored in list.
 * @see ua.mcchickenstudio.opencreative.menus.AbstractListMenu
 */
public abstract class ListBrowserMenu<T> extends AbstractListMenu<T> {

    private final int previousPageButtonSlot;
    private final int nextPageButtonSlot;
    private final int noElementsPageButtonSlot;

    public ListBrowserMenu(Player player, String title) {
        this(player, title, PlacementLayout.LEFT_CHARMS_BAR);
    }

    public ListBrowserMenu(Player player, String title, PlacementLayout layout) {
        this(player, title, layout.getElementsSlots(), layout.getCharmsBarSlots(), layout.getDecorationSlots(),
                layout.getNoElementsSlot(), layout.getPreviousPageSlot(), layout.getNextPageSlot());
    }

    public ListBrowserMenu(Player player, String title, PlacementLayout layout, int[] charmsBarSlots, int[] decorationSlots) {
        this(player, title, layout.getElementsSlots(), charmsBarSlots, decorationSlots,
                layout.getNoElementsSlot(), layout.getPreviousPageSlot(), layout.getNextPageSlot());
    }

    public ListBrowserMenu(Player player, String title,
                           int[] elementsSlots, int[] charmsBarSlots,
                           int[] decorationSlots, int noElementsPageButtonSlot,
                           int previousPageButtonSlot, int nextPageButtonSlot) {
        super(player, title, elementsSlots, charmsBarSlots, decorationSlots);
        this.noElementsPageButtonSlot = noElementsPageButtonSlot;
        this.previousPageButtonSlot = previousPageButtonSlot;
        this.nextPageButtonSlot = nextPageButtonSlot;
    }

    protected abstract ItemStack getElementIcon(T element);

    protected abstract void fillOtherItems();

    protected abstract void onCharmsBarClick(InventoryClickEvent event);

    protected abstract void onElementClick(InventoryClickEvent event);

    public abstract List<T> getElements();

    protected abstract ItemStack getNextPageButton();

    protected abstract ItemStack getPreviousPageButton();

    protected abstract ItemStack getNoElementsButton();

    protected ItemStack getNextPageEmptyButton() {
        return ItemStack.empty();
    }

    protected ItemStack getPreviousPageEmptyButton() {
        return ItemStack.empty();
    }

    protected void fillArrowsItems(int currentPage) {
        if (elements.isEmpty()) {
            setItem(noElementsPageButtonSlot, getNoElementsButton());
        } else {
            int maxPagesAmount = getPages();
            if (currentPage > maxPagesAmount || currentPage < 1) {
                currentPage = 1;
            }
            if (currentPage > 1) {
                setItem(previousPageButtonSlot, getPreviousPageButton());
            }
            if (currentPage < maxPagesAmount) {
                setItem(nextPageButtonSlot, getNextPageButton());
            }
        }
    }

    @Override
    protected void fillElements(int page) {
        fillEmpty();
        if (elements.isEmpty()) {
            setItem(noElementsPageButtonSlot, getNoElementsButton());
        } else {
            List<T> content = getElementsFromPage(page);
            int slot = 0;
            for (T object : content) {
                setItem(getElementsSlots()[slot], getElementIcon(object));
                slot++;
            }
        }
    }

    @Override
    protected void fillEmpty() {
        for (int slot : getElementsSlots()) {
            setItem(slot, null);
        }
        setItem(nextPageButtonSlot, getNextPageEmptyButton());
        setItem(previousPageButtonSlot, getPreviousPageEmptyButton());
    }

    @Override
    public void fillItems(Player player) {
        elements.addAll(getElements());
        fillDecorationItems();
        fillElements(getCurrentPage());
        fillArrowsItems(getCurrentPage());
        fillOtherItems();
    }

    @Override
    public void onClick(@NotNull InventoryClickEvent event) {
        if (!isPlayerClicked(event) || !isClickedInMenuSlots(event)) {
            event.setCancelled(true);
            return;
        }
        if (isElementClicked(event.getSlot()) && isNotEmpty(event.getCurrentItem()) && !itemEquals(event.getCurrentItem(), getNoElementsButton())) {
            onElementClick(event);
        } else if (itemEquals(event.getCurrentItem(), DECORATION_ITEM) || itemEquals(event.getCurrentItem(), DECORATION_PANE_ITEM)) {
            event.setCancelled(true);
        } else if (itemEquals(event.getCurrentItem(), getNextPageButton())) {
            Sounds.MENU_NEXT_PAGE.play(event.getWhoClicked());
            nextPage();
            event.setCancelled(true);
        } else if (itemEquals(event.getCurrentItem(), getPreviousPageButton())) {
            Sounds.MENU_PREVIOUS_PAGE.play(event.getWhoClicked());
            previousPage();
            event.setCancelled(true);
        } else if (isCharmsBarClicked(event.getSlot()) && isNotEmpty(event.getCurrentItem()) && !event.getCurrentItem().equals(DECORATION_ITEM)) {
            onCharmsBarClick(event);
        } else {
            event.setCancelled(true);
        }
    }

    public ItemStack getElementIcon(int index) {
        if (index < 0 || index >= elements.size()) {
            return AIR_ITEM.clone();
        }
        return getElementIcon(elements.get(index));
    }

    public void updateElements() {
        elements.clear();
        elements.addAll(getElements());
    }

    public List<T> getCurrentElements() {
        return elements;
    }

    protected void nextPage() {
        setCurrentPage(getNextPage());
        fillElements(getCurrentPage());
        fillArrowsItems(getCurrentPage());
    }

    protected void previousPage() {
        setCurrentPage(getPreviousPage());
        fillElements(getCurrentPage());
        fillArrowsItems(getCurrentPage());
    }

    private boolean isCharmsBarClicked(int clickedSlot) {
        for (int itemSlot : getCharmsBarSlots()) {
            if (itemSlot == clickedSlot) {
                return true;
            }
        }
        return false;
    }

    private boolean isElementClicked(int clickedSlot) {
        for (int itemSlot : getElementsSlots()) {
            if (itemSlot == clickedSlot) {
                return true;
            }
        }
        return false;
    }

    protected List<T> filterList(List<T> list, Predicate<T> predicate) {
        return list.stream().filter(predicate).toList();
    }

    protected int getNextPageButtonSlot() {
        return nextPageButtonSlot;
    }

    protected int getPreviousPageButtonSlot() {
        return previousPageButtonSlot;
    }

    protected int getNoElementsPageButtonSlot() {
        return noElementsPageButtonSlot;
    }

    public enum PlacementLayout {
        LEFT_CHARMS_BAR(
                new int[]{12, 13, 14, 15, 16, 21, 22, 23, 24, 25, 30, 31, 32, 33, 34, 39, 40, 41, 42, 43},
                new int[]{0, 9, 18, 27, 36, 45},
                new int[]{1, 10, 19, 28, 37, 46},
                23, 47, 53),
        BOTTOM_CHARMS_BAR(
                new int[]{10, 11, 12, 13, 14, 15, 16, 19, 20, 21, 22, 23, 24, 25, 28, 29, 30, 31, 32, 33, 34},
                new int[]{45, 46, 47, 48, 49, 50, 51, 52, 53},
                new int[]{36, 37, 38, 39, 40, 41, 42, 43, 44},
                13, 36, 44),
        BOTTOM_NO_DECORATION(
                new int[]{10, 11, 12, 13, 14, 15, 16, 19, 20, 21, 22, 23, 24, 25, 28, 29, 30, 31, 32, 33, 34},
                new int[]{45, 46, 47, 48, 49, 50, 51, 52, 53},
                new int[]{}, 13, 45, 53),
        LOCATION_CHOOSER(
                new int[]{10, 11, 12, 13, 14, 15, 16, 19, 20, 21, 22, 23, 24, 25, 28, 29, 30, 31, 32, 33, 34, 37, 38, 39, 40, 41, 42, 43},
                new int[]{}, new int[]{0, 1, 2, 3, 4, 5, 6, 7, 8, 45, 46, 47, 48, 49, 50, 51, 52, 53}, 13, 45, 53),
        VALUE_CHOOSER(
                new int[]{10, 11, 12, 13, 14, 15, 16, 19, 20, 21, 22, 23, 24, 25, 28, 29, 30, 31, 32, 33, 34, 37, 38, 39, 40, 41, 42, 43},
                new int[]{}, new int[]{}, 13, 45, 53);

        private final int[] elementsSlots;
        private final int[] charmsBarSlots;
        private final int[] decorationSlots;

        private final int noElementsSlot;
        private final int previousPageSlot;
        private final int nextPageSlot;

        PlacementLayout(int[] elementsSlots, int[] charmsBarSlots, int[] decorationSlots, int noElementsSlot, int previousPageSlot, int nextPageSlot) {
            this.elementsSlots = elementsSlots;
            this.charmsBarSlots = charmsBarSlots;
            this.decorationSlots = decorationSlots;
            this.noElementsSlot = noElementsSlot;
            this.previousPageSlot = previousPageSlot;
            this.nextPageSlot = nextPageSlot;
        }

        private int[] getCharmsBarSlots() {
            return charmsBarSlots;
        }

        private int[] getElementsSlots() {
            return elementsSlots;
        }

        private int[] getDecorationSlots() {
            return decorationSlots;
        }

        private int getNextPageSlot() {
            return nextPageSlot;
        }

        private int getPreviousPageSlot() {
            return previousPageSlot;
        }

        private int getNoElementsSlot() {
            return noElementsSlot;
        }
    }
}
