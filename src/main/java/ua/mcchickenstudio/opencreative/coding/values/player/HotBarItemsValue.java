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

package ua.mcchickenstudio.opencreative.coding.values.player;

import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import ua.mcchickenstudio.opencreative.coding.blocks.actions.Action;
import ua.mcchickenstudio.opencreative.coding.blocks.actions.ActionsHandler;
import ua.mcchickenstudio.opencreative.coding.menus.MenusCategory;
import ua.mcchickenstudio.opencreative.coding.values.ListEventValue;

import java.util.ArrayList;
import java.util.List;

public final class HotBarItemsValue extends ListEventValue {

    public HotBarItemsValue() {
        super("hotbar_items", new ItemStack(Material.CHERRY_CHEST_BOAT), MenusCategory.PLAYER);
    }

    @Override
    public @NotNull List<@NotNull Object> getList(@NotNull ActionsHandler handler, @NotNull Action action, @Nullable Entity entity) {
        List<@NotNull Object> objects = new ArrayList<>();
        if (!(entity instanceof Player player)) {
            return objects;
        }
        for (int slot = 0; slot < 9; slot++) {
            ItemStack item = player.getInventory().getItem(slot);
            if (item == null) {
                item = new ItemStack(Material.AIR);
            }
            objects.add(item);
        }
        return objects;
    }

    @Override
    public @NotNull String getExtensionId() {
        return "default";
    }

    @Override
    public @NotNull String getDescription() {
        return "Returns list of items in player's hotbar";
    }
}
