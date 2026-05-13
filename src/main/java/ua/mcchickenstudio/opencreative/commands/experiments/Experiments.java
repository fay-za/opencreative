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

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import ua.mcchickenstudio.opencreative.OpenCreative;

import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import static ua.mcchickenstudio.opencreative.utils.ErrorUtils.sendWarningMessage;

/**
 * <h1>Experiments</h1>
 * This class represents a registry of experiments.
 */
public final class Experiments {

    private static Experiments instance;

    private final Set<Experiment> experiments = new LinkedHashSet<>();

    private Experiments() {
    }

    /**
     * Returns instance of registry.
     *
     * @return experiments registry.
     */
    public static @NotNull Experiments getInstance() {
        if (instance == null) {
            instance = new Experiments();
            instance.registerExperiments(new CodeDownloaderExperiment(),
                    new WorldDownloaderExperiment(), new WandersExperiment(),
                    new TestificationExperiment());
        }
        return instance;
    }

    /**
     * Checks whether experiment is enabled or not.
     *
     * @param id id of experiment.
     * @return true - enabled, false - disabled.
     */
    public static boolean isEnabled(@NotNull String id) {
        Experiment experiment = getInstance().getExperiment(id);
        if (experiment == null) return false;
        return experiment.isEnabled();
    }

    /**
     * Registers experiments to registry.
     *
     * @param experiments experiments to register.
     */
    private void registerExperiments(@NotNull Experiment... experiments) {
        for (Experiment experiment : experiments) {
            if (List.of("on", "off", "enable", "disable", "list").contains(experiment.getId().toLowerCase())) {
                continue;
            }
            if (exists(experiment.getId())) {
                continue;
            }
            this.experiments.add(experiment);
        }
    }

    /**
     * Returns list of all available experiments, even disabled.
     *
     * @return list of experiments.
     */
    public List<Experiment> getExperiments() {
        return new LinkedList<>(experiments);
    }

    /**
     * Checks whether experiment exists or not.
     *
     * @param id id of experiment.
     * @return true - exists, false - not found.
     */
    public boolean exists(@NotNull String id) {
        return getExperiment(id) != null;
    }

    /**
     * Returns experiment by its id, or null - if not exists.
     *
     * @param id id of experiment.
     * @return experiment, or null - if not exists.
     */
    public @Nullable Experiment getExperiment(@NotNull String id) {
        for (Experiment experiment : getExperiments()) {
            if (experiment.getId().equalsIgnoreCase(id)) {
                return experiment;
            }
        }
        return null;
    }

    /**
     * Sets the enabled state of experiment.
     *
     * @param experiment experiment to enable or disable.
     * @param enabled    true - enabled, false - disabled.
     * @return true - successfully changed state, false - already was set before.
     */
    public boolean setEnabled(@NotNull Experiment experiment, boolean enabled) {
        if (experiment.isEnabled() == enabled) return false;
        if (enabled) {
            if (!experiment.isEnabled()) {
                try {
                    experiment.setEnabled(true);
                    experiment.onEnable();
                } catch (Exception error) {
                    sendWarningMessage("Failed to enable experiment: " + experiment.getId(), error);
                }
            }
        } else if (experiment.isEnabled()) {
            try {
                experiment.setEnabled(false);
                experiment.onDisable();
            } catch (Exception error) {
                sendWarningMessage("Failed to disable experiment: "
                        + experiment.getId(), error);
            }
        }
        OpenCreative.getPlugin().getConfig().set("experiments." + experiment.getId(), enabled);
        OpenCreative.getPlugin().saveConfig();
        return true;
    }

}
