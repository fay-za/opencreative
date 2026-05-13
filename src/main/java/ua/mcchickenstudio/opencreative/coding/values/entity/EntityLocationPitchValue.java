/*
 * OpenCreative+, Minecraft plugin.
 * (C) 2022-2026, McChicken Studio, mcchickenstudio@gmail.com
 *
 * OpenCreative+ is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * OpenCreative+ is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <https://www.gnu.org/licenses/>.
 */

package ua.mcchickenstudio.opencreative.coding.values.entity;

import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import ua.mcchickenstudio.opencreative.coding.blocks.actions.Action;
import ua.mcchickenstudio.opencreative.coding.blocks.actions.ActionsHandler;
import ua.mcchickenstudio.opencreative.coding.menus.MenusCategory;
import ua.mcchickenstudio.opencreative.coding.values.NumberEventValue;

public final class EntityLocationPitchValue extends NumberEventValue {

    public EntityLocationPitchValue() {
        super("pitch", new ItemStack(Material.ORANGE_STAINED_GLASS_PANE), MenusCategory.ENTITY);
    }

    @Override
    public @Nullable Number getNumber(@NotNull ActionsHandler handler, @NotNull Action action, @Nullable Entity entity) {
        if (entity == null) return null;
        return entity.getPitch();
    }

    @Override
    public @NotNull String getExtensionId() {
        return "default";
    }

    @Override
    public @NotNull String getDescription() {
        return "Returns vertical rotation from entity's location";
    }
}
