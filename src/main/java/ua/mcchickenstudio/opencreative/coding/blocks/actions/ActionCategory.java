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

package ua.mcchickenstudio.opencreative.coding.blocks.actions;

import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import ua.mcchickenstudio.opencreative.coding.blocks.CodingBlockCategory;
import ua.mcchickenstudio.opencreative.coding.menus.MenusCategory;
import ua.mcchickenstudio.opencreative.utils.MessageUtils;

import static ua.mcchickenstudio.opencreative.utils.ItemUtils.createItem;

public enum ActionCategory implements CodingBlockCategory {

    PLAYER_ACTION(Material.COBBLESTONE, Material.STONE, NamedTextColor.GRAY, Material.GRAY_STAINED_GLASS_PANE, MenusCategory.COMMUNICATION),
    ENTITY_ACTION(Material.MOSSY_COBBLESTONE, Material.STONE, NamedTextColor.GREEN, Material.GREEN_STAINED_GLASS_PANE, MenusCategory.ENTITY_PARAMS),
    WORLD_ACTION(Material.NETHERRACK, Material.NETHER_GOLD_ORE, NamedTextColor.RED, Material.RED_STAINED_GLASS_PANE, MenusCategory.WORLD),
    VARIABLE_ACTION(Material.IRON_BLOCK, Material.IRON_ORE, NamedTextColor.WHITE, Material.WHITE_STAINED_GLASS_PANE, MenusCategory.OTHER),
    STRUCTURE_ACTION(Material.WAXED_WEATHERED_COPPER, Material.WAXED_WEATHERED_CUT_COPPER, NamedTextColor.AQUA, Material.WHITE_STAINED_GLASS_PANE, MenusCategory.CUSTOM),
    SELECTION_ACTION(Material.PURPUR_BLOCK, Material.PURPUR_PILLAR, NamedTextColor.LIGHT_PURPLE, Material.PINK_STAINED_GLASS_PANE),
    LAUNCH_FUNCTION_ACTION(Material.LAPIS_ORE, Material.STONE, NamedTextColor.AQUA, Material.BLUE_STAINED_GLASS_PANE),
    LAUNCH_METHOD_ACTION(Material.EMERALD_ORE, Material.STONE, NamedTextColor.GREEN, Material.LIME_STAINED_GLASS_PANE),
    CONTROL_ACTION(Material.COAL_BLOCK, Material.COAL_ORE, NamedTextColor.DARK_GRAY, Material.GRAY_STAINED_GLASS_PANE, MenusCategory.LINES),

    CONTROLLER_ACTION(Material.DARK_PRISMARINE, Material.PISTON, NamedTextColor.GREEN, Material.BLUE_STAINED_GLASS_PANE, MenusCategory.CONTROLLER),
    REPEAT_ACTION(Material.PRISMARINE, Material.PISTON, NamedTextColor.AQUA, Material.LIGHT_BLUE_STAINED_GLASS_PANE, MenusCategory.REPEATS),

    PLAYER_CONDITION(Material.OAK_PLANKS, Material.PISTON, NamedTextColor.GOLD, Material.ORANGE_STAINED_GLASS_PANE, MenusCategory.PARAMS),
    VARIABLE_CONDITION(Material.OBSIDIAN, Material.PISTON, NamedTextColor.BLUE, Material.BLUE_STAINED_GLASS_PANE, MenusCategory.OTHER),
    WORLD_CONDITION(Material.RED_NETHER_BRICKS, Material.PISTON, NamedTextColor.RED, Material.RED_STAINED_GLASS_PANE, MenusCategory.BLOCKS),
    ENTITY_CONDITION(Material.BRICKS, Material.PISTON, NamedTextColor.RED, Material.RED_STAINED_GLASS_PANE, MenusCategory.ENTITY_INTERACTION),
    ELSE_CONDITION(Material.END_STONE, Material.PISTON, NamedTextColor.YELLOW, Material.YELLOW_STAINED_GLASS_PANE);

    private final Material block;
    private final Material additionalBlock;
    private final NamedTextColor color;
    private final Material stainedPane;
    private final MenusCategory defaultCategory;

    ActionCategory(Material block, Material additionalBlock, NamedTextColor color, Material stainedPane) {
        this(block, additionalBlock, color, stainedPane, MenusCategory.OTHER);
    }

    ActionCategory(Material block, Material additionalBlock, NamedTextColor color, Material stainedPane, MenusCategory defaultCategory) {
        this.block = block;
        this.additionalBlock = additionalBlock;
        this.color = color;
        this.stainedPane = stainedPane;
        this.defaultCategory = defaultCategory;
    }

    /**
     * Returns action category by first block material.
     *
     * @param material first block material.
     * @return category, or null - if not found.
     */
    public static @Nullable ActionCategory getByMaterial(@NotNull Material material) {
        for (ActionCategory category : values()) {
            if (category.block == material) return category;
        }
        return null;
    }

    /**
     * Returns action category by id of category.
     *
     * @param text id of category.
     * @return category, or null - if not found.
     */
    public static @Nullable ActionCategory getCategory(@NotNull String text) {
        for (ActionCategory category : values()) {
            if (category.name().equalsIgnoreCase(text)) {
                return category;
            }
        }
        return null;
    }

    public boolean isMultiAction() {
        return this.additionalBlock == Material.PISTON;
    }

    public boolean isCondition() {
        // FIXME: maybe we should use class extends Condition?
        return this == PLAYER_CONDITION || this == VARIABLE_CONDITION || this == WORLD_CONDITION || this == ENTITY_CONDITION || this == ELSE_CONDITION;
    }

    @Override
    public @NotNull MenusCategory getDefaultCategory() {
        return defaultCategory;
    }

    @Override
    public @NotNull NamedTextColor getColor() {
        return color;
    }

    @Override
    public @NotNull String getLocaleName() {
        return MessageUtils.getLocaleMessage("blocks." + this.name().toLowerCase(), false);
    }

    @Override
    public @NotNull Material getStainedPane() {
        return stainedPane;
    }

    @Override
    public @NotNull Material getAdditionalBlock() {
        return additionalBlock;
    }

    @Override
    public @NotNull Material getBlock() {
        return block;
    }

    @Override
    public @NotNull ItemStack getItem() {
        return createItem(block, 1, "items.developer." + name().toLowerCase().replace("_", "-"), name().toLowerCase());
    }

}
