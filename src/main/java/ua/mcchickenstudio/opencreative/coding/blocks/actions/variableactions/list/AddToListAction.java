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

package ua.mcchickenstudio.opencreative.coding.blocks.actions.variableactions.list;

import org.jetbrains.annotations.NotNull;
import ua.mcchickenstudio.opencreative.coding.arguments.Arguments;
import ua.mcchickenstudio.opencreative.coding.blocks.actions.ActionType;
import ua.mcchickenstudio.opencreative.coding.blocks.actions.Target;
import ua.mcchickenstudio.opencreative.coding.blocks.actions.variableactions.VariableAction;
import ua.mcchickenstudio.opencreative.coding.blocks.executors.Executor;
import ua.mcchickenstudio.opencreative.coding.exceptions.CollectionWithCollectionException;
import ua.mcchickenstudio.opencreative.coding.exceptions.TooLongTextException;
import ua.mcchickenstudio.opencreative.coding.variables.VariableLink;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

public final class AddToListAction extends VariableAction {
    public AddToListAction(Executor executor, Target target, int x, Arguments args) {
        super(executor, target, x, args);
    }

    @Override
    protected void execute() {
        VariableLink variable = getArguments().getVariableLink("variable", this);
        List<Object> list = new ArrayList<>(getArguments().getList("variable", this));
        List<Object> elements = getArguments().getList("elements", this);
        if (cannotChangeListElements(elements.size())) {
            return;
        }
        changeListElementsChangesAmount(elements.size());
        for (Object element : elements) {
            if (element instanceof Collection<?> || element instanceof Map<?, ?>) {
                throw new CollectionWithCollectionException(list.getClass(), element.getClass());
            }
            if (element instanceof String text) {
                if (text.length() > 200) {
                    throw new TooLongTextException(200);
                }
            }
            list.add(element);
        }
        setVarValue(variable, list);
    }

    @Override
    public @NotNull ActionType getActionType() {
        return ActionType.VAR_ADD_TO_LIST;
    }
}
