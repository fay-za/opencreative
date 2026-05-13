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


package ua.mcchickenstudio.opencreative.coding.blocks.actions.repeatactions.other;

import org.bukkit.Location;
import org.jetbrains.annotations.NotNull;
import ua.mcchickenstudio.opencreative.coding.arguments.Arguments;
import ua.mcchickenstudio.opencreative.coding.blocks.actions.Action;
import ua.mcchickenstudio.opencreative.coding.blocks.actions.ActionType;
import ua.mcchickenstudio.opencreative.coding.blocks.actions.Target;
import ua.mcchickenstudio.opencreative.coding.blocks.actions.repeatactions.RepeatAction;
import ua.mcchickenstudio.opencreative.coding.blocks.executors.Executor;
import ua.mcchickenstudio.opencreative.coding.variables.ValueType;
import ua.mcchickenstudio.opencreative.coding.variables.VariableLink;

import java.util.List;

public final class RepeatForBlocksAction extends RepeatAction {

    public RepeatForBlocksAction(Executor executor, Target target, int x, Arguments args, List<Action> actions) {
        super(executor, target, x, args, actions);
    }

    @Override
    public boolean checkCanContinue() {

        VariableLink link = getArguments().getVariableLink("variable", this);
        if (!getArguments().pathExists("first") || !getArguments().pathExists("second") || link == null) {
            return false;
        }

        Location first = getArguments().getLocation("first", getPlanet().getTerritory().getSpawnLocation(), this);
        Location second = getArguments().getLocation("second", getPlanet().getTerritory().getSpawnLocation(), this);

        int minX = Math.min(first.getBlockX(), second.getBlockX());
        int minY = Math.min(first.getBlockY(), second.getBlockY());
        int minZ = Math.min(first.getBlockZ(), second.getBlockZ());

        int maxX = Math.max(first.getBlockX(), second.getBlockX());
        int maxY = Math.max(first.getBlockY(), second.getBlockY());
        int maxZ = Math.max(first.getBlockZ(), second.getBlockZ());

        int count = (maxX - minX + 1) * (maxY - minY + 1) * (maxZ - minZ + 1);
        if (count > getPlanet().getLimits().getModifyingBlocksLimit()) {
            return false;
        }

        int currentX = arguments.getInt("current-x", minX, this);
        int currentY = arguments.getInt("current-y", minY, this);
        int currentZ = arguments.getInt("current-z", minZ - 1, this);

        currentZ++;
        if (currentZ > maxZ) {
            currentZ = minZ;
            currentX++;
            if (currentX > maxX) {
                currentX = minX;
                currentY++;
            }
        }

        if (currentY > maxY) {
            arguments.removeArgumentValue("current-x", "current-y", "current-z");
            return false;
        }

        setVarValue(link, new Location(getWorld(), currentX, currentY, currentZ));
        arguments.setArgumentValue("current-x", ValueType.NUMBER, currentX);
        arguments.setArgumentValue("current-y", ValueType.NUMBER, currentY);
        arguments.setArgumentValue("current-z", ValueType.NUMBER, currentZ);

        return true;
    }

    @Override
    public @NotNull ActionType getActionType() {
        return ActionType.REPEAT_FOR_BLOCKS;
    }
}

