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

package ua.mcchickenstudio.opencreative.coding.blocks.conditions.playerconditions.blocks;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import ua.mcchickenstudio.opencreative.coding.arguments.Arguments;
import ua.mcchickenstudio.opencreative.coding.blocks.actions.Action;
import ua.mcchickenstudio.opencreative.coding.blocks.actions.ActionType;
import ua.mcchickenstudio.opencreative.coding.blocks.actions.Target;
import ua.mcchickenstudio.opencreative.coding.blocks.conditions.playerconditions.PlayerCondition;
import ua.mcchickenstudio.opencreative.coding.blocks.executors.Executor;

import java.util.List;

public final class IsLookingAtBlockCondition extends PlayerCondition {

    public IsLookingAtBlockCondition(Executor executor, Target target, int x, Arguments args, List<Action> actions, List<Action> reactions, boolean isOpposed) {
        super(executor, target, x, args, actions, reactions, isOpposed);
    }

    @Override
    public boolean checkPlayer(@NotNull Player player) {
        List<ItemStack> blocks = getArguments().getItemList("blocks", this);
        List<Location> locations = getArguments().getLocationList("locations", this);
        if (blocks.isEmpty() && locations.isEmpty()) {
            return false;
        }
        Block block = player.getTargetBlockExact(30);
        if (block == null) {
            return false;
        }
        Material blockType = block.getType();
        for (ItemStack checkBlock : blocks) {
            if (blockType == checkBlock.getType()) {
                return true;
            }
        }
        double radius = getArguments().getDouble("radius", 0.5, this);
        Location location = block.getLocation();
        for (Location checkLocation : locations) {
            if (location.distance(checkLocation) <= radius) {
                return true;
            }
        }
        return false;
    }

    @Override
    public @NotNull ActionType getActionType() {
        return ActionType.IF_PLAYER_LOOKS_AT_BLOCK;
    }
}
