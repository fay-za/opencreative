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

package ua.mcchickenstudio.opencreative.planets;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Bukkit;
import org.bukkit.WorldBorder;
import org.bukkit.entity.Player;

/**
 * <h1>PlanetExperiments</h1>
 * This class represents secret experiments, that are enabled
 * in debug mode only for server admins with specific permission.
 */
public class PlanetExperiments {

    private final Planet planet;

    public PlanetExperiments(Planet planet) {
        this.planet = planet;
    }

    public void handle(Player player, String[] args) {
        switch (args[0].toLowerCase()) {
            case "downloadable" -> {
                planet.getInformation().setDownloadable(!planet.getInformation().isDownloadable());
                announce("Now world " + (planet.getInformation().isDownloadable() ? "can be downloaded" : "can't be downloaded"));
            }
            case "border" -> {
                if (!player.hasPermission("opencreative.test")) return;
                if (args.length == 1) return;
                WorldBorder border = Bukkit.createWorldBorder();
                border.setSize(player.getWorld().getWorldBorder().getSize());
                if ("green".equalsIgnoreCase(args[1])) {
                    border.setSize(border.getSize() + 0.1, 3600);
                } else if ("red".equalsIgnoreCase(args[1])) {
                    border.setSize(border.getSize() - 0.1, 3600);
                }
                player.setWorldBorder(border);
            }
        }
    }

    private void announce(String message) {
        planet.getAudience().sendMessage(Component.text("[Experiment: " + message + "]").decorate(TextDecoration.ITALIC).color(TextColor.color(170, 170, 170)));
    }
}
