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

package ua.mcchickenstudio.opencreative.commands.experiments;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import ua.mcchickenstudio.opencreative.OpenCreative;
import ua.mcchickenstudio.opencreative.menus.world.browsers.WorldsBrowserMenu;
import ua.mcchickenstudio.opencreative.menus.world.browsers.WorldsPickerMenu;

import java.util.HashSet;
import java.util.List;

import static ua.mcchickenstudio.opencreative.utils.MessageUtils.getLocaleMessage;

public final class WorldDownloaderExperiment extends Experiment {

    @Override
    public @NotNull String getId() {
        return "world_downloader";
    }

    @Override
    public @NotNull String getName() {
        return "World Downloader";
    }

    @Override
    public @NotNull String getDescription() {
        return "Creates copies of worlds";
    }

    @Override
    public void handleCommand(@NotNull CommandSender sender, @NotNull String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage(getLocaleMessage("only-players"));
            return;
        }
        WorldsBrowserMenu menu = new WorldsPickerMenu(player, new HashSet<>(OpenCreative.getPlanetsManager().getPlanets().stream().filter(planet -> planet.getInformation().isDownloadable()).toList()));
        menu.open(player);
    }

    @Override
    public @Nullable List<String> tabCommand(@NotNull CommandSender sender, @NotNull String[] args) {
        return null;
    }

}
