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

import org.bukkit.entity.Cat;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Fox;
import org.bukkit.entity.Wolf;
import org.jetbrains.annotations.NotNull;
import ua.mcchickenstudio.opencreative.coding.arguments.Arguments;
import ua.mcchickenstudio.opencreative.coding.blocks.actions.ActionType;
import ua.mcchickenstudio.opencreative.coding.blocks.actions.Target;
import ua.mcchickenstudio.opencreative.coding.blocks.actions.entityactions.EntityAction;
import ua.mcchickenstudio.opencreative.coding.blocks.executors.Executor;
import ua.mcchickenstudio.opencreative.coding.exceptions.UnsupportedEntityException;

public final class EntitySetInterestedAction extends EntityAction {
    public EntitySetInterestedAction(Executor executor, Target target, int x, Arguments args) {
        super(executor, target, x, args);
    }

    @Override
    public void executeEntity(@NotNull Entity entity) {
        boolean value = getArguments().getBoolean("boolean", true, this);
        if (entity instanceof Cat cat) {
            cat.setHeadUp(value);
        } else if (entity instanceof Wolf wolf) {
            wolf.setInterested(value);
        } else if (entity instanceof Fox fox) {
            fox.setInterested(value);
        } else {
            throw new UnsupportedEntityException(Cat.class, entity);
        }
    }

    @Override
    public @NotNull ActionType getActionType() {
        return ActionType.ENTITY_SET_INTERESTED;
    }
}
