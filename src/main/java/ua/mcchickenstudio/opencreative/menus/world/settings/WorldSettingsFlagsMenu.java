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

import org.bukkit.GameRule;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import ua.mcchickenstudio.opencreative.OpenCreative;
import ua.mcchickenstudio.opencreative.menus.AbstractMenu;
import ua.mcchickenstudio.opencreative.menus.buttons.RadioButton;
import ua.mcchickenstudio.opencreative.planets.Planet;
import ua.mcchickenstudio.opencreative.planets.PlanetFlags;
import ua.mcchickenstudio.opencreative.settings.Sounds;
import ua.mcchickenstudio.opencreative.utils.MessageUtils;

import java.util.ArrayList;
import java.util.List;

import static ua.mcchickenstudio.opencreative.utils.ItemUtils.createItem;
import static ua.mcchickenstudio.opencreative.utils.ItemUtils.itemEquals;

public final class WorldSettingsFlagsMenu extends AbstractMenu {

    private final ItemStack BACK_ITEM = createItem(Material.ARROW, 1, "menus.world-settings-flags.items.back");

    public WorldSettingsFlagsMenu() {
        super(6, MessageUtils.getLocaleMessage("menus.world-settings.title"));
    }

    public static RadioButton getPlayerDamageFlagButton(Planet planet) {
        List<Runnable> choicesActions = new ArrayList<>();
        choicesActions.add(() -> planet.setFlagValue(PlanetFlags.PlanetFlag.PLAYER_DAMAGE, (byte) 1));
        choicesActions.add(() -> planet.setFlagValue(PlanetFlags.PlanetFlag.PLAYER_DAMAGE, (byte) 2));
        choicesActions.add(() -> planet.setFlagValue(PlanetFlags.PlanetFlag.PLAYER_DAMAGE, (byte) 3));
        choicesActions.add(() -> planet.setFlagValue(PlanetFlags.PlanetFlag.PLAYER_DAMAGE, (byte) 4));
        choicesActions.add(() -> planet.setFlagValue(PlanetFlags.PlanetFlag.PLAYER_DAMAGE, (byte) 5));
        return new RadioButton(Material.TOTEM_OF_UNDYING, MessageUtils.getLocaleItemName("menus.world-settings-flags.items.player-damage.name"), MessageUtils.getLocaleItemDescription("menus.world-settings-flags.items.player-damage.lore"), planet.getFlagValue(PlanetFlags.PlanetFlag.PLAYER_DAMAGE), 5, choicesActions, "menus.world-settings-flags.items.player-damage.choices", "menus.world-settings-flags");
    }

    public static RadioButton getMobInteractFlagButton(Planet planet) {
        List<Runnable> choicesActions = new ArrayList<>();
        choicesActions.add(() -> planet.setFlagValue(PlanetFlags.PlanetFlag.MOB_INTERACT, (byte) 1));
        choicesActions.add(() -> planet.setFlagValue(PlanetFlags.PlanetFlag.MOB_INTERACT, (byte) 2));
        choicesActions.add(() -> planet.setFlagValue(PlanetFlags.PlanetFlag.MOB_INTERACT, (byte) 3));
        return new RadioButton(Material.VILLAGER_SPAWN_EGG, MessageUtils.getLocaleItemName("menus.world-settings-flags.items.mob-interact.name"), MessageUtils.getLocaleItemDescription("menus.world-settings-flags.items.mob-interact.lore"), planet.getFlagValue(PlanetFlags.PlanetFlag.MOB_INTERACT), 3, choicesActions, "menus.world-settings-flags.items.mob-interact.choices", "menus.world-settings-flags");
    }

    public static RadioButton getMobLootFlagButton(Planet planet) {
        List<Runnable> choicesActions = new ArrayList<>();
        choicesActions.add(() -> {
            planet.getTerritory().getWorld().setGameRule(GameRule.DO_MOB_LOOT, true);
            planet.setFlagValue(PlanetFlags.PlanetFlag.MOB_LOOT, (byte) 1);
        });
        choicesActions.add(() -> {
            planet.getTerritory().getWorld().setGameRule(GameRule.DO_MOB_LOOT, false);
            planet.setFlagValue(PlanetFlags.PlanetFlag.MOB_LOOT, (byte) 2);
        });
        return new RadioButton(Material.FEATHER, MessageUtils.getLocaleItemName("menus.world-settings-flags.items.mob-loot.name"), MessageUtils.getLocaleItemDescription("menus.world-settings-flags.items.mob-loot.lore"), planet.getFlagValue(PlanetFlags.PlanetFlag.MOB_LOOT), 2, choicesActions, "menus.world-settings-flags.items.mob-loot.choices", "menus.world-settings-flags");
    }

    public static RadioButton getKeepInventoryFlagButton(Planet planet) {
        List<Runnable> choicesActions = new ArrayList<>();
        choicesActions.add(() -> {
            planet.getTerritory().getWorld().setGameRule(GameRule.KEEP_INVENTORY, false);
            planet.setFlagValue(PlanetFlags.PlanetFlag.KEEP_INVENTORY, (byte) 1);
        });
        choicesActions.add(() -> {
            planet.getTerritory().getWorld().setGameRule(GameRule.KEEP_INVENTORY, true);
            planet.setFlagValue(PlanetFlags.PlanetFlag.KEEP_INVENTORY, (byte) 2);
        });
        return new RadioButton(Material.CHEST_MINECART, MessageUtils.getLocaleItemName("menus.world-settings-flags.items.keep-inventory.name"), MessageUtils.getLocaleItemDescription("menus.world-settings-flags.items.keep-inventory.lore"), planet.getFlagValue(PlanetFlags.PlanetFlag.KEEP_INVENTORY), 2, choicesActions, "menus.world-settings-flags.items.keep-inventory.choices", "menus.world-settings-flags");
    }

    public static RadioButton getNaturalRegenerationFlagButton(Planet planet) {
        List<Runnable> choicesActions = new ArrayList<>();
        choicesActions.add(() -> {
            planet.getTerritory().getWorld().setGameRule(GameRule.NATURAL_REGENERATION, true);
            planet.setFlagValue(PlanetFlags.PlanetFlag.NATURAL_REGENERATION, (byte) 1);
        });
        choicesActions.add(() -> {
            planet.getTerritory().getWorld().setGameRule(GameRule.NATURAL_REGENERATION, false);
            planet.setFlagValue(PlanetFlags.PlanetFlag.NATURAL_REGENERATION, (byte) 2);
        });
        return new RadioButton(Material.POTION, MessageUtils.getLocaleItemName("menus.world-settings-flags.items.natural-regeneration.name"), MessageUtils.getLocaleItemDescription("menus.world-settings-flags.items.natural-regeneration.lore"), planet.getFlagValue(PlanetFlags.PlanetFlag.NATURAL_REGENERATION), 2, choicesActions, "menus.world-settings-flags.items.natural-regeneration.choices", "menus.world-settings-flags");
    }

    public static RadioButton getBlockChangingFlagButton(Planet planet) {
        List<Runnable> choicesActions = new ArrayList<>();
        choicesActions.add(() -> planet.setFlagValue(PlanetFlags.PlanetFlag.BLOCK_CHANGING, (byte) 1));
        choicesActions.add(() -> planet.setFlagValue(PlanetFlags.PlanetFlag.BLOCK_CHANGING, (byte) 2));
        return new RadioButton(Material.ICE, MessageUtils.getLocaleItemName("menus.world-settings-flags.items.block-changing.name"), MessageUtils.getLocaleItemDescription("menus.world-settings-flags.items.block-changing.lore"), planet.getFlagValue(PlanetFlags.PlanetFlag.BLOCK_CHANGING), 2, choicesActions, "menus.world-settings-flags.items.block-changing.choices", "menus.world-settings-flags");
    }

    public static RadioButton getImmediateRespawnFlagButton(Planet planet) {
        List<Runnable> choicesActions = new ArrayList<>();
        choicesActions.add(() -> {
            planet.getTerritory().getWorld().setGameRule(GameRule.DO_IMMEDIATE_RESPAWN, false);
            planet.setFlagValue(PlanetFlags.PlanetFlag.IMMEDIATE_RESPAWN, (byte) 1);
        });
        choicesActions.add(() -> {
            planet.getTerritory().getWorld().setGameRule(GameRule.DO_IMMEDIATE_RESPAWN, true);
            planet.setFlagValue(PlanetFlags.PlanetFlag.IMMEDIATE_RESPAWN, (byte) 2);
        });
        return new RadioButton(Material.SKELETON_SKULL, MessageUtils.getLocaleItemName("menus.world-settings-flags.items.immediate-respawn.name"), MessageUtils.getLocaleItemDescription("menus.world-settings-flags.items.immediate-respawn.lore"), planet.getFlagValue(PlanetFlags.PlanetFlag.IMMEDIATE_RESPAWN), 2, choicesActions, "menus.world-settings-flags.items.immediate-respawn.choices", "menus.world-settings-flags");
    }

    public static RadioButton getLocatorBarButton(Planet planet) {
        List<Runnable> choicesActions = new ArrayList<>();
        choicesActions.add(() -> {
            planet.getTerritory().setGameRuleIfExists("locator_bar", true);
            planet.setFlagValue(PlanetFlags.PlanetFlag.LOCATOR_BAR, (byte) 1);
        });
        choicesActions.add(() -> {
            planet.getTerritory().setGameRuleIfExists("locator_bar", false);
            planet.setFlagValue(PlanetFlags.PlanetFlag.LOCATOR_BAR, (byte) 2);
        });
        return new RadioButton(Material.ENDER_EYE, MessageUtils.getLocaleItemName("menus.world-settings-flags.items.locator-bar.name"),
                MessageUtils.getLocaleItemDescription("menus.world-settings-flags.items.locator-bar.lore"),
                planet.getFlagValue(PlanetFlags.PlanetFlag.LOCATOR_BAR), 2, choicesActions, "menus.world-settings-flags.items.locator-bar.choices", "menus.world-settings-flags");
    }

    public static RadioButton getDeathMessagesFlagButton(Planet planet) {
        List<Runnable> choicesActions = new ArrayList<>();
        choicesActions.add(() -> planet.setFlagValue(PlanetFlags.PlanetFlag.DEATH_MESSAGES, (byte) 1));
        choicesActions.add(() -> planet.setFlagValue(PlanetFlags.PlanetFlag.DEATH_MESSAGES, (byte) 2));
        return new RadioButton(Material.WITHER_SKELETON_SKULL, MessageUtils.getLocaleItemName("menus.world-settings-flags.items.death-messages.name"), MessageUtils.getLocaleItemDescription("menus.world-settings-flags.items.death-messages.lore"), planet.getFlagValue(PlanetFlags.PlanetFlag.DEATH_MESSAGES), 2, choicesActions, "menus.world-settings-flags.items.death-messages.choices", "menus.world-settings-flags");
    }

    public static RadioButton getLikeMessagesFlagButton(Planet planet) {
        List<Runnable> choicesActions = new ArrayList<>();
        choicesActions.add(() -> planet.setFlagValue(PlanetFlags.PlanetFlag.LIKE_MESSAGES, (byte) 1));
        choicesActions.add(() -> planet.setFlagValue(PlanetFlags.PlanetFlag.LIKE_MESSAGES, (byte) 2));
        return new RadioButton(Material.KNOWLEDGE_BOOK, MessageUtils.getLocaleItemName("menus.world-settings-flags.items.like-messages.name"), MessageUtils.getLocaleItemDescription("menus.world-settings-flags.items.like-messages.lore"), planet.getFlagValue(PlanetFlags.PlanetFlag.LIKE_MESSAGES), 2, choicesActions, "menus.world-settings-flags.items.like-messages.choices", "menus.world-settings-flags");
    }

    public static RadioButton getMobSpawnFlagButton(Planet planet) {
        List<Runnable> choicesActions = new ArrayList<>();
        choicesActions.add(() -> {
            planet.getTerritory().getWorld().setGameRule(GameRule.DO_MOB_SPAWNING, false);
            planet.setFlagValue(PlanetFlags.PlanetFlag.MOB_SPAWN, (byte) 1);
        });
        choicesActions.add(() -> {
            planet.getTerritory().getWorld().setGameRule(GameRule.DO_MOB_SPAWNING, true);
            planet.setFlagValue(PlanetFlags.PlanetFlag.MOB_SPAWN, (byte) 2);
        });
        choicesActions.add(() -> {
            planet.getTerritory().getWorld().setGameRule(GameRule.DO_MOB_SPAWNING, true);
            planet.setFlagValue(PlanetFlags.PlanetFlag.MOB_SPAWN, (byte) 3);
        });
        choicesActions.add(() -> {
            planet.getTerritory().getWorld().setGameRule(GameRule.DO_MOB_SPAWNING, true);
            planet.setFlagValue(PlanetFlags.PlanetFlag.MOB_SPAWN, (byte) 4);
        });
        choicesActions.add(() -> {
            planet.getTerritory().getWorld().setGameRule(GameRule.DO_MOB_SPAWNING, true);
            planet.setFlagValue(PlanetFlags.PlanetFlag.MOB_SPAWN, (byte) 5);
        });
        return new RadioButton(Material.EGG, MessageUtils.getLocaleItemName("menus.world-settings-flags.items.mob-spawn.name"), MessageUtils.getLocaleItemDescription("menus.world-settings-flags.items.mob-spawn.lore"), planet.getFlagValue(PlanetFlags.PlanetFlag.MOB_SPAWN), 5, choicesActions, "menus.world-settings-flags.items.mob-spawn.choices", "menus.world-settings-flags");
    }

    public static RadioButton getWorldBordersButton(Planet planet) {
        List<Runnable> choicesActions = new ArrayList<>();
        choicesActions.add(() -> {
            planet.setFlagValue(PlanetFlags.PlanetFlag.WORLD_BORDERS, (byte) 1);
            for (Player player : planet.getPlayers()) {
                planet.getTerritory().showBorders(player);
            }
        });
        choicesActions.add(() -> {
            planet.setFlagValue(PlanetFlags.PlanetFlag.WORLD_BORDERS, (byte) 2);
            for (Player player : planet.getPlayers()) {
                planet.getTerritory().showBorders(player);
            }
        });
        choicesActions.add(() -> {
            planet.setFlagValue(PlanetFlags.PlanetFlag.WORLD_BORDERS, (byte) 3);
            for (Player player : planet.getPlayers()) {
                planet.getTerritory().showBorders(player);
            }
        });
        choicesActions.add(() -> {
            planet.setFlagValue(PlanetFlags.PlanetFlag.WORLD_BORDERS, (byte) 4);
            for (Player player : planet.getPlayers()) {
                planet.getTerritory().showBorders(player);
            }
        });
        return new RadioButton(Material.LIGHT_BLUE_STAINED_GLASS, MessageUtils.getLocaleItemName("menus.world-settings-flags.items.world-borders.name"), MessageUtils.getLocaleItemDescription("menus.world-settings-flags.items.world-borders.lore"), planet.getFlagValue(PlanetFlags.PlanetFlag.WORLD_BORDERS), 4, choicesActions, "menus.world-settings-flags.items.world-borders.choices", "menus.world-settings-flags");
    }

    public static RadioButton getBlockExplosionFlagButton(Planet planet) {
        List<Runnable> choicesActions = new ArrayList<>();
        choicesActions.add(() -> {
            planet.getTerritory().getWorld().setGameRule(GameRule.MOB_GRIEFING, true);
            planet.setFlagValue(PlanetFlags.PlanetFlag.BLOCK_EXPLOSION, (byte) 1);
        });
        choicesActions.add(() -> {
            planet.getTerritory().getWorld().setGameRule(GameRule.MOB_GRIEFING, false);
            planet.setFlagValue(PlanetFlags.PlanetFlag.BLOCK_EXPLOSION, (byte) 2);
        });
        return new RadioButton(Material.TNT, MessageUtils.getLocaleItemName("menus.world-settings-flags.items.block-explosion.name"), MessageUtils.getLocaleItemDescription("menus.world-settings-flags.items.block-explosion.lore"), planet.getFlagValue(PlanetFlags.PlanetFlag.BLOCK_EXPLOSION), 2, choicesActions, "menus.world-settings-flags.items.block-explosion.choices", "menus.world-settings-flags");
    }

    public static RadioButton FireSpreadButton(Planet planet) {
        List<Runnable> choicesActions = new ArrayList<>();
        choicesActions.add(() -> {
            planet.getTerritory().getWorld().setGameRule(GameRule.DO_FIRE_TICK, true);
            planet.setFlagValue(PlanetFlags.PlanetFlag.FIRE_SPREAD, (byte) 1);
        });
        choicesActions.add(() -> {
            planet.getTerritory().getWorld().setGameRule(GameRule.DO_FIRE_TICK, false);
            planet.setFlagValue(PlanetFlags.PlanetFlag.FIRE_SPREAD, (byte) 2);
        });
        return new RadioButton(Material.CAMPFIRE, MessageUtils.getLocaleItemName("menus.world-settings-flags.items.fire-spread.name"), MessageUtils.getLocaleItemDescription("menus.world-settings-flags.items.fire-spread.lore"), planet.getFlagValue(PlanetFlags.PlanetFlag.FIRE_SPREAD), 2, choicesActions, "menus.world-settings-flags.items.fire-spread.choices", "menus.world-settings-flags");
    }

    public static RadioButton getJoinQuitMessagesFlagButton(Planet planet) {
        List<Runnable> choicesActions = new ArrayList<>();
        choicesActions.add(() -> planet.setFlagValue(PlanetFlags.PlanetFlag.JOIN_MESSAGES, (byte) 1));
        choicesActions.add(() -> planet.setFlagValue(PlanetFlags.PlanetFlag.JOIN_MESSAGES, (byte) 2));
        return new RadioButton(Material.OAK_SIGN, MessageUtils.getLocaleItemName("menus.world-settings-flags.items.join-messages.name"), MessageUtils.getLocaleItemDescription("menus.world-settings-flags.items.join-messages.lore"), planet.getFlagValue(PlanetFlags.PlanetFlag.JOIN_MESSAGES), 2, choicesActions, "menus.world-settings-flags.items.join-messages.choices", "menus.world-settings-flags");
    }

    public static RadioButton getBlockInteractFlagButton(Planet planet) {
        List<Runnable> choicesActions = new ArrayList<>();
        choicesActions.add(() -> planet.setFlagValue(PlanetFlags.PlanetFlag.BLOCK_INTERACT, (byte) 1));
        choicesActions.add(() -> planet.setFlagValue(PlanetFlags.PlanetFlag.BLOCK_INTERACT, (byte) 2));
        choicesActions.add(() -> planet.setFlagValue(PlanetFlags.PlanetFlag.BLOCK_INTERACT, (byte) 3));
        choicesActions.add(() -> planet.setFlagValue(PlanetFlags.PlanetFlag.BLOCK_INTERACT, (byte) 4));
        choicesActions.add(() -> planet.setFlagValue(PlanetFlags.PlanetFlag.BLOCK_INTERACT, (byte) 5));
        return new RadioButton(Material.CHEST, MessageUtils.getLocaleItemName("menus.world-settings-flags.items.block-interact.name"), MessageUtils.getLocaleItemDescription("menus.world-settings-flags.items.block-interact.lore"), planet.getFlagValue(PlanetFlags.PlanetFlag.BLOCK_INTERACT), 5, choicesActions, "menus.world-settings-flags.items.block-interact.choices", "menus.world-settings-flags");
    }

    public static RadioButton getWeatherFlagButton(Planet planet) {
        List<Runnable> choicesActions = getRunnables(planet);
        Boolean isWeatherChanging = planet.getTerritory().getWorld().getGameRuleValue(GameRule.DO_WEATHER_CYCLE);
        int currentValue = (isWeatherChanging != null && isWeatherChanging ? 3 : planet.getTerritory().getWorld().hasStorm() ? 2 : 1);
        return new RadioButton(Material.WATER_BUCKET, MessageUtils.getLocaleItemName("menus.world-settings-flags.items.weather.name"), MessageUtils.getLocaleItemDescription("menus.world-settings-flags.items.weather.lore"), currentValue, 3, choicesActions, "menus.world-settings-flags.items.weather.choices", "menus.world-settings-flags");
    }

    private static List<Runnable> getRunnables(Planet planet) {
        List<Runnable> choicesActions = new ArrayList<>();
        choicesActions.add(() -> {
            planet.getTerritory().getWorld().setGameRule(GameRule.DO_WEATHER_CYCLE, false);
            planet.getTerritory().getWorld().setStorm(false);
            planet.setFlagValue(PlanetFlags.PlanetFlag.WEATHER, (byte) 1);
        });
        choicesActions.add(() -> {
            planet.getTerritory().getWorld().setGameRule(GameRule.DO_WEATHER_CYCLE, false);
            planet.getTerritory().getWorld().setStorm(true);
            planet.setFlagValue(PlanetFlags.PlanetFlag.WEATHER, (byte) 2);
        });
        choicesActions.add(() -> {
            planet.getTerritory().getWorld().setStorm(false);
            planet.getTerritory().getWorld().setGameRule(GameRule.DO_WEATHER_CYCLE, true);
            planet.setFlagValue(PlanetFlags.PlanetFlag.WEATHER, (byte) 3);
        });
        return choicesActions;
    }

    @Override
    public void fillItems(Player player) {
        Planet planet = OpenCreative.getPlanetsManager().getPlanetByPlayer(player);
        setItem(46, BACK_ITEM);
        setItem(DECORATION_PANE_ITEM, 45, 47, 48, 49, 50, 51, 52, 53);
        if (planet == null) return;
        if (!planet.isOwner(player.getName())) return;
        setItem(10, getPlayerDamageFlagButton(planet).getButtonItem());
        setItem(11, getBlockExplosionFlagButton(planet).getButtonItem());
        setItem(12, getBlockInteractFlagButton(planet).getButtonItem());
        setItem(13, FireSpreadButton(planet).getButtonItem());
        setItem(14, getMobInteractFlagButton(planet).getButtonItem());
        setItem(15, getWeatherFlagButton(planet).getButtonItem());
        setItem(16, getKeepInventoryFlagButton(planet).getButtonItem());
        setItem(19, getMobSpawnFlagButton(planet).getButtonItem());
        setItem(20, getImmediateRespawnFlagButton(planet).getButtonItem());
        setItem(21, getJoinQuitMessagesFlagButton(planet).getButtonItem());
        setItem(22, getDeathMessagesFlagButton(planet).getButtonItem());
        setItem(23, getLikeMessagesFlagButton(planet).getButtonItem());
        setItem(24, getBlockChangingFlagButton(planet).getButtonItem());
        setItem(25, getNaturalRegenerationFlagButton(planet).getButtonItem());
        setItem(28, getMobLootFlagButton(planet).getButtonItem());
        setItem(29, getWorldBordersButton(planet).getButtonItem());
        if (GameRule.getByName("locator_bar") != null) {
            setItem(30, getLocatorBarButton(planet).getButtonItem());
        }
    }

    @Override
    public void onClick(@NotNull InventoryClickEvent event) {
        event.setCancelled(true);

        if (!isClickedInMenuSlots(event)) return;
        if (event.getCurrentItem() == null) return;
        if (event.getCurrentItem().getType().isAir()) return;
        if (itemEquals(event.getCurrentItem(), DECORATION_PANE_ITEM)) return;

        Planet planet = OpenCreative.getPlanetsManager().getPlanetByPlayer((Player) event.getWhoClicked());
        if (planet == null) return;
        if (event.getCurrentItem().getType() == Material.ARROW) {
            new WorldSettingsMenu(planet, (Player) event.getWhoClicked()).open((Player) event.getWhoClicked());
        } else if (event.getCurrentItem().getType() != Material.AIR) {
            RadioButton rd = RadioButton.getRadioButtonByItemStack(event.getCurrentItem());
            if (rd != null) {
                rd.onChoice();
                Sounds.WORLD_SETTINGS_FLAG_CHANGE.play(event.getWhoClicked());
                new WorldSettingsFlagsMenu().open((Player) event.getWhoClicked());
            }
        }
    }

    @Override
    public void onOpen(@NotNull InventoryOpenEvent event) {
    }
}