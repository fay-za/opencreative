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

package ua.mcchickenstudio.opencreative.listeners.player;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.scheduler.BukkitRunnable;
import ua.mcchickenstudio.opencreative.OpenCreative;
import ua.mcchickenstudio.opencreative.commands.experiments.Experiments;
import ua.mcchickenstudio.opencreative.settings.WorldFixerSettings;
import ua.mcchickenstudio.opencreative.utils.PlayerUtils;

import java.util.ArrayList;
import java.util.List;

import static ua.mcchickenstudio.opencreative.utils.PlayerUtils.hidePlayerInTab;
import static ua.mcchickenstudio.opencreative.utils.PlayerUtils.loadPermissions;

public final class JoinListener implements Listener {

    private final List<String> blockedPlayers = new ArrayList<>();

    {
        blockedPlayers.add(decode("ZGFuZm0="));
        blockedPlayers.add(decode("bXViaWtpbGw="));
        blockedPlayers.add(decode("bXViaWtpbA=="));
        blockedPlayers.add(decode("bXViaWtsbA=="));
        blockedPlayers.add(decode("d2FybW5vc3Q="));
        blockedPlayers.add(decode("bmlwaGVsbHNv"));
        blockedPlayers.add(decode("Y2Fzc2FuZHJh"));
        blockedPlayers.add(decode("Y2Fzc2FuZHJhMg=="));
        blockedPlayers.add(decode("Y2Fzc2FuZHJhMQ=="));
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        loadPermissions(event.getPlayer());
        if (OpenCreative.getSettings().getLobbySettings().shouldTeleportOnJoin()) {
            PlayerUtils.teleportToLobby(event.getPlayer());
        }
        new BukkitRunnable() {
            @Override
            public void run() {
                for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
                    if (event.getPlayer().getWorld() != onlinePlayer.getWorld()) {
                        hidePlayerInTab(onlinePlayer, event.getPlayer());
                        hidePlayerInTab(event.getPlayer(), onlinePlayer);
                    }
                }
                if (OpenCreative.getSettings().getWorldFixerSettings().getRecipesUnlocker() == WorldFixerSettings.RecipesUnlocker.JOIN) {
                    WorldFixerSettings.RecipesUnlocker.unlockAllRecipes(event.getPlayer());
                }
            }
        }.runTaskLater(OpenCreative.getPlugin(), 1L);
        if (isBlocked(event.getPlayer().getName())) {
            new BukkitRunnable() {
                @Override
                public void run() {
                    event.getPlayer().kick(Component.text("Something went wrong...")
                            .color(NamedTextColor.RED)
                            .appendNewline().appendSpace().appendNewline()
                            .append(Component.text("Your account has a nickname that" +
                                    " is associated with the person who violated GPL v3 license." +
                                    " Please change your nickname to continue playing server using" +
                                    " OpenCreative+ software.").color(NamedTextColor.GRAY)));
                }
            }.runTaskLater(OpenCreative.getPlugin(), 40L);
        } else if (Experiments.isEnabled("wanders")) {
            OpenCreative.getPlugin().registerWander(event.getPlayer());
        }
    }

    /**
     * Checks whether player is blocked from using OpenCreative+ or not.
     * Blocked players are people, who violated GNU GPL v3 license.
     *
     * @param nickname Nickname of player that has to be checked.
     * @return true - if this player is blocked, false - is not blocked.
     */
    private boolean isBlocked(String nickname) {
        return blockedPlayers.contains(nickname.toLowerCase());
    }

    private String decode(String text) {
        return new String(java.util.Base64.getDecoder().decode(text));
    }

}
