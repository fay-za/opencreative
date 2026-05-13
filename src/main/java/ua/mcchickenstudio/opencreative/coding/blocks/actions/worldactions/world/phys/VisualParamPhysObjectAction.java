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

import org.bukkit.Particle;
import org.jetbrains.annotations.NotNull;
import ua.mcchickenstudio.opencreative.coding.arguments.Arguments;
import ua.mcchickenstudio.opencreative.coding.blocks.actions.ActionType;
import ua.mcchickenstudio.opencreative.coding.blocks.actions.Target;
import ua.mcchickenstudio.opencreative.coding.blocks.actions.worldactions.WorldAction;
import ua.mcchickenstudio.opencreative.coding.blocks.executors.Executor;

import java.util.Arrays;

public final class VisualParamPhysObjectAction extends WorldAction {

    // Made by pawsashatoy :)
    public VisualParamPhysObjectAction(Executor executor, Target target, int x, Arguments args) {
        super(executor, target, x, args);
    }

    @Override
    protected void execute() {
        final Arguments a = getArguments();
        setVarValue(getArguments().getVariableLink("variable", this), Arrays.asList(
                a.getParticle("particle", Particle.CLOUD, this),
                a.getValue("particle2", this),
                a.getInt("param1", 1, this),
                a.getInt("param2", 0, this),
                a.getInt("param3", 0, this),
                a.getInt("param4", 0, this),
                a.getParticle("hit-particle", Particle.EXPLOSION, this),
                a.getInt("hit-param1", 1, this)
        ));
    }

    @Override
    public @NotNull ActionType getActionType() {
        return ActionType.WORLD_VISUAL_PARAM_PHYS_OBJECT;
    }
}
