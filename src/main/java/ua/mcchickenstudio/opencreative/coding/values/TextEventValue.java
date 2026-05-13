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

package ua.mcchickenstudio.opencreative.coding.values;

import org.bukkit.entity.Entity;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import ua.mcchickenstudio.opencreative.coding.blocks.actions.Action;
import ua.mcchickenstudio.opencreative.coding.blocks.actions.ActionsHandler;
import ua.mcchickenstudio.opencreative.coding.menus.MenusCategory;

/**
 * <h1>TextEventValue</h1>
 * This class represents an event value, that
 * returns string, or null.
 */
public abstract class TextEventValue extends EventValue {

    public TextEventValue(@NotNull String id, @NotNull ItemStack displayIcon, @NotNull MenusCategory category) {
        super(id, displayIcon, category);
    }

    /**
     * Returns a string that can be got from
     * player, event, action, or null.
     *
     * @return string, or null.
     */
    public abstract @Nullable String getText(@NotNull ActionsHandler handler, @NotNull Action action, @Nullable Entity entity);

    @Override
    public final @Nullable Object getValue(@NotNull ActionsHandler handler, @NotNull Action action, @Nullable Entity entity) {
        String text = getText(handler, action, entity);
        if (text == null) return null;
        return new StringBuilder(text).substring(0, Math.min(1024, text.length()));
    }
}
