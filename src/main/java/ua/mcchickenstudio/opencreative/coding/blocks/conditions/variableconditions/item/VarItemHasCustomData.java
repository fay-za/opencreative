package ua.mcchickenstudio.opencreative.coding.blocks.conditions.variableconditions.item;

import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.NotNull;
import ua.mcchickenstudio.opencreative.OpenCreative;
import ua.mcchickenstudio.opencreative.coding.arguments.Arguments;
import ua.mcchickenstudio.opencreative.coding.blocks.actions.Action;
import ua.mcchickenstudio.opencreative.coding.blocks.actions.ActionType;
import ua.mcchickenstudio.opencreative.coding.blocks.actions.Target;
import ua.mcchickenstudio.opencreative.coding.blocks.conditions.variableconditions.VariableCondition;
import ua.mcchickenstudio.opencreative.coding.blocks.executors.Executor;

import java.util.List;

public final class VarItemHasCustomData extends VariableCondition {
    public VarItemHasCustomData(Executor executor, Target target, int x, Arguments args, List<Action> actions, List<Action> reactions, boolean isOpposed) {
        super(executor, target, x, args, actions, reactions, isOpposed);
    }

    @Override
    public boolean check() {
        ItemStack item = getArguments().getItem("item", new ItemStack(Material.AIR), this);
        ItemMeta meta = item.getItemMeta();
        if (meta == null) {
            return false;
        }
        String key = getArguments().getText("key", "key", this);
        List<String> values = getArguments().getTextList("values", this);
        String data = meta.getPersistentDataContainer().get(
                new NamespacedKey(OpenCreative.getPlugin(), "custom_" + key),
                PersistentDataType.STRING);
        if (data == null) {
            return false;
        }
        for (String text : values) {
            if (data.equals(text)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public @NotNull ActionType getActionType() {
        return ActionType.IF_VAR_ITEM_HAS_CUSTOM_DATA;
    }
}
