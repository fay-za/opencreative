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

package ua.mcchickenstudio.opencreative.commands.world;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextReplacementConfig;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.event.HoverEvent;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;
import ua.mcchickenstudio.opencreative.OpenCreative;
import ua.mcchickenstudio.opencreative.coding.CodingBlockPlacer;
import ua.mcchickenstudio.opencreative.coding.blocks.events.player.world.JoinEvent;
import ua.mcchickenstudio.opencreative.coding.blocks.events.player.world.LikeEvent;
import ua.mcchickenstudio.opencreative.coding.blocks.events.player.world.PlayEvent;
import ua.mcchickenstudio.opencreative.coding.blocks.events.player.world.QuitEvent;
import ua.mcchickenstudio.opencreative.coding.blocks.events.world.other.GamePlayEvent;
import ua.mcchickenstudio.opencreative.coding.blocks.executors.PlanetExecutors;
import ua.mcchickenstudio.opencreative.coding.blocks.executors.other.Function;
import ua.mcchickenstudio.opencreative.coding.blocks.executors.other.Method;
import ua.mcchickenstudio.opencreative.coding.prompters.PrompterBadCodeException;
import ua.mcchickenstudio.opencreative.coding.prompters.PrompterDownException;
import ua.mcchickenstudio.opencreative.coding.prompters.PrompterLimitedException;
import ua.mcchickenstudio.opencreative.coding.prompters.UnauthorizedPrompterException;
import ua.mcchickenstudio.opencreative.coding.variables.ValueType;
import ua.mcchickenstudio.opencreative.coding.variables.VariableLink;
import ua.mcchickenstudio.opencreative.coding.variables.WorldVariable;
import ua.mcchickenstudio.opencreative.commands.CommandHandler;
import ua.mcchickenstudio.opencreative.menus.world.settings.WorldEnvironmentMenu;
import ua.mcchickenstudio.opencreative.planets.DevPlanet;
import ua.mcchickenstudio.opencreative.planets.DevPlatform;
import ua.mcchickenstudio.opencreative.planets.Planet;
import ua.mcchickenstudio.opencreative.settings.Sounds;
import ua.mcchickenstudio.opencreative.utils.CooldownUtils;
import ua.mcchickenstudio.opencreative.utils.MessageUtils;
import ua.mcchickenstudio.opencreative.utils.PlayerUtils;

import java.io.StringReader;
import java.net.ConnectException;
import java.net.http.HttpTimeoutException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static ua.mcchickenstudio.opencreative.utils.CooldownUtils.CooldownType;
import static ua.mcchickenstudio.opencreative.utils.CooldownUtils.checkAndSetCooldownWithMessage;
import static ua.mcchickenstudio.opencreative.utils.ErrorUtils.*;
import static ua.mcchickenstudio.opencreative.utils.MessageUtils.*;

/**
 * <h1>EnvironmentCommand</h1>
 * This command is responsible for setting up world's
 * developers world and code environment.
 * <p>
 * Available: For world developers.
 */
public class EnvironmentCommand extends CommandHandler {

    @Override
    public void onExecute(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, String[] args) {
        if (sender instanceof Player player) {
            if (!checkAndSetCooldownWithMessage(player, CooldownUtils.CooldownType.GENERIC_COMMAND)) return;

            Planet planet = OpenCreative.getPlanetsManager().getPlanetByPlayer(player);
            if (planet == null) {
                player.sendMessage(getLocaleMessage("only-in-world"));
                return;
            }

            if (!planet.getWorldPlayers().canDevelop(player)) {
                player.sendMessage(getLocaleMessage("not-developer"));
                return;
            }

            if (args.length == 0) {
                new WorldEnvironmentMenu(player, planet.getDevPlanet()).open(player);
                return;
            }

            switch (args[0].toLowerCase()) {
                case "vars", "variables", "var":
                    if (args.length == 1) {
                        return;
                    }
                    if (args[1].equalsIgnoreCase("size")) {
                        player.sendMessage(getLocaleMessage("environment.variables.size").replace("%count%", String.valueOf(planet.getVariables().getTotalVariablesAmount())));
                    } else if (args[1].equalsIgnoreCase("set")) {
                        if (args.length <= 4) {
                            player.sendMessage(getLocaleMessage("environment.variables.set.help"));
                            return;
                        }
                        // /env var set VAR_NAME VAR_TYPE VALUE_TYPE VALUE
                        // /env var set VariableName global number 1
                        String varName = args[2];
                        VariableLink.VariableType type = VariableLink.VariableType.getEnum(args[3]);
                        if (type == null || type == VariableLink.VariableType.LOCAL) return;
                        ValueType valueType = ValueType.TEXT;
                        Object value = null;
                        switch (args[4].toLowerCase()) {
                            case "number", "n", "num", "numb" -> {
                                if (args.length == 5) return;
                                String numberString = args[5];
                                if (numberString.equalsIgnoreCase("p") || numberString.equalsIgnoreCase("pi")) {
                                    numberString = "3.1415926";
                                }
                                valueType = ValueType.NUMBER;
                                value = parseTicks(numberString, 0);
                            }
                            case "boolean", "bool", "b" -> {
                                if (args.length == 5) return;
                                value = Boolean.parseBoolean(args[5]);
                                valueType = ValueType.BOOLEAN;
                            }
                            case "text", "t" -> value = String.join(" ", Arrays.copyOfRange(args, 5, args.length));
                            case "item", "i" -> {
                                value = player.getInventory().getItemInMainHand();
                                valueType = ValueType.ITEM;
                            }
                            case "location", "loc" -> {
                                try {
                                    if (args.length < 8) return;
                                    double x = parseCoordinate(args[5], player.getX());
                                    double y = parseCoordinate(args[6], player.getY());
                                    double z = parseCoordinate(args[7], player.getZ());
                                    float yaw = player.getYaw();
                                    float pitch = player.getPitch();
                                    if (args.length >= 9) {
                                        yaw = parseCoordinate(args[8], player.getYaw());
                                    }
                                    if (args.length >= 10) {
                                        pitch = parseCoordinate(args[9], player.getPitch());
                                    }
                                    value = new Location(planet.getTerritory().getWorld(), x, y, z, yaw, pitch);
                                    valueType = ValueType.LOCATION;
                                } catch (NumberFormatException ignored) {
                                }
                            }
                            case "vector", "vec" -> {
                                try {
                                    if (args.length < 6) return;
                                    double x = Double.parseDouble(args[5]);
                                    double y = Double.parseDouble(args[6]);
                                    double z = Double.parseDouble(args[7]);
                                    value = new Vector(x, y, z);
                                    valueType = ValueType.VECTOR;
                                } catch (NumberFormatException ignored) {
                                }
                            }
                        }
                        if (value != null) {
                            if (planet.getVariables().setVariableValue(new VariableLink(varName, type), valueType, value)) {
                                player.sendMessage(getLocaleMessage("environment.variables.set.message")
                                        .replace("%variable%", varName)
                                        .replace("%value%", value.toString().length() > 100 ? value.toString().substring(0, 100) + "..." : value.toString()));
                            } else {
                                player.sendMessage(getLocaleMessage("environment.variables.set.limit")
                                        .replace("%limit%", String.valueOf(planet.getLimits().getVariablesAmountLimit())));
                            }
                        }
                    } else if (args[1].equalsIgnoreCase("get")) {
                        if (args.length == 2) return;
                        VariableLink.VariableType type = VariableLink.VariableType.GLOBAL;
                        String varName = args[2];
                        if (args.length >= 4) {
                            type = VariableLink.VariableType.getEnum(args[3]);
                            if (type == null || type == VariableLink.VariableType.LOCAL)
                                type = VariableLink.VariableType.GLOBAL;
                        }
                        WorldVariable var = planet.getVariables().getVariable(varName, type, null);
                        if (var == null && args.length == 3) {
                            var = planet.getVariables().getVariable(varName, VariableLink.VariableType.SAVED, null);
                        }
                        if (var == null) {
                            player.sendMessage(getLocaleMessage("environment.variables.get.empty"));
                        } else {
                            String message = getLocaleMessage("environment.variables.get.message")
                                    .replace("%variable%", varName)
                                    .replace("%type%", var.getType().getLocaleName())
                                    .replace("%valuetype%", var.getVarType().getLocalized());
                            message = message.replace("%value%", message.length()
                                    + (var.getValue() == null ? "null" : var.getValue()).toString().length() > 700 ? var.getValue().toString().substring(0, Math.min(var.getValue().toString().length(), 700)) + "..." : var.getValue().toString());
                            player.sendMessage(message);
                        }
                    } else if (args[1].equalsIgnoreCase("clear")) {
                        planet.getVariables().clearVariables();
                        player.sendMessage(getLocaleMessage("environment.variables.cleared"));
                    } else if (args[1].equalsIgnoreCase("list")) {
                        int page = 0;
                        List<WorldVariable> allVariables = new ArrayList<>(planet.getVariables().getSet());
                        if (allVariables.isEmpty()) {
                            player.sendMessage(getLocaleMessage("environment.variables.list.empty"));
                            return;
                        }
                        if (args.length > 2) {
                            try {
                                page = Integer.parseInt(args[2]) - 1;
                                if (page < 0 || page * 20 > allVariables.size()) {
                                    page = 0;
                                }
                            } catch (NumberFormatException ignored) {
                            }
                        }
                        int current = Math.min(((page + 1) * 20), allVariables.size());
                        List<WorldVariable> variables = new ArrayList<>(allVariables.subList(page * 20, current));
                        Sounds.DEV_VAR_LIST.play(player);
                        player.sendMessage(getLocaleMessage("environment.variables.list.header").replace("%current%", String.valueOf(current)).replace("%amount%", String.valueOf(allVariables.size())));
                        for (WorldVariable variable : variables) {
                            String name = variable.getName();
                            VariableLink.VariableType type = variable.getVarType();
                            String value = (variable.getValue() != null ? variable.getValue().toString() : "null");
                            if (name.length() > 40) {
                                name = name.substring(0, 40) + "...";
                            }
                            if (value.length() > 40) {
                                value = value.substring(0, 40) + "...";
                            }
                            player.sendMessage(getLocaleMessage("environment.variables.list.variable", false).replace("%name%", name).replace("%type%", type.getLocalized()).replace("%value%", value));
                        }
                        Component navigation = toComponent(getLocaleMessage("environment.variables.list.navigation"));
                        page += 1;
                        if (page * 20 > 20) {
                            navigation = navigation.append(toComponent(getLocaleMessage("environment.variables.list.previous-page")).clickEvent(ClickEvent.clickEvent(ClickEvent.Action.RUN_COMMAND, "/environment variables list " + (page - 1))));
                        }
                        if (allVariables.size() > current) {
                            navigation = navigation.append(toComponent(getLocaleMessage("environment.variables.list.next-page")).clickEvent(ClickEvent.clickEvent(ClickEvent.Action.RUN_COMMAND, "/environment variables list " + (page + 1))));
                        }
                        if (!toComponent(getLocaleMessage("environment.variables.list.navigation")).equals(navigation)) {
                            player.sendMessage(navigation);
                        }
                        player.sendMessage(" ");
                    }
                    break;
                case "containers", "barrel", "barrels": {
                    DevPlanet devPlanet = OpenCreative.getPlanetsManager().getDevPlanet(player);
                    if (devPlanet == null) {
                        player.sendMessage(getLocaleMessage("only-in-dev-world"));
                        return;
                    }
                    devPlanet.setContainerMaterial(devPlanet.getContainerMaterial() == Material.CHEST ? Material.BARREL : Material.CHEST);
                    devPlanet.updateContainers();
                    break;
                }
                case "container": {
                    if (args.length < 2) {
                        sender.sendMessage(getLocaleMessage("too-few-args"));
                        return;
                    }
                    DevPlanet devPlanet = OpenCreative.getPlanetsManager().getDevPlanet(player);
                    if (devPlanet == null) {
                        sender.sendMessage(getLocaleMessage("only-in-dev-world"));
                        return;
                    }
                    Material material = Material.CHEST;
                    try {
                        material = Material.valueOf((args[1].equalsIgnoreCase("chest") || (args[1].equalsIgnoreCase("barrel") || args[1].equalsIgnoreCase("shulker_box")) ? args[1].toUpperCase() : args[1].toUpperCase() + "_SHULKER_BOX"));
                    } catch (Exception ignored) {
                    }
                    if (devPlanet.setContainerMaterial(material)) {
                        devPlanet.updateContainers();
                    }
                    break;

                }
                case "clearitems": {
                    if (!planet.getDevPlanet().isLoaded()) return;
                    int count = 0;
                    for (Entity entity : new ArrayList<>(planet.getDevPlanet().getWorld().getEntities())) {
                        if (entity instanceof Item) {
                            entity.remove();
                            count++;
                        }
                    }
                    if (count == 0) {
                        Sounds.PLAYER_FAIL.play(player);
                        break;
                    }
                    for (Player p : planet.getPlayers()) {
                        if (planet.getWorldPlayers().canDevelop(p)) {
                            p.sendMessage(MessageUtils.getPlayerLocaleMessage("menus.entities-browser.removed-all",
                                    player).replace("%count%", String.valueOf(count)));
                        }
                    }
                    break;
                }
                case "drops", "drop", "drop-items": {
                    DevPlanet devPlanet = OpenCreative.getPlanetsManager().getDevPlanet(player);
                    if (devPlanet == null) {
                        player.sendMessage(getLocaleMessage("only-in-dev-world"));
                        return;
                    }
                    boolean value = !devPlanet.isDropItems();
                    if (args.length >= 2) {
                        value = switch (args[1].toLowerCase()) {
                            case "on", "enable" -> true;
                            default -> false;
                        };
                    }
                    player.sendMessage(getLocaleMessage("environment.drops." + (value ? "enabled" : "disabled")));
                    devPlanet.setDropItems(value);
                    Sounds.DEV_SETTINGS_DROP_ITEMS.play(player);
                    break;
                }
                case "night-vision": {
                    DevPlanet devPlanet = OpenCreative.getPlanetsManager().getDevPlanet(player);
                    if (devPlanet == null) {
                        player.sendMessage(getLocaleMessage("only-in-dev-world"));
                        return;
                    }
                    boolean value = !devPlanet.isNightVision();
                    if (args.length >= 2) {
                        value = switch (args[1].toLowerCase()) {
                            case "on", "enable" -> true;
                            default -> false;
                        };
                    }
                    devPlanet.setNightVision(value);
                    if (value) {
                        player.addPotionEffect(new PotionEffect(PotionEffectType.NIGHT_VISION, Integer.MAX_VALUE, 0, false, false, false));
                    } else {
                        player.removePotionEffect(PotionEffectType.NIGHT_VISION);
                    }
                    player.sendMessage(getLocaleMessage("environment.night-vision." + (value ? "enabled" : "disabled")));
                    Sounds.DEV_SETTINGS_NIGHT_VISION.play(player);
                    break;
                }
                case "save-location": {
                    DevPlanet devPlanet = OpenCreative.getPlanetsManager().getDevPlanet(player);
                    if (devPlanet == null) {
                        player.sendMessage(getLocaleMessage("only-in-dev-world"));
                        return;
                    }
                    boolean value = !devPlanet.isSaveLocation();
                    if (args.length >= 2) {
                        value = switch (args[1].toLowerCase()) {
                            case "on", "enable" -> true;
                            default -> false;
                        };
                    }
                    player.sendMessage(getLocaleMessage("environment.save-location." + (value ? "enabled" : "disabled")));
                    devPlanet.setSaveLocation(value);
                    Sounds.DEV_SETTINGS_SAVE_LOCATION.play(player);
                    break;
                }
                case "createplatform": {
                    if (!sender.hasPermission("opencreative.debug")) {
                        sender.sendMessage(getLocaleMessage("no-perms"));
                        return;
                    }
                    if (args.length < 3) {
                        sender.sendMessage(getLocaleMessage("too-few-args"));
                        return;
                    }
                    DevPlanet devPlanet = OpenCreative.getPlanetsManager().getDevPlanet(player);
                    if (devPlanet == null) {
                        sender.sendMessage(getLocaleMessage("only-in-dev-world"));
                        return;
                    }
                    int x = 1;
                    int z = 1;
                    try {
                        x = Integer.parseInt(args[1]);
                    } catch (Exception ignored) {
                    }
                    try {
                        z = Integer.parseInt(args[2]);
                    } catch (Exception ignored) {
                    }
                    if (devPlanet.createPlatform(x, z)) {
                        sender.sendMessage("Created platform " + x + " " + z);
                    }
                    break;
                }
                case "platform", "p": {
                    DevPlanet devPlanet = OpenCreative.getPlanetsManager().getDevPlanet(player);
                    if (devPlanet == null) {
                        sender.sendMessage(getLocaleMessage("only-in-dev-world"));
                        return;
                    }
                    if (devPlanet.getPlatforms().size() >= devPlanet.getPlanet().getLimits().getCodingPlatformsLimit()) {
                        sender.sendMessage(getLocaleMessage("environment.platform.limit").replace("%amount%", String.valueOf(devPlanet.getPlanet().getLimits().getCodingPlatformsLimit())));
                        return;
                    }
                    DevPlatform platform = devPlanet.getDevPlatformer().getNextAvailablePlatform(devPlanet);
                    devPlanet.claimPlatform(platform, player);
                    break;
                }
                case "sign": {
                    if (args.length < 2) {
                        sender.sendMessage(getLocaleMessage("too-few-args"));
                        return;
                    }
                    DevPlanet devPlanet = OpenCreative.getPlanetsManager().getDevPlanet(player);
                    if (devPlanet == null) {
                        sender.sendMessage(getLocaleMessage("only-in-dev-world"));
                        return;
                    }
                    Material material = Material.OAK_WALL_SIGN;
                    try {
                        material = Material.valueOf(args[1].toUpperCase() + "_WALL_SIGN");
                    } catch (Exception ignored) {
                    }
                    if (devPlanet.setSignMaterial(material)) {
                        Sounds.DEV_PLATFORM_SIGN.play(player);
                        devPlanet.updateSigns();
                    }
                    break;
                }
                case "floor": {
                    if (args.length < 2) {
                        sender.sendMessage(getLocaleMessage("too-few-args"));
                        return;
                    }
                    DevPlanet devPlanet = OpenCreative.getPlanetsManager().getDevPlanet(player);
                    if (devPlanet == null) {
                        sender.sendMessage(getLocaleMessage("only-in-dev-world"));
                        return;
                    }
                    Material material = Material.WHITE_STAINED_GLASS;
                    try {
                        material = Material.valueOf(args[1].equalsIgnoreCase("barrier")
                                || args[1].equalsIgnoreCase("glass") ? args[1].toUpperCase() : args[1].toUpperCase() + "_STAINED_GLASS");
                    } catch (Exception ignored) {
                    }
                    DevPlatform currentPlatform = devPlanet.getPlatformInLocation(player.getLocation());
                    boolean changed = false;
                    if (currentPlatform == null) {
                        for (DevPlatform platform : devPlanet.getPlatforms()) {
                            if (platform.setFloorMaterial(material)) changed = true;
                        }
                    } else {
                        changed = currentPlatform.setFloorMaterial(material);
                    }
                    if (changed) Sounds.DEV_PLATFORM_COLOR.play(player);
                    break;
                }
                case "action": {
                    if (args.length < 2) {
                        sender.sendMessage(getLocaleMessage("too-few-args"));
                        return;
                    }
                    DevPlanet devPlanet = OpenCreative.getPlanetsManager().getDevPlanet(player);
                    if (devPlanet == null) {
                        sender.sendMessage(getLocaleMessage("only-in-dev-world"));
                        return;
                    }
                    Material material = Material.GRAY_STAINED_GLASS;
                    try {
                        material = Material.valueOf(args[1].equalsIgnoreCase("barrier")
                                || args[1].equalsIgnoreCase("glass") ? args[1].toUpperCase() : args[1].toUpperCase() + "_STAINED_GLASS");
                    } catch (Exception ignored) {
                    }
                    DevPlatform currentPlatform = devPlanet.getPlatformInLocation(player.getLocation());
                    boolean changed = false;
                    if (currentPlatform == null) {
                        for (DevPlatform platform : devPlanet.getPlatforms()) {
                            if (platform.setActionMaterial(material)) changed = true;
                        }
                    } else {
                        changed = currentPlatform.setActionMaterial(material);
                    }
                    if (changed) Sounds.DEV_PLATFORM_COLOR.play(player);
                    break;
                }
                case "event", "executor": {
                    if (args.length < 2) {
                        sender.sendMessage(getLocaleMessage("too-few-args"));
                        return;
                    }
                    DevPlanet devPlanet = OpenCreative.getPlanetsManager().getDevPlanet(player);
                    if (devPlanet == null) {
                        sender.sendMessage(getLocaleMessage("only-in-dev-world"));
                        return;
                    }
                    Material material = Material.BLUE_STAINED_GLASS;
                    try {
                        material = Material.valueOf(args[1].equalsIgnoreCase("barrier")
                                || args[1].equalsIgnoreCase("glass") ? args[1].toUpperCase() : args[1].toUpperCase() + "_STAINED_GLASS");
                    } catch (Exception ignored) {
                    }
                    DevPlatform currentPlatform = devPlanet.getPlatformInLocation(player.getLocation());
                    boolean changed = false;
                    if (currentPlatform == null) {
                        for (DevPlatform platform : devPlanet.getPlatforms()) {
                            if (platform.setEventMaterial(material)) changed = true;
                        }
                    } else {
                        changed = currentPlatform.setEventMaterial(material);
                    }
                    if (changed) Sounds.DEV_PLATFORM_COLOR.play(player);
                    break;
                }
                case "theme", "settheme", "themes": {
                    if (args.length < 2) {
                        sender.sendMessage(getLocaleMessage("too-few-args"));
                        return;
                    }
                    DevPlanet devPlanet = OpenCreative.getPlanetsManager().getDevPlanet(player);
                    if (devPlanet == null) {
                        sender.sendMessage(getLocaleMessage("only-in-dev-world"));
                        return;
                    }
                    DevPlatform platform = devPlanet.getPlatformInLocation(player.getLocation());
                    if (platform == null) {
                        return;
                    }
                    if (switch (args[1].toLowerCase()) {
                        case "dark", "black", "darkmode", "space", "night" ->
                                platform.setMaterials(Material.BARRIER, Material.GRAY_STAINED_GLASS, Material.BLACK_STAINED_GLASS);
                        case "light", "white", "lightmode" ->
                                platform.setMaterials(Material.BARRIER, Material.GRAY_STAINED_GLASS, Material.WHITE_STAINED_GLASS);
                        case "pink", "magenta", "purple" ->
                                platform.setMaterials(Material.BARRIER, Material.PINK_STAINED_GLASS, Material.MAGENTA_STAINED_GLASS);
                        case "blue", "ocean", "cyan" ->
                                platform.setMaterials(Material.BARRIER, Material.BLUE_STAINED_GLASS, Material.LIGHT_BLUE_STAINED_GLASS);
                        case "ukraine", "ua", "uk" ->
                                platform.setMaterials(Material.BARRIER, Material.BLUE_STAINED_GLASS, Material.YELLOW_STAINED_GLASS);
                        case "rhombus", "old", "legacy" ->
                                platform.setMaterials(Material.WHITE_STAINED_GLASS, Material.LIGHT_BLUE_STAINED_GLASS, Material.LIGHT_GRAY_STAINED_GLASS);
                        case "just", "planet", "default" ->
                                platform.setMaterials(Material.WHITE_STAINED_GLASS, Material.BLUE_STAINED_GLASS, Material.GRAY_STAINED_GLASS);
                        case "art", "artur" ->
                                platform.setMaterials(Material.WHITE_STAINED_GLASS, Material.BLACK_STAINED_GLASS, Material.CYAN_STAINED_GLASS);
                        case "cloud" ->
                                platform.setMaterials(Material.WHITE_STAINED_GLASS, Material.CYAN_STAINED_GLASS, Material.GRAY_STAINED_GLASS);
                        default -> false;
                    }) {
                        Sounds.DEV_PLATFORM_COLOR.play(player);
                    }
                    break;
                }
                case "execute", "exec", "launch", "run": {
                    if (planet.getMode() != Planet.Mode.PLAYING) {
                        sender.sendMessage(getLocaleMessage("world.not-in-play-mode"));
                        return;
                    }
                    if (args.length < 3) {
                        sender.sendMessage(getLocaleMessage("too-few-args"));
                        return;
                    }
                    String eventName = args[1];
                    String argument = String.join(" ", Arrays.copyOfRange(args, 2, args.length));
                    // /env execute player_join PlayerName
                    // /env execute function Function
                    switch (eventName.toLowerCase()) {
                        case "join", "player_join" -> {
                            Player eventPlayer = Bukkit.getPlayer(argument);
                            if (eventPlayer == null || !planet.getTerritory().getWorld().getPlayers().contains(eventPlayer)) {
                                sender.sendMessage(getLocaleMessage("environment.execute.offline"));
                                return;
                            }
                            new JoinEvent(player).callEvent();
                        }
                        case "quit", "player_quit" -> {
                            Player eventPlayer = Bukkit.getPlayer(argument);
                            if (eventPlayer == null || !planet.getTerritory().getWorld().getPlayers().contains(eventPlayer)) {
                                sender.sendMessage(getLocaleMessage("environment.execute.offline"));
                                return;
                            }
                            new QuitEvent(player).callEvent();
                        }
                        case "liked", "like", "player_like", "player_liked" -> {
                            Player eventPlayer = Bukkit.getPlayer(argument);
                            if (eventPlayer == null || !planet.getTerritory().getWorld().getPlayers().contains(eventPlayer)) {
                                sender.sendMessage(getLocaleMessage("environment.execute.offline"));
                                return;
                            }
                            new LikeEvent(player).callEvent();
                        }
                        case "play", "player_play" -> {
                            Player eventPlayer = Bukkit.getPlayer(argument);
                            if (eventPlayer == null || !planet.getTerritory().getWorld().getPlayers().contains(eventPlayer)) {
                                sender.sendMessage(getLocaleMessage("environment.execute.offline"));
                                return;
                            }
                            new PlayEvent(player).callEvent();
                        }
                        case "world_play" -> new GamePlayEvent(planet).callEvent();
                        case "function", "func" -> {
                            boolean found = false;
                            for (Function function : planet.getTerritory().getScript().getExecutors().getFunctionsList()) {
                                if (argument.equalsIgnoreCase(function.getCallName())) {
                                    if (!found) {
                                        /*
                                         * For sending message once and
                                         * before function activation.
                                         */
                                        found = true;
                                        sender.sendMessage(getLocaleMessage("environment.execute.function").replace("%function%", argument));
                                    }
                                    PlanetExecutors.activate(function, new JoinEvent(player));
                                }
                            }
                            if (!found)
                                sender.sendMessage(getLocaleMessage("environment.execute.function-not-found"));
                        }
                        case "method", "meth" -> {
                            boolean found = false;
                            for (Method method : planet.getTerritory().getScript().getExecutors().getMethodsList()) {
                                if (argument.equalsIgnoreCase(method.getCallName())) {
                                    if (!found) {
                                        found = true;
                                        sender.sendMessage(getLocaleMessage("environment.execute.method").replace("%method%", argument));
                                    }
                                    PlanetExecutors.activate(method, new JoinEvent(player));
                                }
                            }
                            if (!found)
                                sender.sendMessage(getLocaleMessage("environment.execute.method-not-found"));
                        }
                        default -> sender.sendMessage(getLocaleMessage("environment.execute.help"));
                    }
                    break;
                }
                case "debug": {
                    if (args.length == 1) {
                        player.sendMessage(getLocaleMessage("environment.debug.help"));
                        return;
                    }
                    if (args[1].equalsIgnoreCase("enable") || args[1].equalsIgnoreCase("on")) {
                        for (Player planetPlayer : planet.getPlayers()) {
                            planetPlayer.sendMessage(getPlayerLocaleMessage("environment.debug.enabled", player));
                        }
                        Sounds.DEV_DEBUG_ON.play(player);
                        planet.setDebug(true);
                    } else if (args[1].equalsIgnoreCase("disable") || args[1].equalsIgnoreCase("off")) {
                        for (Player planetPlayer : planet.getPlayers()) {
                            planetPlayer.sendMessage(getPlayerLocaleMessage("environment.debug.disabled", player));
                        }
                        Sounds.DEV_DEBUG_OFF.play(player);
                        planet.setDebug(false);
                    }
                    break;
                }
                case "generate", "make": {
                    if (args.length == 1) { // /env make a code that does something...
                        player.sendMessage(getLocaleMessage("environment.prompter.help"));
                        return;
                    }
                    if (!OpenCreative.getSettings().getGroups().getGroup(player).canUsePrompter() && !player.hasPermission("opencreative.prompter.bypass")) {
                        player.sendMessage(getLocaleMessage("no-perms"));
                        return;
                    }
                    if (!OpenCreative.getCodingPrompter().isEnabled()) {
                        sender.sendMessage(getLocaleMessage("environment.prompter.disabled"));
                        return;
                    }
                    DevPlanet devPlanet = OpenCreative.getPlanetsManager().getDevPlanet(player);
                    if (devPlanet == null) {
                        sender.sendMessage(getLocaleMessage("only-in-dev-world"));
                        return;
                    }
                    if (args.length <= 4) {
                        player.sendMessage(getLocaleMessage("environment.prompter.few-args"));
                        return;
                    }
                    if (!checkAndSetCooldownWithMessage(player, CooldownType.PROMPTER_REQUEST)) return;
                    String request = String.join(" ", Arrays.copyOfRange(args, 1, args.length));
                    sendDebug("[CODING PROMPT] Player " + player.getName() + " requested to create a code: " + request);
                    player.sendMessage(getLocaleMessage("environment.prompter.thinking"));
                    broadcastPrompter(planet, player, request, "request");
                    Sounds.DEV_PROMPTER_THINKING.play(player);
                    long time = System.currentTimeMillis();
                    int actionsLimit = devPlanet.getDevPlatformer().getCodingBlocksLimit(devPlanet) - 1; // -1 because executor counts too
                    new BukkitRunnable() {
                        @Override
                        public void run() {
                            OpenCreative.getCodingPrompter().generateCode(player.getName(),
                                    player.getUniqueId(), request, actionsLimit).thenAccept(
                                    response -> {
                                        sendDebug("[CODING PROMPT] Responded to " + player.getName() + "'s wish: "
                                                + request + " in " + (System.currentTimeMillis() - time) + "  ms.");
                                        sendDebug("The response:\n" + response);
                                        YamlConfiguration config = new YamlConfiguration();
                                        try {
                                            config.load(new StringReader(response));
                                            sendDebug("[CODING PROMPT] Response:\n" + config.saveToString());
                                        } catch (Exception error) {
                                            sendDebugError("Failed to generate a code by " + player.getName() +  ": " + request, error);
                                            throw new PrompterBadCodeException(error);
                                        }
                                        ConfigurationSection section = config.getConfigurationSection("code.blocks");
                                        if (section == null) {
                                            section = config.getConfigurationSection("blocks");
                                            if (section == null) {
                                                player.sendMessage(getLocaleMessage("environment.prompter.bad-prompt"));
                                                Sounds.PLAYER_FAIL.play(player);
                                                Bukkit.getScheduler().runTask(OpenCreative.getPlugin(),
                                                        () -> broadcastPrompter(planet, player, request, "failed")
                                                );
                                                return;
                                            }
                                        }
                                        if (section.getKeys(false).size() > OpenCreative.getSettings().getCodingSettings().getPrompterMaxExecutors()) {
                                            player.sendMessage(getLocaleMessage("environment.prompter.few-space"));
                                            Bukkit.getScheduler().runTask(OpenCreative.getPlugin(),
                                                    () -> broadcastPrompter(planet, player, request, "failed")
                                            );
                                            Sounds.PLAYER_FAIL.play(player);
                                            return;
                                        }
                                        if (!player.isOnline() || !devPlanet.equals(OpenCreative.getPlanetsManager().getDevPlanet(player))) {
                                            return;
                                        }
                                        ConfigurationSection finalSection = section;
                                        Bukkit.getScheduler().runTask(OpenCreative.getPlugin(),
                                                () -> {
                                                    CodingBlockPlacer placer = new CodingBlockPlacer(devPlanet);
                                                    CodingBlockPlacer.CodePlacementResult result = placer.placeCodingLines(devPlanet, finalSection);
                                                    if (result.getType() == CodingBlockPlacer.CodePlacementResult.Type.NOT_ENOUGH_SPACE) {
                                                        player.sendMessage(getLocaleMessage("environment.prompter.few-space"));
                                                        Sounds.PLAYER_FAIL.play(player);
                                                        broadcastPrompter(planet, player, request, "failed");
                                                    } else if (result.getType().isSuccess()) {
                                                        long responseTime = System.currentTimeMillis() - time;
                                                        player.sendMessage(getLocaleMessage("environment.prompter.success")
                                                                .replace("%time%", String.valueOf(responseTime / 1000))
                                                                .replace("%idea%", request));
                                                        Sounds.DEV_PROMPTER_DONE.play(player);
                                                        broadcastPrompter(planet, player, request, "success");
                                                        for (Location placedExecutor : result.getPlacedColumns()) {
                                                            devPlanet.addChangedColumn(placedExecutor);
                                                        }
                                                    } else {
                                                        broadcastPrompter(planet, player, request, "failed");
                                                    }
                                                });
                                    }
                            ).exceptionally(
                                    error -> {
                                        switch (error.getCause()) {
                                            case UnauthorizedPrompterException ignored ->
                                                    player.sendMessage(getLocaleMessage("environment.prompter.unauthorized"));
                                            case PrompterLimitedException ignored ->
                                                    player.sendMessage(getLocaleMessage("environment.prompter.limited"));
                                            case PrompterDownException ignored ->
                                                    player.sendMessage(getLocaleMessage("environment.prompter.unavailable"));
                                            case PrompterBadCodeException ignored -> {
                                                    player.sendMessage(getLocaleComponent("environment.prompter.bad-prompt")
                                                            .hoverEvent(HoverEvent.showText(Component.text(parseException(ignored, true)))));
                                                    Sounds.PLAYER_FAIL.play(player);
                                            }
                                            case HttpTimeoutException ignored ->
                                                    player.sendMessage(getLocaleMessage("environment.prompter.timeout"));
                                            case ConnectException ignored ->
                                                    player.sendMessage(getLocaleMessage("environment.prompter.unknown-host"));
                                            case Exception exception ->
                                                    sendPlayerErrorMessage(player, "Failed to generate a code with " + OpenCreative.getCodingPrompter().getName() + ".", exception);
                                            default ->
                                                    sendPlayerErrorMessage(player, "Failed to generate a code with " + OpenCreative.getCodingPrompter().getName() + ".");
                                        }
                                        return null;
                                    });
                        }
                    }.runTaskAsynchronously(OpenCreative.getPlugin());
                }
            }

        }
    }

    @Override
    public List<String> onTab(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, String[] args) {
        List<String> tabCompleter = new ArrayList<>();
        if (args.length == 1) {
            Collections.addAll(tabCompleter, "platform", "variables", "debug", "execute", "barrel", "floor", "action", "theme", "event", "sign", "save-location", "night-vision", "drops", "clearitems");
            if (OpenCreative.getCodingPrompter().isEnabled()) tabCompleter.add("make");
            return tabCompleter;
        }
        if (args.length == 2) {
            if (List.of("var", "vars", "variables").contains(args[0].toLowerCase())) {
                Collections.addAll(tabCompleter, "set", "get", "size", "clear", "list");
            } else if (List.of("execute", "exec", "run").contains(args[0].toLowerCase())) {
                Collections.addAll(tabCompleter, "function", "method", "player_join", "player_quit", "player_liked", "player_play", "world_play");
            } else if ("debug".equalsIgnoreCase(args[0]) || "drops".equalsIgnoreCase(args[0]) || "night-vision".equalsIgnoreCase(args[0]) || "save-location".equalsIgnoreCase(args[0])) {
                Collections.addAll(tabCompleter, "on", "off");
            } else if ("floor".equalsIgnoreCase(args[0]) || "event".equalsIgnoreCase(args[0]) || "action".equalsIgnoreCase(args[0])) {
                Collections.addAll(tabCompleter,
                        "barrier", "black", "blue"
                        , "light_blue", "light_gray", "white"
                        , "red", "orange", "yellow", "purple"
                        , "green", "lime", "magenta", "brown"
                        , "cyan", "pink");
            } else if ("theme".equalsIgnoreCase(args[0])) {
                Collections.addAll(tabCompleter,
                        "default", "dark", "light",
                        "legacy", "cloud", "art", "ukraine",
                        "blue", "purple");
            } else if ("sign".equalsIgnoreCase(args[0])) {
                Collections.addAll(tabCompleter,
                        "oak", "acacia", "bamboo", "cherry",
                        "birch", "jungle");
            } else if ("container".equalsIgnoreCase(args[0])) {
                Collections.addAll(tabCompleter,
                        "barrel", "chest", "black", "blue"
                        , "light_blue", "light_gray", "white"
                        , "red", "orange", "yellow", "purple"
                        , "green", "lime", "magenta", "brown"
                        , "cyan", "pink");
            }
            return tabCompleter;
        }
        if (List.of("execute", "exec", "run").contains(args[0].toLowerCase())) {
            if (sender instanceof Player player) {
                if (PlayerUtils.isEntityInLobby(player)) return tabCompleter;
                Planet planet = OpenCreative.getPlanetsManager().getPlanetByPlayer(player);
                if (planet == null || !planet.getWorldPlayers().canDevelop(player)) return tabCompleter;
                if (List.of("join", "quit", "player_join", "player_quit", "player_play", "play", "player_liked", "liked").contains(args[1].toLowerCase())) {
                    tabCompleter.addAll(planet.getTerritory().getWorld().getPlayers().stream().map(Player::getName).toList());
                } else if (args[1].equalsIgnoreCase("function")) {
                    tabCompleter.addAll(planet.getTerritory().getScript().getExecutors().getFunctionsList().stream().map(Function::getCallName).toList());
                } else if (args[1].equalsIgnoreCase("method")) {
                    tabCompleter.addAll(planet.getTerritory().getScript().getExecutors().getMethodsList().stream().map(Method::getCallName).toList());
                }
            }
        }
        if (List.of("var", "vars", "variables").contains(args[0].toLowerCase())) {
            if (args[1].equalsIgnoreCase("set")) {
                if (args.length == 3) {
                    if (sender instanceof Player player) {
                        if (PlayerUtils.isEntityInLobby(player)) return tabCompleter;
                        Planet planet = OpenCreative.getPlanetsManager().getPlanetByPlayer(player);
                        if (planet == null || !planet.getWorldPlayers().canDevelop(player)) return tabCompleter;
                        List<WorldVariable> allVariables = new ArrayList<>(planet.getVariables().getSet());
                        if (allVariables.isEmpty()) return tabCompleter;
                        List<WorldVariable> vars = allVariables.subList(Math.max(0, allVariables.size() - 10), allVariables.size());
                        tabCompleter.addAll(vars.stream().map(WorldVariable::getName).toList());
                    }
                }
                if (args.length == 4) {
                    Collections.addAll(tabCompleter, "global", "saved");
                }
                if (args.length == 5) {
                    Collections.addAll(tabCompleter, "text", "number", "location", "item", "boolean", "vector");
                }
                if (args.length == 6) {
                    switch (args[4].toLowerCase()) {
                        case "number", "n", "numb", "num" ->
                                Collections.addAll(tabCompleter, "0", "1", "16", "32", "64", "100", "500");
                        case "boolean", "b", "bool" -> Collections.addAll(tabCompleter, "true", "false");
                    }
                }
            }
            if (args[1].equalsIgnoreCase("get")) {
                if (args.length == 3) {
                    if (sender instanceof Player player) {
                        if (PlayerUtils.isEntityInLobby(player)) return tabCompleter;
                        Planet planet = OpenCreative.getPlanetsManager().getPlanetByPlayer(player);
                        if (planet == null || !planet.getWorldPlayers().canDevelop(player)) return tabCompleter;
                        List<WorldVariable> allVariables = new ArrayList<>(planet.getVariables().getSet());
                        if (allVariables.isEmpty()) return tabCompleter;
                        List<WorldVariable> vars = allVariables.subList(Math.max(0, allVariables.size() - 10), allVariables.size());
                        tabCompleter.addAll(vars.stream().map(WorldVariable::getName).toList());
                    }
                } else if (args.length == 4) {
                    Collections.addAll(tabCompleter, "global", "saved");
                } else {
                    return null;
                }
            }
        }
        return tabCompleter;
    }

    private double parseCoordinate(String arg, double current) throws NumberFormatException {
        if (arg.startsWith("~")) {
            return arg.equals("~") ? current : current + Double.parseDouble(arg.substring(1));
        } else {
            return Double.parseDouble(arg);
        }
    }

    private float parseCoordinate(String arg, float current) throws NumberFormatException {
        if (arg.startsWith("~")) {
            return arg.equals("~") ? current : current + Float.parseFloat(arg.substring(1));
        } else {
            return Float.parseFloat(arg);
        }
    }

    private void broadcastPrompter(Planet planet, Player player, String request, String messageID) {
        for (Player developer : planet.getPlayers()) {
            if (planet.getWorldPlayers().canDevelop(developer) && !developer.equals(player)) {
                developer.sendMessage(getPlayerLocaleComponent("environment.prompter.broadcast." + messageID, player)
                        .hoverEvent(HoverEvent.showText(getLocaleComponent("environment.prompter.broadcast.hover")
                                .replaceText(TextReplacementConfig.builder().match("%idea%")
                                        .replacement(request)
                                        .build()))));
            }
        }
    }
}
