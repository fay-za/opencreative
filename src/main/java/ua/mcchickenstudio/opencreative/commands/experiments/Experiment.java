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
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

/**
 * <h1>Experiment</h1>
 * Represents an experimental feature, that can be enabled
 * in settings for testing new content.
 */
public abstract class Experiment {

    private boolean enabled = false;

    /**
     * Returns id of experiment.
     *
     * @return short id of experiment that will be used in settings.
     * <p>
     * It must be lower-snake-cased, for example: "future_update", "code_downloader".
     */
    public abstract @NotNull String getId();

    /**
     * Returns name of experiment, that will be used
     * for displaying in experiments list.
     *
     * @return name of experiment.
     */
    public abstract @NotNull String getName();

    /**
     * Returns description of experiment, that will
     * tell purpose of experiment and describes what it adds.
     *
     * @return description of content in experiment.
     */
    public abstract @NotNull String getDescription();

    /**
     * Handles command for experiment.
     *
     * @param sender sender of command.
     * @param args   arguments of command.
     */
    public abstract void handleCommand(@NotNull CommandSender sender, @NotNull String[] args);

    /**
     * Returns list of command suggestions for experiment.
     *
     * @param sender sender of command.
     * @param args   arguments of command.
     * @return list of command suggestions.
     */
    public abstract @Nullable List<String> tabCommand(@NotNull CommandSender sender, @NotNull String[] args);

    /**
     * Executes enable operations for experiment.
     */
    @SuppressWarnings("EmptyMethod")
    public void onEnable() {
    }

    /**
     * Executes disable operations for experiment.
     */
    public void onDisable() {
    }

    /**
     * Checks whether experiment is enabled or not.
     *
     * @return true - enabled, false - disabled.
     */
    public boolean isEnabled() {
        return enabled;
    }

    /**
     * Sets enabled state of experiment.
     *
     * @param enabled true - enabled, false - disabled.
     */
    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    @Override
    public String toString() {
        return "Experiment: " + getId() + " - " + enabled + " - " + getName() + " - " + getDescription();
    }

    @Override
    public int hashCode() {
        return getId().hashCode();
    }
}
