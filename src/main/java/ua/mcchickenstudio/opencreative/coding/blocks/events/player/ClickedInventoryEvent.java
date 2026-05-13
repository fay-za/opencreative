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

package ua.mcchickenstudio.opencreative.coding.blocks.events.player;

import org.bukkit.inventory.Inventory;
import org.jetbrains.annotations.Nullable;

/**
 * <h1>ClickedInventoryEvent</h1>
 * This interface is used in events, that
 * involves clicked inventory.
 */
public interface ClickedInventoryEvent {

    /**
     * Returns inventory, involved in event.
     *
     * @return clicked inventory from event, or null.
     */
    @Nullable Inventory getClickedInventory();

}
