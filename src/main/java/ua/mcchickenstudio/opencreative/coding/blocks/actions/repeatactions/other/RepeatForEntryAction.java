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

import org.jetbrains.annotations.NotNull;
import ua.mcchickenstudio.opencreative.coding.arguments.Arguments;
import ua.mcchickenstudio.opencreative.coding.blocks.actions.Action;
import ua.mcchickenstudio.opencreative.coding.blocks.actions.ActionType;
import ua.mcchickenstudio.opencreative.coding.blocks.actions.Target;
import ua.mcchickenstudio.opencreative.coding.blocks.actions.repeatactions.RepeatAction;
import ua.mcchickenstudio.opencreative.coding.blocks.executors.Executor;
import ua.mcchickenstudio.opencreative.coding.variables.ValueType;
import ua.mcchickenstudio.opencreative.coding.variables.VariableLink;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public final class RepeatForEntryAction extends RepeatAction {

    public RepeatForEntryAction(Executor executor, Target target, int x, Arguments args, List<Action> actions) {
        super(executor, target, x, args, actions);
    }

    @Override
    public boolean checkCanContinue() {
        VariableLink keyLink = getArguments().getVariableLink("key", this);
        VariableLink valueLink = getArguments().getVariableLink("value", this);

        Map<Object, Object> map = getArguments().getMap("map", this);
        if ((keyLink == null && valueLink == null) || map.isEmpty()) {
            return false;
        }
        int index = getArguments().getInt("index", 1, this);
        if (index > map.size()) {
            arguments.removeArgumentValue("index");
            return false;
        }
        List<Map.Entry<Object, Object>> entries = new ArrayList<>(map.entrySet());
        Object key = entries.get(index - 1).getKey();
        Object value = entries.get(index - 1).getValue();
        setVarValue(keyLink, key);
        setVarValue(valueLink, value);
        getArguments().setArgumentValue("index", ValueType.NUMBER, index + 1);
        return true;
    }

    @Override
    public @NotNull ActionType getActionType() {
        return ActionType.REPEAT_FOR_ENTRY;
    }
}

