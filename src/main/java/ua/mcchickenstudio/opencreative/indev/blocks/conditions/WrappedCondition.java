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

package ua.mcchickenstudio.opencreative.indev.blocks.conditions;

import org.bukkit.entity.Entity;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import ua.mcchickenstudio.opencreative.coding.arguments.Arguments;
import ua.mcchickenstudio.opencreative.coding.blocks.actions.ActionsHandler;
import ua.mcchickenstudio.opencreative.coding.blocks.actions.Target;
import ua.mcchickenstudio.opencreative.indev.blocks.WrappedActionBlock;
import ua.mcchickenstudio.opencreative.indev.blocks.actions.ActionBlock;

import java.util.List;

public class WrappedCondition extends WrappedActionBlock<ConditionBlock> {

    private final List<ActionBlock> actions;
    private final List<ActionBlock> reactions;
    private final boolean isOpposed;

    public WrappedCondition(ConditionBlock conditionBlock, @NotNull Target target, @NotNull Arguments arguments, int x, int y, int z, List<ActionBlock> actions, List<ActionBlock> reactions, boolean isOpposed) {
        super(conditionBlock, target, arguments, x, y, z);
        this.actions = actions;
        this.reactions = reactions;
        this.isOpposed = isOpposed;
    }

    @Override
    public void execute(@Nullable Entity target, @NotNull ActionsHandler actionsHandler) {
        codingBlock.execute(target, actionsHandler, arguments, actions, reactions, isOpposed);
    }
}
