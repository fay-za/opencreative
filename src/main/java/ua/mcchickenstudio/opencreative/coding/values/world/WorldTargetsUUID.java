package ua.mcchickenstudio.opencreative.coding.values.world;

import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import ua.mcchickenstudio.opencreative.coding.blocks.actions.Action;
import ua.mcchickenstudio.opencreative.coding.blocks.actions.ActionsHandler;
import ua.mcchickenstudio.opencreative.coding.menus.MenusCategory;
import ua.mcchickenstudio.opencreative.coding.values.ListEventValue;

import java.util.ArrayList;
import java.util.List;

public final class WorldTargetsUUID extends ListEventValue {
    public WorldTargetsUUID() {
        super("targets_uuids", new ItemStack(Material.ARMADILLO_SPAWN_EGG), MenusCategory.WORLD);
    }

    @Override
    public List<@NotNull Object> getList(@NotNull ActionsHandler handler, @NotNull Action action, @Nullable Entity entity) {
        List<Object> list = new ArrayList<>();
        handler.getSelectedTargets().forEach(ent -> list.add(ent.getUniqueId()));
        return list;
    }

    @Override
    public @NotNull String getExtensionId() {
        return "default";
    }

    @Override
    public @NotNull String getDescription() {
        return "Return selected targets UUIDs";
    }
}
