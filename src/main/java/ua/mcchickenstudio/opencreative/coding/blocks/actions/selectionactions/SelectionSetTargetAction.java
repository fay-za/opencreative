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

package ua.mcchickenstudio.opencreative.coding.blocks.actions.selectionactions;

import org.bukkit.entity.Entity;
import org.jetbrains.annotations.NotNull;
import ua.mcchickenstudio.opencreative.coding.arguments.Arguments;
import ua.mcchickenstudio.opencreative.coding.blocks.actions.ActionCategory;
import ua.mcchickenstudio.opencreative.coding.blocks.actions.ActionType;
import ua.mcchickenstudio.opencreative.coding.blocks.actions.Target;
import ua.mcchickenstudio.opencreative.coding.blocks.executors.Executor;

import java.util.List;
import java.util.Set;

public final class SelectionSetTargetAction extends SelectionAction {

    public SelectionSetTargetAction(Executor executor, int x, Arguments args, ActionCategory condition, ActionType conditionType, boolean isOpposed) {
        super(executor, x, args, condition, conditionType, isOpposed);
    }

    public SelectionSetTargetAction(Executor executor, int x, Arguments args, Target target) {
        super(executor, x, args, target);
    }

    @Override
    protected void modifyTargets(List<Entity> newTarget, Set<Entity> currentTarget) {
        currentTarget.clear();
        currentTarget.addAll(newTarget);
    }

    @Override
    public @NotNull ActionType getActionType() {
        return ActionType.SELECTION_SET;
    }
}
