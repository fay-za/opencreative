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
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.inventory.ShapelessRecipe;
import org.bukkit.inventory.recipe.CraftingBookCategory;
import org.jetbrains.annotations.NotNull;
import ua.mcchickenstudio.opencreative.OpenCreative;
import ua.mcchickenstudio.opencreative.coding.arguments.Arguments;
import ua.mcchickenstudio.opencreative.coding.blocks.actions.ActionType;
import ua.mcchickenstudio.opencreative.coding.blocks.actions.Target;
import ua.mcchickenstudio.opencreative.coding.blocks.actions.worldactions.WorldAction;
import ua.mcchickenstudio.opencreative.coding.blocks.executors.Executor;

import java.util.*;

import static ua.mcchickenstudio.opencreative.utils.ErrorUtils.sendPlanetLimitWarningMessage;

public final class AddCraftingRecipeAction extends WorldAction {
    public AddCraftingRecipeAction(Executor executor, Target target, int x, Arguments args) {
        super(executor, target, x, args);
    }

    @Override
    protected void execute() {

        if (!getArguments().pathExists("result")) {
            return;
        }

        List<ItemStack> items = getArguments().getItemList("items", this);
        if (items.isEmpty()) {
            return;
        }

        int total = getPlanet().getTerritory().getRecipes().getAmount();
        if (total > getPlanet().getLimits().getRecipesLimit()) {
            sendPlanetLimitWarningMessage(this, "recipes", total, getPlanet().getLimits().getRecipesLimit());
            return;
        }

        ItemStack result = getArguments().getItem("result", new ItemStack(Material.APPLE), this);
        boolean ignoreShape = getArguments().getBoolean("ignore-shape", false, this);
        String name = getArguments().getText("name", "custom", this);
        String categoryString = getArguments().getText("category", "misc", this);
        boolean ignoreData = getArguments().getBoolean("ignore-data", false, this);

        CraftingBookCategory category = switch (categoryString.toLowerCase()) {
            case "building" -> CraftingBookCategory.BUILDING;
            case "equipment" -> CraftingBookCategory.EQUIPMENT;
            case "redstone" -> CraftingBookCategory.REDSTONE;
            default -> CraftingBookCategory.MISC;
        };

        NamespacedKey key = new NamespacedKey(OpenCreative.getPlugin(), "oc_recipe_"
                + getPlanet().getId() + "_" + name);

        if (ignoreShape) {
            // Shapeless recipe
            // Item can be crafted in any position
            ShapelessRecipe recipe = new ShapelessRecipe(key, result);
            for (ItemStack item : items) {
                if (item.isEmpty()) continue;
                if (ignoreData) {
                    recipe.addIngredient(item.getType());
                } else {
                    recipe.addIngredient(item);
                }
            }
            recipe.setCategory(category);
            getPlanet().getTerritory().getRecipes().addRecipe(recipe.getKey(), recipe);
            return;
        }

        // Shaped recipe
        // Requires specific slots order

        char[] letters = new char[]{'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'J'};
        Map<ItemStack, Character> recipeShape = new LinkedHashMap<>();
        StringBuilder shape = new StringBuilder();

        int count = 0;
        for (ItemStack item : items) {
            if (item.isEmpty()) {
                shape.append(' ');
                continue;
            }
            if (count > 8) break;
            //
            // Apple Diamond Gold  A B C
            // Apple Apple Gold    A A B
            //
            if (ignoreData) item = new ItemStack(item.getType());
            if (!recipeShape.containsKey(item)) {
                char character = letters[count++];
                recipeShape.put(item, character);
                shape.append(character);
            } else {
                shape.append(recipeShape.get(item));
            }
        }

        ShapedRecipe recipe = new ShapedRecipe(key, result);
        recipe.shape(splitThreeTimes(shape.toString()));
        recipe.setCategory(category);

        char lastCharacter = '.';
        for (Map.Entry<ItemStack, Character> entry : recipeShape.entrySet()) {
            if (entry.getValue() != lastCharacter) {
                lastCharacter = entry.getValue();
                if (ignoreData) {
                    recipe.setIngredient(lastCharacter, entry.getKey().getType());
                } else {
                    recipe.setIngredient(lastCharacter, entry.getKey());
                }
            }
        }

        getPlanet().getTerritory().getRecipes().addRecipe(recipe.getKey(), recipe);
    }

    private @NotNull String[] splitThreeTimes(@NotNull String recipe) {
        List<String> parts = new ArrayList<>();
        int n = recipe.length();

        int base = n / 3;
        int remainder = n % 3;

        int index = 0;
        for (int i = 0; i < 3; i++) {
            int size = base + (i < remainder ? 1 : 0);
            if (size > 0) {
                parts.add(recipe.substring(index, index + size));
                index += size;
            }
        }

        return parts.toArray(new String[0]);
    }

    @Override
    public @NotNull ActionType getActionType() {
        return ActionType.WORLD_ADD_CRAFTING_RECIPE;
    }
}
