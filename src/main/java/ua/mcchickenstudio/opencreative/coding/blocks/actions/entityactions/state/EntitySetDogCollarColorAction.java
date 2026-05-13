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

package ua.mcchickenstudio.opencreative.coding.blocks.actions.entityactions.state;

import org.bukkit.DyeColor;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Wolf;
import org.jetbrains.annotations.NotNull;
import ua.mcchickenstudio.opencreative.coding.arguments.Arguments;
import ua.mcchickenstudio.opencreative.coding.blocks.actions.ActionType;
import ua.mcchickenstudio.opencreative.coding.blocks.actions.Target;
import ua.mcchickenstudio.opencreative.coding.blocks.actions.entityactions.EntityAction;
import ua.mcchickenstudio.opencreative.coding.blocks.executors.Executor;
import ua.mcchickenstudio.opencreative.coding.exceptions.UnsupportedEntityException;

public final class EntitySetDogCollarColorAction extends EntityAction {
    public EntitySetDogCollarColorAction(Executor executor, Target target, int x, Arguments args) {
        super(executor, target, x, args);
    }

    @Override
    public void executeEntity(@NotNull Entity entity) {
        String colorName = getArguments().getText("color", "red", this);
        DyeColor color;
        try {
            color = DyeColor.valueOf(colorName.toUpperCase().replace("-", "_"));
        } catch (Exception ignored) {
            color = DyeColor.RED;
        }
        if (entity instanceof Wolf wolf) {
            wolf.setCollarColor(color);
        } else {
            throw new UnsupportedEntityException(Wolf.class, entity);
        }
    }

    @Override
    public @NotNull ActionType getActionType() {
        return ActionType.ENTITY_SET_DOG_COLLAR_COLOR;
    }
}
