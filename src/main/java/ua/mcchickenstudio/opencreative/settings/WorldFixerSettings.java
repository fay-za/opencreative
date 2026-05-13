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

package ua.mcchickenstudio.opencreative.settings;

import org.bukkit.Bukkit;
import org.bukkit.Keyed;
import org.bukkit.NamespacedKey;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Recipe;
import org.jetbrains.annotations.NotNull;
import ua.mcchickenstudio.opencreative.OpenCreative;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * <h1>WorldFixerSettings</h1>
 * This class represents a settings of world fixer.
 */
public final class WorldFixerSettings {

    private boolean fixVehicleCollisions = true;
    private int maxMinecartCollisionsAmount = 100;
    private RecipesUnlocker recipesUnlocker = RecipesUnlocker.NONE;

    private boolean fixBadEntitiesInAir = true;

    /**
     * Loads settings of world fixer from configuration.
     */
    public void load() {
        FileConfiguration config = OpenCreative.getPlugin().getConfig();
        ConfigurationSection section = config.getConfigurationSection("world-fixer");
        if (section == null) {
            section = config.createSection("world-fixer");
        }

        fixVehicleCollisions = section.getBoolean("vehicle-collisions.enabled", true);
        maxMinecartCollisionsAmount = section.getInt("vehicle-collisions.max-collisions", 100);

        fixBadEntitiesInAir = section.getBoolean("bad-entities-in-air.enabled", true);
        recipesUnlocker = switch (section.getString("give-all-recipes-on", "none").toLowerCase()) {
            case "crafting" -> RecipesUnlocker.CRAFTING;
            case "join" -> RecipesUnlocker.JOIN;
            default -> RecipesUnlocker.NONE;
        };
    }

    /**
     * Checks whether vehicles with too many collisions should be destroyed.
     *
     * @return true - will be fixed, false - not.
     */
    public boolean shouldFixVehicleCollisions() {
        return fixVehicleCollisions;
    }

    /**
     * Checks whether bad entities in air should be fixed.
     *
     * @return true - will be fixed, false - not.
     */
    public boolean shouldFixBadEntitiesInAir() {
        return fixBadEntitiesInAir;
    }

    /**
     * Returns maximum amount of vehicle collisions
     * in the last 0.5 seconds before its removal.
     *
     * @return limit of vehicle collisions.
     */
    public int getMaxMinecartCollisionsAmount() {
        return maxMinecartCollisionsAmount;
    }

    /**
     * Returns option, when all crafting recipes should be
     * unlocked for player.
     *
     * @return recipes unlocker type.
     */
    public @NotNull RecipesUnlocker getRecipesUnlocker() {
        return recipesUnlocker;
    }

    /**
     * <h1>RecipesUnlocker</h1>
     * This enum represents all options to unlock player
     * all crafting recipes at once. Required only, if
     * server has disabled advancements.
     */
    public enum RecipesUnlocker {

        /**
         * When player opens crafting table menu.
         */
        CRAFTING,
        /**
         * When player joins the server.
         */
        JOIN,
        /**
         * Don't unlock recipes.
         */
        NONE;

        private static int recipesAmount = -1;

        /**
         * Unlocks all Minecraft recipes for player.
         *
         * @param player player to unlock recipe.
         */
        public static void unlockAllRecipes(@NotNull Player player) {
            if (recipesAmount == -1) {
                recipesAmount = getMinecraftRecipes().size();
            }
            if (player.getDiscoveredRecipes().size() < recipesAmount) {
                player.discoverRecipes(getMinecraftRecipes());
            }

        }

        /**
         * Returns list of all registered Minecraft recipes.
         */
        public static @NotNull List<NamespacedKey> getMinecraftRecipes() {
            List<NamespacedKey> recipes = new ArrayList<>();
            for (@NotNull Iterator<Recipe> it = Bukkit.recipeIterator(); it.hasNext();) {
                Recipe recipe = it.next();
                if (recipe instanceof Keyed keyed) {
                    NamespacedKey key = keyed.getKey();
                    if (key.getNamespace().equals("minecraft")) {
                        recipes.add(key);
                    }
                }
            }
            return recipes;
        }

    }

}
