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

package ua.mcchickenstudio.opencreative.managers.stability;

import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.jetbrains.annotations.NotNull;
import ua.mcchickenstudio.opencreative.OpenCreative;
import ua.mcchickenstudio.opencreative.planets.Planet;
import ua.mcchickenstudio.opencreative.settings.Sounds;

import java.io.IOException;
import java.nio.file.FileStore;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashSet;
import java.util.Set;

import static ua.mcchickenstudio.opencreative.utils.ErrorUtils.sendWarningErrorMessage;
import static ua.mcchickenstudio.opencreative.utils.MessageUtils.*;
import static ua.mcchickenstudio.opencreative.utils.world.WorldUtils.isDevPlanet;

public final class Watchdog implements StabilityManager {

    private FileStore STORAGE_VOLUME;
    private BukkitRunnable runnable;

    private StabilityState pluginState = StabilityState.FINE;
    private StabilityState databaseState = StabilityState.FINE;
    private StabilityState storageState = StabilityState.FINE;
    private StabilityState memoryState = StabilityState.FINE;
    private StabilityState ticksState = StabilityState.FINE;

    @Override
    public void init() {
        try {
            STORAGE_VOLUME = Files.getFileStore(Paths.get("."));
        } catch (IOException ignored) {
            STORAGE_VOLUME = null;
        }
        if (runnable != null) {
            runnable.cancel();
        }
        StabilityManager manager = this;
        runnable = new BukkitRunnable() {
            @Override
            public void run() {

                if (!manager.equals(OpenCreative.getStability())) {
                    cancel();
                    return;
                }

                long heapSize = Runtime.getRuntime().totalMemory();
                long heapMaxSize = Runtime.getRuntime().maxMemory();
                if (heapSize > heapMaxSize) {
                    OpenCreative.getPlugin().getLogger().warning("[WATCHDOG] A lot of memory was used :(");
                    storageState = StabilityState.NOT_OKAY;
                    dumpPlanets();
                }
                
                long availableSpace = getAvailableSpace();
                if (availableSpace >= 200) { // 200 MB
                    storageState = StabilityState.FINE;
                } else if (availableSpace >= 100) { // 100 MB
                    storageState = StabilityState.NOT_OKAY;
                } else {
                    storageState = StabilityState.NIGHTMARE;
                }

                if (OpenCreative.getPlanetsManager().isStableConnection()) {
                    databaseState = StabilityState.FINE;
                } else if (OpenCreative.getPlanetsManager().isEnabled()) {
                    databaseState = StabilityState.NOT_OKAY;
                } else {
                    databaseState = StabilityState.NIGHTMARE;
                }

                long freeMemory = Runtime.getRuntime().freeMemory() / 1000000;
                if (freeMemory >= 100) { // 100 MB
                    memoryState = StabilityState.FINE;
                } else if (freeMemory >= 50) { // 50 MB
                    memoryState = StabilityState.NOT_OKAY;
                } else {
                    memoryState = StabilityState.NIGHTMARE;
                }

                if (getTPS() >= 17) {
                    ticksState = StabilityState.FINE;
                } else if (getTPS() >= 13) {
                    ticksState = StabilityState.NOT_OKAY;
                } else {
                    ticksState = StabilityState.NIGHTMARE;
                }

                StabilityState oldState = pluginState;
                if (storageState == databaseState && databaseState == memoryState && memoryState == ticksState) {
                    pluginState = storageState;
                } else {
                    pluginState = StabilityState.NIGHTMARE;
                }

                if (getState() == StabilityState.NIGHTMARE) {
                    if (memoryState == StabilityState.NIGHTMARE) {
                        sendWarningErrorMessage("[WATCHDOG] Too low available memory: " + freeMemory + " MB");
                    } else {
                        OpenCreative.getPlugin().getLogger().warning("OpenCreative+ cannot continue work due to stability issues.");
                        OpenCreative.getPlugin().getLogger().warning(" TPS: " + ticksState.name() + " (" + Math.floor(getTPS()) + "/20)");
                        OpenCreative.getPlugin().getLogger().warning(" Memory: " + memoryState.name() + " (" + freeMemory + " MB free)");
                        OpenCreative.getPlugin().getLogger().warning(" Storage: " + storageState.name() + " (" + availableSpace + " MB available)");
                        OpenCreative.getPlugin().getLogger().warning(" Database: " + databaseState.name());
                        dumpPlanets();
                        for (Player player : Bukkit.getOnlinePlayers()) {
                            player.sendActionBar(getLocaleMessage("creative.stability.actionbar")
                                    .replace("%memory%", memoryState.getLocalized())
                                    .replace("%storage%", storageState.getLocalized())
                                    .replace("%tps%", ticksState.getLocalized())
                                    .replace("%database%", databaseState.getLocalized())
                            );
                        }
                    }
                    if (oldState != StabilityState.NIGHTMARE) {
                        for (Player player : Bukkit.getOnlinePlayers()) {
                            player.sendMessage(getLocaleMessage("creative.stability.unload"));
                            for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
                                Sounds.MAINTENANCE_START.play(onlinePlayer);
                                for (Planet planet : OpenCreative.getPlanetsManager().getPlanets()) {
                                    if (planet.isLoaded()) {
                                        planet.getTerritory().unload();
                                    }
                                }
                            }
                        }
                    }
                    return;
                }

                if (databaseState != StabilityState.FINE)
                    sendWarningErrorMessage("[WATCHDOG] Database connection is not stable.");
                if (ticksState != StabilityState.FINE)
                    sendWarningErrorMessage("[WATCHDOG] Server ticks aren't stable.");
                if (storageState != StabilityState.FINE)
                    sendWarningErrorMessage("[WATCHDOG] Storage cannot be accessed.");
            }
        };
        runnable.runTaskTimerAsynchronously(OpenCreative.getPlugin(), 20L, 200L);
    }

    private void dumpPlanets() {
        long now = System.currentTimeMillis();
        Set<Planet> loadedPlanets = new HashSet<>();
        for (World world : Bukkit.getWorlds()) {
            if (isDevPlanet(world)) continue;
            Planet planet = OpenCreative.getPlanetsManager().getPlanetByWorld(world);
            if (planet == null) continue;
            if (!isDevPlanet(world)) {
                loadedPlanets.add(planet);
            }
        }
        if (loadedPlanets.isEmpty()) {
            OpenCreative.getPlugin().getLogger().info("[WATCHDOG] No loaded planets detected.");
            return;
        }
        OpenCreative.getPlugin().getLogger().warning("[WATCHDOG] Dump of loaded planets (" + loadedPlanets.size() + "): ");
        for (Planet planet : loadedPlanets) {
            OpenCreative.getPlugin().getLogger().warning("[WATCHDOG] " + planet.getId() +
                    " - Players (" + planet.getInformation().getAsyncOnline() + ") - " +
                    (planet.getMode() == Planet.Mode.PLAYING ? "Play" : "Build") +
                    " - Loaded: " + getElapsedTime(now, planet.getLastActivityTime()));
        }
    }

    @Override
    public boolean isEnabled() {
        return true;
    }

    @Override
    public @NotNull StabilityState getDatabaseState() {
        return databaseState;
    }

    @Override
    public @NotNull StabilityState getMemoryState() {
        return memoryState;
    }

    @Override
    public @NotNull StabilityState getStorageState() {
        return storageState;
    }

    @Override
    public @NotNull StabilityState getTicksState() {
        return ticksState;
    }

    @Override
    public String getName() {
        return "Stability Watchdog";
    }

    public long getAvailableSpace() {
        return (getTotalSpace() - getUsedSpace()) / 1000000;
    }

    public long getUsedSpace() {
        if (STORAGE_VOLUME == null) {
            return 1;
        }
        try {
            long total = STORAGE_VOLUME.getTotalSpace();
            return total - STORAGE_VOLUME.getUsableSpace();
        } catch (IOException e) {
            return 1;
        }
    }

    public double getTPS() {
        if (Bukkit.getTPS().length >= 1) {
            return Bukkit.getTPS()[0];
        }
        return 20;
    }

    public long getTotalSpace() {
        if (STORAGE_VOLUME == null) {
            return 1;
        }
        try {
            return STORAGE_VOLUME.getTotalSpace();
        } catch (IOException e) {
            return 1;
        }
    }

    @Override
    public @NotNull StabilityState getState() {
        return pluginState;
    }
}
