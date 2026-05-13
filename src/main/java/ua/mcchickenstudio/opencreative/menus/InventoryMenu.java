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

package ua.mcchickenstudio.opencreative.menus;

import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.inventory.InventoryHolder;
import org.jetbrains.annotations.NotNull;

/**
 * <h1>InventoryMenu</h1>
 * This interface represents an inventory menus,
 * that can be opened for player. It has functions
 * to listen click, open and close events.
 */
public interface InventoryMenu extends InventoryHolder {

    /**
     * Opens the inventory for player.
     *
     * @param player player to open menus.
     */
    void open(@NotNull Player player);

    /**
     * Executes when player clicks in inventory.
     *
     * @param event event of click in inventory.
     */
    void onClick(@NotNull InventoryClickEvent event);

    /**
     * Executes when player opens inventory and
     * sees it first time. Useful for playing
     * sounds or setting items.
     *
     * @param event event of inventory open.
     */
    void onOpen(@NotNull InventoryOpenEvent event);

    /**
     * Executes when player closes inventory.
     * <p>
     * <b>NOTE:</b> Menu should be unregistered after
     * closing it.
     *
     * @param event event of inventory close.
     * @see #destroy()
     */
    default void onClose(@NotNull InventoryCloseEvent event) {
        destroy();
    }

    /**
     * Returns the creation time of menu in milliseconds of Unix format.
     *
     * @return creation time of menu.
     */
    long getCreationTime();

    /**
     * Destroys menus from memory and disables
     * all event listeners for it.
     */
    default void destroy() {
        Menus.removeMenu(this);
    }

}
