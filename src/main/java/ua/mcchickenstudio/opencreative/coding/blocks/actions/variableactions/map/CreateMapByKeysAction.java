package ua.mcchickenstudio.opencreative.coding.blocks.actions.variableactions.map;

import org.jetbrains.annotations.NotNull;
import ua.mcchickenstudio.opencreative.coding.arguments.Arguments;
import ua.mcchickenstudio.opencreative.coding.blocks.actions.ActionType;
import ua.mcchickenstudio.opencreative.coding.blocks.actions.Target;
import ua.mcchickenstudio.opencreative.coding.blocks.actions.variableactions.VariableAction;
import ua.mcchickenstudio.opencreative.coding.blocks.executors.Executor;
import ua.mcchickenstudio.opencreative.coding.variables.VariableLink;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class CreateMapByKeysAction extends VariableAction {
    public CreateMapByKeysAction(Executor executor, Target target, int x, Arguments args) {
        super(executor, target, x, args);
    }

    @Override
    protected void execute() {
        VariableLink variable = getArguments().getVariableLink("variable", this);
        List<Object> keys = getArguments().getList("keys", this);
        List<Object> values = getArguments().getList("values", this);

        Map<Object, Object> map = new LinkedHashMap<>();
        if (!keys.isEmpty() && !values.isEmpty()) {
            for (int i = 0; i < keys.size(); i++) {
                if (i != values.size()) {
                    map.put(keys.get(i), values.get(i));
                }
            }
        }
        setVarValue(variable, map);
    }

    @Override
    public @NotNull ActionType getActionType() {
        return ActionType.VAR_CREATE_MAP_BY_KEYS;
    }
}
