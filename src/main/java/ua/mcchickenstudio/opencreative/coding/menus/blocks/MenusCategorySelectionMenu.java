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

package ua.mcchickenstudio.opencreative.coding.menus.blocks;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import ua.mcchickenstudio.opencreative.OpenCreative;
import ua.mcchickenstudio.opencreative.coding.menus.MenusCategory;
import ua.mcchickenstudio.opencreative.menus.AbstractListMenu;
import ua.mcchickenstudio.opencreative.menus.AbstractMenu;
import ua.mcchickenstudio.opencreative.settings.Sounds;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import static ua.mcchickenstudio.opencreative.utils.ItemUtils.createItem;
import static ua.mcchickenstudio.opencreative.utils.ItemUtils.itemEquals;

/**
 * <h1>MenusCategorySelectionMenu</h1>
 * This class represents a menu for selecting menus category
 * and opening a content filtered by menus category selection menu.
 */
public abstract class MenusCategorySelectionMenu extends AbstractMenu {

    protected final Player player;
    protected final Material additionalPane;
    protected final ItemStack mainItem;
    protected final String mainCategory;
    protected final List<MenusCategory> menusCategories = new ArrayList<>();
    protected final Object frequency;
    protected final boolean legacy = OpenCreative.getSettings().getCodingSettings().isLegacySelectionMenu();
    protected ContentWithMenusCategoryMenu<?> contentMenu;

    public MenusCategorySelectionMenu(@NotNull Player player,
                                      @NotNull ItemStack mainItem,
                                      @NotNull Material additionalPane,
                                      @NotNull Collection<MenusCategory> menusCategories,
                                      @NotNull String title,
                                      @NotNull String mainCategory,
                                      @NotNull Object frequency
    ) {
        super(6, title);
        this.player = player;
        this.menusCategories.addAll(menusCategories);
        this.additionalPane = additionalPane;
        this.mainItem = mainItem;
        this.mainCategory = mainCategory;
        this.frequency = frequency;
    }

    public abstract @NotNull ContentWithMenusCategoryMenu<?> getContentBrowserMenu(Object frequency);

    @Override
    public void fillItems(Player player) {
        if (contentMenu == null) this.contentMenu = getContentBrowserMenu(frequency);
        contentMenu.setCategoriesMenu(this);
        if (legacy) {
            fillLegacy();
            return;
        }
        switch (menusCategories.size()) {
            case 0 -> {
                setRows(4);
                setItem(13, createItem(Material.BARRIER, 1, "items.developer.categories." + mainCategory + ".empty"));
            }
            case 1 -> {
                setRows(4);
                setItem(13, menusCategories.getFirst().getItem(mainCategory));
            }
            case 2 -> {
                setRows(4);
                setItem(11, menusCategories.getFirst().getItem(mainCategory));
                setItem(15, menusCategories.get(1).getItem(mainCategory));
            }
            case 3 -> {
                setRows(4);
                setItem(11, menusCategories.getFirst().getItem(mainCategory));
                setItem(13, menusCategories.get(1).getItem(mainCategory));
                setItem(15, menusCategories.get(2).getItem(mainCategory));
            }
            case 4 -> {
                setRows(4);
                setItem(10, menusCategories.getFirst().getItem(mainCategory));
                setItem(12, menusCategories.get(1).getItem(mainCategory));
                setItem(14, menusCategories.get(2).getItem(mainCategory));
                setItem(16, menusCategories.get(3).getItem(mainCategory));
            }
            case 5 -> {
                setRows(6);
                setItem(10, menusCategories.getFirst().getItem(mainCategory));
                setItem(13, menusCategories.get(1).getItem(mainCategory));
                setItem(16, menusCategories.get(2).getItem(mainCategory));
                setItem(28, menusCategories.get(3).getItem(mainCategory));
                setItem(31, menusCategories.get(4).getItem(mainCategory));
            }
            case 6 -> {
                setRows(6);
                setItem(10, menusCategories.getFirst().getItem(mainCategory));
                setItem(13, menusCategories.get(1).getItem(mainCategory));
                setItem(16, menusCategories.get(2).getItem(mainCategory));
                setItem(28, menusCategories.get(3).getItem(mainCategory));
                setItem(31, menusCategories.get(4).getItem(mainCategory));
                setItem(34, menusCategories.get(5).getItem(mainCategory));
            }
            case 7 -> {
                setRows(6);
                setItem(10, menusCategories.getFirst().getItem(mainCategory));
                setItem(12, menusCategories.get(1).getItem(mainCategory));
                setItem(14, menusCategories.get(2).getItem(mainCategory));
                setItem(16, menusCategories.get(3).getItem(mainCategory));
                setItem(28, menusCategories.get(4).getItem(mainCategory));
                setItem(30, menusCategories.get(5).getItem(mainCategory));
                setItem(32, menusCategories.get(6).getItem(mainCategory));
            }
            case 8 -> {
                setRows(6);
                setItem(10, menusCategories.getFirst().getItem(mainCategory));
                setItem(12, menusCategories.get(1).getItem(mainCategory));
                setItem(14, menusCategories.get(2).getItem(mainCategory));
                setItem(16, menusCategories.get(3).getItem(mainCategory));
                setItem(28, menusCategories.get(4).getItem(mainCategory));
                setItem(30, menusCategories.get(5).getItem(mainCategory));
                setItem(32, menusCategories.get(6).getItem(mainCategory));
                setItem(34, menusCategories.get(7).getItem(mainCategory));
            }
            case 9 -> {
                setRows(6);
                setItem(10, menusCategories.getFirst().getItem(mainCategory));
                setItem(12, menusCategories.get(1).getItem(mainCategory));
                setItem(14, menusCategories.get(2).getItem(mainCategory));
                setItem(16, menusCategories.get(3).getItem(mainCategory));
                setItem(28, menusCategories.get(4).getItem(mainCategory));
                setItem(30, menusCategories.get(5).getItem(mainCategory));
                setItem(32, menusCategories.get(6).getItem(mainCategory));
                setItem(34, menusCategories.get(7).getItem(mainCategory));
                setItem(20, menusCategories.get(8).getItem(mainCategory));
            }
            case 10 -> {
                setRows(6);
                setItem(10, menusCategories.getFirst().getItem(mainCategory));
                setItem(12, menusCategories.get(1).getItem(mainCategory));
                setItem(14, menusCategories.get(2).getItem(mainCategory));
                setItem(16, menusCategories.get(3).getItem(mainCategory));
                setItem(28, menusCategories.get(4).getItem(mainCategory));
                setItem(30, menusCategories.get(5).getItem(mainCategory));
                setItem(32, menusCategories.get(6).getItem(mainCategory));
                setItem(34, menusCategories.get(7).getItem(mainCategory));
                setItem(20, menusCategories.get(8).getItem(mainCategory));
                setItem(22, menusCategories.get(9).getItem(mainCategory));
            }
            default -> {
                setRows(6);
                setItem(10, menusCategories.getFirst().getItem(mainCategory));
                setItem(12, menusCategories.get(1).getItem(mainCategory));
                setItem(14, menusCategories.get(2).getItem(mainCategory));
                setItem(16, menusCategories.get(3).getItem(mainCategory));
                setItem(28, menusCategories.get(4).getItem(mainCategory));
                setItem(30, menusCategories.get(5).getItem(mainCategory));
                setItem(32, menusCategories.get(6).getItem(mainCategory));
                setItem(34, menusCategories.get(7).getItem(mainCategory));
                setItem(20, menusCategories.get(8).getItem(mainCategory));
                setItem(22, menusCategories.get(9).getItem(mainCategory));
                setItem(24, menusCategories.get(10).getItem(mainCategory));
            }
        }
        fillRow(getRows());
    }

    private void fillLegacy() {
        if (getItem(45).isEmpty()) {
            setRows(6);
            setTitle(contentMenu.getTitle());
            setItem(DECORATION_PANE_ITEM, 36, 37, 38, 39, 40, 41, 42, 43, 44);
            int category = 0;
            for (int slot = 45; slot < 54; slot++) {
                if (category == menusCategories.size()) {
                    setItem(slot, DECORATION_ITEM);
                } else {
                    setItem(slot, menusCategories.get(category).getItem(mainCategory));
                    category++;
                }
            }
            contentMenu.updateElements();
        }
        int size = contentMenu.getCurrentElements().size();
        if (contentMenu.getCurrentPage() > 1) {
            setItem(27, contentMenu.getPreviousPageButton());
        } else {
            setItem(27, AIR_ITEM);
        }
        if (contentMenu.getCurrentPage() < contentMenu.getPages()) {
            setItem(35, contentMenu.getNextPageButton());
        } else {
            setItem(35, AIR_ITEM);
        }
        int element = AbstractListMenu.PlacementLayout.BOTTOM_CHARMS_BAR.getElementsSlots().length * (contentMenu.getCurrentPage() - 1);
        for (int slot : AbstractListMenu.PlacementLayout.BOTTOM_CHARMS_BAR.getElementsSlots()) {
            if (element == size) {
                setItem(slot, AIR_ITEM);
            } else {
                setItem(slot, contentMenu.getElementIcon(element));
                element++;
            }
        }
    }

    @Override
    public void onClick(@NotNull InventoryClickEvent event) {
        if (!isClickedInMenuSlots(event) || !isPlayerClicked(event)) return;
        event.setCancelled(true);
        ItemStack clicked = event.getCurrentItem();
        MenusCategory category = MenusCategory.getByIcon(clicked);
        if (category != null) {
            Sounds.DEV_CHANGE_CATEGORY.play(event.getWhoClicked());
            contentMenu.setCurrentCategory(category);
            contentMenu.setCurrentPage(1);
            if (legacy) {
                contentMenu.updateElements();
                fillItems(player);
                return;
            }
            contentMenu.open(player);
        } else {
            if (legacy) {
                contentMenu.onClick(event);
                if (itemEquals(clicked, contentMenu.getNextPageButton()) || itemEquals(clicked, contentMenu.getPreviousPageButton())) {
                    fillItems(player);
                }
            }
        }
    }


    private void fillRow(int row) {
        if (row < 1) row = 1;
        if (row > 6) row = 6;
        int firstSlot = (row - 1) * 9;
        setItem(firstSlot, DECORATION_PANE_ITEM);
        setItem(firstSlot + 1, DECORATION_PANE_ITEM);
        setItem(firstSlot + 2, createItem(additionalPane, 1));

        setItem(firstSlot + 4, mainItem);

        setItem(firstSlot + 6, createItem(additionalPane, 1));
        setItem(firstSlot + 7, DECORATION_PANE_ITEM);
        setItem(firstSlot + 8, DECORATION_PANE_ITEM);
    }

    @Override
    public void onOpen(@NotNull InventoryOpenEvent event) {
    }

}
