package ua.mcchickenstudio.opencreative.coding.blocks.actions.variableactions.list;

import org.jetbrains.annotations.NotNull;
import ua.mcchickenstudio.opencreative.coding.arguments.Arguments;
import ua.mcchickenstudio.opencreative.coding.blocks.actions.ActionType;
import ua.mcchickenstudio.opencreative.coding.blocks.actions.Target;
import ua.mcchickenstudio.opencreative.coding.blocks.actions.variableactions.VariableAction;
import ua.mcchickenstudio.opencreative.coding.blocks.executors.Executor;
import ua.mcchickenstudio.opencreative.coding.variables.VariableLink;

import java.util.List;

public final class GetLastValueFromListAction extends VariableAction {
    public GetLastValueFromListAction(Executor executor, Target target, int x, Arguments args) {
        super(executor, target, x, args);
    }

    @Override
    protected void execute() {
        VariableLink result = getArguments().getVariableLink("variable", this);
        List<Object> target = getArguments().getList("list", this);

        setVarValue(result, target.getLast());
    }

    @Override
    public @NotNull ActionType getActionType() {
        return ActionType.VAR_GET_LAST_FROM_LIST;
    }
}
