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

package ua.mcchickenstudio.opencreative.listeners.player;

import com.destroystokyo.paper.event.player.PlayerArmorChangeEvent;
import io.papermc.paper.event.player.PlayerInventorySlotChangeEvent;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.*;
import org.bukkit.event.player.*;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import ua.mcchickenstudio.opencreative.OpenCreative;
import ua.mcchickenstudio.opencreative.coding.blocks.events.player.inventory.*;
import ua.mcchickenstudio.opencreative.coding.menus.layouts.Layout;
import ua.mcchickenstudio.opencreative.coding.modules.BlocksManipulatorMenu;
import ua.mcchickenstudio.opencreative.menus.EnderChestMenu;
import ua.mcchickenstudio.opencreative.planets.DevPlanet;
import ua.mcchickenstudio.opencreative.planets.Planet;
import ua.mcchickenstudio.opencreative.settings.Sounds;
import ua.mcchickenstudio.opencreative.utils.ItemUtils;

import static ua.mcchickenstudio.opencreative.listeners.player.ChangedWorld.addPlayerWithLocation;
import static ua.mcchickenstudio.opencreative.listeners.player.ChangedWorld.isPlayerWithLocation;
import static ua.mcchickenstudio.opencreative.utils.BlockUtils.isOutOfBorders;
import static ua.mcchickenstudio.opencreative.utils.MessageUtils.getLocaleMessage;
import static ua.mcchickenstudio.opencreative.utils.PlayerUtils.spawnGlowingBlock;

public final class ClickListener implements Listener {

    private static void cantDev(Player player) {
        player.closeInventory();
        player.sendActionBar(getLocaleMessage("world.dev-mode.cant-dev"));
    }

    @EventHandler
    public void onCraft(CraftItemEvent event) {
        if (!(event.getWhoClicked() instanceof Player player)) return;
        new PlayerItemCraftEvent(player, event).callEvent();
    }

    @EventHandler
    public void onChestOpen(InventoryOpenEvent event) {
        /*
         * Removes container items, that are located
         * in container item to prevent a server crash.
         */
        if (event.isCancelled()) return;
        if (event.getInventory().getLocation() == null) return;
        if (event.getInventory().getHolder() instanceof Layout) return;
        for (ItemStack insideItem : event.getInventory().getContents()) {
            if (insideItem == null) continue;
            ItemUtils.fixItem(insideItem);
        }
    }

    @EventHandler
    public void onOpen(InventoryOpenEvent event) {
        if (event.getInventory().getType() != InventoryType.ENDER_CHEST) return;
        if (!(event.getPlayer() instanceof Player player)) return;
        Planet planet = OpenCreative.getPlanetsManager().getPlanetByPlayer(player);
        if (planet == null) return;
        event.setCancelled(true);
        event.getInventory().close();
        new EnderChestMenu(planet, event.getInventory().getLocation()).open(player);
    }

    @EventHandler
    public void onArmorChange(PlayerArmorChangeEvent event) {
        new ChangedArmorEvent(event.getPlayer(), event).callEvent();
    }

    @EventHandler
    public void onItemDamage(PlayerItemDamageEvent event) {
        new PlayerItemDamagedEvent(event.getPlayer(), event).callEvent();
    }

    @EventHandler
    public void onClick(InventoryClickEvent event) {
        Player player = (Player) event.getWhoClicked();

        Planet planet1 = OpenCreative.getPlanetsManager().getPlanetByPlayer(player);
        if (planet1 != null) {
            new ItemClickEvent(player, event).callEvent();
            if (event.getAction().name().startsWith("PLACE")) {
                new ItemMoveEvent(player, event).callEvent();
            }
        }

        if (event.getCurrentItem() != null) {
            ItemStack item = event.getCurrentItem();
            if (event.getClickedInventory() != null) {
                InventoryHolder clickedHolder = event.getClickedInventory().getHolder();
                InventoryHolder eventHolder = event.getInventory().getHolder();
                if (clickedHolder != null && clickedHolder.equals(eventHolder)) {
                    ItemUtils.fixItem(item);
                }
            }
        }

        DevPlanet devPlanet = OpenCreative.getPlanetsManager().getDevPlanet(player);
        if (devPlanet == null) return;
        if (player.getGameMode() == GameMode.ADVENTURE) {
            event.setCancelled(true);
            cantDev(player);
        }
    }

    @EventHandler
    public void onInventoryOpen(InventoryOpenEvent event) {
        Planet planet = OpenCreative.getPlanetsManager().getPlanetByPlayer((Player) event.getPlayer());
        if (planet != null) new OpenInventoryEvent((Player) event.getPlayer()).callEvent();
    }

    @EventHandler
    public void onInventoryClose(InventoryCloseEvent event) {
        Planet planet = OpenCreative.getPlanetsManager().getPlanetByPlayer((Player) event.getPlayer());
        if (planet != null) new CloseInventoryEvent((Player) event.getPlayer()).callEvent();
    }

    @EventHandler
    public void onSwap(PlayerSwapHandItemsEvent event) {
        Player player = event.getPlayer();
        ItemStack fixedMain = ItemUtils.fixItem(player.getInventory().getItemInMainHand().clone());
        ItemStack fixedOff = ItemUtils.fixItem(player.getInventory().getItemInOffHand().clone());
        if (!fixedMain.equals(player.getInventory().getItemInMainHand())) {
            player.getInventory().setItemInMainHand(fixedMain);
        }
        if (!fixedOff.equals(player.getInventory().getItemInOffHand())) {
            player.getInventory().setItemInOffHand(fixedOff);
        }
        Planet planet = OpenCreative.getPlanetsManager().getPlanetByPlayer(player);
        DevPlanet devPlanet = OpenCreative.getPlanetsManager().getDevPlanet(player);
        ItemStack currentItem = event.getOffHandItem();
        if (devPlanet != null) {
            if (currentItem.getType() == Material.PAPER) {
                event.setCancelled(true);
                if (!player.hasCooldown(currentItem.getType())) {
                    if (planet != null && planet.getTerritory().getWorld() != null) {
                        addPlayerWithLocation(player);
                        if (currentItem.hasItemMeta()) {
                            ItemMeta meta = currentItem.getItemMeta();
                            String locationString = ChatColor.stripColor(meta.getDisplayName());
                            String[] locCoords = locationString.split(" ");
                            if (locCoords.length == 5) {
                                try {
                                    double x, y, z;
                                    float yaw, pitch;
                                    x = Double.parseDouble(locCoords[0]);
                                    y = Double.parseDouble(locCoords[1]);
                                    z = Double.parseDouble(locCoords[2]);
                                    yaw = Float.parseFloat(locCoords[3]);
                                    pitch = Float.parseFloat(locCoords[4]);
                                    Location location = new Location(planet.getTerritory().getWorld(), x, y, z, yaw, pitch);
                                    if (isOutOfBorders(location)) location = planet.getTerritory().getSpawnLocation();
                                    player.teleport(location);
                                    spawnGlowingBlock(player, location);
                                } catch (Exception error) {
                                    player.teleport(planet.getTerritory().getSpawnLocation());
                                }
                            }
                        } else {
                            player.teleport(planet.getTerritory().getSpawnLocation());
                        }
                        Sounds.DEV_LOCATION_TELEPORT.play(player);
                        player.setCooldown(currentItem.getType(), 60);
                    }
                }
            } else if (currentItem.getType() == Material.COMPARATOR) {
                if (!devPlanet.getPlanet().getWorldPlayers().canDevelop(player)) {
                    return;
                }
                event.setCancelled(true);
                if (player.hasCooldown(currentItem.getType())) {
                    return;
                }
                player.setCooldown(currentItem.getType(), 40);
                int size = devPlanet.getMarkedExecutors(player).size();
                if (size == 0) {
                    player.sendActionBar(getLocaleMessage("menus.developer.manipulator.not-selected"));
                    Sounds.DEV_NOT_ALLOWED.play(player);
                    return;
                }
                new BlocksManipulatorMenu(player, devPlanet, size).open(player);
            }
        } else {
            if (planet != null) {
                if (isPlayerWithLocation(player) && currentItem.getType() == Material.PAPER) {
                    event.setCancelled(true);
                    if (!player.hasCooldown(currentItem.getType())) {
                        if (currentItem.hasItemMeta()) {
                            ItemMeta meta = currentItem.getItemMeta();
                            String locationString = ChatColor.stripColor(meta.getDisplayName());
                            String[] locCoords = locationString.split(" ");
                            if (locCoords.length == 5) {
                                try {
                                    double x, y, z;
                                    float yaw, pitch;
                                    x = Double.parseDouble(locCoords[0]);
                                    y = Double.parseDouble(locCoords[1]);
                                    z = Double.parseDouble(locCoords[2]);
                                    yaw = Float.parseFloat(locCoords[3]);
                                    pitch = Float.parseFloat(locCoords[4]);
                                    player.teleport(new Location(planet.getTerritory().getWorld(), x, y, z, yaw, pitch));
                                } catch (Exception error) {
                                    player.teleport(planet.getTerritory().getSpawnLocation());
                                }
                            } else {
                                player.teleport(planet.getTerritory().getSpawnLocation());
                            }
                            Sounds.DEV_LOCATION_TELEPORT.play(player);
                            player.setCooldown(currentItem.getType(), 60);
                        }
                    }
                } else {
                    new ItemChangeEvent(event.getPlayer(), event).callEvent();
                }
            }
        }
    }

    @EventHandler
    public void onBookWrite(PlayerEditBookEvent event) {
        Planet planet = OpenCreative.getPlanetsManager().getPlanetByPlayer(event.getPlayer());
        if (planet != null) new BookWriteEvent(event.getPlayer(), event).callEvent();
    }

    @EventHandler
    public void onItemConsume(PlayerItemConsumeEvent event) {
        Planet planet = OpenCreative.getPlanetsManager().getPlanetByPlayer(event.getPlayer());
        if (planet != null) new ItemConsumeEvent(event.getPlayer(), event).callEvent();
    }

    @EventHandler
    public void onItemBreak(PlayerItemBreakEvent event) {
        ItemUtils.fixItem(event.getPlayer().getInventory().getItemInMainHand());
        ItemUtils.fixItem(event.getPlayer().getInventory().getItemInOffHand());
        Planet planet = OpenCreative.getPlanetsManager().getPlanetByPlayer(event.getPlayer());
        if (planet != null) new ItemBreakEvent(event.getPlayer(), event).callEvent();
    }

    @EventHandler
    public void onInventorySlotChange(PlayerInventorySlotChangeEvent event) {
        if (event.getPlayer().getOpenInventory().getTopInventory().getHolder() instanceof Layout) return;
        ItemUtils.fixItem(event.getNewItemStack());
        ItemUtils.fixItem(event.getOldItemStack());
    }

    @EventHandler
    public void onSlotChange(PlayerItemHeldEvent event) {
        Player player = event.getPlayer();
        int previousSlot = event.getPreviousSlot();
        int newSlot = event.getNewSlot();
        ItemStack previousItem = player.getInventory().getItem(previousSlot);
        ItemStack newItem = player.getInventory().getItem(newSlot);
        if (previousItem != null) {
            player.getInventory().setItem(previousSlot, ItemUtils.fixItem(previousItem));
        }
        if (newItem != null) {
            player.getInventory().setItem(newSlot, ItemUtils.fixItem(newItem));
        }
        Planet planet = OpenCreative.getPlanetsManager().getPlanetByPlayer(event.getPlayer());
        if (planet != null) new SlotChangeEvent(event.getPlayer(), event).callEvent();
    }
}
