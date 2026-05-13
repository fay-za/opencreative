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

package ua.mcchickenstudio.opencreative.coding.blocks.actions.variableactions.map;

import org.jetbrains.annotations.NotNull;
import ua.mcchickenstudio.opencreative.coding.arguments.Arguments;
import ua.mcchickenstudio.opencreative.coding.blocks.actions.ActionType;
import ua.mcchickenstudio.opencreative.coding.blocks.actions.Target;
import ua.mcchickenstudio.opencreative.coding.blocks.actions.variableactions.VariableAction;
import ua.mcchickenstudio.opencreative.coding.blocks.executors.Executor;
import ua.mcchickenstudio.opencreative.coding.exceptions.CollectionWithCollectionException;
import ua.mcchickenstudio.opencreative.coding.variables.VariableLink;

import java.util.Map;

public final class PutIntoMapAction extends VariableAction {
    public PutIntoMapAction(Executor executor, Target target, int x, Arguments args) {
        super(executor, target, x, args);
    }

    @Override
    protected void execute() {
        VariableLink variable = getArguments().getVariableLink("variable", this);
        Map<Object, Object> map = getArguments().getMap("variable", this);
        Object key = getArguments().getValue("key", this);
        Object value = getArguments().getValue("value", this);
        if (cannotChangeListElements(1)) {
            return;
        }
        changeListElementsChangesAmount(1);
        if (value instanceof Map<?, ?>) {
            throw new CollectionWithCollectionException(value.getClass(), value.getClass());
        }
        map.put(key, value);
        setVarValue(variable, map);
    }

    @Override
    public @NotNull ActionType getActionType() {
        return ActionType.VAR_PUT_INTO_MAP;
    }
}
