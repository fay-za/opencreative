package ua.mcchickenstudio.opencreative.coding.blocks.actions.variableactions.vector;

import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;
import ua.mcchickenstudio.opencreative.coding.arguments.Arguments;
import ua.mcchickenstudio.opencreative.coding.blocks.actions.ActionType;
import ua.mcchickenstudio.opencreative.coding.blocks.actions.Target;
import ua.mcchickenstudio.opencreative.coding.blocks.actions.variableactions.VariableAction;
import ua.mcchickenstudio.opencreative.coding.blocks.executors.Executor;
import ua.mcchickenstudio.opencreative.coding.variables.VariableLink;

public class GetVectorAllAction extends VariableAction {
    public GetVectorAllAction(Executor executor, Target target, int x, Arguments args) {
        super(executor, target, x, args);
    }

    @Override
    protected void execute() {
        Vector target = getArguments().getVector("target", new Vector(0,0,0), this);
        VariableLink x = getArguments().getVariableLink("x", this);
        VariableLink y = getArguments().getVariableLink("y", this);
        VariableLink z = getArguments().getVariableLink("z", this);

        if (getArguments().pathExists("target")) {
            if (x != null) setVarValue(x, target.getX());
            if (x != null) setVarValue(y, target.getY());
            if (x != null) setVarValue(z, target.getZ());
        }
    }

    @Override
    public @NotNull ActionType getActionType() {
        return ActionType.VAR_GET_VECTOR_ALL;
    }
}
