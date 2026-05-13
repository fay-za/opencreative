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
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;
import org.jetbrains.annotations.NotNull;
import ua.mcchickenstudio.opencreative.OpenCreative;
import ua.mcchickenstudio.opencreative.commands.world.AdvertisementCommand;
import ua.mcchickenstudio.opencreative.events.planet.PlanetAdvertisementEvent;
import ua.mcchickenstudio.opencreative.events.planet.PlanetModeChangeEvent;
import ua.mcchickenstudio.opencreative.events.planet.PlanetSharingChangeEvent;
import ua.mcchickenstudio.opencreative.menus.AbstractMenu;
import ua.mcchickenstudio.opencreative.menus.ConfirmationMenu;
import ua.mcchickenstudio.opencreative.planets.Planet;
import ua.mcchickenstudio.opencreative.settings.Sounds;
import ua.mcchickenstudio.opencreative.utils.CooldownUtils;
import ua.mcchickenstudio.opencreative.utils.MessageUtils;

import static ua.mcchickenstudio.opencreative.utils.CooldownUtils.getCooldown;
import static ua.mcchickenstudio.opencreative.utils.ItemUtils.*;
import static ua.mcchickenstudio.opencreative.utils.MessageUtils.*;

public final class WorldAccessMenu extends AbstractMenu implements WorldMenu {

    private final Planet planet;
    private final ItemStack CONNECT = createItem(Material.NETHER_STAR, 1, "menus.world-access.items.connect", "connect");

    private final ItemStack PLAY_MODE = createItem(Material.DIAMOND_BLOCK, 1, "menus.world-access.items.play-mode", "play");
    private final ItemStack BUILD_MODE = createItem(Material.BRICKS, 1, "menus.world-access.items.build-mode", "build");
    private final ItemStack ADVERTISEMENT = createItem(Material.BEACON, 1, "menus.world-access.items.advertisement", "ad");

    private final ItemStack OPENED = createItem(Material.LIME_STAINED_GLASS, 1, "menus.world-access.items.opened", "close");
    private final ItemStack CLOSED = createItem(Material.RED_STAINED_GLASS, 1, "menus.world-access.items.closed", "open");
    private final ItemStack DELETE = createItem(Material.TNT_MINECART, 1, "menus.world-access.items.delete", "delete");

    public WorldAccessMenu(Planet planet) {
        super(4, MessageUtils.getLocaleMessage("menus.world-access.title", false).replace("%name%", substring(ChatColor.stripColor(planet.getInformation().getDisplayName()), 25)));
        this.planet = planet;
    }

    @Override
    public void fillItems(Player player) {
        if (getCooldown(player, CooldownUtils.CooldownType.GENERIC_COMMAND) > 0) {
            if (!player.hasCooldown(PLAY_MODE.getType()))
                player.setCooldown(PLAY_MODE.getType(), getCooldown(player, CooldownUtils.CooldownType.GENERIC_COMMAND) * 20);
            if (!player.hasCooldown(BUILD_MODE.getType()))
                player.setCooldown(BUILD_MODE.getType(), getCooldown(player, CooldownUtils.CooldownType.GENERIC_COMMAND) * 20);
            if (!player.hasCooldown(CLOSED.getType()))
                player.setCooldown(CLOSED.getType(), getCooldown(player, CooldownUtils.CooldownType.GENERIC_COMMAND) * 20);
            if (!player.hasCooldown(OPENED.getType()))
                player.setCooldown(OPENED.getType(), getCooldown(player, CooldownUtils.CooldownType.GENERIC_COMMAND) * 20);
        }
        if (getCooldown(player, CooldownUtils.CooldownType.ADVERTISEMENT_COMMAND) > 0) {
            if (!player.hasCooldown(ADVERTISEMENT.getType()))
                player.setCooldown(ADVERTISEMENT.getType(), getCooldown(player, CooldownUtils.CooldownType.ADVERTISEMENT_COMMAND) * 20);
        }
        setItem(DECORATION_PANE_ITEM, 28, 34);
        setItem(createItem(Material.BLUE_STAINED_GLASS_PANE, 1), 29, 33);
        setItem(31, setPersistentData(planet.getInformation().getIcon().clone(), getItemTypeKey(), "connect"));
        setItem(13, CONNECT);
        CONNECT.setAmount(Math.clamp(1, planet.getOnline(), 64));
        setItem(10, planet.getMode() == Planet.Mode.PLAYING ? PLAY_MODE : BUILD_MODE);
        setItem(27, ADVERTISEMENT);
        setItem(16, planet.getSharing() == Planet.Sharing.PUBLIC ? OPENED : CLOSED);
        setItem(35, DELETE);
    }

    @Override
    public void onClick(@NotNull InventoryClickEvent event) {
        event.setCancelled(true);
        if (!isPlayerClicked(event) || !isClickedInMenuSlots(event)) return;
        ItemStack item = event.getCurrentItem();
        if (item == null) return;
        Player player = (Player) event.getWhoClicked();
        switch (getItemType(item)) {
            case "connect" -> {
                player.closeInventory();
                if (planet.equals(OpenCreative.getPlanetsManager().getPlanetByPlayer(player))) {
                    player.sendMessage(getPlayerLocaleMessage("same-world", player));
                    Sounds.PLAYER_FAIL.play(player);
                    return;
                }
                planet.connectPlayer(player);
            }
            case "play" -> {
                if (planet.equals(OpenCreative.getPlanetsManager().getPlanetByPlayer(player))) {
                    player.closeInventory();
                    player.sendMessage(MessageUtils.getPlayerLocaleMessage("same-world", player));
                    Sounds.PLAYER_FAIL.play(player);
                    return;
                }
                if (player.hasCooldown(item.getType()) || getCooldown(player, CooldownUtils.CooldownType.GENERIC_COMMAND) > 0) {
                    Sounds.PLAYER_FAIL.play(player);
                    return;
                }
                player.setCooldown(BUILD_MODE.getType(), OpenCreative.getSettings().getGroups().getGroup(player).getGenericCommandCooldown() * 20);
                PlanetModeChangeEvent planetEvent = new PlanetModeChangeEvent(planet, planet.getMode(), Planet.Mode.BUILD);
                planetEvent.callEvent();
                if (planetEvent.isCancelled()) {
                    Sounds.PLAYER_FAIL.play(player);
                    return;
                }
                Sounds.WORLD_MODE_BUILD.play(player);
                planet.setMode(Planet.Mode.BUILD);
                fillItems(player);
            }
            case "build" -> {
                if (player.hasCooldown(item.getType()) || getCooldown(player, CooldownUtils.CooldownType.GENERIC_COMMAND) > 0) {
                    Sounds.PLAYER_FAIL.play(player);
                    return;
                }
                player.setCooldown(PLAY_MODE.getType(), OpenCreative.getSettings().getGroups().getGroup(player).getGenericCommandCooldown() * 20);
                PlanetModeChangeEvent planetEvent = new PlanetModeChangeEvent(planet, planet.getMode(), Planet.Mode.PLAYING, player);
                planetEvent.callEvent();
                if (planetEvent.isCancelled()) {
                    Sounds.PLAYER_FAIL.play(player);
                    return;
                }
                Sounds.WORLD_MODE_BUILD.play(player);
                planet.setMode(Planet.Mode.PLAYING);
                fillItems(player);
            }
            case "ad" -> {
                if (player.hasCooldown(item.getType()) || getCooldown(player, CooldownUtils.CooldownType.ADVERTISEMENT_COMMAND) > 0) {
                    Sounds.PLAYER_FAIL.play(player);
                    return;
                }
                if (planet.getSharing() != Planet.Sharing.PUBLIC) {
                    player.sendMessage(getLocaleMessage("advertisement.closed-world"));
                    Sounds.PLAYER_FAIL.play(player);
                    player.setCooldown(item.getType(), OpenCreative.getSettings().getGroups().getGroup(player).getGenericCommandCooldown() * 20);
                    return;
                }
                PlanetAdvertisementEvent planetEvent = new PlanetAdvertisementEvent(planet, player);
                planetEvent.callEvent();
                if (planetEvent.isCancelled()) {
                    Sounds.PLAYER_FAIL.play(player);
                    return;
                }
                if (OpenCreative.getEconomy().isEnabled()) {
                    double playerBalance = OpenCreative.getEconomy().getBalance(player).doubleValue();
                    double advertisementPrice = OpenCreative.getSettings().getGroups().getGroup(player).getAdvertisementPrice();
                    if (playerBalance < advertisementPrice) {
                        player.sendMessage(getPlayerLocaleMessage("advertisement.no-money", player)
                                .replace("%money%", String.valueOf(Math.round(advertisementPrice - playerBalance))));
                        Sounds.PLAYER_FAIL.play(player);
                        player.setCooldown(item.getType(), OpenCreative.getSettings().getGroups().getGroup(player).getGenericCommandCooldown() * 20);
                        return;
                    }
                }
                player.setCooldown(item.getType(), OpenCreative.getSettings().getGroups().getGroup(player).getAdvertisementCooldown() * 20);
                AdvertisementCommand.handlePlanetAdvertisement(player, planet);
            }
            case "open" -> {
                if (player.hasCooldown(item.getType()) || getCooldown(player, CooldownUtils.CooldownType.GENERIC_COMMAND) > 0) {
                    Sounds.PLAYER_FAIL.play(player);
                    return;
                }
                player.setCooldown(OPENED.getType(), OpenCreative.getSettings().getGroups().getGroup(player).getGenericCommandCooldown() * 20);
                PlanetSharingChangeEvent planetEvent = new PlanetSharingChangeEvent(planet, planet.getSharing(), Planet.Sharing.PUBLIC);
                planetEvent.callEvent();
                if (planetEvent.isCancelled()) {
                    Sounds.PLAYER_FAIL.play(player);
                    return;
                }
                Sounds.WORLD_SETTINGS_SHARING_PUBLIC.play(player);
                planet.setSharing(Planet.Sharing.PUBLIC);
                fillItems(player);
            }
            case "close" -> {
                if (player.hasCooldown(item.getType()) || getCooldown(player, CooldownUtils.CooldownType.GENERIC_COMMAND) > 0) {
                    Sounds.PLAYER_FAIL.play(player);
                    return;
                }
                player.setCooldown(CLOSED.getType(), OpenCreative.getSettings().getGroups().getGroup(player).getGenericCommandCooldown() * 20);
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
            case "delete" -> {
                player.closeInventory();
                Bukkit.getScheduler().scheduleSyncDelayedTask(OpenCreative.getPlugin(),
                        () -> new ConfirmationMenu(
                                getLocaleMessage("menus.confirmation.delete-world", false).replace("%name%", substring(ChatColor.stripColor(planet.getInformation().getDisplayName()), 20)),
                                Material.TNT,
                                getLocaleItemName("menus.confirmation.items.delete-world.name"),
                                getLocaleItemDescription("menus.confirmation.items.delete-world.lore"),
                                new BukkitRunnable() {
                                    @Override
                                    public void run() {
                                        player.closeInventory();
                                        if (!OpenCreative.getPlanetsManager().getPlanets().contains(planet)) {
                                            cancel();
                                            return;
                                        }
                                        if (!planet.isOwner(player)) {
                                            cancel();
                                            return;
                                        }
                                        Sounds.WORLD_DELETION.play(player);
                                        OpenCreative.getPlugin().getLogger().info("Planet " + planet.getId() + " is being deleted by world's owner " + player.getName());
                                        OpenCreative.getPlanetsManager().deletePlanet(planet);
                                        Bukkit.getServer().getScheduler().runTaskLater(OpenCreative.getPlugin(), () -> player.sendMessage(MessageUtils.getLocaleMessage("deleting-world.message")), 60);
                                    }
                                }).open(player), 5L);

            }
        }
    }

    @Override
    public void onOpen(@NotNull InventoryOpenEvent event) {
        Sounds.MENU_OPEN_WORLD_ACCESS.play(event.getPlayer());
    }

    @Override
    public Planet getPlanet() {
        return planet;
    }
}
