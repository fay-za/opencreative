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

import org.jetbrains.annotations.NotNull;

public final class DisabledWatchdog implements StabilityManager {

    @Override
    public void init() {

    }

    @Override
    public boolean isEnabled() {
        return true;
    }

    @Override
    public @NotNull StabilityState getDatabaseState() {
        return StabilityState.FINE;
    }

    @Override
    public @NotNull StabilityState getMemoryState() {
        return StabilityState.FINE;
    }

    @Override
    public @NotNull StabilityState getStorageState() {
        return StabilityState.FINE;
    }

    @Override
    public @NotNull StabilityState getTicksState() {
        return StabilityState.FINE;
    }

    @Override
    public String getName() {
        return "Disabled Watchdog";
    }


    @Override
    public @NotNull StabilityState getState() {
        return StabilityState.FINE;
    }
}
