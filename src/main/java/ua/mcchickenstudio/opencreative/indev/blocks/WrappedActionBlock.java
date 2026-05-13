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

package ua.mcchickenstudio.opencreative.indev.blocks;

import org.bukkit.entity.Entity;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import ua.mcchickenstudio.opencreative.coding.arguments.Arguments;
import ua.mcchickenstudio.opencreative.coding.blocks.actions.ActionsHandler;
import ua.mcchickenstudio.opencreative.coding.blocks.actions.Target;

/**
 * <h1>WrappedActionBlock</h1>
 * This class represents an action metadata provider
 * for any action block, like {@link ua.mcchickenstudio.opencreative.indev.blocks.actions.ActionBlock ActionBlock},
 * {@link ua.mcchickenstudio.opencreative.indev.blocks.multiactions.MultiActionBlock MultiActionBlock},
 * {@link ua.mcchickenstudio.opencreative.indev.blocks.conditions.ConditionBlock ConditionBlock}.
 * It stores {@link WrappedActionBlock#target target}, {@link WrappedActionBlock#arguments arguments} and
 * coding block coordinates in developers world from {@link WrappedCodingBlock}.
 * @see WrappedActionBlock#execute(Entity, ActionsHandler) 
 * @param <T>
 */
public abstract class WrappedActionBlock<T extends CodingBlock> extends WrappedCodingBlock<T> {

    protected final @NotNull Target target;
    protected final @NotNull Arguments arguments;

    public WrappedActionBlock(@NotNull T codingBlock, @NotNull Target target, @NotNull Arguments arguments, int x, int y, int z) {
        super(codingBlock, x, y, z);
        this.target = target;
        this.arguments = arguments;
    }

    public @NotNull Target getTarget() {
        return target;
    }

    public @NotNull Arguments getArguments() {
        return arguments;
    }

    public abstract void execute(@Nullable Entity target, @NotNull ActionsHandler actionsHandler);

}
