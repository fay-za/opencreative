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

package ua.mcchickenstudio.opencreative.coding.blocks.conditions.worldconditions.world;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import ua.mcchickenstudio.opencreative.coding.arguments.Arguments;
import ua.mcchickenstudio.opencreative.coding.blocks.actions.Action;
import ua.mcchickenstudio.opencreative.coding.blocks.actions.ActionType;
import ua.mcchickenstudio.opencreative.coding.blocks.actions.Target;
import ua.mcchickenstudio.opencreative.coding.blocks.conditions.worldconditions.WorldCondition;
import ua.mcchickenstudio.opencreative.coding.blocks.executors.Executor;

import java.util.List;

public final class IsWorldPlayerInWorldCondition extends WorldCondition {

    public IsWorldPlayerInWorldCondition(Executor executor, Target target, int x, Arguments args, List<Action> actions, List<Action> reactions, boolean isOpposed) {
        super(executor, target, x, args, actions, reactions, isOpposed);
    }

    @Override
    public boolean check() {
        List<String> players = getArguments().getTextList("players", this);
        boolean allElements = getArguments().getBoolean("all", true, this);
        String consider = getArguments().getText("consider", "all", this);
        boolean inWorld = false;
        for (String requiredPlayer : players) {
            Player player = Bukkit.getPlayerExact(requiredPlayer);
            if (player == null) {
                if (allElements) {
                    return false;
                }
                continue;
            }
            if (getWorld().equals(player.getWorld())) {
                // If player in build world
                if (consider.equals("all") || consider.equals("build")) {
                    inWorld = true;
                } else if (allElements) {
                    return false;
                }
            } else if (getWorld().equals(getPlanet().getDevPlanet().getWorld())) {
                // If player in dev world
                if (consider.equals("all") || consider.equals("dev")) {
                    inWorld = true;
                } else if (allElements) {
                    return false;
                }
            } else {
                if (allElements) {
                    return false;
                }
            }
        }
        return inWorld;
    }

    @Override
    public @NotNull ActionType getActionType() {
        return ActionType.IF_WORLD_IS_PLAYER_IN;
    }
}
