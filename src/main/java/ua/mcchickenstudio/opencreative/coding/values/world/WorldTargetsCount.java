package ua.mcchickenstudio.opencreative.coding.values.world;

import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import ua.mcchickenstudio.opencreative.coding.blocks.actions.Action;
import ua.mcchickenstudio.opencreative.coding.blocks.actions.ActionsHandler;
import ua.mcchickenstudio.opencreative.coding.menus.MenusCategory;
import ua.mcchickenstudio.opencreative.coding.values.NumberEventValue;

public final class WorldTargetsCount extends NumberEventValue {
    public WorldTargetsCount() {
        super("targets_count", new ItemStack(Material.ACACIA_BUTTON), MenusCategory.WORLD);
    }

    @Override
    public @NotNull Number getNumber(@NotNull ActionsHandler handler, @NotNull Action action, @Nullable Entity entity) {
        return handler.getSelectedTargets().size();
    }

    @Override
    public @NotNull String getExtensionId() {
        return "default";
    }

    @Override
    public @NotNull String getDescription() {
        return "Return amount of selected targets";
    }
}
