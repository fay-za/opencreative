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

import org.bukkit.Keyed;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.inventory.PrepareItemCraftEvent;
import org.bukkit.event.player.PlayerRecipeDiscoverEvent;
import ua.mcchickenstudio.opencreative.OpenCreative;
import ua.mcchickenstudio.opencreative.planets.Planet;
import ua.mcchickenstudio.opencreative.settings.WorldFixerSettings;

public final class CraftListener implements Listener {

    @EventHandler
    public void onCraftingOpen(InventoryOpenEvent event) {
        if (OpenCreative.getSettings().getWorldFixerSettings().getRecipesUnlocker() != WorldFixerSettings.RecipesUnlocker.CRAFTING)
            return;
        if (!event.getInventory().getType().name().contains("CRAFT") && event.getInventory().getType() != InventoryType.WORKBENCH)
            return;
        if (event.getPlayer() instanceof Player player) {
            WorldFixerSettings.RecipesUnlocker.unlockAllRecipes(player);
        }
    }

    @EventHandler
    public void onCraftTry(PrepareItemCraftEvent event) {
        if (!(event.getRecipe() instanceof Keyed keyed)) return;
        String key = keyed.getKey().getKey();
        if (!key.startsWith("oc_recipe_")) return;
        if (!(event.getInventory().getViewers().getFirst() instanceof Player player)) return;
        Planet planet = OpenCreative.getPlanetsManager().getPlanetByPlayer(player);
        if (planet == null) {
            event.getInventory().setResult(null);
            return;
        }
        if (!key.startsWith("oc_recipe_" + planet.getId() + "_")) {
            event.getInventory().setResult(null);
        }
    }

    @EventHandler
    public void onRequest(PlayerRecipeDiscoverEvent event) {
        String key = event.getRecipe().getKey();
        if (!key.startsWith("oc_recipe_")) return;
        Planet planet = OpenCreative.getPlanetsManager().getPlanetByPlayer(event.getPlayer());
        if (planet == null) {
            event.setCancelled(true);
            return;
        }
        if (!key.startsWith("oc_recipe_" + planet.getId() + "_")) {
            event.setCancelled(true);
        }
    }

}
