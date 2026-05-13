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
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.jetbrains.annotations.NotNull;
import ua.mcchickenstudio.opencreative.coding.arguments.Arguments;
import ua.mcchickenstudio.opencreative.coding.blocks.actions.ActionType;
import ua.mcchickenstudio.opencreative.coding.blocks.actions.Target;
import ua.mcchickenstudio.opencreative.coding.blocks.actions.worldactions.WorldAction;
import ua.mcchickenstudio.opencreative.coding.blocks.executors.Executor;

import static ua.mcchickenstudio.opencreative.utils.ErrorUtils.sendCodingDebugLog;

public final class SpawnEntityAction extends WorldAction {

    public SpawnEntityAction(Executor executor, Target target, int x, Arguments args) {
        super(executor, target, x, args);
    }

    @Override
    protected void execute() {

        if (getWorld().getEntities().size() >= getPlanet().getLimits().getEntitiesLimit()) {
            sendCodingDebugLog(getPlanet(), "Too many entities: spawn entity action is cancelled.");
            return;
        }

        EntityType type = getArguments().getEntityType("type", EntityType.CHICKEN, this);
        String customName = getArguments().getText("name", "", this);

        boolean ai = getArguments().getBoolean("ai", true, this);
        boolean gravity = getArguments().getBoolean("gravity", true, this);
        boolean glowing = getArguments().getBoolean("glowing", false, this);
        boolean invisible = getArguments().getBoolean("invisible", false, this);
        boolean invulnerable = getArguments().getBoolean("invulnerable", false, this);
        boolean customNameVisible = getArguments().getBoolean("show-name", true, this);
        boolean visibleByDefault = getArguments().getBoolean("visible-for-all", true, this);

        if (isBannedEntity(type)) {
            throw new IllegalArgumentException("Cannot spawn " + type.name() + ", because it's disallowed entity type.");
        }

        for (Location location : getArguments().getLocationList("locations", this)) {
            Entity spawnedEntity = getPlanet().getTerritory().getWorld().spawnEntity(location, type);

            spawnedEntity.setGravity(gravity);
            if (!customName.isEmpty()) {
                spawnedEntity.setCustomName(customName);
            }
            spawnedEntity.setGlowing(glowing);
            spawnedEntity.setInvisible(invisible);
            spawnedEntity.setInvulnerable(invulnerable);
            spawnedEntity.setCustomNameVisible(customNameVisible);
            spawnedEntity.setVisibleByDefault(visibleByDefault);

            if (spawnedEntity instanceof LivingEntity living) {
                living.setAI(ai);
            }
            setLastSpawnedEntity(spawnedEntity);
        }
    }

    public boolean isBannedEntity(EntityType type) {
        return switch (type) {
            case PLAYER, ITEM, LIGHTNING_BOLT -> true;
            default -> false;
        };
    }

    @Override
    public @NotNull ActionType getActionType() {
        return ActionType.WORLD_SPAWN_ENTITY;
    }
}
