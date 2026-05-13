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

package ua.mcchickenstudio.opencreative.coding.modules;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.OfflinePlayer;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.jetbrains.annotations.NotNull;
import ua.mcchickenstudio.opencreative.OpenCreative;
import ua.mcchickenstudio.opencreative.coding.CodeStorage;
import ua.mcchickenstudio.opencreative.coding.CodingBlockPlacer;
import ua.mcchickenstudio.opencreative.events.module.ModuleInstallationEvent;
import ua.mcchickenstudio.opencreative.planets.DevPlanet;
import ua.mcchickenstudio.opencreative.planets.DevPlatform;
import ua.mcchickenstudio.opencreative.settings.Sounds;
import ua.mcchickenstudio.opencreative.utils.MessageUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static ua.mcchickenstudio.opencreative.utils.FileUtils.getModuleConfig;
import static ua.mcchickenstudio.opencreative.utils.MessageUtils.getLocaleMessage;
import static ua.mcchickenstudio.opencreative.utils.MessageUtils.parseModuleLines;

/**
 * <h1>Module</h1>
 * This class represents a module, a code that
 * can be pasted in developer world.
 * <p>Modules are stored in ./modules/moduleID.yml files.
 */
public class Module {

    private final int id;
    private final ModuleInfo info;
    private UUID owner;

    public Module(int id) {
        this.id = id;

        String uuid = getModuleConfig(this).getString("owner", "");
        try {
            owner = UUID.fromString(uuid);
        } catch (Exception ignored) {
            owner = new UUID(0, 0);
        }

        this.info = new ModuleInfo(this);
        new BukkitRunnable() {
            @Override
            public void run() {
                info.updateIcon();
            }
        }.runTaskAsynchronously(OpenCreative.getPlugin());
    }

    public @NotNull UUID getOwner() {
        return owner;
    }

    public @NotNull String getOwnerName() {
        OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(owner);
        String offlineName = offlinePlayer.getName();
        return offlinePlayer.hasPlayedBefore() ? (offlineName == null ? "Unknown owner" : offlineName) : "Unknown owner";
    }

    public boolean isOwner(@NotNull OfflinePlayer player) {
        return owner.equals(player.getUniqueId());
    }

    public int getId() {
        return id;
    }

    public @NotNull ModuleInfo getInformation() {
        return info;
    }

    /**
     * Builds code from module.
     *
     * @param devPlanet dev planet where module will be pasted.
     * @param player    player who loads module.
     * @return true - built module successfully, false - failed.
     */
    public boolean place(@NotNull DevPlanet devPlanet, @NotNull Player player) {

        if (!devPlanet.isLoaded()) return false;
        if (!devPlanet.getWorld().getPlayers().contains(player)) return false;
        if (!getInformation().isPublic() && !isOwner(player) && !player.hasPermission("opencreative.modules.private.bypass")) {
            player.sendMessage(getLocaleMessage("modules.private"));
            return true;
        }

        CodeStorage config = getModuleConfig(this);
        ConfigurationSection section = config.getSection("code.blocks");
        if (section == null) {
            return true;
        }

        List<Location> freeColumns = new ArrayList<>();
        int requiredColumns = section.getKeys(false).size();
        for (DevPlatform platform : devPlanet.getPlatforms()) {
            freeColumns.addAll(platform.getFreeColumns());
            if (freeColumns.size() >= requiredColumns) {
                break;
            }
        }

        ModuleInstallationEvent event = new ModuleInstallationEvent(this, player);
        event.callEvent();
        if (event.isCancelled()) {
            return false;
        }
        CodingBlockPlacer placer = new CodingBlockPlacer(devPlanet);
        CodingBlockPlacer.CodePlacementResult result = placer.placeCodingLines(devPlanet, section);
        if (result.getType() == CodingBlockPlacer.CodePlacementResult.Type.NOT_ENOUGH_SPACE) {
            player.sendMessage(getLocaleMessage("modules.few-space")
                    .replace("%required%", String.valueOf(requiredColumns)));
            Sounds.DEV_NOT_ALLOWED.play(player);
            return false;
        } else if (result.getType() == CodingBlockPlacer.CodePlacementResult.Type.ERROR) {
            player.sendMessage(parseModuleLines(this, MessageUtils.getPlayerLocaleMessage("modules.fail", player)));
            Sounds.PLAYER_FAIL.play(player);
            for (Location placedExecutor : result.getPlacedColumns()) {
                devPlanet.addChangedColumn(placedExecutor);
            }
            return false;
        } else {
            Sounds.DEV_MODULE_INSTALLED.play(player);
            for (Player planetPlayer : devPlanet.getPlanet().getPlayers()) {
                if (devPlanet.getPlanet().getWorldPlayers().canDevelop(planetPlayer)) {
                    planetPlayer.sendMessage(parseModuleLines(this, MessageUtils.getPlayerLocaleMessage("modules.installed", player)));
                }
            }
            for (Location placedExecutor : result.getPlacedColumns()) {
                devPlanet.addChangedColumn(placedExecutor);
            }
            getInformation().addDownload(devPlanet.getPlanet());
            return true;
        }
    }

    @Override
    public int hashCode() {
        return String.valueOf(id).hashCode();
    }
}
