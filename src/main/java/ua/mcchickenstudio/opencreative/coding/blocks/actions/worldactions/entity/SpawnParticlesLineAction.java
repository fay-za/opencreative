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

package ua.mcchickenstudio.opencreative.coding.blocks.actions.worldactions.entity;

import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;
import ua.mcchickenstudio.opencreative.coding.arguments.Arguments;
import ua.mcchickenstudio.opencreative.coding.blocks.actions.ActionType;
import ua.mcchickenstudio.opencreative.coding.blocks.actions.Target;
import ua.mcchickenstudio.opencreative.coding.blocks.actions.worldactions.WorldAction;
import ua.mcchickenstudio.opencreative.coding.blocks.executors.Executor;

import static ua.mcchickenstudio.opencreative.utils.ErrorUtils.sendCodingDebugLog;

public final class SpawnParticlesLineAction extends WorldAction {
    public SpawnParticlesLineAction(Executor executor, Target target, int x, Arguments args) {
        super(executor, target, x, args);
    }

    @Override
    protected void execute() {
        if (getWorld().getEntities().size() >= getPlanet().getLimits().getEntitiesLimit()) {
            sendCodingDebugLog(getPlanet(), "Too many entities: spawn particles action is cancelled.");
            return;
        }
        Particle particle = getArguments().getParticle("particle", Particle.HEART, this);
        int count = Math.min(30, getArguments().getInt("count", 1, this));
        double offsetX = getArguments().getDouble("offset-x", 0.0d, this);
        double offsetY = getArguments().getDouble("offset-y", 0.0d, this);
        double offsetZ = getArguments().getDouble("offset-z", 0.0d, this);
        Location first = getArguments().getLocation("first", getPlanet().getTerritory().getSpawnLocation(), this);
        Location second = getArguments().getLocation("second", getPlanet().getTerritory().getSpawnLocation(), this);
        Vector firstVector = first.toVector();
        Vector secondVector = second.toVector();
        Vector locationVector = secondVector.subtract(firstVector);
        for (int i = 1; i <= first.distance(second); i += 1) {
            locationVector.multiply(i);
            first.add(locationVector);
            getWorld().spawnParticle(particle, first, count, offsetX, offsetY, offsetZ);
            first.subtract(locationVector);
            locationVector.normalize();
        }
    }

    @Override
    public @NotNull ActionType getActionType() {
        return ActionType.WORLD_SPAWN_PARTICLES_LINE;
    }
}
