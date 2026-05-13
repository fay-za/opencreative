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

import org.bukkit.entity.Display;
import org.bukkit.entity.Entity;
import org.bukkit.util.Transformation;
import org.jetbrains.annotations.NotNull;
import org.joml.Vector3f;
import ua.mcchickenstudio.opencreative.coding.arguments.Arguments;
import ua.mcchickenstudio.opencreative.coding.blocks.actions.ActionType;
import ua.mcchickenstudio.opencreative.coding.blocks.actions.Target;
import ua.mcchickenstudio.opencreative.coding.blocks.actions.entityactions.EntityAction;
import ua.mcchickenstudio.opencreative.coding.blocks.executors.Executor;
import ua.mcchickenstudio.opencreative.coding.exceptions.UnsupportedEntityException;

public final class EntitySetDisplayTranslationAction extends EntityAction {
    public EntitySetDisplayTranslationAction(Executor executor, Target target, int x, Arguments args) {
        super(executor, target, x, args);
    }

    @Override
    public void executeEntity(@NotNull Entity entity) {
        if (!(entity instanceof Display display)) {
            throw new UnsupportedEntityException(Display.class, entity);
        }
        boolean add = getArguments().getBoolean("add", false, this);
        float x = (add ? 0.0f : display.getTransformation().getTranslation().x());
        float y = (add ? 0.0f : display.getTransformation().getTranslation().y());
        float z = (add ? 0.0f : display.getTransformation().getTranslation().z());
        if (getArguments().pathExists("x")) {
            x = getArguments().getFloat("x", x, this);
        }
        if (getArguments().pathExists("y")) {
            y = getArguments().getFloat("y", y, this);
        }
        if (getArguments().pathExists("z")) {
            z = getArguments().getFloat("z", z, this);
        }
        Vector3f vector3f = new Vector3f();
        if (add) {
            vector3f.x = display.getTransformation().getTranslation().x() + x;
            vector3f.y = display.getTransformation().getTranslation().y() + y;
            vector3f.z = display.getTransformation().getTranslation().z() + z;
        } else {
            vector3f.x = x;
            vector3f.y = y;
            vector3f.z = z;
        }
        display.setTransformation(new Transformation(
                vector3f,
                display.getTransformation().getLeftRotation(),
                display.getTransformation().getScale(),
                display.getTransformation().getRightRotation()
        ));
    }

    @Override
    public @NotNull ActionType getActionType() {
        return ActionType.ENTITY_SET_DISPLAY_TRANSLATION;
    }
}
