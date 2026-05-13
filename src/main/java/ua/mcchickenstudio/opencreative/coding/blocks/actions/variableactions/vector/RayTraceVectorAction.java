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
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;
import ua.mcchickenstudio.opencreative.coding.arguments.Arguments;
import ua.mcchickenstudio.opencreative.coding.blocks.actions.ActionType;
import ua.mcchickenstudio.opencreative.coding.blocks.actions.Target;
import ua.mcchickenstudio.opencreative.coding.blocks.actions.variableactions.VariableAction;
import ua.mcchickenstudio.opencreative.coding.blocks.executors.Executor;
import ua.mcchickenstudio.opencreative.coding.variables.VariableLink;
import ua.mcchickenstudio.opencreative.utils.async.AsyncScheduler;
import ua.mcchickenstudio.opencreative.utils.millennium.math.AxisAlignedBB;
import ua.mcchickenstudio.opencreative.utils.millennium.math.BuildSpeed;
import ua.mcchickenstudio.opencreative.utils.millennium.math.MovingObjectPosition;
import ua.mcchickenstudio.opencreative.utils.millennium.math.RayTrace;
import ua.mcchickenstudio.opencreative.utils.millennium.vectors.Vec2f;
import ua.mcchickenstudio.opencreative.utils.millennium.vectors.Vec3;

public final class RayTraceVectorAction extends VariableAction {

    // Made by pawsashatoy :)
    public RayTraceVectorAction(Executor executor, Target target, int x, Arguments args) {
        super(executor, target, x, args);
    }

    public static Vec2f getYawPitch(final Vector vector) {
        float yaw;
        float pitch;
        if (vector.getX() == 0 && vector.getZ() == 0) {
            yaw = 0;
            pitch = vector.getY() > 0 ? -90 : 90;
        } else {
            yaw = (float) Math.toDegrees(Math.atan2(-vector.getX(), vector.getZ()));
            double xz = Math.sqrt(vector.getX() * vector.getX() + vector.getZ() * vector.getZ());
            pitch = (float) Math.toDegrees(Math.atan(-vector.getY() / xz));
        }
        if (yaw < 0) yaw += 360;
        return new Vec2f(yaw, pitch);
    }

    @Override
    protected void execute() {
        VariableLink hitVec = getArguments().getVariableLink("hitVec", this);
        final Vector vector = getArguments().getVector("vector", new Vector(0, 0, 0), this);
        final Location from = getArguments().getLocation("from", new Location(getPlanet().getWorld(), 0, 0, 0), this);
        final Location to = getArguments().getLocation("to", new Location(getPlanet().getWorld(), 0, 0, 0), this);
        final double
                x = to.getX(),
                y = to.getY(),
                z = to.getZ();
        final double range = getArguments().getDouble("range", 3.0, this);
        final double
                xSize = getArguments().getDouble("xSize", 0.3, this) / 2.0,
                ySize = getArguments().getDouble("ySize", 1.8, this) / 2.0,
                zSize = getArguments().getDouble("zSize", 0.3, this) / 2.0;
        AsyncScheduler.run(() -> {
            final BuildSpeed buildSpeed =
                    (getArguments().getText("calculation", "vanilla-java", this)
                            .equals("vanilla-java") ? BuildSpeed.NORMAL : BuildSpeed.FAST);
            final Vec2f rotation = getYawPitch(vector);
            final AxisAlignedBB aabb = new AxisAlignedBB(
                    x - xSize, y - ySize, z - zSize,
                    x + xSize, y + ySize, z + zSize
            );
            final MovingObjectPosition result = RayTrace.rayCast(rotation.getX(), rotation.getY(),
                    aabb, new Vec3(from.getX(), from.getY(), from.getZ()), range, buildSpeed);
            if (result == null) {
                setVarValue(hitVec, hitVec);
            } else {
                final Vec3 hit = result.hitVec;
                setVarValue(hitVec, new Location(to.getWorld(), hit.xCoord, hit.yCoord, hit.zCoord));
            }
        }, AsyncScheduler.getScheduler());
    }

    @Override
    public @NotNull ActionType getActionType() {
        return ActionType.VAR_DO_RAY_TRACE;
    }
}
