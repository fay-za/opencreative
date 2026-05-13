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

package ua.mcchickenstudio.opencreative.coding.blocks.actions.worldactions.world.phys;

import org.jetbrains.annotations.NotNull;
import ua.mcchickenstudio.opencreative.coding.arguments.Arguments;
import ua.mcchickenstudio.opencreative.coding.blocks.actions.ActionType;
import ua.mcchickenstudio.opencreative.coding.blocks.actions.Target;
import ua.mcchickenstudio.opencreative.coding.blocks.actions.worldactions.WorldAction;
import ua.mcchickenstudio.opencreative.coding.blocks.actions.worldactions.world.phys.data.PhysObject;
import ua.mcchickenstudio.opencreative.coding.blocks.actions.worldactions.world.phys.data.PhysService;
import ua.mcchickenstudio.opencreative.coding.blocks.executors.Executor;

import java.util.List;

public final class AddPhysObjectAction extends WorldAction {

    // Made by pawsashatoy :)
    public AddPhysObjectAction(Executor executor, Target target, int x, Arguments args) {
        super(executor, target, x, args);
    }

    @Override
    protected void execute() {
        final Arguments a = getArguments();
        final List<?>
                visual = a.getList("visual", this),
                motion = a.getList("motion", this),
                settings = a.getList("settings", this);
        final PhysObject physObject = new PhysObject(getPlanet().getWorld(), visual, motion, settings);
        PhysService.add(physObject, getPlanet().getLimits().getPhysicalObjectsLimit());
    }

    @Override
    public @NotNull ActionType getActionType() {
        return ActionType.WORLD_ADD_PHYS_OBJECT;
    }
}
