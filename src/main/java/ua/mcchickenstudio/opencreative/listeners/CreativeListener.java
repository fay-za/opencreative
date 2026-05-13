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

package ua.mcchickenstudio.opencreative.listeners;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import ua.mcchickenstudio.opencreative.OpenCreative;
import ua.mcchickenstudio.opencreative.events.planet.PlanetConnectPlayerEvent;
import ua.mcchickenstudio.opencreative.events.planet.PlanetCreationEvent;
import ua.mcchickenstudio.opencreative.events.planet.PlanetDisconnectPlayerEvent;
import ua.mcchickenstudio.opencreative.events.player.CreativeChatEvent;
import ua.mcchickenstudio.opencreative.events.player.PlayerLobbyEvent;
import ua.mcchickenstudio.opencreative.events.player.WorldChatEvent;
import ua.mcchickenstudio.opencreative.events.status.MaintenanceEndEvent;
import ua.mcchickenstudio.opencreative.events.status.MaintenanceStartEvent;

import java.util.HashMap;
import java.util.Map;

public final class CreativeListener implements Listener {

    @EventHandler
    public void onLobbyTeleport(PlayerLobbyEvent event) {
        Map<String, Object> placeholders = new HashMap<>();
        placeholders.put("%player%", event.getPlayer().getName());
        OpenCreative.getSettings().getCommands().execute(event.getPlayer(), "onLobby", placeholders);
    }

    @EventHandler
    public void onCreativeChat(CreativeChatEvent event) {
        if (!(event.getSender() instanceof Player player)) return;
        Map<String, Object> placeholders = new HashMap<>();
        placeholders.put("%player%", event.getSender().getName());
        placeholders.put("%message%", event.getMessage());
        placeholders.put("%formatted%", event.getFormattedMessage());
        OpenCreative.getSettings().getCommands().execute(player, "onCreativeChat", placeholders);
    }

    @EventHandler
    public void onWorldChat(WorldChatEvent event) {
        Map<String, Object> placeholders = new HashMap<>();
        placeholders.put("%player%", event.getPlayer().getName());
        placeholders.put("%world%", event.getPlayer().getWorld().getName());
        placeholders.put("%message%", event.getMessage());
        placeholders.put("%formatted%", event.getFormattedMessage());
        OpenCreative.getSettings().getCommands().execute(event.getPlayer(), "onWorldChat", placeholders);
    }

    @EventHandler
    public void onPlanetConnect(PlanetConnectPlayerEvent event) {
        Map<String, Object> placeholders = new HashMap<>();
        placeholders.put("%player%", event.getPlayer().getName());
        placeholders.put("%planet%", event.getPlanet().getId());
        OpenCreative.getSettings().getCommands().execute(event.getPlayer(), "onPlanetConnect", placeholders);
    }

    @EventHandler
    public void onPlanetDisconnect(PlanetDisconnectPlayerEvent event) {
        Map<String, Object> placeholders = new HashMap<>();
        placeholders.put("%player%", event.getPlayer().getName());
        placeholders.put("%planet%", event.getPlanet().getId());
        OpenCreative.getSettings().getCommands().execute(event.getPlayer(), "onPlanetDisconnect", placeholders);
    }

    @EventHandler
    public void onPlanetCreation(PlanetCreationEvent event) {
        Map<String, Object> placeholders = new HashMap<>();
        placeholders.put("%planet%", event.getPlanet().getId());
        placeholders.put("%player%", event.getPlayer().getName());
        placeholders.put("%generator%", event.getGenerator().getID());
        placeholders.put("%environment%", event.getEnvironment().name().toLowerCase());
        placeholders.put("%structures%", event.shouldGenerateStructures());
        placeholders.put("%seed%", event.getSeed());
        OpenCreative.getSettings().getCommands().execute(event.getPlayer(), "onPlanetCreation", placeholders);
    }

    @EventHandler
    public void onMaintenanceStart(MaintenanceStartEvent event) {
        Map<String, Object> placeholders = new HashMap<>();
        if (event.getSender() instanceof Player player) {
            placeholders.put("%player%", player.getName());
            OpenCreative.getSettings().getCommands().execute(player, "onMaintenanceStart", placeholders);
        } else if (event.getSender() != null) {
            placeholders.put("%player%", Bukkit.getConsoleSender().getName());
            OpenCreative.getSettings().getCommands().execute(null, "onMaintenanceStart", placeholders);
        } else {
            OpenCreative.getSettings().getCommands().execute(null, "onMaintenanceStart", placeholders);
        }
    }

    @EventHandler
    public void onMaintenanceEnd(MaintenanceEndEvent event) {
        Map<String, Object> placeholders = new HashMap<>();
        if (event.getSender() instanceof Player player) {
            placeholders.put("%player%", player.getName());
            OpenCreative.getSettings().getCommands().execute(player, "onMaintenanceEnd", placeholders);
        } else if (event.getSender() != null) {
            placeholders.put("%player%", Bukkit.getConsoleSender().getName());
            OpenCreative.getSettings().getCommands().execute(null, "onMaintenanceEnd", placeholders);
        } else {
            OpenCreative.getSettings().getCommands().execute(null, "onMaintenanceEnd", placeholders);
        }
    }

}
