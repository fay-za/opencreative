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


package ua.mcchickenstudio.opencreative.menus.world.settings;

import com.destroystokyo.paper.profile.PlayerProfile;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;
import org.jetbrains.annotations.NotNull;
import ua.mcchickenstudio.opencreative.menus.ListBrowserMenu;
import ua.mcchickenstudio.opencreative.menus.buttons.ParameterButton;
import ua.mcchickenstudio.opencreative.menus.world.WorldMenu;
import ua.mcchickenstudio.opencreative.planets.Planet;
import ua.mcchickenstudio.opencreative.settings.Sounds;
import ua.mcchickenstudio.opencreative.utils.PlayerUtils;

import java.util.ArrayList;
import java.util.List;

import static ua.mcchickenstudio.opencreative.utils.ItemUtils.*;
import static ua.mcchickenstudio.opencreative.utils.MessageUtils.getLocaleMessage;

/**
 * This class represents a menu, that displays list of players in world.
 */
public final class PlayersBrowserMenu extends ListBrowserMenu<String> implements WorldMenu {

    private final Planet planet;
    private final List<ParameterButton> buttons = new ArrayList<>();

    private final ItemStack KICK_ALL = createItem(Material.STRUCTURE_VOID, 1, "menus.players-browser.items.kick-all");
    private final ItemStack BACK_TO_SETTINGS = createItem(Material.ARROW, 1, "menus.players-browser.items.back");

    public PlayersBrowserMenu(Player player, Planet planet) {
        super(player, getLocaleMessage("menus.players-browser.title", false),
                PlacementLayout.BOTTOM_NO_DECORATION, new int[]{45, 48, 50}, new int[]{46, 52});
        this.planet = planet;
    }

    @Override
    protected ItemStack getElementIcon(String object) {
        if (object instanceof String player) {
            return createPlayerItem(player);
        }
        return null;
    }

    private ItemStack createPlayerItem(String nickname) {

        Player player = Bukkit.getPlayerExact(nickname);
        String statusKey;
        if (planet.getWorldPlayers().isBanned(nickname)) {
            statusKey = "banned";
        } else if (player == null || !planet.getPlayers().contains(player)) {
            statusKey = "offline";
        } else {
            statusKey = "online";
        }
        ItemStack item = createItem(Material.PLAYER_HEAD, 1,
                "menus.players-browser.items.player." + statusKey);
        ItemMeta meta = item.getItemMeta();
        if (meta == null) {
            return item;
        }
        if (meta instanceof SkullMeta skullMeta) {
            PlayerProfile profile = Bukkit.createProfile(nickname);
            skullMeta.setPlayerProfile(profile);
            item.setItemMeta(skullMeta);
        }

        String displayName = nickname.substring(0, Math.min(20, nickname.length()));
        replacePlaceholderInLore(item, "%name%", displayName);

        replacePlaceholderInLore(item, "%status%",
                getLocaleMessage("menus.players-browser.items.player." + statusKey));

        String devKey;
        if (player != null && planet.getWorldPlayers().isDeveloperGuest(player)) {
            devKey = "guest";
        } else if (planet.getWorldPlayers().getDevelopersTrusted().contains(nickname)) {
            devKey = "trusted";
        } else if (planet.getWorldPlayers().getDevelopersNotTrusted().contains(nickname)) {
            devKey = "not-trusted";
        } else {
            devKey = "none";
        }
        replacePlaceholderInLore(item, "%dev%",
                getLocaleMessage("menus.players-browser.items.player.dev." + devKey));

        String buildKey;
        if (planet.getWorldPlayers().getBuildersTrusted().contains(nickname)) {
            buildKey = "trusted";
        } else if (planet.getWorldPlayers().getBuildersNotTrusted().contains(nickname)) {
            buildKey = "not-trusted";
        } else {
            buildKey = "none";
        }
        replacePlaceholderInLore(item, "%build%",
                getLocaleMessage("menus.players-browser.items.player.build." + buildKey));

        String whitelistKey = String.valueOf(planet.getWorldPlayers().isWhitelisted(nickname));
        replacePlaceholderInLore(item, "%white-list%",
                getLocaleMessage("menus.players-browser.items.player.white-list." + whitelistKey));

        String flightKey;
        if (player == null || !player.getWorld().equals(planet.getWorld())) {
            flightKey = "offline";
        } else {
            flightKey = String.valueOf(player.getAllowFlight());
        }
        replacePlaceholderInLore(item, "%flight%",
                getLocaleMessage("menus.players-browser.items.player.flight." + flightKey));

        setPersistentData(item, getItemTypeKey(), nickname);
        return item;
    }

    @Override
    protected void fillOtherItems() {
        ParameterButton type = new ParameterButton(
                "all",
                List.of("all", "online", "builders", "developers", "whitelisted", "banned", "offline"),
                "type",
                "menus.all-worlds",
                "menus.players-browser.items.type",
                List.of(Material.HOPPER, Material.BEACON, Material.BRICKS, Material.COMMAND_BLOCK_MINECART, Material.FILLED_MAP, Material.STRUCTURE_VOID, Material.BARRIER)
        );
        buttons.add(type);
        setItem(47, createItem(Material.BLUE_STAINED_GLASS_PANE, 1));
        setItem(48, type.getItem());
        setItem(51, createItem(Material.BLUE_STAINED_GLASS_PANE, 1));
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
                if (event.getRawSlot() == 48) {
                    button.next();
                    elements.clear();
                    switch (button.getCurrentChoice()) {
                        case 2 -> elements.addAll(filterList(getElements(), nickname -> {
                            Player player = Bukkit.getPlayerExact(nickname);
                            return player != null && planet.getPlayers().contains(player);
                        }));
                        case 3 ->
                                elements.addAll(filterList(getElements(), nickname -> planet.getWorldPlayers().getAllBuilders().contains(nickname)));
                        case 4 ->
                                elements.addAll(filterList(getElements(), nickname -> planet.getWorldPlayers().getAllDevelopers().contains(nickname)));
                        case 5 ->
                                elements.addAll(filterList(getElements(), nickname -> planet.getWorldPlayers().getWhitelistedPlayers().contains(nickname)));
                        case 6 ->
                                elements.addAll(filterList(getElements(), nickname -> planet.getWorldPlayers().isBanned(nickname)));
                        case 7 -> elements.addAll(filterList(getElements(), nickname -> {
                            Player player = Bukkit.getPlayerExact(nickname);
                            return player == null || !planet.getPlayers().contains(player);
                        }));
                        default -> elements.addAll(planet.getWorldPlayers().getAllPlayersFromConfig());
                    }
                    fillElements(getCurrentPage());
                    fillArrowsItems(getCurrentPage());
                    setItem(48, button.getItem());
                    Sounds.MENU_PLAYERS_BROWSER_SORT.play(getPlayer());
                    return;
                }
            }
        }
        if (itemEquals(item, BACK_TO_SETTINGS) && planet.isOwner(getPlayer())) {
            new WorldSettingsMenu(planet, getPlayer()).open(getPlayer());
        } else if (itemEquals(item, KICK_ALL)) {
            if (elements.isEmpty()) return;
            for (String nickname : new ArrayList<>(elements)) {
                Player playerToKick = Bukkit.getPlayerExact(nickname);
                if (playerToKick != null && playerToKick.getWorld().equals(planet.getWorld())) {
                    planet.getWorldPlayers().kickPlayer(playerToKick);
                }
            }
            updateElements();
            fillElements(getCurrentPage());
            fillArrowsItems(getCurrentPage());
        }
    }

    @Override
    protected void onElementClick(InventoryClickEvent event) {
        ItemStack currentItem = event.getCurrentItem();
        event.setCancelled(true);
        if (currentItem == null) {
            return;
        }
        if (!planet.isLoaded() || !(planet.getWorldPlayers().canBuild(getPlayer()))) {
            getPlayer().closeInventory();
            return;
        }
        String nickname = getItemType(currentItem);
        if (nickname.isEmpty()) {
            return;
        }
        Player player = Bukkit.getPlayerExact(nickname);
        switch (event.getClick()) {
            case LEFT -> new PlayerControlMenu(nickname, planet).open(getPlayer());
            case RIGHT -> {
                if (PlayerUtils.isEntityInDevPlanet(getPlayer())) {
                    Sounds.PLAYER_FAIL.play(getPlayer());
                    return;
                }
                if (player == null || !player.getWorld().equals(planet.getWorld())) {
                    Sounds.PLAYER_FAIL.play(getPlayer());
                    return;
                }
                getPlayer().closeInventory();
                Sounds.WORLD_TELEPORT_TO_ENTITY.play(getPlayer());
                getPlayer().teleport(player.getLocation());
            }
            case SHIFT_RIGHT -> {
                if (PlayerUtils.isEntityInDevPlanet(getPlayer())) {
                    Sounds.PLAYER_FAIL.play(getPlayer());
                    return;
                }
                if (player == null || !player.getWorld().equals(planet.getWorld())) {
                    Sounds.PLAYER_FAIL.play(getPlayer());
                    return;
                }
                Sounds.WORLD_TELEPORT_ENTITY_TO_ME.play(getPlayer());
                player.teleport(getPlayer().getLocation());
            }
        }
    }

    @Override
    protected void fillArrowsItems(int currentPage) {
        if (elements.isEmpty()) {
            setItem(getNoElementsPageButtonSlot(), getNoElementsButton());
            setItem(getPreviousPageButtonSlot(), planet.isOwner(getPlayer()) ? BACK_TO_SETTINGS : DECORATION_ITEM);
            setItem(getNextPageButtonSlot(), DECORATION_ITEM);
            setItem(50, DECORATION_ITEM);
        } else {
            int maxPagesAmount = getPages();
            if (currentPage > maxPagesAmount || currentPage < 1) {
                this.setCurrentPage(1);
            }
            setItem(getPreviousPageButtonSlot(), currentPage > 1 ? getPreviousPageButton() : planet.isOwner(getPlayer()) ? BACK_TO_SETTINGS : DECORATION_ITEM);
            setItem(getNextPageButtonSlot(), currentPage < maxPagesAmount ? getNextPageButton() : DECORATION_ITEM);
            if (planet.getPlayers().size() > 1) {
                setItem(50, KICK_ALL);
            } else {
                setItem(50, DECORATION_ITEM);
            }
        }
    }

    @Override
    public List<String> getElements() {
        if (!planet.isLoaded()) {
            return List.of();
        }
        return new ArrayList<>(planet.getWorldPlayers().getAllPlayersFromConfig());
    }

    @Override
    protected ItemStack getNextPageButton() {
        return replacePlaceholderInLore(createItem(Material.ARROW, getCurrentPage() + 1, "menus.players-browser.items.next-page"), "%page%", getCurrentPage() + 1);
    }

    @Override
    protected ItemStack getPreviousPageButton() {
        return replacePlaceholderInLore(createItem(Material.ARROW, Math.max(1, getCurrentPage() - 1), "menus.players-browser.items.previous-page"), "%page%", getCurrentPage() - 1);
    }

    @Override
    protected ItemStack getNoElementsButton() {
        return createItem(Material.BARRIER, 1, "menus.players-browser.items.no-players");
    }

    @Override
    public void onOpen(@NotNull InventoryOpenEvent event) {
        if (!planet.isOwner(getPlayer())) {
            event.setCancelled(true);
            getPlayer().closeInventory();
            return;
        }
        Sounds.MENU_OPEN_PLAYERS_BROWSER.play(getPlayer());
    }

    @Override
    public Planet getPlanet() {
        return planet;
    }
}
