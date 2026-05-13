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

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import ua.mcchickenstudio.opencreative.OpenCreative;

import java.util.Map;

import static ua.mcchickenstudio.opencreative.utils.MessageUtils.parsePAPI;

public record Command(String commandLine, boolean console, long delay) {

    public void execute(Player player, Map<String, Object> placeholders) {
        if (delay > 0) {
            new BukkitRunnable() {
                @Override
                public void run() {
                    dispatch(player, placeholders);
                }
            }.runTaskLater(OpenCreative.getPlugin(), delay);
        } else {
            dispatch(player, placeholders);
        }
    }

    private void dispatch(Player player, Map<String, Object> placeholders) {
        String dispatchedCommand = commandLine;
        for (String placeholder : placeholders.keySet()) {
            dispatchedCommand = dispatchedCommand.replace(placeholder, placeholders.get(placeholder).toString());
        }
        if (player != null) {
            dispatchedCommand = parsePAPI(player, dispatchedCommand);
        }
        if (console) {
            Bukkit.dispatchCommand(Bukkit.getConsoleSender(), dispatchedCommand);
        } else if (player != null) {
            player.performCommand(dispatchedCommand);
        }
    }

}
