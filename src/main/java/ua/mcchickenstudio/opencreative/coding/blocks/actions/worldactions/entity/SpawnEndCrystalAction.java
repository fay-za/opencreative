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

import net.kyori.adventure.text.Component;
import org.bukkit.Location;
import org.bukkit.entity.EnderCrystal;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.jetbrains.annotations.NotNull;
import ua.mcchickenstudio.opencreative.coding.arguments.Arguments;
import ua.mcchickenstudio.opencreative.coding.blocks.actions.ActionType;
import ua.mcchickenstudio.opencreative.coding.blocks.actions.Target;
import ua.mcchickenstudio.opencreative.coding.blocks.actions.worldactions.WorldAction;
import ua.mcchickenstudio.opencreative.coding.blocks.executors.Executor;

import static ua.mcchickenstudio.opencreative.utils.ErrorUtils.sendCodingDebugLog;

public final class SpawnEndCrystalAction extends WorldAction {

    public SpawnEndCrystalAction(Executor executor, Target target, int x, Arguments args) {
        super(executor, target, x, args);
    }

    @Override
    protected void execute() {

        if (getWorld().getEntities().size() >= getPlanet().getLimits().getEntitiesLimit()) {
            sendCodingDebugLog(getPlanet(), "Too many entities: spawn entity action is cancelled.");
            return;
        }

        Component customName = getArguments().getComponent("name", Component.text(""), this);
        Location beamLocation = getArguments().getLocation("beam", getPlanet().getTerritory().getSpawnLocation(), this);
        boolean bottom = getArguments().getBoolean("bottom", true, this);

        for (Location location : getArguments().getLocationList("locations", this)) {
            Entity spawnedEntity = getWorld().spawnEntity(location, EntityType.END_CRYSTAL);

            if (spawnedEntity instanceof EnderCrystal crystal) {
                if (getArguments().pathExists("name")) {
                    crystal.customName(customName);
                }
                if (getArguments().pathExists("beam")) {
                    crystal.setBeamTarget(beamLocation);
                }
                crystal.setShowingBottom(bottom);
                setLastSpawnedEntity(crystal);
            }
        }
    }

    @Override
    public @NotNull ActionType getActionType() {
        return ActionType.WORLD_SPAWN_END_CRYSTAL;
    }
}
