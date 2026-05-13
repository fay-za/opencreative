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

package ua.mcchickenstudio.opencreative.coding.values.events;

import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import ua.mcchickenstudio.opencreative.coding.blocks.actions.Action;
import ua.mcchickenstudio.opencreative.coding.blocks.actions.ActionsHandler;
import ua.mcchickenstudio.opencreative.coding.blocks.events.entity.fighting.EntityGetDamagedEvent;
import ua.mcchickenstudio.opencreative.coding.blocks.events.player.fighting.PlayerDamagedEvent;
import ua.mcchickenstudio.opencreative.coding.blocks.events.player.fighting.PlayerDamagesMobEvent;
import ua.mcchickenstudio.opencreative.coding.blocks.events.player.fighting.PlayerDamagesPlayerEvent;
import ua.mcchickenstudio.opencreative.coding.blocks.events.player.inventory.PlayerItemDamagedEvent;
import ua.mcchickenstudio.opencreative.coding.menus.MenusCategory;
import ua.mcchickenstudio.opencreative.coding.values.NumberEventValue;

public final class DamageValue extends NumberEventValue {

    public DamageValue() {
        super("damage", new ItemStack(Material.SOUL_TORCH), MenusCategory.EVENTS);
    }

    @Override
    public @Nullable Number getNumber(@NotNull ActionsHandler handler, @NotNull Action action, @Nullable Entity entity) {
        return switch (action.getEvent()) {
            case PlayerItemDamagedEvent event -> event.getDamage();
            case PlayerDamagesPlayerEvent event -> event.getDamage();
            case PlayerDamagesMobEvent event -> event.getDamage();
            case PlayerDamagedEvent event -> event.getDamage();
            case EntityGetDamagedEvent event -> event.getDamage();
            default -> null;
        };
    }

    @Override
    public @NotNull String getExtensionId() {
        return "default";
    }

    @Override
    public @NotNull String getDescription() {
        return "Returns damage amount from damage event";
    }
}
