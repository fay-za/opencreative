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
import ua.mcchickenstudio.opencreative.coding.variables.VariableLink;

import java.util.*;

public final class RemoveByValueFromListAction extends VariableAction {
    public RemoveByValueFromListAction(Executor executor, Target target, int x, Arguments args) {
        super(executor, target, x, args);
    }

    @Override
    protected void execute() {

        List<Object> list = new ArrayList<>(getArguments().getList("variable", this));
        List<Object> elements = getArguments().getList("elements", this);
        if (list.isEmpty() || elements.isEmpty()) return;
        VariableLink variable = getArguments().getVariableLink("variable", this);
        String deletionType = getArguments().getText("deletion", "all", this);

        if (cannotChangeListElements(elements.size())) {
            return;
        }

        Set<Integer> indexes = new LinkedHashSet<>();
        switch (deletionType.toLowerCase()) {
            case "first" -> {
                for (Object element : elements) {
                    int index = list.indexOf(element);
                    if (index != -1) indexes.add(index);
                }
            }
            case "last" -> {
                for (Object element : elements) {
                    int index = list.lastIndexOf(element);
                    if (index != -1) indexes.add(index);
                }
            }
            default -> {
                for (Object element : elements) {
                    int index = 0;
                    for (Object listElement : list) {
                        if (element.equals(listElement)) {
                            indexes.add(index);
                        }
                        index++;
                    }
                }
            }
        }

        if (cannotChangeListElements(indexes.size())) {
            return;
        }
        changeListElementsChangesAmount(indexes.size());

        List<Integer> sortedIndexes = new ArrayList<>(indexes);
        sortedIndexes.sort(Comparator.reverseOrder());
        for (int index : sortedIndexes) {
            if (index < list.size()) list.remove(index);
        }
        setVarValue(variable, list);

    }

    @Override
    public @NotNull ActionType getActionType() {
        return ActionType.VAR_REMOVE_BY_VALUE_FROM_LIST;
    }
}
