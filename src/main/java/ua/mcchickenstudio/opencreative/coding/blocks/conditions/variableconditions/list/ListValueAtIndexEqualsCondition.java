package ua.mcchickenstudio.opencreative.coding.blocks.conditions.variableconditions.list;

import org.jetbrains.annotations.NotNull;
import ua.mcchickenstudio.opencreative.coding.arguments.Arguments;
import ua.mcchickenstudio.opencreative.coding.blocks.actions.Action;
import ua.mcchickenstudio.opencreative.coding.blocks.actions.ActionType;
import ua.mcchickenstudio.opencreative.coding.blocks.actions.Target;
import ua.mcchickenstudio.opencreative.coding.blocks.conditions.variableconditions.VariableCondition;
import ua.mcchickenstudio.opencreative.coding.blocks.executors.Executor;

import java.util.List;

public final class ListValueAtIndexEqualsCondition extends VariableCondition {
    public ListValueAtIndexEqualsCondition(Executor executor, Target target, int x, Arguments args, List<Action> actions, List<Action> reactions, boolean isOpposed) {
        super(executor, target, x, args, actions, reactions, isOpposed);
    }

    @Override
    public boolean check() {
        List<Object> list = getArguments().getList("list", this);
        int index = getArguments().getInt("index", 1, this);
        List<Object> values = getArguments().getList("values", this);

        Object element = list.get(index-1);
        for (Object value : values) {
            if (value.equals(element)) {
                return true;
            }
        }

        return false;
    }

    @Override
    public @NotNull ActionType getActionType() {
        return ActionType.IF_VAR_LIST_VALUE_AT_INDEX_EQUALS;
    }
}
