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

package ua.mcchickenstudio.opencreative.coding.blocks.events.player.fighting;

import org.bukkit.entity.Entity;
import org.jetbrains.annotations.NotNull;

/**
 * <h1>KillerVictimEvent</h1>
 * This interface is used for entities damage events,
 * when there's victim and killer.
 */
public interface KillerVictimEvent {

    /**
     * Returns victim of damage event, that got damaged.
     *
     * @return entity victim.
     */
    @NotNull Entity getVictim();

    /**
     * Returns killer of damage event, that attacked victim.
     *
     * @return entity killer.
     */
    @NotNull Entity getKiller();

}
