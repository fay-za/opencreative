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

package ua.mcchickenstudio.opencreative.indev.blocks;

import org.apache.commons.io.FileUtils;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.scheduler.BukkitRunnable;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import ua.mcchickenstudio.opencreative.OpenCreative;
import ua.mcchickenstudio.opencreative.coding.CodeConfiguration;
import ua.mcchickenstudio.opencreative.coding.CodeStorage;
import ua.mcchickenstudio.opencreative.coding.blocks.events.WorldEvent;
import ua.mcchickenstudio.opencreative.indev.blocks.executors.ExecutorBlock;
import ua.mcchickenstudio.opencreative.indev.blocks.executors.WrappedExecutor;
import ua.mcchickenstudio.opencreative.planets.Planet;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import static ua.mcchickenstudio.opencreative.utils.ErrorUtils.sendCodingDebugLog;
import static ua.mcchickenstudio.opencreative.utils.ErrorUtils.sendPlanetErrorMessage;
import static ua.mcchickenstudio.opencreative.utils.FileUtils.getPlanetScriptFile;
import static ua.mcchickenstudio.opencreative.utils.MessageUtils.getLocaleMessage;

public class CodingScript {

    private final Planet planet;
    private final List<WrappedExecutor> registeredExecutors = new ArrayList<>();

    private CodeStorage codeHolder;

    public CodingScript(Planet planet) {
        this.planet = planet;
        codeHolder = null;
    }

    public boolean load() {
        sendCodingDebugLog(planet, getLocaleMessage("coding-debug.loading-code", false));
        File scriptFile = getPlanetScriptFile(planet);
        long totalSize = ua.mcchickenstudio.opencreative.utils.FileUtils.getFileSize(scriptFile);
        long limit = planet.getGroup().getScriptSizeLimit() * 1024L * 1024L;
        if (totalSize > limit) {
            sendPlanetErrorMessage(planet, getLocaleMessage("world.script-size-limit")
                    .replace("%amount%", FileUtils.byteCountToDisplaySize(totalSize))
                    .replace("%limit%", String.valueOf(planet.getGroup().getScriptSizeLimit())));
            sendCodingDebugLog(planet, "Script File is too large to load :(");
            return false;
        }
        codeHolder = new CodeConfiguration();
        codeHolder.loadCode(scriptFile);
        new BukkitRunnable() {
            @Override
            public void run() {
                load(getPlanetScriptFile(planet));
            }
        }.run();
        return true;
    }

    public void load(File file) {
        YamlConfiguration config = YamlConfiguration.loadConfiguration(file);
        ConfigurationSection section = config.getConfigurationSection("code.blocks");
        if (section != null) {
            OpenCreative.getPlugin().getLogger().info("Loading code in planet " + planet.getId() + "...");
            long time = System.currentTimeMillis();
            List<WrappedExecutor> executors = new ArrayList<>();
            Set<String> keys = section.getKeys(false);
            String path;
            for (String key : keys) {
                path = "code.blocks." + key;
                if (config.getString(path + ".type") != null) {
                    ConfigurationSection executorSection = config.getConfigurationSection(path);
                    if (executorSection == null) continue;
                    WrappedExecutor executor = createExecutor(executorSection);
                    if (executor != null) {
                        executors.add(executor);
                    }
                }
            }
            registeredExecutors.clear();
            registeredExecutors.addAll(executors);
            sendCodingDebugLog(planet, getLocaleMessage("coding-debug.loaded-code", false)
                    .replace("%time%", String.valueOf(Math.floor((System.currentTimeMillis() - time) / 10.0) / 100.0)));
            OpenCreative.getPlugin().getLogger().info("Loaded code in planet " + planet.getId() + " in " + (System.currentTimeMillis() - time) + " ms with " + executors.size() + " executors!");
        } else {
            sendCodingDebugLog(planet, getLocaleMessage("coding-debug.loaded-code", false)
                    .replace("%time%", "0"));
            OpenCreative.getPlugin().getLogger().info("Planet " + planet.getId() + " has no code to load.");
        }
    }

    private @Nullable WrappedExecutor createExecutor(@NotNull ConfigurationSection section) {
        try {
            ExecutorBlock executorBlock = ExecutorsNew.getExecutorById(section.getString("type", ""));
            if (executorBlock == null) return null;
            return executorBlock.createWrapped(section.getValues(false));
        } catch (Exception ignored) {
            return null;
        }
    }

    private int[] getCoords(ConfigurationSection section) {
        int[] coords = new int[3];
        coords[0] = section.getInt("location.x");
        coords[1] = section.getInt("location.y");
        coords[2] = section.getInt("location.z");
        return coords;
    }

    @ApiStatus.Experimental
    public void execute(WorldEvent event, ExecutorBlock executorBlock) {
        if (!planet.isLoaded()) return;
        if (planet.getMode() != Planet.Mode.PLAYING) return;
        for (WrappedExecutor wrapped : registeredExecutors) {
            if (wrapped.getBlock().equals(executorBlock)) {
                wrapped.execute(event);
            }
        }
    }

}
