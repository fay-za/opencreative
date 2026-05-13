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

package ua.mcchickenstudio.opencreative.coding.blocks.actions.other;

import org.bukkit.entity.Entity;
import org.jetbrains.annotations.NotNull;
import ua.mcchickenstudio.opencreative.coding.arguments.Arguments;
import ua.mcchickenstudio.opencreative.coding.blocks.actions.*;
import ua.mcchickenstudio.opencreative.coding.blocks.executors.Executor;
import ua.mcchickenstudio.opencreative.coding.blocks.executors.other.Method;
import ua.mcchickenstudio.opencreative.coding.exceptions.UnknownMethodException;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public final class LaunchMethodAction extends Action {

    public LaunchMethodAction(Executor executor, Target target, int x, Arguments args) {
        super(executor, target, x, args);
    }

    @Override
    protected void execute(Entity entity) {
        String name = getArguments().getText("name", "", this);
        if (name.isEmpty()) return;
        List<Method> methods = new ArrayList<>();
        for (Method method : getPlanet().getTerritory().getScript().getExecutors().getMethodsList()) {
            if (method.getCallName().equalsIgnoreCase(name)) {
                methods.add(method);
            }
        }
        if (methods.isEmpty()) {
            throw new UnknownMethodException(name);
        }
        for (Method method : methods) {
            ActionsHandler handler = new ActionsHandler(this);
            if (entity != null) {
                Set<Entity> targets = new HashSet<>();
                targets.add(entity);
                handler.setSelectedTargets(targets);
            } else {
                handler.setSelectedTargets(new HashSet<>());
            }
            handler.executeActions(method.getActions());
        }
    }

    @Override
    public @NotNull ActionType getActionType() {
        return ActionType.LAUNCH_METHOD;
    }

    @Override
    public @NotNull ActionCategory getActionCategory() {
        return ActionCategory.LAUNCH_METHOD_ACTION;
    }
}
