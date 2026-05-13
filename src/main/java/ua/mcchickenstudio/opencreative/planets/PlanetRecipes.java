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

package ua.mcchickenstudio.opencreative.planets;

import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Recipe;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import ua.mcchickenstudio.opencreative.OpenCreative;

import java.util.HashSet;
import java.util.Set;

/**
 * <h1>PlanetRecipes</h1>
 * This class represents planet's recipes registry, that has
 * methods to register and unregister recipe.
 */
public class PlanetRecipes {

    private final Set<NamespacedKey> recipes = new HashSet<>();
    private final Planet planet;

    public PlanetRecipes(@NotNull Planet planet) {
        this.planet = planet;
    }

    /**
     * Registers recipe, or replaces old one with specified new.
     *
     * @param key    key of recipe (must start with "oc_recipe_WORLDID").
     * @param recipe recipe to register.
     */
    public void addRecipe(@NotNull NamespacedKey key, @NotNull Recipe recipe) {
        if (recipes.contains(key)) {
            return;
        }
        if (planet.getLimits().isTooManyRecipeOperationsAtOnce()) {
            throw new RuntimeException("Too many recipe operations at once");
        }
        if (Bukkit.getRecipe(key) != null) {
            removeRecipe(key);
        }
        recipes.add(key);
        Bukkit.addRecipe(recipe, false);
    }

    /**
     * Unregisters recipe from registry and removes it from players.
     *
     * @param name name of recipe.
     */
    public void removeRecipe(@NotNull String name) {
        NamespacedKey found = getRecipe(name);
        if (found == null) return;
        removeRecipe(found);
    }

    /**
     * Unregisters recipe from registry and takes it from players.
     *
     * @param recipe namespaced key of recipe.
     */
    public void removeRecipe(@NotNull NamespacedKey recipe) {
        recipes.remove(recipe);
        for (Player player : planet.getPlayers()) {
            player.undiscoverRecipe(recipe);
        }
        Bukkit.removeRecipe(recipe, false);
    }

    /**
     * Returns recipe's namespaced key.
     *
     * @param name name of recipe.
     * @return recipe's namespaced key, or null - if not found.
     */
    public @Nullable NamespacedKey getRecipe(@NotNull String name) {
        name = getFullKey(name);
        NamespacedKey found = null;
        for (NamespacedKey key : recipes) {
            if (key.getKey().equalsIgnoreCase(name)) {
                found = key;
                break;
            }
        }
        return found;
    }

    /**
     * Removes all recipes and destroys them.
     */
    public void clear() {
        if (OpenCreative.getPlugin().isEnabled()) {
            Bukkit.getScheduler().runTask(OpenCreative.getPlugin(), () -> {
                for (NamespacedKey recipe : recipes) {
                    Bukkit.removeRecipe(recipe, false);
                }
                for (Player player : planet.getPlayers()) {
                    player.undiscoverRecipes(recipes);
                }
                recipes.clear();
            });
        } else {
            recipes.clear();
        }
    }

    /**
     * Removes all recipes from player.
     *
     * @param player player to remove recipes.
     */
    public void clearForPlayer(@NotNull Player player) {
        player.undiscoverRecipes(recipes);
    }

    /**
     * Returns amount of all recipes.
     *
     * @return amount of recipes.
     */
    public int getAmount() {
        return recipes.size();
    }

    /**
     * Returns full namespaced key for recipe
     * with specified name.
     *
     * @param name name of recipe.
     * @return namespaced key ("oc_recipe_PLANETID_NAME")
     */
    public @NotNull String getFullKey(@NotNull String name) {
        return "oc_recipe_" + planet.getId() + "_" + name;
    }

}
