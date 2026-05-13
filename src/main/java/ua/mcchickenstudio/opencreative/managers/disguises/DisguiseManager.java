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

package ua.mcchickenstudio.opencreative.managers.disguises;

import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.jetbrains.annotations.NotNull;
import ua.mcchickenstudio.opencreative.managers.Manager;

/**
 * <h1>DisguiseManager</h1>
 * This interface represents a disguise manager, that adds
 * disguises for entities and clears them.
 */
public interface DisguiseManager extends Manager {

    /**
     * Disguises entity as player.
     *
     * @param entity entity to disguise.
     * @param skin skin nickname for disguise.
     * @param nickname nickname of player.
     */
    void disguiseAsPlayer(@NotNull Entity entity, @NotNull String skin, @NotNull String nickname);

    /**
     * Disguises entity as other entity type.
     *
     * @param entity entity, that will be disguised.
     * @param type type of entity to disguise as.
     */
    void disguiseAsEntity(@NotNull Entity entity, @NotNull EntityType type);

    /**
     * Disguises entity as block.
     *
     * @param entity entity to disguise.
     * @param material material of block.
     */
    void disguiseAsBlock(@NotNull Entity entity, @NotNull Material material);

    /**
     * Sets new display name for current disguise
     * of entity. If entity doesn't have disguise,
     * it should be ignored.
     *
     * @param entity entity to change name.
     * @param displayName display name to set.
     */
    void setDisguiseDisplayName(@NotNull Entity entity, @NotNull String displayName);

    /**
     * Removes all disguises from entity.
     *
     * @param entity entity to remove disguises.
     */
    void clearDisguises(@NotNull Entity entity);

}
