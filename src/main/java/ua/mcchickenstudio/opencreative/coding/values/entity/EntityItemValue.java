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
import org.bukkit.entity.*;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import ua.mcchickenstudio.opencreative.coding.blocks.actions.Action;
import ua.mcchickenstudio.opencreative.coding.blocks.actions.ActionsHandler;
import ua.mcchickenstudio.opencreative.coding.menus.MenusCategory;
import ua.mcchickenstudio.opencreative.coding.values.ItemEventValue;

public final class EntityItemValue extends ItemEventValue {

    public EntityItemValue() {
        super("entity_item", new ItemStack(Material.ITEM_FRAME), MenusCategory.ENTITY);
    }

    @Override
    public @Nullable ItemStack getItem(@NotNull ActionsHandler handler, @NotNull Action action, @Nullable Entity entity) {
        if (entity == null) return null;
        return switch (entity) {
            case Item item -> item.getItemStack();
            case ItemDisplay display -> display.getItemStack();
            case AbstractArrow arrow -> arrow.getItemStack();
            case Firework firework -> firework.getItem();
            case FallingBlock fallingBlock -> {
                Material material = fallingBlock.getBlockData().getMaterial();
                if (material.isItem()) {
                    yield new ItemStack(material);
                } else {
                    yield new ItemStack(Material.AIR);
                }
            }
            case ThrownPotion potion -> potion.getItem();
            case ThrowableProjectile snowball -> snowball.getItem();
            default -> null;
        };
    }

    @Override
    public @NotNull String getExtensionId() {
        return "default";
    }

    @Override
    public @NotNull String getDescription() {
        return "Returns item of entity";
    }
}
