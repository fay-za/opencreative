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

package ua.mcchickenstudio.opencreative.coding.blocks.conditions.playerconditions;

import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import ua.mcchickenstudio.opencreative.coding.arguments.Arguments;
import ua.mcchickenstudio.opencreative.coding.blocks.actions.Action;
import ua.mcchickenstudio.opencreative.coding.blocks.actions.ActionCategory;
import ua.mcchickenstudio.opencreative.coding.blocks.actions.Target;
import ua.mcchickenstudio.opencreative.coding.blocks.conditions.Condition;
import ua.mcchickenstudio.opencreative.coding.blocks.executors.Executor;

import java.util.ArrayList;
import java.util.List;

public abstract class PlayerCondition extends Condition {

    /**
     * Creates a Condition with linked executor and specified arguments.
     *
     * @param executor Executor where this action will be added.
     * @param x        X from Action's block location in developers planet.
     * @param args     List of arguments for action.
     */
    public PlayerCondition(Executor executor, Target target, int x, Arguments args, List<Action> actions, List<Action> reactions, boolean isOpposed) {
        super(executor, target, x, args, actions, reactions, isOpposed);
    }

    @Override
    public final boolean check(Entity entity) {
        if (entity == null) return false;
        if (!entity.getWorld().equals(getPlanet().getWorld())) return false;
        if (entity instanceof Player player) {
            return checkPlayer(player);
        }
        return false;
    }

    public abstract boolean checkPlayer(@NotNull Player player);

    public @NotNull ActionCategory getActionCategory() {
        return ActionCategory.PLAYER_CONDITION;
    }

    protected List<Player> getPlayers(List<Entity> selection) {
        List<Player> players = new ArrayList<>();
        for (Entity entity : selection) {
            if (entity instanceof Player) {
                players.add((Player) entity);
            }
        }
        return players;
    }
}
