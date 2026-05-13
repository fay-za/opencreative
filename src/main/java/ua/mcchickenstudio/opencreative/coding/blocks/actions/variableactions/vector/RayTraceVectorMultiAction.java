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

import java.util.ArrayList;
import java.util.List;

import static ua.mcchickenstudio.opencreative.coding.blocks.actions.variableactions.vector.RayTraceVectorAction.getYawPitch;

public final class RayTraceVectorMultiAction extends VariableAction {

    // Made by pawsashatoy :)
    public RayTraceVectorMultiAction(Executor executor, Target target, int x, Arguments args) {
        super(executor, target, x, args);
    }

    @Override
    protected void execute() {
        VariableLink hitVec = getArguments().getVariableLink("hitVec", this);
        final Vector vector = getArguments().getVector("vector", new Vector(0, 0, 0), this);
        final Location from = getArguments().getLocation("from", new Location(getPlanet().getWorld(), 0, 0, 0), this);
        final List<Object> list = getArguments().getList("to", this);
        final List<Location> resultList = new ArrayList<>();
        AsyncScheduler.run(() -> {
            for (final Object o : list) {
                if (o instanceof Location to) {
                    final double
                            x = to.getX(),
                            y = to.getY(),
                            z = to.getZ();
                    final double range = getArguments().getDouble("range", 3.0, this);
                    final double
                            xSize = getArguments().getDouble("xSize", 0.3, this) / 2.0,
                            ySize = getArguments().getDouble("ySize", 1.8, this) / 2.0,
                            zSize = getArguments().getDouble("zSize", 0.3, this) / 2.0;
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
                    if (result != null) {
                        final Vec3 hit = result.hitVec;
                        resultList.add(new Location(to.getWorld(), hit.xCoord, hit.yCoord, hit.zCoord));
                    }
                }
            }
            setVarValue(hitVec, resultList);
        }, AsyncScheduler.getScheduler());
    }

    @Override
    public @NotNull ActionType getActionType() {
        return ActionType.VAR_DO_RAY_TRACE_MULTI;
    }
}
