package ua.mcchickenstudio.opencreative.coding.blocks.actions.variableactions.location;

import org.bukkit.Location;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;
import ua.mcchickenstudio.opencreative.coding.arguments.Arguments;
import ua.mcchickenstudio.opencreative.coding.blocks.actions.ActionType;
import ua.mcchickenstudio.opencreative.coding.blocks.actions.Target;
import ua.mcchickenstudio.opencreative.coding.blocks.actions.variableactions.VariableAction;
import ua.mcchickenstudio.opencreative.coding.blocks.executors.Executor;
import ua.mcchickenstudio.opencreative.coding.variables.VariableLink;

public class MoveLocationByVector extends VariableAction {
    public MoveLocationByVector(Executor executor, Target target, int x, Arguments args) {
        super(executor, target, x, args);
    }

    @Override
    protected void execute() {
        VariableLink link = getArguments().getVariableLink("variable", this);
        Location loc = getArguments().getLocation("target", getDefaultLocation(), this);
        Vector vec = getArguments().getVector("vector",  new Vector(0,0,0),this);
        double len = getArguments().getDouble("distance", vec.length(), this);

        if (vec.length() != 0) {
            vec.normalize().multiply(len);
        }

        setVarValue(link, loc.add(vec));
    }

    @Override
    public @NotNull ActionType getActionType() {
        return ActionType.VAR_MOVE_LOCATION_BY_VECTOR;
    }
}
