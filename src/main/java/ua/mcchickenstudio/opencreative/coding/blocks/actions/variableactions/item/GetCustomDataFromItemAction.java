package ua.mcchickenstudio.opencreative.coding.blocks.actions.variableactions.item;

import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.NotNull;
import ua.mcchickenstudio.opencreative.OpenCreative;
import ua.mcchickenstudio.opencreative.coding.arguments.Arguments;
import ua.mcchickenstudio.opencreative.coding.blocks.actions.ActionType;
import ua.mcchickenstudio.opencreative.coding.blocks.actions.Target;
import ua.mcchickenstudio.opencreative.coding.blocks.actions.variableactions.VariableAction;
import ua.mcchickenstudio.opencreative.coding.blocks.executors.Executor;
import ua.mcchickenstudio.opencreative.coding.variables.VariableLink;

public final class GetCustomDataFromItemAction extends VariableAction {
    public GetCustomDataFromItemAction(Executor executor, Target target, int x, Arguments args) {
        super(executor, target, x, args);
    }

    @Override
    protected void execute() {
        VariableLink var = getArguments().getVariableLink("variable", this);

        ItemStack item = getArguments().getItem(
                "item",
                getArguments().getItem("variable", new ItemStack(Material.AIR),this),
                this
        );
        ItemMeta meta = item.getItemMeta();
        if (meta == null) {
            return;
        }
        String key = getArguments().getText("key", "key", this);

        String value = meta.getPersistentDataContainer().get(
                new NamespacedKey(OpenCreative.getPlugin(), "custom_" + key),
                PersistentDataType.STRING
        );
        if (value == null) {
            return;
        }
        setVarValue(var, value);
    }

    @Override
    public @NotNull ActionType getActionType() {
        return ActionType.VAR_GET_CUSTOM_DATA_FROM_ITEM;
    }
}
