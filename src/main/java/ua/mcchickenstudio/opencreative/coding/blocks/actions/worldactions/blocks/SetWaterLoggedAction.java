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

package ua.mcchickenstudio.opencreative.coding.blocks.actions.worldactions.blocks;

import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.block.data.Waterlogged;
import org.jetbrains.annotations.NotNull;
import ua.mcchickenstudio.opencreative.coding.arguments.Arguments;
import ua.mcchickenstudio.opencreative.coding.blocks.actions.ActionType;
import ua.mcchickenstudio.opencreative.coding.blocks.actions.Target;
import ua.mcchickenstudio.opencreative.coding.blocks.actions.worldactions.WorldAction;
import ua.mcchickenstudio.opencreative.coding.blocks.executors.Executor;

import java.util.List;

public final class SetWaterLoggedAction extends WorldAction {
    public SetWaterLoggedAction(Executor executor, Target target, int x, Arguments args) {
        super(executor, target, x, args);
    }

    @Override
    protected void execute() {
        List<Location> locations = getArguments().getLocationList("locations", this);
        boolean water = getArguments().getBoolean("water", true, this);
        for (Location location : locations) {
            if (getPlanet().getLimits().cantModifyBlock(this)) {
                return;
            }
            Block block = location.getBlock();
            if (block.getState() instanceof Waterlogged waterlogged) {
                waterlogged.setWaterlogged(water);
                block.setBlockData(waterlogged);
            }
        }
        getPlanet().getLimits().clearModifiedBlocks();

    }

    @Override
    public @NotNull ActionType getActionType() {
        return ActionType.WORLD_SET_BLOCK_WATERLOGGED;
    }
}
