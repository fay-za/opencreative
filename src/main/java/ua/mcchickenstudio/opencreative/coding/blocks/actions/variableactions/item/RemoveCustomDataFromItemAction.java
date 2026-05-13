package ua.mcchickenstudio.opencreative.coding.blocks.actions.variableactions.item;

import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;
import ua.mcchickenstudio.opencreative.OpenCreative;
import ua.mcchickenstudio.opencreative.coding.arguments.Arguments;
import ua.mcchickenstudio.opencreative.coding.blocks.actions.ActionType;
import ua.mcchickenstudio.opencreative.coding.blocks.actions.Target;
import ua.mcchickenstudio.opencreative.coding.blocks.actions.variableactions.VariableAction;
import ua.mcchickenstudio.opencreative.coding.blocks.executors.Executor;
import ua.mcchickenstudio.opencreative.coding.variables.VariableLink;

import java.util.List;

public final class RemoveCustomDataFromItemAction extends VariableAction {
    public RemoveCustomDataFromItemAction(Executor executor, Target target, int x, Arguments args) {
        super(executor, target, x, args);
    }

    @Override
    protected void execute() {
        VariableLink result = getArguments().getVariableLink("result", this);
        ItemStack item = getArguments().getItem("item", new ItemStack(Material.AIR), this).clone();
        ItemMeta meta = item.getItemMeta();
        if (meta == null) {
            return;
        }
        List<String> keys = getArguments().getTextList("keys", this);
        for (String key : keys) {
            meta.getPersistentDataContainer().remove(
                    new NamespacedKey(OpenCreative.getPlugin(), "custom_" + key)
            );
        }
        item.setItemMeta(meta);
        setVarValue(result, item);
    }

    @Override
    public @NotNull ActionType getActionType() {
        return ActionType.VAR_REMOVE_CUSTOM_DATA_FROM_ITEM;
    }
}
