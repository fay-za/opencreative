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

package ua.mcchickenstudio.opencreative.coding;

import org.apache.commons.io.FileUtils;
import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.scheduler.BukkitRunnable;
import org.jetbrains.annotations.NotNull;
import ua.mcchickenstudio.opencreative.OpenCreative;
import ua.mcchickenstudio.opencreative.coding.blocks.executors.PlanetExecutors;
import ua.mcchickenstudio.opencreative.planets.Planet;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

import static ua.mcchickenstudio.opencreative.utils.ErrorUtils.*;
import static ua.mcchickenstudio.opencreative.utils.FileUtils.getPlanetScriptFile;
import static ua.mcchickenstudio.opencreative.utils.MessageUtils.getLocaleMessage;

/**
 * <h1>CodeScript</h1>
 * This class represents configuration file that stores planet's code.
 * It has methods to load code and save coding blocks.
 *
 * @see CodingBlockParser
 */
public class CodeScript {

    private final Planet planet;
    private final PlanetExecutors executors;
    private CodeStorage scriptConfig;
    private long lastLaunch;

    public CodeScript(@NotNull Planet planet) {
        this.planet = planet;
        this.executors = new PlanetExecutors(planet);
        this.scriptConfig = new CodeConfiguration();
    }

    /**
     * Loads code from codeScript.yml file.
     */
    public @NotNull CompletableFuture<Boolean> loadCode() {
        CompletableFuture<Boolean> future = new CompletableFuture<>();
        Bukkit.getScheduler().runTaskAsynchronously(OpenCreative.getPlugin(), () -> {
            sendCodingDebugLog(planet, getLocaleMessage("coding-debug.loading-code", false));
            File scriptFile = getPlanetScriptFile(planet);
            long totalSize = ua.mcchickenstudio.opencreative.utils.FileUtils.getFileSize(scriptFile);
            long limit = planet.getGroup().getScriptSizeLimit() * 1024L * 1024L;
            if (totalSize > limit) {
                sendPlanetErrorMessage(planet, getLocaleMessage("world.script-size-limit")
                        .replace("%amount%", FileUtils.byteCountToDisplaySize(totalSize))
                        .replace("%limit%", String.valueOf(planet.getGroup().getScriptSizeLimit())));
                sendCodingDebugLog(planet, "Script File is too large to load :(");
                future.complete(false);
            }
            scriptConfig = new CodeConfiguration();
            scriptConfig.loadCode(scriptFile);
            new BukkitRunnable() {
                @Override
                public void run() {
                    executors.load(getPlanetScriptFile(planet));
                    lastLaunch = System.currentTimeMillis();
                    future.complete(true);
                }
            }.run();
        });
        return future;
    }

    /**
     * Returns last code load timestamp,
     * or 0 - if world is unloaded.
     *
     * @return timestamp of last code launch.
     */
    public long getLastLaunch() {
        return lastLaunch;
    }

    /**
     * Saves code script config into file.
     *
     * @return true - if saved, false - if failed.
     */
    public boolean saveCode() {
        long time = System.currentTimeMillis();
        OpenCreative.getPlugin().getLogger().info("Saving code in planet " + planet.getId() + "...");
        sendCodingDebugLog(planet, getLocaleMessage("coding-debug.saving-code", false));
        try {
            scriptConfig.saveToFile(getPlanetScriptFile(planet));
            if (OpenCreative.getSettings().getCodingSettings().shouldSaveScriptsHistory()) {
                copyToHistoryFolder(time);
            }
            OpenCreative.getPlugin().getLogger().info("Saved code in planet " + planet.getId() + " in " + (System.currentTimeMillis() - time) + " ms.");
            sendCodingDebugLog(planet, getLocaleMessage("coding-debug.saved-code", false)
                    .replace("%time%", String.valueOf(Math.floor((System.currentTimeMillis() - time) / 10.0) / 100.0)));
            return true;
        } catch (Exception error) {
            sendCriticalErrorMessage("Failed to save code in planet " + planet.getId() + ".", error);
            return false;
        }
    }

    /**
     * Moves stored code in old-code section to prevent being overwritten by new code.
     */
    public void clear(boolean removeCurrentCode) {
        executors.clear();
        lastLaunch = 0;
        ConfigurationSection section = scriptConfig.getSection("code.blocks");
    }

    /**
     * Clears temporary data: config and executors.
     */
    public void unload() {
        scriptConfig = new CodeConfiguration();
        executors.clear();
    }

    /**
     * Copies codeScript.yml to /plugins/OpenCreative/history folder.
     *
     * @param time timestamp, when script was changed.
     */
    public void copyToHistoryFolder(long time) {
        try {
            File historyFolder = new File(OpenCreative.getPlugin().getDataFolder().getPath() + File.separator + "history");
            if (historyFolder.isFile()) {
                historyFolder.delete();
            }
            if (!historyFolder.exists()) {
                historyFolder.mkdirs();
            }
            File dateFolder = new File(historyFolder, new SimpleDateFormat("yyyy-MM-dd").format(time));
            if (dateFolder.isFile()) {
                dateFolder.delete();
            }
            if (!dateFolder.exists()) {
                dateFolder.mkdirs();
            }
            String date = new SimpleDateFormat("dd-MM-yyyy--HH-mm-ss").format(time);
            File tempScript = new File(dateFolder.getPath() + File.separator
                    + "codeScript-" + planet.getOwner() + "-" + planet.getId() + "--" + date + ".yml");
            FileUtils.copyFile(getPlanetScriptFile(planet), tempScript);
        } catch (Exception error) {
            sendCriticalErrorMessage("Failed to copy codeScript.yml from " + planet.getId() + " to history folder.", error);
        }
    }

    /**
     * Returns config, that stores code script.
     *
     * @return code script config.
     */
    public @NotNull CodeStorage getConfig() {
        return scriptConfig;
    }

    /**
     * Returns instance of executors.
     *
     * @return executors of script.
     */
    public @NotNull PlanetExecutors getExecutors() {
        return executors;
    }

    public @NotNull Planet getPlanet() {
        return planet;
    }
}

