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
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;
import ua.mcchickenstudio.opencreative.coding.arguments.Arguments;
import ua.mcchickenstudio.opencreative.coding.blocks.actions.ActionType;
import ua.mcchickenstudio.opencreative.coding.blocks.actions.Target;
import ua.mcchickenstudio.opencreative.coding.blocks.actions.worldactions.WorldAction;
import ua.mcchickenstudio.opencreative.coding.blocks.executors.Executor;

import static ua.mcchickenstudio.opencreative.utils.BlockUtils.isOutOfBorders;

public final class CopyBlocksAction extends WorldAction {
    public CopyBlocksAction(Executor executor, Target target, int x, Arguments args) {
        super(executor, target, x, args);
    }

    @Override
    protected void execute() {
        if (!getArguments().pathExists("first") || !getArguments().pathExists("second") || !getArguments().pathExists("from") || !getArguments().pathExists("where")) {
            return;
        }
        Location first = getArguments().getLocation("first", getPlanet().getTerritory().getSpawnLocation(), this);
        Location second = getArguments().getLocation("second", getPlanet().getTerritory().getSpawnLocation(), this);
        Location from = getArguments().getLocation("from", getPlanet().getTerritory().getSpawnLocation(), this);
        Location where = getArguments().getLocation("where", getPlanet().getTerritory().getSpawnLocation(), this);

        int minX = Math.min(first.getBlockX(), second.getBlockX());
        int minY = Math.min(first.getBlockY(), second.getBlockY());
        int minZ = Math.min(first.getBlockZ(), second.getBlockZ());

        int maxX = Math.max(first.getBlockX(), second.getBlockX());
        int maxY = Math.max(first.getBlockY(), second.getBlockY());
        int maxZ = Math.max(first.getBlockZ(), second.getBlockZ());

        /*
         * Example
         * FIRST: 2,7,19
         * SECOND: 1,6,18
         * FROM: 0,6,17
         * WHERE: -2,7,21
         *
         * Result
         * FIRST: -2,7,22
         * SECOND: -1,8,23
         *
         * x = x(old) + (WHERE - FROM)
         */

        // 0,1,-13
        // -76 63 -86
        Vector whereFromSubtraction = where.subtract(from).toVector();
        for (int x = minX; x <= maxX; x++) {
            for (int y = minY; y <= maxY; y++) {
                for (int z = minZ; z <= maxZ; z++) {
                    if (getPlanet().getLimits().cantModifyBlock(this)) {
                        return;
                    }
                    Location oldLocation = new Location(getWorld(), x, y, z);
                    Location newLocation = oldLocation.clone().add(whereFromSubtraction);
                    if (isOutOfBorders(oldLocation) || isOutOfBorders(newLocation)) {
                        continue;
                    }
                    Block newBlock = newLocation.getBlock();
                    newBlock.setType(oldLocation.getBlock().getType(), false);
                    newBlock.setBlockData(oldLocation.getBlock().getBlockData(), false);
                }
            }
        }
        getPlanet().getLimits().clearModifiedBlocks();
    }

    @Override
    public @NotNull ActionType getActionType() {
        return ActionType.WORLD_COPY_BLOCKS;
    }
}
