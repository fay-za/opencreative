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

package ua.mcchickenstudio.opencreative.coding.blocks.actions.worldactions.world;

import org.jetbrains.annotations.NotNull;
import ua.mcchickenstudio.opencreative.coding.arguments.Arguments;
import ua.mcchickenstudio.opencreative.coding.blocks.actions.ActionType;
import ua.mcchickenstudio.opencreative.coding.blocks.actions.Target;
import ua.mcchickenstudio.opencreative.coding.blocks.actions.worldactions.WorldAction;
import ua.mcchickenstudio.opencreative.coding.blocks.executors.Executor;

public final class SetWeatherAction extends WorldAction {
    public SetWeatherAction(Executor executor, Target target, int x, Arguments args) {
        super(executor, target, x, args);
    }

    @Override
    protected void execute() {
        String weather = getArguments().getText("weather", "clean", this);
        int duration = getArguments().getInt("duration", 6000, this);
        switch (weather.toLowerCase()) {
            case "storm": {
                getPlanet().getTerritory().getWorld().setStorm(true);
                if (duration >= 0) {
                    getPlanet().getTerritory().getWorld().setWeatherDuration(duration);
                }
                break;
            }
            case "thunder": {
                getPlanet().getTerritory().getWorld().setThundering(true);
                if (duration >= 0) {
                    getPlanet().getTerritory().getWorld().setThunderDuration(duration);
                }
                break;
            }
            default: {
                getPlanet().getTerritory().getWorld().setClearWeatherDuration(Math.max(duration, 0));
                break;
            }
        }
    }

    @Override
    public @NotNull ActionType getActionType() {
        return ActionType.WORLD_SET_WEATHER;
    }
}
