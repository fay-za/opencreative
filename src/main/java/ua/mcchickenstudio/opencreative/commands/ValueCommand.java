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

package ua.mcchickenstudio.opencreative.commands;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import ua.mcchickenstudio.opencreative.OpenCreative;
import ua.mcchickenstudio.opencreative.coding.values.EventValue;
import ua.mcchickenstudio.opencreative.coding.values.EventValues;
import ua.mcchickenstudio.opencreative.coding.variables.ValueType;
import ua.mcchickenstudio.opencreative.coding.variables.VariableLink;
import ua.mcchickenstudio.opencreative.planets.DevPlanet;
import ua.mcchickenstudio.opencreative.planets.Planet;
import ua.mcchickenstudio.opencreative.settings.Sounds;
import ua.mcchickenstudio.opencreative.utils.CooldownUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static ua.mcchickenstudio.opencreative.listeners.player.InteractListener.formatLocation;
import static ua.mcchickenstudio.opencreative.utils.BlockUtils.isOutOfBorders;
import static ua.mcchickenstudio.opencreative.utils.CooldownUtils.checkAndSetCooldownWithMessage;
import static ua.mcchickenstudio.opencreative.utils.ItemUtils.*;
import static ua.mcchickenstudio.opencreative.utils.MessageUtils.getLocaleMessage;

/**
 * <h1>ValueCommand</h1>
 * This command allows players to get coding values, like Text,
 * Number, Variable, Location, Vector, Event Value with already
 * set values.
 * <p>
 * Available: For world developers in developers world.
 */
public class ValueCommand extends CommandHandler {

    @Override
    public void onExecute(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage(getLocaleMessage("only-players"));
            return;
        }
        if (!checkAndSetCooldownWithMessage(player, CooldownUtils.CooldownType.GENERIC_COMMAND)) return;
        DevPlanet planet = OpenCreative.getPlanetsManager().getDevPlanet(player);
        if (planet == null) {
            player.sendMessage(getLocaleMessage("only-in-dev-world"));
            return;
        }
        ItemStack itemStack = null;
        switch (label.toLowerCase()) {
            case "text" -> {
                itemStack = createItem(Material.BOOK, 1, "menus.developer.variables.items.text");
                if (args.length != 0) {
                    setDisplayName(itemStack, ChatColor.translateAlternateColorCodes('&', String.join(" ", args)));
                }
            }
            case "num", "number" -> {
                itemStack = createItem(Material.SLIME_BALL, 1, "menus.developer.variables.items.number");
                if (args.length > 0) {
                    double number = 0.0d;
                    try {
                        number = Double.parseDouble(args[0]);
                    } catch (NumberFormatException ignored) {
                    }
                    if (args[0].equalsIgnoreCase("p") || args[0].equalsIgnoreCase("pi")) {
                        number = 3.1415926d;
                    }
                    setDisplayName(itemStack, "§a" + number);
                }
            }
            case "loc", "location" -> {
                itemStack = createItem(Material.PAPER, 1, "menus.developer.variables.items.location");
                try {
                    double x = 0;
                    double y = 0;
                    double z = 0;
                    float yaw = 0;
                    float pitch = 0;
                    if (args.length >= 3) {
                        x = Double.parseDouble(args[0]);
                        y = Double.parseDouble(args[1]);
                        z = Double.parseDouble(args[2]);
                    }
                    if (args.length >= 5) {
                        yaw = Float.parseFloat(args[3]);
                        pitch = Float.parseFloat(args[4]);
                    }
                    World world = planet.getPlanet().getWorld();
                    if (world == null) break;
                    Location location = new Location(world, x, y, z, yaw, pitch);
                    if (isOutOfBorders(location)) break;
                    setDisplayName(itemStack, formatLocation(location));
                } catch (Exception ignored) {
                }
            }
            case "vector" -> {
                itemStack = createItem(Material.PRISMARINE_SHARD, 1, "menus.developer.variables.items.vector");
                try {
                    double x = 0;
                    double y = 0;
                    double z = 0;
                    if (args.length >= 3) {
                        x = Double.parseDouble(args[0]);
                        y = Double.parseDouble(args[1]);
                        z = Double.parseDouble(args[2]);
                    }
                    setDisplayName(itemStack, "§b" + x + " " + y + " " + z);
                } catch (Exception ignored) {
                }
            }
            case "bool", "boolean" -> {
                itemStack = createItem(Material.CLOCK, 1, "menus.developer.variables.items.boolean");
                if (args.length > 0) {
                    boolean value = Boolean.parseBoolean(args[0]);
                    setDisplayName(itemStack, (value ? "§a" : "§c") + value);
                }
            }
            case "value", "eventvalue", "gamevalue", "worldvalue" -> {
                itemStack = createItem(Material.NAME_TAG, 1, "menus.developer.variables.items.event-value");
                if (args.length > 0) {
                    try {
                        EventValue value = EventValues.getInstance().getById(args[0]);
                        if (value == null) return;
                        setDisplayName(itemStack, value.getLocaleName());
                        setPersistentData(itemStack, getCodingVariableTypeKey(), args[0].toUpperCase());
                    } catch (Exception ignored) {
                    }
                }
            }
            case "var", "variable" -> {
                itemStack = createItem(Material.MAGMA_CREAM, 1, "menus.developer.variables.items.variable");
                if (args.length > 0) {
                    VariableLink.VariableType type = VariableLink.VariableType.getEnum(args[0]);
                    if (type == null) break;
                    setPersistentData(itemStack, getCodingVariableTypeKey(), type.name());
                    if (args.length > 1) {
                        setDisplayName(itemStack,
                                (type == VariableLink.VariableType.SAVED ? "§a" :
                                        type == VariableLink.VariableType.GLOBAL ? "§e" : "§c")
                                        + String.join(" ", Arrays.stream(args).toList().subList(1, args.length)));
                    }
                }
            }
        }
        if (itemStack != null) {
            ValueType valueType = ValueType.getByMaterial(itemStack.getType());
            if (valueType == null) valueType = ValueType.TEXT;
            setPersistentData(itemStack, getCodingValueKey(), valueType.name());
            Sounds.DEV_TAKE_VALUE.play(player);
            player.getInventory().addItem(itemStack);
        }
    }

    @Override
    public @Nullable List<String> onTab(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (!(sender instanceof Player player)) {
            return null;
        }
        Planet planet = OpenCreative.getPlanetsManager().getPlanetByPlayer(player);
        if (planet == null) return null;
        if (!planet.getWorldPlayers().canDevelop(player)) return null;
        List<String> completer = new ArrayList<>();
        switch (label.toLowerCase()) {
            case "number", "num" -> {
                if (args.length != 1) return null;
                completer.addAll(List.of("1", "100", "50", "25"));
            }
            case "boolean", "bool" -> {
                if (args.length != 1) return null;
                completer.addAll(List.of("true", "false"));
            }
            case "variable", "var" -> {
                if (args.length == 1) {
                    completer.addAll(List.of("global", "saved", "local"));
                }
            }
            case "eventvalue", "gamevalue", "worldvalue", "value" -> {
                if (args.length != 1) return null;
                completer.addAll(EventValues.getInstance().getEventValues().stream()
                        .map(EventValue::getID)
                        .filter(name -> name.startsWith(args[0])).toList());
            }
        }
        return completer;
    }
}
