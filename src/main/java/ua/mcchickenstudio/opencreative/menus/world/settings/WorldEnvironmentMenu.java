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

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import ua.mcchickenstudio.opencreative.OpenCreative;
import ua.mcchickenstudio.opencreative.menus.AbstractMenu;
import ua.mcchickenstudio.opencreative.menus.buttons.ParameterButton;
import ua.mcchickenstudio.opencreative.menus.world.WorldMenu;
import ua.mcchickenstudio.opencreative.planets.DevPlanet;
import ua.mcchickenstudio.opencreative.planets.DevPlatform;
import ua.mcchickenstudio.opencreative.planets.Planet;
import ua.mcchickenstudio.opencreative.settings.Sounds;

import java.util.List;

import static ua.mcchickenstudio.opencreative.utils.ItemUtils.*;
import static ua.mcchickenstudio.opencreative.utils.MessageUtils.getLocaleMessage;
import static ua.mcchickenstudio.opencreative.utils.world.WorldUtils.isDevPlanet;

public final class WorldEnvironmentMenu extends AbstractMenu implements WorldMenu {

    private final Player player;
    private final DevPlanet devPlanet;
    private final DevPlatform devPlatform;

    private final ItemStack back = createItem(Material.ARROW, 1, "menus.developer.environment.items.back");

    private final ParameterButton debug;

    private final ItemStack variablesSize = createItem(Material.MAGMA_CREAM, 1, "menus.developer.environment.items.variables-size");
    private final ItemStack variablesList = createItem(Material.BOOKSHELF, 1, "menus.developer.environment.items.variables-list");
    private final ItemStack clearVariables = createItem(Material.BRUSH, 1, "menus.developer.environment.items.clear-variables");

    private final ItemStack info;
    private final ItemStack createPlatform;

    private final ParameterButton containers;
    private final ParameterButton time;
    private final ItemStack floorMaterial;
    private final ItemStack eventMaterial;
    private final ItemStack actionMaterial;


    public WorldEnvironmentMenu(Player player, DevPlanet devPlanet) {
        super(6, getLocaleMessage("menus.developer.environment.title"));
        this.player = player;
        this.devPlanet = devPlanet;
        this.devPlatform = isDevPlanet(player.getWorld()) ? devPlanet.getPlatformInLocation(player.getLocation()) : null;
        debug = new ParameterButton(devPlanet.getPlanet().isDebug() ? "all" : "disabled", List.of("disabled", "all"), "debug", "menus.developer.environment", "menus.developer.environment.items.debug", List.of(Material.PUFFERFISH_BUCKET, Material.PUFFERFISH));
        containers = new ParameterButton(devPlanet.getContainerMaterial() == Material.CHEST ? "chest" : "barrel", List.of("chest", "barrel"), "containers", "menus.developer.environment", "menus.developer.environment.items.containers", List.of(Material.CHEST, Material.BARREL));
        info = createInfoItem();
        createPlatform = createPlatformsItem();
        long currentTime = devPlanet.getWorld() == null ? 0 : devPlanet.getWorld().getTime();
        boolean isMorning = currentTime >= 0L && currentTime < 6000L;
        boolean isNight = currentTime >= 15000L && currentTime <= 23000L;
        boolean isEvening = currentTime >= 12500L && currentTime < 15000L;
        time = new ParameterButton(isMorning ? "morning" : isNight ? "night" : isEvening ? "evening" : "day", List.of("morning", "day", "evening", "night"), "time", "menus.developer.environment", "menus.developer.environment.items.time", Material.CLOCK);
        floorMaterial = createItem(devPlatform != null ? devPlatform.getFloorMaterial() : DevPlanet.getDefaultFloorMaterial(), 1, "menus.developer.environment.items.floor-material");
        eventMaterial = createItem(devPlatform != null ? devPlatform.getEventMaterial() : DevPlanet.getDefaultEventMaterial(), 1, "menus.developer.environment.items.event-material");
        actionMaterial = createItem(devPlatform != null ? devPlatform.getActionMaterial() : DevPlanet.getDefaultActionMaterial(), 1, "menus.developer.environment.items.action-material");
    }

    private ItemStack createInfoItem() {
        ItemStack info = createItem(Material.AMETHYST_CLUSTER, 1, "menus.developer.environment.items.info");
        replacePlaceholderInLore(info, "%executors%", devPlanet.getPlanet().getTerritory().getScript().getExecutors().getExecutorsList().size());
        replacePlaceholderInLore(info, "%scoreboards%", devPlanet.getPlanet().getTerritory().getScoreboards().getAmount());
        replacePlaceholderInLore(info, "%scoreboards-limit%", devPlanet.getPlanet().getLimits().getScoreboardsLimit());
        replacePlaceholderInLore(info, "%bossbars%", devPlanet.getPlanet().getTerritory().getBossBars().size());
        replacePlaceholderInLore(info, "%bossbars-limit%", devPlanet.getPlanet().getLimits().getBossBarsLimit());
        replacePlaceholderInLore(info, "%recipes%", devPlanet.getPlanet().getTerritory().getRecipes().getAmount());
        replacePlaceholderInLore(info, "%recipes-limit%", devPlanet.getPlanet().getLimits().getRecipesLimit());
        replacePlaceholderInLore(info, "%variables%", devPlanet.getPlanet().getVariables().getTotalVariablesAmount());
        replacePlaceholderInLore(info, "%variables-limit%", devPlanet.getPlanet().getLimits().getVariablesAmountLimit());
        replacePlaceholderInLore(info, "%executor-calls-limit%", devPlanet.getPlanet().getLimits().getCodeOperationsLimit());
        replacePlaceholderInLore(info, "%planetID%", devPlanet.getPlanet().getId());
        replacePlaceholderInLore(info, "%version%", OpenCreative.getVersion());
        return info;
    }

    private ItemStack createPlatformsItem() {
        if (devPlanet.isLoaded()) {
            int amount = devPlanet.getPlatforms().size();
            int limit = devPlanet.getPlanet().getLimits().getCodingPlatformsLimit();
            ItemStack item = createItem(Material.NETHER_STAR, 1, "menus.developer.environment.items." +
                    (amount >= limit ? "create-platform-limit" : "create-platform"), (amount >= limit ? "" : "platform"));
            replacePlaceholderInLore(item, "%limit%", limit);
            replacePlaceholderInLore(item, "%amount%", amount);
            return item;
        } else {
            return DECORATION_ITEM;
        }
    }

    @Override
    public void fillItems(Player player) {

        setItem(10, debug.getItem());
        setItem(12, variablesSize);
        setItem(14, variablesList);
        setItem(16, clearVariables);

        setItem(28, devPlanet.getWorld() != null ? containers.getItem() : DECORATION_ITEM);
        setItem(30, devPlanet.getWorld() != null ? time.getItem() : DECORATION_ITEM);
        setItem(32, devPlanet.getWorld() != null ? floorMaterial : DECORATION_ITEM);
        setItem(33, devPlanet.getWorld() != null ? eventMaterial : DECORATION_ITEM);
        setItem(34, devPlanet.getWorld() != null ? actionMaterial : DECORATION_ITEM);

        setItem(45, devPlanet.getPlanet().isOwner(player) ? back : DECORATION_PANE_ITEM);
        setItem(46, DECORATION_PANE_ITEM);

        setItem(47, createItem(Material.MAGENTA_STAINED_GLASS_PANE, 1));
        setItem(49, info);
        setItem(51, createItem(Material.MAGENTA_STAINED_GLASS_PANE, 1));
        setItem(52, DECORATION_PANE_ITEM);
        setItem(53, devPlanet.getWorld() != null ? createPlatform : DECORATION_ITEM);
    }

    @Override
    public void onClick(@NotNull InventoryClickEvent event) {
        if (!isClickedInMenuSlots(event) || !isPlayerClicked(event)) {
            return;
        }
        event.setCancelled(true);
        ItemStack currentItem = event.getCurrentItem();
        if (currentItem == null || currentItem.equals(DECORATION_ITEM)) return;
        if (itemEquals(currentItem, variablesList)) {
            player.performCommand("env vars list");
            player.closeInventory();
        } else if (itemEquals(currentItem, variablesSize)) {
            player.performCommand("env vars size");
            player.closeInventory();
        } else if (itemEquals(currentItem, clearVariables)) {
            player.performCommand("env vars clear");
            player.closeInventory();
        } else if (getItemType(currentItem).equals("platform")) {
            player.performCommand("env platform");
            player.closeInventory();
        } else if (itemEquals(currentItem, time.getItem())) {
            if (devPlanet.getWorld() == null) return;
            time.next();
            Sounds.WORLD_SETTINGS_TIME_CHANGE.play(player);
            setItem(event.getRawSlot(), time.getItem());
            if ("night".equals(time.getCurrentValue().toString())) {
                devPlanet.getWorld().setTime(15000L);
            } else if ("evening".equals(time.getCurrentValue().toString())) {
                devPlanet.getWorld().setTime(12500L);
            } else if ("day".equals(time.getCurrentValue().toString())) {
                devPlanet.getWorld().setTime(6000L);
            } else {
                devPlanet.getWorld().setTime(0L);
            }
        } else if (itemEquals(currentItem, containers.getItem())) {
            if (devPlanet.getWorld() == null) return;
            player.performCommand("env barrel");
            player.closeInventory();
        } else if (itemEquals(currentItem, debug.getItem())) {
            player.performCommand("env debug " + (debug.getCurrentValue().toString().equals("all") ? "disable" : "enable"));
            player.closeInventory();
        } else if (itemEquals(currentItem, back)) {
            if (devPlanet.getPlanet().isOwner(player)) {
                new WorldSettingsMenu(devPlanet.getPlanet(), player).open(player);
            }
        } else if (itemEquals(currentItem, eventMaterial)) {
            new WorldEnvironmentColorMenu(player, devPlanet, devPlatform, "event").open(player);
        } else if (itemEquals(currentItem, actionMaterial)) {
            new WorldEnvironmentColorMenu(player, devPlanet, devPlatform, "action").open(player);
        } else if (itemEquals(currentItem, floorMaterial)) {
            new WorldEnvironmentColorMenu(player, devPlanet, devPlatform, "floor").open(player);
        }
    }

    @Override
    public void onOpen(@NotNull InventoryOpenEvent event) {
        Player player = (Player) event.getPlayer();
        Sounds.MENU_OPEN_ENVIRONMENT.play(player);
    }

    @Override
    public Planet getPlanet() {
        return devPlanet.getPlanet();
    }
}
