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
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.jetbrains.annotations.NotNull;
import ua.mcchickenstudio.opencreative.coding.arguments.Arguments;
import ua.mcchickenstudio.opencreative.coding.blocks.actions.ActionType;
import ua.mcchickenstudio.opencreative.coding.blocks.actions.Target;
import ua.mcchickenstudio.opencreative.coding.blocks.actions.worldactions.WorldAction;
import ua.mcchickenstudio.opencreative.coding.blocks.executors.Executor;

import static ua.mcchickenstudio.opencreative.utils.BlockUtils.isOutOfBorders;

public final class SetBlocksAreaTypeAction extends WorldAction {
    public SetBlocksAreaTypeAction(Executor executor, Target target, int x, Arguments args) {
        super(executor, target, x, args);
    }

    @Override
    protected void execute() {
        if (!getArguments().pathExists("first") || !getArguments().pathExists("second")) {
            return;
        }
        Location firstLocation = getArguments().getLocation("first", getPlanet().getTerritory().getSpawnLocation(), this);
        Location secondLocation = getArguments().getLocation("second", getPlanet().getTerritory().getSpawnLocation(), this);
        Material type = getArguments().getBlockMaterial("type", Material.AIR, this);
        int minX = Math.min(firstLocation.getBlockX(), secondLocation.getBlockX());
        int minY = Math.min(firstLocation.getBlockY(), secondLocation.getBlockY());
        int minZ = Math.min(firstLocation.getBlockZ(), secondLocation.getBlockZ());
        int maxX = Math.max(firstLocation.getBlockX(), secondLocation.getBlockX());
        int maxY = Math.max(firstLocation.getBlockY(), secondLocation.getBlockY());
        int maxZ = Math.max(firstLocation.getBlockZ(), secondLocation.getBlockZ());
        for (int x = minX; x <= maxX; x++) {
            for (int y = minY; y <= maxY; y++) {
                for (int z = minZ; z <= maxZ; z++) {
                    if (getPlanet().getLimits().cantModifyBlock(this)) {
                        return;
                    }
                    Block block = getWorld().getBlockAt(x, y, z);
                    if (type.isBlock() && !isOutOfBorders(block.getLocation())) {
                        block.setType(type, false);
                    }
                }
            }
        }
        getPlanet().getLimits().clearModifiedBlocks();
    }

    @Override
    public @NotNull ActionType getActionType() {
        return ActionType.WORLD_SET_BLOCKS_AREA_TYPE;
    }
}
