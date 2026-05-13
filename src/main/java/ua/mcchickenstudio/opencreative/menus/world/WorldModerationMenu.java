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

package ua.mcchickenstudio.opencreative.menus.world;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import ua.mcchickenstudio.opencreative.OpenCreative;
import ua.mcchickenstudio.opencreative.events.planet.PlanetSharingChangeEvent;
import ua.mcchickenstudio.opencreative.menus.AbstractMenu;
import ua.mcchickenstudio.opencreative.planets.Planet;
import ua.mcchickenstudio.opencreative.settings.Sounds;
import ua.mcchickenstudio.opencreative.utils.MessageUtils;

import static ua.mcchickenstudio.opencreative.utils.ItemUtils.*;
import static ua.mcchickenstudio.opencreative.utils.MessageUtils.getLocaleMessage;

public final class WorldModerationMenu extends AbstractMenu implements WorldMenu {

    private final Planet planet;
    private final ItemStack CLEAR_NAME = createItem(Material.NAME_TAG, 1, "menus.world-moderation.items.clear-name", "clear-name");
    private final ItemStack CLEAR_DESCRIPTION = createItem(Material.BOOK, 1, "menus.world-moderation.items.clear-description", "clear-description");
    private final ItemStack CLEAR_ICON = createItem(Material.DIAMOND, 1, "menus.world-moderation.items.clear-icon", "clear-icon");
    private final ItemStack CLEAR_ID = createItem(Material.LEAD, 1, "menus.world-moderation.items.clear-id", "clear-id");

    private final ItemStack CONNECT_SILENT = createItem(Material.ENDER_EYE, 1, "menus.world-moderation.items.connect-silent", "connect-silent");
    private final ItemStack CONNECT_DEV_SILENT = createItem(Material.ENDER_PEARL, 1, "menus.world-moderation.items.connect-dev-silent", "connect-dev-silent");
    private final ItemStack LOAD = createItem(Material.CHERRY_CHEST_BOAT, 1, "menus.world-moderation.items.load", "load");
    private final ItemStack UNLOAD = createItem(Material.CHEST_MINECART, 1, "menus.world-moderation.items.unload", "unload");

    private final ItemStack CLOSE_WORLD = createItem(Material.BARRIER, 1, "menus.world-moderation.items.close-world", "close-world");

    public WorldModerationMenu(Planet planet) {
        super(4, MessageUtils.getLocaleMessage("menus.world-moderation.title", false));
        this.planet = planet;
    }

    @Override
    public void fillItems(Player player) {
        setItem(DECORATION_PANE_ITEM, 28, 34);
        setItem(createItem(Material.LIGHT_BLUE_STAINED_GLASS_PANE, 1), 29, 33);
        setItem(31, setPersistentData(planet.getInformation().getIcon().clone(), getItemTypeKey(), "connect"));
        setItem(10, player.hasPermission("opencreative.moderation.clear-name") ? CLEAR_NAME : NO_PERMS_ITEM);
        setItem(11, player.hasPermission("opencreative.moderation.clear-description") ? CLEAR_DESCRIPTION : NO_PERMS_ITEM);
        setItem(12, player.hasPermission("opencreative.moderation.clear-icon") ? CLEAR_ICON : NO_PERMS_ITEM);
        setItem(13, player.hasPermission("opencreative.moderation.clear-id") ? CLEAR_ID : NO_PERMS_ITEM);
        setItem(15, player.hasPermission("opencreative.moderation.connect-silent") ? (planet.isLoaded() ? CONNECT_SILENT : DECORATION_ITEM) : NO_PERMS_ITEM);
        setItem(16, player.hasPermission("opencreative.moderation.connect-dev-silent") ? (planet.isLoaded() ? CONNECT_DEV_SILENT : DECORATION_ITEM) : NO_PERMS_ITEM);
        setItem(27, planet.isLoaded() ? (player.hasPermission("opencreative.world.unload") ? UNLOAD : NO_PERMS_ITEM) : (player.hasPermission("opencreative.world.load") ? LOAD : NO_PERMS_ITEM));
        setItem(35, player.hasPermission("opencreative.moderation.close-world") ? (planet.getSharing() == Planet.Sharing.PUBLIC ? CLOSE_WORLD : DECORATION_ITEM) : NO_PERMS_ITEM);
    }

    @Override
    public void onClick(@NotNull InventoryClickEvent event) {
        event.setCancelled(true);
        ItemStack item = event.getCurrentItem();
        if (!isPlayerClicked(event) || !isClickedInMenuSlots(event)) return;
        if (item == null) return;
        Player player = (Player) event.getWhoClicked();
        switch (getItemType(item)) {
            case "connect" -> {
                player.closeInventory();
                if (planet.equals(OpenCreative.getPlanetsManager().getPlanetByPlayer(player))) {
                    player.sendMessage(MessageUtils.getPlayerLocaleMessage("same-world", player));
                    Sounds.PLAYER_FAIL.play(player);
                    return;
                }
                planet.connectPlayer(player);
            }
            case "connect-silent" -> {
                player.closeInventory();
                if (planet.equals(OpenCreative.getPlanetsManager().getPlanetByPlayer(player))) {
                    player.sendMessage(MessageUtils.getPlayerLocaleMessage("same-world", player));
                    Sounds.PLAYER_FAIL.play(player);
                    return;
                }
                planet.connectPlayer(player, true);
            }
            case "connect-dev-silent" -> {
                player.closeInventory();
                planet.connectToDevPlanet(player, true);
            }
            case "clear-name" -> {
                if (player.hasCooldown(item.getType())) {
                    Sounds.PLAYER_FAIL.play(player);
                    return;
                }
                player.setCooldown(item.getType(), 20);
                planet.getInformation().setDisplayName(getLocaleMessage("creating-world.default-world-name").replace("%player%", planet.getOwner()));
                Sounds.MENU_CLEAR_DATA.play(player);
                planet.getInformation().updateIcon();
                fillItems(player);
            }
            case "clear-description" -> {
                if (player.hasCooldown(item.getType())) {
                    Sounds.PLAYER_FAIL.play(player);
                    return;
                }
                player.setCooldown(item.getType(), 20);
                planet.getInformation().setDescription(getLocaleMessage("creating-world.default-world-description").replace("%player%", planet.getOwner()));
                Sounds.MENU_CLEAR_DATA.play(player);
                planet.getInformation().updateIcon();
                fillItems(player);
            }
            case "clear-icon" -> {
                if (player.hasCooldown(item.getType())) {
                    Sounds.PLAYER_FAIL.play(player);
                    return;
                }
                player.setCooldown(item.getType(), 20);
                planet.getInformation().setIcon(new ItemStack(Material.DIAMOND));
                Sounds.MENU_CLEAR_DATA.play(player);
                planet.getInformation().updateIcon();
                fillItems(player);
            }
            case "clear-id" -> {
                if (player.hasCooldown(item.getType())) {
                    Sounds.PLAYER_FAIL.play(player);
                    return;
                }
                player.setCooldown(item.getType(), 20);
                planet.getInformation().resetCustomID();
                Sounds.MENU_CLEAR_DATA.play(player);
                planet.getInformation().updateIcon();
                fillItems(player);
            }
            case "close-world" -> {
                if (player.hasCooldown(item.getType())) {
                    Sounds.PLAYER_FAIL.play(player);
                    return;
                }
                player.setCooldown(item.getType(), 20);
                PlanetSharingChangeEvent planetEvent = new PlanetSharingChangeEvent(planet, planet.getSharing(), Planet.Sharing.PRIVATE);
                planetEvent.callEvent();
                if (planetEvent.isCancelled()) {
                    Sounds.PLAYER_FAIL.play(player);
                    return;
                }
                Sounds.WORLD_SETTINGS_SHARING_PRIVATE.play(player);
                planet.setSharing(Planet.Sharing.PRIVATE);
                fillItems(player);
            }
            case "load" -> {
                if (player.hasCooldown(item.getType())) {
                    Sounds.PLAYER_FAIL.play(player);
                    return;
                }
                player.setCooldown(LOAD.getType(), 300);
                player.setCooldown(UNLOAD.getType(), 300);
                Sounds.WORLD_LOAD.play(player);
                OpenCreative.getPlugin().getLogger().info("Player " + player.getName() + " loads planet " + planet.getId());
                planet.getTerritory().load();
                Bukkit.getScheduler().runTaskLater(OpenCreative.getPlugin(), () -> {
                    if (player.isOnline() && equals(player.getOpenInventory().getTopInventory().getHolder())) {
                        fillItems(player);
                    }
                }, 10L);
            }
            case "unload" -> {
                if (player.hasCooldown(item.getType())) {
                    Sounds.PLAYER_FAIL.play(player);
                    return;
                }
                player.setCooldown(LOAD.getType(), 300);
                player.setCooldown(UNLOAD.getType(), 300);
                Sounds.WORLD_UNLOAD.play(player);
                OpenCreative.getPlugin().getLogger().info("Player " + player.getName() + " unloads planet " + planet.getId());
                planet.getTerritory().unload();
                Bukkit.getScheduler().runTaskLater(OpenCreative.getPlugin(), () -> {
                    if (player.isOnline() && equals(player.getOpenInventory().getTopInventory().getHolder())) {
                        fillItems(player);
                    }
                }, 10L);
            }
        }
    }

    @Override
    public void onOpen(@NotNull InventoryOpenEvent event) {
        Sounds.MENU_OPEN_WORLD_MODERATION.play(event.getPlayer());
    }

    @Override
    public Planet getPlanet() {
        return planet;
    }
}
