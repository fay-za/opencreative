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

import net.kyori.adventure.text.Component;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityPickupItemEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.inventory.ItemStack;
import ua.mcchickenstudio.opencreative.OpenCreative;
import ua.mcchickenstudio.opencreative.coding.blocks.events.player.inventory.ItemDropEvent;
import ua.mcchickenstudio.opencreative.coding.blocks.events.player.inventory.ItemPickupEvent;
import ua.mcchickenstudio.opencreative.planets.Planet;
import ua.mcchickenstudio.opencreative.utils.ItemUtils;
import ua.mcchickenstudio.opencreative.utils.world.WorldUtils;

import static ua.mcchickenstudio.opencreative.utils.ItemUtils.getItemType;

public final class DropItemListener implements Listener {

    @EventHandler
    public void onPlayerDropItem(PlayerDropItemEvent event) {
        Player player = event.getPlayer();
        ItemStack item = event.getItemDrop().getItemStack();
        if (item.hasItemMeta() && item.getItemMeta() != null) {
            Component name = item.getItemMeta().displayName();
            if (name != null) {
                if (WorldUtils.isLobbyWorld(player.getWorld())) {
                    if (item.getPersistentDataContainer().has(ItemUtils.getCodingDoNotDropMeKey())) {
                        event.setCancelled(true);
                    }
                } else {
                    if (getItemType(item).equalsIgnoreCase("world_settings")) {
                        event.setCancelled(true);
                    }
                }
            }
        }
        Planet planet = OpenCreative.getPlanetsManager().getPlanetByPlayer(player);
        if (planet != null) new ItemDropEvent(event.getPlayer(), event).callEvent();
    }

    @EventHandler
    public void onPlayerPickupItem(EntityPickupItemEvent event) {
        ItemStack item = ItemUtils.fixItem(event.getItem().getItemStack());
        if (item.getType().isAir()) {
            event.setCancelled(true);
        }
        event.getItem().setItemStack(item);
        if (!(event.getEntity() instanceof Player player)) return;
        Planet planet = OpenCreative.getPlanetsManager().getPlanetByPlayer(player);
        if (planet != null) new ItemPickupEvent(player, event).callEvent();
    }

}
