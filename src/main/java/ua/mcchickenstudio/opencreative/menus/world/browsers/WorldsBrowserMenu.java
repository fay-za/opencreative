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

package ua.mcchickenstudio.opencreative.menus.world.browsers;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import ua.mcchickenstudio.opencreative.OpenCreative;
import ua.mcchickenstudio.opencreative.menus.ListBrowserMenu;
import ua.mcchickenstudio.opencreative.menus.buttons.ParameterButton;
import ua.mcchickenstudio.opencreative.menus.world.WorldModerationMenu;
import ua.mcchickenstudio.opencreative.planets.Planet;
import ua.mcchickenstudio.opencreative.settings.Sounds;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Set;

import static ua.mcchickenstudio.opencreative.utils.ItemUtils.*;
import static ua.mcchickenstudio.opencreative.utils.MessageUtils.getLocaleMessage;
import static ua.mcchickenstudio.opencreative.utils.MessageUtils.getPlayerLocaleMessage;

/**
 * This class represents a menu, that displays specified list of worlds.
 * Player can sort worlds and change pages.
 */
public class WorldsBrowserMenu extends ListBrowserMenu<Planet> {

    private final List<Planet> planets;
    private final List<ParameterButton> buttons = new ArrayList<>();
    private final ItemStack RECOMMENDED;
    private int sortType = 1;

    public WorldsBrowserMenu(Player player, Set<Planet> planets) {
        super(player, getLocaleMessage("menus.all-worlds.title", false), PlacementLayout.BOTTOM_NO_DECORATION,
                new int[]{45, 48, 50}, new int[]{45, 46, 52, 53});
        this.planets = new ArrayList<>(planets);
        Comparator<Planet> sortByOnline = (planet1, planet2) -> Integer.compare(planet2.getOnline(), planet1.getOnline());
        this.planets.sort(sortByOnline);
        RECOMMENDED = createItem(Material.WIND_CHARGE, 1, "menus.all-worlds.items.recommended");
    }

    public WorldsBrowserMenu(Player player, Set<Planet> planets, boolean withRecommendedButton) {
        super(player, getLocaleMessage("menus.all-worlds.title", false), PlacementLayout.BOTTOM_NO_DECORATION,
                new int[]{45, 48, 50}, new int[]{45, 46, 52, 53});
        this.planets = new ArrayList<>(planets);
        Comparator<Planet> sortByOnline = (planet1, planet2) -> Integer.compare(planet2.getOnline(), planet1.getOnline());
        this.planets.sort(sortByOnline);
        RECOMMENDED = withRecommendedButton ? createItem(Material.WIND_CHARGE, 1, "menus.all-worlds.items.recommended") : DECORATION_ITEM;
    }

    @Override
    protected ItemStack getElementIcon(Planet planet) {
        return planet.getInformation().getIcon();
    }

    @Override
    protected void fillOtherItems() {
        ParameterButton sort = new ParameterButton(
                "online",
                List.of("online", "likes", "last"),
                "sort",
                "menus.all-worlds",
                "menus.all-worlds.items.sort",
                List.of(Material.HOPPER, Material.GOLDEN_APPLE, Material.CLOCK)
        );
        ParameterButton category = new ParameterButton(
                "online",
                List.of("all", "sandbox", "adventure", "arcade", "roleplay", "simulator", "experiment", "story", "strategy"),
                "category",
                "menus.all-worlds",
                "menus.all-worlds.items.category",
                List.of(Material.CHERRY_CHEST_BOAT, Material.SANDSTONE, Material.DARK_PRISMARINE_STAIRS, Material.TARGET, Material.AXOLOTL_BUCKET, Material.CAMPFIRE, Material.TNT, Material.WRITABLE_BOOK, Material.CROSSBOW)
        );
        buttons.add(sort);
        buttons.add(category);
        setItem(45, RECOMMENDED);
        setItem(47, createItem(Material.CYAN_STAINED_GLASS_PANE, 1));
        setItem(48, category.getItem());
        setItem(50, sort.getItem());
        setItem(51, createItem(Material.CYAN_STAINED_GLASS_PANE, 1));
    }

    @Override
    protected void onCharmsBarClick(InventoryClickEvent event) {
        event.setCancelled(true);
        ItemStack item = event.getCurrentItem();
        if (item == null) {
            return;
        }
        for (ParameterButton button : buttons) {
            if (itemEquals(item, button.getItem(true))) {
                if (event.getRawSlot() == 50) {
                    button.next();
                    sortType = button.getCurrentChoice();
                    sortElements();
                    fillElements(getCurrentPage());
                    fillArrowsItems(getCurrentPage());
                    setItem(50, button.getItem());
                    Sounds.MENU_WORLDS_BROWSER_SORT.play(getPlayer());
                } else if (event.getRawSlot() == 48) {
                    button.next();
                    elements.clear();
                    if (button.getCurrentValue().equals("all")) {
                        elements.addAll(getElements());
                    } else {
                        elements.addAll(new ArrayList<>(planets).stream().filter(planet -> planet.getInformation().getCategory().name().equalsIgnoreCase(button.getCurrentValue().toString())).toList());
                    }
                    sortElements();
                    fillElements(getCurrentPage());
                    fillArrowsItems(getCurrentPage());
                    setItem(48, button.getItem());
                    Sounds.MENU_WORLDS_BROWSER_CATEGORY.play(getPlayer());
                }
            }
        }
        if (itemEquals(item, RECOMMENDED)) {
            new WorldsCompassMenu().open(getPlayer());
        }

    }

    @Override
    protected void onElementClick(InventoryClickEvent event) {
        ItemStack currentItem = event.getCurrentItem();
        event.setCancelled(true);
        if (currentItem == null) {
            return;
        }
        String worldID = getPersistentData(currentItem, getItemIdKey());
        if (worldID.isEmpty()) {
            return;
        }
        Planet planet = OpenCreative.getPlanetsManager().getPlanetByCustomID(worldID);
        if (planet != null) {
            if (event.getClick() == ClickType.SHIFT_LEFT) {
                if (!getPlayer().hasPermission("opencreative.moderation.menus")) {
                    onPlanetClick(getPlayer(), planet);
                    return;
                }
                new WorldModerationMenu(planet).open(getPlayer());
            } else {
                onPlanetClick(getPlayer(), planet);
            }
        }
    }

    protected void onPlanetClick(Player player, Planet planet) {
        player.closeInventory();
        if (planet.equals(OpenCreative.getPlanetsManager().getPlanetByPlayer(player))) {
            player.sendMessage(getPlayerLocaleMessage("same-world", player));
            Sounds.PLAYER_FAIL.play(player);
            return;
        }
        planet.connectPlayer(player);
    }

    @Override
    protected void fillArrowsItems(int currentPage) {
        if (elements.isEmpty()) {
            setItem(getNoElementsPageButtonSlot(), getNoElementsButton());
            setItem(getPreviousPageButtonSlot(), RECOMMENDED);
            setItem(getNextPageButtonSlot(), DECORATION_ITEM);
        } else {
            int maxPagesAmount = getPages();
            if (currentPage > maxPagesAmount || currentPage < 1) {
                currentPage = 1;
            }
            setItem(getPreviousPageButtonSlot(), currentPage > 1 ? getPreviousPageButton() : RECOMMENDED);
            setItem(getNextPageButtonSlot(), currentPage < maxPagesAmount ? getNextPageButton() : DECORATION_ITEM);
        }
    }

    private void sortElements() {
        Comparator<Object> planetComparator = switch (sortType) {
            case 2 ->
                    (planet1, planet2) -> Integer.compare(((Planet) planet2).getInformation().getReputation(), ((Planet) planet1).getInformation().getReputation());
            case 3 ->
                    (planet1, planet2) -> Long.compare(((Planet) planet2).getCreationTime(), ((Planet) planet1).getCreationTime());
            default ->
                    (planet1, planet2) -> Integer.compare(((Planet) planet2).getOnline(), ((Planet) planet1).getOnline());
        };
        elements.sort(planetComparator);
    }

    @Override
    public List<Planet> getElements() {
        return new ArrayList<>(planets);
    }

    @Override
    protected ItemStack getNextPageButton() {
        return replacePlaceholderInLore(createItem(Material.SPECTRAL_ARROW, getCurrentPage() + 1, "menus.all-worlds.items.next-page"), "%page%", getCurrentPage() + 1);
    }

    @Override
    protected ItemStack getPreviousPageButton() {
        return replacePlaceholderInLore(createItem(Material.ARROW, Math.max(1, getCurrentPage() - 1), "menus.all-worlds.items.previous-page"), "%page%", getCurrentPage() - 1);
    }

    @Override
    protected ItemStack getNoElementsButton() {
        return createItem(Material.BARRIER, 1, "menus.all-worlds.items.no-worlds");
    }

    @Override
    public void onOpen(@NotNull InventoryOpenEvent event) {
        Sounds.MENU_OPEN_WORLDS_BROWSER.play(event.getPlayer());
    }
}
