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

package ua.mcchickenstudio.opencreative.coding.blocks.actions.variableactions.vector;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;
import ua.mcchickenstudio.opencreative.coding.arguments.Arguments;
import ua.mcchickenstudio.opencreative.coding.blocks.actions.ActionType;
import ua.mcchickenstudio.opencreative.coding.blocks.actions.Target;
import ua.mcchickenstudio.opencreative.coding.blocks.actions.variableactions.VariableAction;
import ua.mcchickenstudio.opencreative.coding.blocks.executors.Executor;
import ua.mcchickenstudio.opencreative.coding.variables.VariableLink;
import ua.mcchickenstudio.opencreative.utils.async.AsyncScheduler;
import ua.mcchickenstudio.opencreative.utils.millennium.math.*;
import ua.mcchickenstudio.opencreative.utils.millennium.vectors.Vec2f;
import ua.mcchickenstudio.opencreative.utils.millennium.vectors.Vec3;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static ua.mcchickenstudio.opencreative.coding.blocks.actions.variableactions.vector.RayTraceVectorAction.getYawPitch;

public final class RayTraceVectorMultiEntitiesAction extends VariableAction {

    // Made by pawsashatoy :)
    public RayTraceVectorMultiEntitiesAction(Executor executor, Target target, int x, Arguments args) {
        super(executor, target, x, args);
    }

    private static List<Entity> getEntitiesAroundPoint(Location location, double radius) {
        List<Entity> entities = new ArrayList<>();
        World world = location.getWorld();
        int smallX = FastMath.floor((location.getX() - radius) / 16.0D);
        int bigX = FastMath.floor((location.getX() + radius) / 16.0D);
        int smallZ = FastMath.floor((location.getZ() - radius) / 16.0D);
        int bigZ = FastMath.floor((location.getZ() + radius) / 16.0D);

        for (int x = smallX; x <= bigX; x++) {
            for (int z = smallZ; z <= bigZ; z++) {
                if (world.isChunkLoaded(x, z)) {
                    entities.addAll(Arrays.asList(world.getChunkAt(x, z).getEntities()));
                }
            }
        }
        entities.removeIf(entity -> entity.getLocation().distanceSquared(location) > radius * radius);
        return entities;
    }

    @Override
    protected void execute() {
        VariableLink hitVec = getArguments().getVariableLink("hitVec", this);
        final Vector vector = getArguments().getVector("vector", new Vector(0, 0, 0), this);
        final Location from = getArguments().getLocation("from", new Location(getPlanet().getWorld(), 0, 0, 0), this);
        AsyncScheduler.run(() -> {
            final double range = getArguments().getDouble("range", 3.0, this);
            final List<Entity> list = getEntitiesAroundPoint(from, range + 1.0);
            final List<Location> resultList = new ArrayList<>();
            final String filter = getArguments().getText("filter", "no-filter", this);
            for (final Entity e : list) {
                final boolean isPlayer = (e instanceof Player);
                if (filter.equals("only-players") && !isPlayer) continue;
                if (filter.equals("only-entities") && (isPlayer || !(e instanceof LivingEntity))) continue;
                if (e.isDead()) continue;
                final Location to = e.getLocation();
                final double
                        x = to.getX(),
                        y = to.getY(),
                        z = to.getZ();
                final BuildSpeed buildSpeed =
                        (getArguments().getText("calculation", "vanilla-java", this)
                                .equals("vanilla-java") ? BuildSpeed.NORMAL : BuildSpeed.FAST);
                final Vec2f rotation = getYawPitch(vector);
                final AxisAlignedBB aabb = new AxisAlignedBB(
                        x - 0.3, y - 0.1, z - 0.3,
                        x + 0.3, y + 1.9, z + 0.3
                );
                final MovingObjectPosition result = RayTrace.rayCast(rotation.getX(), rotation.getY(),
                        aabb, new Vec3(from.getX(), from.getY(), from.getZ()), range, buildSpeed);
                if (result != null) {
                    final Vec3 hit = result.hitVec;
                    resultList.add(new Location(to.getWorld(), hit.xCoord, hit.yCoord, hit.zCoord));
                }
            }
            setVarValue(hitVec, resultList);
        }, AsyncScheduler.getScheduler());
    }

    @Override
    public @NotNull ActionType getActionType() {
        return ActionType.VAR_DO_RAY_TRACE_MULTI_ENTITIES;
    }
}
