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

import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.scheduler.BukkitRunnable;
import ua.mcchickenstudio.opencreative.OpenCreative;

import java.util.ArrayList;
import java.util.List;

import static ua.mcchickenstudio.opencreative.utils.ErrorUtils.sendPlayerErrorMessage;

/**
 * <h1>Menus</h1>
 * This class represents a menu manager, that
 * stores current opened menus for handling
 * inventory open, click and close events.
 * <p>
 * To make {@link InventoryMenu} usable for handling inventory events, register this menus with
 * {@link Menus#addMenu(InventoryMenu)} method. To avoid memory leaks, please unregister it
 * on inventory close event with {@link Menus#removeMenu(InventoryMenu)}
 */
public final class Menus implements Listener {

    private static final List<InventoryMenu> activeMenus = new ArrayList<>();
    private final BukkitRunnable runnable;

    public Menus() {
        runnable = new BukkitRunnable() {
            @Override
            public void run() {
                if (activeMenus.isEmpty()) return;
                for (InventoryMenu inventoryMenu : new ArrayList<>(activeMenus)) {
                    if (inventoryMenu instanceof BlockMenu blockMenu && blockMenu.getLocation() != null && blockMenu.getBlockState() != null) {
                        if (!blockMenu.getLocation().getBlock().getState().equals(blockMenu.getBlockState())) {
                            if (inventoryMenu.getInventory().getViewers().isEmpty()) {
                                removeMenu(inventoryMenu);
                            }
                            inventoryMenu.getInventory().close();
                        }
                    }
                    if (System.currentTimeMillis() - inventoryMenu.getCreationTime() > 600000L) {
                        if (inventoryMenu.getInventory().getViewers().isEmpty()) {
                            removeMenu(inventoryMenu);
                        }
                        inventoryMenu.getInventory().close();
                    }
                }
            }
        };
        runnable.runTaskTimer(OpenCreative.getPlugin(), 20L, 20L);
    }

    public static void onBlockDestroy(Location location) {
        if (activeMenus.isEmpty()) return;
        for (InventoryMenu inventoryMenu : new ArrayList<>(activeMenus)) {
            if (inventoryMenu instanceof BlockMenu blockMenu && location.equals(blockMenu.getLocation())) {
                if (inventoryMenu.getInventory().getViewers().isEmpty()) {
                    removeMenu(inventoryMenu);
                }
                inventoryMenu.getInventory().close();
            }
        }
    }

    /**
     * Registers menus in menus manager for handling inventory events.
     *
     * @param menu menus to add.
     */
    public static void addMenu(InventoryMenu menu) {
        activeMenus.add(menu);
    }

    /**
     * Unregisters menus from menus event listeners, required if menus is not more
     * useful because player closed it.
     *
     * @param menu menus to remove.
     */
    public static void removeMenu(InventoryMenu menu) {
        activeMenus.remove(menu);
    }

    @EventHandler
    public void onClick(InventoryClickEvent event) {
        for (InventoryMenu menu : activeMenus) {
            if (event.getInventory().getHolder() == menu.getInventory().getHolder()) {
                try {
                    menu.onClick(event);
                } catch (Exception error) {
                    if (event.getWhoClicked() instanceof Player player) {
                        sendPlayerErrorMessage(player, "Can't handle click event in " + menu.getClass().getSimpleName(), error);
                    }
                }
                return;
            }
        }
    }

    @EventHandler
    public void onOpen(InventoryOpenEvent event) {
        for (InventoryMenu menu : activeMenus) {
            if (event.getInventory().getHolder() == menu.getInventory().getHolder()) {
                menu.onOpen(event);
                return;
            }
        }
    }

    @EventHandler
    public void onClose(InventoryCloseEvent event) {
        for (InventoryMenu menu : activeMenus) {
            if (event.getInventory().getHolder() == menu.getInventory().getHolder()) {
                menu.onClose(event);
                return;
            }
        }
    }

}
