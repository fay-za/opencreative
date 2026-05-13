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

package ua.mcchickenstudio.opencreative.coding.modules;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import ua.mcchickenstudio.opencreative.OpenCreative;
import ua.mcchickenstudio.opencreative.menus.ListBrowserMenu;
import ua.mcchickenstudio.opencreative.planets.DevPlanet;
import ua.mcchickenstudio.opencreative.settings.Sounds;

import java.util.ArrayList;
import java.util.List;

import static ua.mcchickenstudio.opencreative.utils.ItemUtils.*;
import static ua.mcchickenstudio.opencreative.utils.MessageUtils.getLocaleMessage;

public final class ModulesBrowserMenu extends ListBrowserMenu<Module> {

    private final List<Module> modules = new ArrayList<>();

    public ModulesBrowserMenu(Player player, List<Module> modules) {
        super(player, getLocaleMessage("menus.modules.title", false), PlacementLayout.BOTTOM_NO_DECORATION,
                new int[]{49}, new int[]{45, 46, 52, 53});
        this.modules.addAll(modules);
    }

    public ModulesBrowserMenu(Player player) {
        this(player, new ArrayList<>(OpenCreative.getModuleManager().getModules()));
    }

    @Override
    protected ItemStack getElementIcon(Module module) {
        return module.getInformation().getIcon();
    }

    @Override
    protected void fillOtherItems() {
        setItem(createItem(Material.BROWN_STAINED_GLASS_PANE, 1), 47, 51);
        setItem(createItem(Material.CHEST_MINECART, 1, "menus.modules.items.own-modules", "own-modules"), 49);
    }

    @Override
    protected void onCharmsBarClick(InventoryClickEvent event) {
        ItemStack currentItem = event.getCurrentItem();
        event.setCancelled(true);
        if (currentItem == null) {
            return;
        }
        if (getItemType(currentItem).equalsIgnoreCase("own-modules")) {
            new OwnModulesBrowserMenu(getPlayer()).open(getPlayer());
        }
    }

    @Override
    protected void fillArrowsItems(int currentPage) {
        if (elements.isEmpty()) {
            setItem(getNoElementsPageButtonSlot(), getNoElementsButton());
            setItem(getPreviousPageButtonSlot(), DECORATION_ITEM);
            setItem(getNextPageButtonSlot(), DECORATION_ITEM);
        } else {
            int maxPagesAmount = getPages();
            if (currentPage > maxPagesAmount || currentPage < 1) {
                currentPage = 1;
            }
            setItem(getPreviousPageButtonSlot(), currentPage > 1 ? getPreviousPageButton() : DECORATION_ITEM);
            setItem(getNextPageButtonSlot(), currentPage < maxPagesAmount ? getNextPageButton() : DECORATION_ITEM);
        }
    }

    @Override
    protected void onElementClick(InventoryClickEvent event) {
        ItemStack currentItem = event.getCurrentItem();
        event.setCancelled(true);
        if (currentItem == null) {
            return;
        }
        String id = getPersistentData(currentItem, getItemIdKey());
        if (id.isEmpty()) {
            return;
        }
        Module module = OpenCreative.getModuleManager().getModuleById(id);
        if (module == null) return;
        DevPlanet devPlanet = OpenCreative.getPlanetsManager().getDevPlanet(getPlayer());
        event.getWhoClicked().closeInventory();
        if (devPlanet == null) {
            getPlayer().sendMessage(getLocaleMessage("only-in-dev-world"));
            Sounds.PLAYER_FAIL.play(event.getWhoClicked());
            return;
        }
        module.place(devPlanet, getPlayer());
    }

    @Override
    public List<Module> getElements() {
        return modules;
    }

    @Override
    protected ItemStack getNextPageButton() {
        return replacePlaceholderInLore(createItem(Material.SPECTRAL_ARROW, getCurrentPage() + 1, "menus.modules.items.next-page"), "%page%", getCurrentPage() + 1);
    }

    @Override
    protected ItemStack getPreviousPageButton() {
        return replacePlaceholderInLore(createItem(Material.ARROW, Math.max(1, getCurrentPage() - 1), "menus.modules.items.previous-page"), "%page%", getCurrentPage() - 1);
    }

    @Override
    protected ItemStack getNoElementsButton() {
        return createItem(Material.BARRIER, 1, "menus.modules.items.no-modules");
    }

    @Override
    public void onOpen(@NotNull InventoryOpenEvent event) {
        Sounds.MENU_OPEN_MODULES_BROWSER.play(event.getPlayer());
    }
}
