package ua.mcchickenstudio.opencreative.coding.blocks.actions.variableactions.item;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import ua.mcchickenstudio.opencreative.coding.arguments.Arguments;
import ua.mcchickenstudio.opencreative.coding.blocks.actions.ActionType;
import ua.mcchickenstudio.opencreative.coding.blocks.actions.Target;
import ua.mcchickenstudio.opencreative.coding.blocks.actions.variableactions.VariableAction;
import ua.mcchickenstudio.opencreative.coding.blocks.executors.Executor;
import ua.mcchickenstudio.opencreative.coding.variables.VariableLink;

public final class GetItemById extends VariableAction {
    public GetItemById(Executor executor, Target target, int x, Arguments args) {
        super(executor, target, x, args);
    }

    @Override
    protected void execute() {
        VariableLink variable = getArguments().getVariableLink("variable", this);
        String id = getArguments().getText("id", "air", this);
        ItemStack item = new ItemStack(Material.valueOf(id.toUpperCase()));
        setVarValue(variable, item);
    }

    @Override
    public @NotNull ActionType getActionType() {
        return ActionType.VAR_CREATE_ITEM_BY_ID;
    }
}
