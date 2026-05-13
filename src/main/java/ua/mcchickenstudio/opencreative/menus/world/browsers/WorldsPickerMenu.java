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

package ua.mcchickenstudio.opencreative.menus.world.browsers;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import ua.mcchickenstudio.opencreative.OpenCreative;
import ua.mcchickenstudio.opencreative.planets.Planet;
import ua.mcchickenstudio.opencreative.utils.FileUtils;
import ua.mcchickenstudio.opencreative.utils.world.WorldUtils;

import java.io.File;
import java.util.Set;

public final class WorldsPickerMenu extends WorldsBrowserMenu {

    public WorldsPickerMenu(Player player, Set<Planet> planets) {
        super(player, planets, false);
    }

    @Override
    protected void onPlanetClick(Player player, Planet downloadablePlanet) {
        if (downloadablePlanet.getInformation().isDownloadable()) {
            int id = WorldUtils.generateWorldID();
            FileUtils.copyFilesToDirectory(FileUtils.getPlanetFolder(downloadablePlanet), new File(Bukkit.getWorldContainer().getPath() + File.separator + "planets" + File.separator + "planet" + id));
            if (downloadablePlanet.getDevPlanet().exists()) {
                FileUtils.copyFilesToDirectory(FileUtils.getDevPlanetFolder(downloadablePlanet.getDevPlanet()), new File(Bukkit.getWorldContainer().getPath() + File.separator + "planets" + File.separator + "planet" + id + "dev"));
            }
            Planet newPlanet = new Planet(id);
            FileUtils.setPlanetConfigParameter(newPlanet, "creation-time", System.currentTimeMillis());
            newPlanet.setOwner(player.getName());
            newPlanet.getInformation().setCustomID(String.valueOf(id));
            newPlanet.getInformation().setDownloadable(false);
            newPlanet.getWorldPlayers().purgeData();
            OpenCreative.getPlanetsManager().registerPlanet(newPlanet);
            FileUtils.deleteFolder(new File(FileUtils.getPlanetFolder(newPlanet).getPath() + File.separator + "playersData"));
            FileUtils.deleteUnnecessaryWorldFiles(FileUtils.getPlanetFolder(newPlanet));
            newPlanet.connectPlayer(player);
        }
    }
}
