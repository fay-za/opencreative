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

import org.bukkit.Bukkit;
import org.bukkit.event.Event;
import org.bukkit.event.EventException;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.plugin.EventExecutor;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import ua.mcchickenstudio.opencreative.OpenCreative;
import ua.mcchickenstudio.opencreative.coding.blocks.events.WorldEvent;
import ua.mcchickenstudio.opencreative.indev.blocks.executors.ExecutorBlock;

import java.util.LinkedHashSet;
import java.util.Set;

import static ua.mcchickenstudio.opencreative.utils.ErrorUtils.sendCriticalErrorMessage;

/**
 * <h1>Executors</h1>
 *
 * @version 6 Future
 */
public class ExecutorsNew implements EventExecutor, Listener {

    private final static Set<ExecutorBlock> executors = new LinkedHashSet<>();

    public void registerExecutor(ExecutorBlock executor) {
        try {
            Bukkit.getPluginManager().registerEvent(
                    executor.getEventClass(),
                    this, EventPriority.NORMAL, this,
                    OpenCreative.getPlugin());
            executors.add(executor);
            OpenCreative.getPlugin().getLogger().info("[EXECUTORS] Registered " + executor);
        } catch (Exception error) {
            sendCriticalErrorMessage("Cannot register executor: " + executor.toString(), error);
        }
    }

    public static @Nullable ExecutorBlock getExecutorById(@NotNull String id) {
        for (ExecutorBlock executor : executors) {
            if (executor.getId().equalsIgnoreCase(id)) {
                return executor;
            }
        }
        return null;
    }

    public void unregisterExecutor(ExecutorBlock executor) {
        executors.remove(executor);
    }

    public void handleEvent(WorldEvent event) {
        for (ExecutorBlock executorBlock : executors) {
            if (event.getClass().equals(executorBlock.getEventClass())) {
                //event.getPlanet().getTerritory().getScript().execute(event, executorBlock);
                /*List<WrappedExecutor> registeredExecutors = new ArrayList<>();

                 *//*
                @ApiStatus.Experimental
                public void execute(WorldEvent event, ExecutorBlock executorBlock) {
                    if (!planet.isLoaded()) return;
                    if (planet.getMode() != Planet.Mode.PLAYING) return;
                    for (WrappedExecutor wrapped : registeredExecutors) {
                        if (wrapped.getBlock().equals(executorBlock)) {
                            wrapped.execute(event);
                        }
                    }

                    File historyFolder = new File(OpenCreative.getPlugin().getDataFolder().getPath() + File.separator + "history");
                    if (!historyFolder.exists()) {
                        historyFolder.mkdirs();
                    }
                    String date = new SimpleDateFormat("dd-MM-yyyy--HH-mm-ss").format(time);
                    File tempScript = new File(historyFolder.getPath() + File.separator
                        + "codeScript-" + planet.getOwner() + "-" + planet.getId() + "--" + date + ".yml");
                    FileUtils.copyFile(getPlanetScriptFile(planet), tempScript);


                }*/
            }
        }
    }

    @Override
    public void execute(@NotNull Listener listener, @NotNull Event event) throws EventException {
        if (event instanceof WorldEvent worldEvent) {
            handleEvent(worldEvent);
        }
    }
}
