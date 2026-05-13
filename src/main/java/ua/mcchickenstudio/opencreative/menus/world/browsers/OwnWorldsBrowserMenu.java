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

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;
import ua.mcchickenstudio.opencreative.OpenCreative;
import ua.mcchickenstudio.opencreative.menus.ListBrowserMenu;
import ua.mcchickenstudio.opencreative.menus.world.WorldAccessMenu;
import ua.mcchickenstudio.opencreative.menus.world.WorldGenerationMenu;
import ua.mcchickenstudio.opencreative.planets.Planet;
import ua.mcchickenstudio.opencreative.settings.Sounds;
import ua.mcchickenstudio.opencreative.utils.MessageUtils;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import static ua.mcchickenstudio.opencreative.utils.ItemUtils.*;
import static ua.mcchickenstudio.opencreative.utils.MessageUtils.convertTime;
import static ua.mcchickenstudio.opencreative.utils.MessageUtils.getLocaleMessage;

/**
 * This class represents a menu, that displays specified list of worlds.
 * Player can sort worlds and change pages.
 */
public final class OwnWorldsBrowserMenu extends ListBrowserMenu<Planet> {

    private final List<Planet> planets;

    private final ItemStack CREATE_WORLD = createItem(Material.ENDER_EYE, 1, "menus.own-worlds.items.create-world");
    private final ItemStack WORLDS_LIMIT = createItem(Material.RED_STAINED_GLASS, 1, "menus.own-worlds.items.limit");
    private final ItemStack RECOMMENDED = createItem(Material.WIND_CHARGE, 1, "menus.own-worlds.items.recommended");

    public OwnWorldsBrowserMenu(Player player) {
        super(player, getLocaleMessage("menus.own-worlds.title", false), PlacementLayout.BOTTOM_NO_DECORATION,
                new int[]{45, 49}, new int[]{45, 46, 47, 51, 52, 53});
        this.planets = new ArrayList<>(OpenCreative.getPlanetsManager().getPlanetsByOwner(player));
        Comparator<Planet> sortByOnline = (planet1, planet2) -> Integer.compare(planet2.getOnline(), planet1.getOnline());
        this.planets.sort(sortByOnline);
        CREATE_WORLD.setAmount(Math.min(64, getAvailableAmount()));
    }

    @Override
    protected ItemStack getElementIcon(Planet planet) {
        ItemStack item = clearItemMeta(planet.getInformation().getIcon().clone());
        ItemMeta meta = item.getItemMeta();
        meta.displayName(planet.getInformation().displayName());
        List<String> lore = new ArrayList<>();
        for (String loreLine : MessageUtils.getLocaleItemDescription("menus.own-worlds.items.world.lore")) {
            if (loreLine.contains("%planetDescription%")) {
                String[] newLines = planet.getInformation().getDescription().split("\\\\n");
                for (String newLine : newLines) {
                    lore.add(loreLine.replace("%planetDescription%", ChatColor.translateAlternateColorCodes('&', newLine)));
                }
            } else {
                lore.add(MessageUtils.parsePlanetLines(planet, loreLine));
            }
        }
        meta.setLore(lore);
        item.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
        meta.addItemFlags(ItemFlag.HIDE_ARMOR_TRIM);
        meta.addItemFlags(ItemFlag.HIDE_DESTROYS);
        meta.addItemFlags(ItemFlag.HIDE_UNBREAKABLE);
        meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
        meta.addItemFlags(ItemFlag.HIDE_DYE);
        meta.addItemFlags(ItemFlag.HIDE_PLACED_ON);
        item.setItemMeta(meta);
        setPersistentData(item, getItemIdKey(), String.valueOf(planet.getId()));
        return item;
    }

    @Override
    protected void fillOtherItems() {
        setItem(45, RECOMMENDED);
        int amount = OpenCreative.getPlanetsManager().getPlanetsByOwner(getPlayer()).size();
        int limit = OpenCreative.getSettings().getGroups().getGroup(getPlayer()).getWorldsLimit();
        if (OpenCreative.getSettings().isWorldGenerationUnavailable()) {
            setItem(createItem(Material.RED_STAINED_GLASS_PANE, 1), 47, 51);
            setItem(49, DISABLED_ITEM);
            return;
        }
        if (amount >= limit) {
            setItem(createItem(Material.RED_STAINED_GLASS_PANE, 1), 47, 51);
            replacePlaceholderInLore(WORLDS_LIMIT, "%limit%", limit);
            replacePlaceholderInLore(WORLDS_LIMIT, "%planets%", amount);
            setItem(49, WORLDS_LIMIT);
        } else {
            setItem(createItem(Material.LIME_STAINED_GLASS_PANE, 1), 47, 51);
            setItem(49, CREATE_WORLD);
        }
    }

    @Override
    protected void onCharmsBarClick(InventoryClickEvent event) {
        event.setCancelled(true);
        ItemStack item = event.getCurrentItem();
        if (item == null) {
            return;
        }
        if (itemEquals(item, RECOMMENDED)) {
            new WorldsCompassMenu().open(getPlayer());
        } else if (itemEquals(item, CREATE_WORLD)) {
            if (isNotLimitReached()) {
                long now = System.currentTimeMillis();
                long minSeconds = OpenCreative.getSettings().getRequirements().getWorldCreationMinSeconds();
                long playedSeconds = (now - getPlayer().getFirstPlayed()) / 1000;

                if (playedSeconds < minSeconds) {
                    long unlockTime = ((minSeconds - playedSeconds) * 1000);

                    Sounds.PLAYER_CANCEL.play(getPlayer());
                    getPlayer().closeInventory();
                    getPlayer().sendMessage(
                            MessageUtils.getPlayerLocaleMessage("creating-world.not-enough-played", getPlayer())
                                    .replace("%time%", convertTime(unlockTime))
                    );
                    return;
                }
                new WorldGenerationMenu(getPlayer()).open(getPlayer());
            }
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
        Planet planet = OpenCreative.getPlanetsManager().getPlanetById(worldID);
        if (planet != null) {
            new WorldAccessMenu(planet).open(getPlayer());
        }
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

    @Override
    public List<Planet> getElements() {
        return new ArrayList<>(planets);
    }

    @Override
    protected ItemStack getNextPageButton() {
        return replacePlaceholderInLore(createItem(Material.SPECTRAL_ARROW, getCurrentPage() + 1, "menus.own-worlds.items.next-page"), "%page%", getCurrentPage() + 1);
    }

    @Override
    protected ItemStack getPreviousPageButton() {
        return replacePlaceholderInLore(createItem(Material.ARROW, Math.max(1, getCurrentPage() - 1), "menus.own-worlds.items.previous-page"), "%page%", getCurrentPage() - 1);
    }

    @Override
    protected ItemStack getNoElementsButton() {
        return createItem(Material.BARRIER, 1, "menus.own-worlds.items.no-worlds");
    }

    @Override
    public void onOpen(@NotNull InventoryOpenEvent event) {
        Sounds.MENU_OPEN_OWN_WORLDS_BROWSER.play(event.getPlayer());
    }

    private int getAvailableAmount() {
        int planetsAmount = OpenCreative.getPlanetsManager().getPlanetsByOwner(getPlayer()).size();
        int planetsLimit = OpenCreative.getSettings().getGroups().getGroup(getPlayer()).getWorldsLimit();
        return Math.max(1, planetsLimit - planetsAmount);
    }

    private boolean isNotLimitReached() {
        int planetsAmount = OpenCreative.getPlanetsManager().getPlanetsByOwner(getPlayer()).size();
        int planetsLimit = OpenCreative.getSettings().getGroups().getGroup(getPlayer()).getWorldsLimit();
        return planetsAmount < planetsLimit;
    }

}
