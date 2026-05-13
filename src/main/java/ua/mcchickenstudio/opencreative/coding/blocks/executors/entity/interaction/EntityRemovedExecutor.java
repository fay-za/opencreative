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

package ua.mcchickenstudio.opencreative.coding.blocks.executors.entity.interaction;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import ua.mcchickenstudio.opencreative.coding.blocks.events.WorldEvent;
import ua.mcchickenstudio.opencreative.coding.blocks.events.entity.interaction.EntityRemovedEvent;
import ua.mcchickenstudio.opencreative.coding.blocks.executors.entity.EntityExecutor;
import ua.mcchickenstudio.opencreative.coding.menus.MenusCategory;

public final class EntityRemovedExecutor extends EntityExecutor {

    public EntityRemovedExecutor() {
        super("removed");
    }

    @Override
    public @NotNull ItemStack getDisplayIcon() {
        return new ItemStack(Material.BARRIER);
    }

    @Override
    public @NotNull MenusCategory getCategory() {
        return MenusCategory.ENTITY_INTERACTION;
    }

    @Override
    public @NotNull Class<? extends WorldEvent> getEventClass() {
        return EntityRemovedEvent.class;
    }

    @Override
    public @NotNull String getName() {
        return "Entity Remove Event";
    }

    @Override
    public @NotNull String getDescription() {
        return "When entity is removed from world";
    }

    @Override
    public @NotNull String getExtensionId() {
        return "default";
    }
}
