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

package ua.mcchickenstudio.opencreative.settings;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import ua.mcchickenstudio.opencreative.OpenCreative;

import java.util.LinkedHashMap;
import java.util.Map;

import static ua.mcchickenstudio.opencreative.utils.ErrorUtils.sendWarningErrorMessage;

public final class Commands {

    private final Map<String, Command> onLobbyCommands = new LinkedHashMap<>();
    private final Map<String, Command> onPlanetConnectCommands = new LinkedHashMap<>();
    private final Map<String, Command> onPlanetDisconnectCommands = new LinkedHashMap<>();
    private final Map<String, Command> onWorldChatCommands = new LinkedHashMap<>();
    private final Map<String, Command> onCreativeChatCommands = new LinkedHashMap<>();
    private final Map<String, Command> onMaintenanceStartCommands = new LinkedHashMap<>();
    private final Map<String, Command> onMaintenanceEndCommands = new LinkedHashMap<>();
    private final Map<String, Command> onPlanetCreationCommands = new LinkedHashMap<>();

    public void load() {
        onLobbyCommands.clear();
        onPlanetConnectCommands.clear();
        onPlanetDisconnectCommands.clear();
        onWorldChatCommands.clear();
        onCreativeChatCommands.clear();
        onMaintenanceStartCommands.clear();
        onMaintenanceEndCommands.clear();
        onPlanetCreationCommands.clear();
        FileConfiguration config = OpenCreative.getPlugin().getConfig();
        ConfigurationSection allCommandsSection = config.getConfigurationSection("commands");
        if (allCommandsSection == null) {
            return;
        }
        for (String eventName : allCommandsSection.getKeys(false)) {
            Map<String, Command> commandMap = getMap(eventName);
            if (commandMap == null) {
                sendWarningErrorMessage("Unknown event in commands section from config.yml: " + eventName);
                continue;
            }
            ConfigurationSection eventCommandsSection = allCommandsSection.getConfigurationSection(eventName);
            if (eventCommandsSection == null) {
                continue;
            }
            for (String commandName : eventCommandsSection.getKeys(false)) {
                String command = eventCommandsSection.getString(commandName + ".command");
                if (command == null || command.isEmpty()) continue;
                boolean console = eventCommandsSection.getBoolean(commandName + ".console", true);
                long delay = eventCommandsSection.getLong(commandName + ".delay", 0);
                commandMap.put(commandName, new Command(command, console, delay));
            }
        }
        if (!onLobbyCommands.isEmpty())
            OpenCreative.getPlugin().getLogger().info("Registered " + onLobbyCommands.size() + " commands for onSpawn");
        if (!onPlanetConnectCommands.isEmpty())
            OpenCreative.getPlugin().getLogger().info("Registered " + onPlanetConnectCommands.size() + " commands for onPlanetConnect");
        if (!onPlanetDisconnectCommands.isEmpty())
            OpenCreative.getPlugin().getLogger().info("Registered " + onPlanetDisconnectCommands.size() + " commands for onPlanetDisconnect");
        if (!onWorldChatCommands.isEmpty())
            OpenCreative.getPlugin().getLogger().info("Registered " + onWorldChatCommands.size() + " commands for onWorldChat");
        if (!onCreativeChatCommands.isEmpty())
            OpenCreative.getPlugin().getLogger().info("Registered " + onCreativeChatCommands.size() + " commands for onCreativeChat");
        if (!onMaintenanceStartCommands.isEmpty())
            OpenCreative.getPlugin().getLogger().info("Registered " + onMaintenanceStartCommands.size() + " commands for onMaintenanceStart");
        if (!onMaintenanceEndCommands.isEmpty())
            OpenCreative.getPlugin().getLogger().info("Registered " + onMaintenanceEndCommands.size() + " commands for onMaintenanceEnd");
        if (!onPlanetCreationCommands.isEmpty())
            OpenCreative.getPlugin().getLogger().info("Registered " + onPlanetCreationCommands.size() + " commands for onPlanetCreation");

    }

    private Map<String, Command> getMap(String eventName) {
        return switch (eventName) {
            case "onLobby" -> onLobbyCommands;
            case "onPlanetConnect" -> onPlanetConnectCommands;
            case "onPlanetDisconnect" -> onPlanetDisconnectCommands;
            case "onPlanetCreation" -> onPlanetCreationCommands;
            case "onWorldChat" -> onWorldChatCommands;
            case "onCreativeChat" -> onCreativeChatCommands;
            case "onMaintenanceStart" -> onMaintenanceStartCommands;
            case "onMaintenanceEnd" -> onMaintenanceEndCommands;
            default -> null;
        };
    }

    public void execute(Player player, String eventName, Map<String, Object> placeholders) {
        Map<String, Command> map = getMap(eventName);
        if (map == null) return;
        for (String commandName : map.keySet()) {
            Command command = map.get(commandName);
            command.execute(player, placeholders);
        }
    }

}
