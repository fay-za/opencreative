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
import org.bukkit.Material;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.SpectralArrow;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import ua.mcchickenstudio.opencreative.coding.arguments.Arguments;
import ua.mcchickenstudio.opencreative.coding.blocks.actions.ActionType;
import ua.mcchickenstudio.opencreative.coding.blocks.actions.Target;
import ua.mcchickenstudio.opencreative.coding.blocks.actions.worldactions.WorldAction;
import ua.mcchickenstudio.opencreative.coding.blocks.executors.Executor;

import static ua.mcchickenstudio.opencreative.utils.ErrorUtils.sendCodingDebugLog;

public final class SpawnArrowAction extends WorldAction {
    public SpawnArrowAction(Executor executor, Target target, int x, Arguments args) {
        super(executor, target, x, args);
    }

    @Override
    protected void execute() {
        if (getWorld().getEntities().size() >= getPlanet().getLimits().getEntitiesLimit()) {
            sendCodingDebugLog(getPlanet(), "Too many entities: spawn entity action is cancelled.");
            return;
        }
        ItemStack arrowItem = getArguments().getItem("arrow", new ItemStack(Material.ARROW, 1), this);
        for (Location location : getArguments().getLocationList("locations", this)) {
            if (arrowItem.getType() == Material.ARROW) {
                Entity spawnedEntity = getPlanet().getTerritory().getWorld().spawnEntity(location, EntityType.ARROW);
                if (spawnedEntity instanceof Arrow arrow) {
                    arrow.setItemStack(arrowItem);
                    setLastSpawnedEntity(arrow);
                }
            } else if (arrowItem.getType() == Material.SPECTRAL_ARROW) {
                Entity spawnedEntity = getPlanet().getTerritory().getWorld().spawnEntity(location, EntityType.SPECTRAL_ARROW);
                if (spawnedEntity instanceof SpectralArrow arrow) {
                    arrow.setItemStack(arrowItem);
                    setLastSpawnedEntity(arrow);
                }
            }

        }
    }

    @Override
    public @NotNull ActionType getActionType() {
        return ActionType.WORLD_SPAWN_ARROW;
    }
}
