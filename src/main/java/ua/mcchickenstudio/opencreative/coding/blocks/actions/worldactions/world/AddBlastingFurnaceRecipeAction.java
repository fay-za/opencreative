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

package ua.mcchickenstudio.opencreative.coding.blocks.actions.worldactions.world;

import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.BlastingRecipe;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.recipe.CookingBookCategory;
import org.jetbrains.annotations.NotNull;
import ua.mcchickenstudio.opencreative.OpenCreative;
import ua.mcchickenstudio.opencreative.coding.arguments.Arguments;
import ua.mcchickenstudio.opencreative.coding.blocks.actions.ActionType;
import ua.mcchickenstudio.opencreative.coding.blocks.actions.Target;
import ua.mcchickenstudio.opencreative.coding.blocks.actions.worldactions.WorldAction;
import ua.mcchickenstudio.opencreative.coding.blocks.executors.Executor;

import static ua.mcchickenstudio.opencreative.utils.ErrorUtils.sendPlanetLimitWarningMessage;

public final class AddBlastingFurnaceRecipeAction extends WorldAction {
    public AddBlastingFurnaceRecipeAction(Executor executor, Target target, int x, Arguments args) {
        super(executor, target, x, args);
    }

    @Override
    protected void execute() {

        if (!getArguments().pathExists("item") || !getArguments().pathExists("result")) {
            return;
        }

        int total = getPlanet().getTerritory().getRecipes().getAmount();
        if (total > getPlanet().getLimits().getRecipesLimit()) {
            sendPlanetLimitWarningMessage(this, "recipes", total, getPlanet().getLimits().getRecipesLimit());
            return;
        }

        ItemStack item = getArguments().getItem("item", new ItemStack(Material.EGG), this);
        ItemStack result = getArguments().getItem("result", new ItemStack(Material.CHICKEN_SPAWN_EGG), this);

        String name = getArguments().getText("name", "recipe", this);
        String categoryString = getArguments().getText("category", "misc", this);

        CookingBookCategory category = CookingBookCategory.MISC;
        if (categoryString.equalsIgnoreCase("blocks")) {
            category = CookingBookCategory.BLOCKS;
        }

        int cookingTime = getArguments().getInt("time", 100, this);
        if (cookingTime < 1) {
            cookingTime = 1;
        }
        if (cookingTime > 6000) {
            cookingTime = 6000;
        }
        float experience = getArguments().getFloat("experience", 0.0f, this);

        NamespacedKey key = new NamespacedKey(OpenCreative.getPlugin(), "oc_recipe_"
                + getPlanet().getId() + "_" + name);

        BlastingRecipe recipe = new BlastingRecipe(key, result, item.getType(), experience, cookingTime);
        recipe.setCategory(category);
        getPlanet().getTerritory().getRecipes().addRecipe(recipe.getKey(), recipe);
    }
    @Override
    public @NotNull ActionType getActionType() {
        return ActionType.WORLD_ADD_BLAST_FURNACE_RECIPE;
    }
}
