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

package ua.mcchickenstudio.opencreative.indev.values;

import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

/**
 * <h1>InteractableValue</h1>
 * This interface is used for coding values, that
 * do certain actions when player clicks left or right
 * clicks while holding them in main hand.
 */
public interface InteractableValue {

    /**
     * Used for doing actions, when player clicks with item in hand.
     * <p>
     * Returns whether action was done successfully.
     * @param player player, that interacted with coding value.
     * @param item item of value.
     * @param event event of interaction.
     * @return true - if action was done, false - interaction was ignored.
     */
    boolean onPlayerInteract(@NotNull Player player, @NotNull ItemStack item, @NotNull PlayerInteractEvent event);

}
