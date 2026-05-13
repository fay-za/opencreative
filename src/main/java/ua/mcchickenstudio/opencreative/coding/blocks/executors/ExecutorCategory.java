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

package ua.mcchickenstudio.opencreative.coding.blocks.executors;

import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import ua.mcchickenstudio.opencreative.coding.blocks.CodingBlockCategory;
import ua.mcchickenstudio.opencreative.coding.menus.MenusCategory;
import ua.mcchickenstudio.opencreative.utils.MessageUtils;

import static ua.mcchickenstudio.opencreative.utils.ItemUtils.createItem;

/**
 * <h1>ExecutorCategory</h1>
 * This enum defines different categories for coding blocks with executor type.
 * Every category has material of block that player will place. Members are:
 * EVENT_PLAYER, EVENT_WORLD, CYCLE, FUNCTION etc.
 *
 * @author McChicken Studio
 * @version 6.0
 * @since 5.0
 */
public enum ExecutorCategory implements CodingBlockCategory {

    EVENT_PLAYER(Material.DIAMOND_BLOCK, Material.DIAMOND_ORE, NamedTextColor.AQUA, Material.LIGHT_BLUE_STAINED_GLASS_PANE, MenusCategory.WORLD),
    EVENT_ENTITY(Material.GOLD_BLOCK, Material.GOLD_ORE, NamedTextColor.YELLOW, Material.YELLOW_STAINED_GLASS_PANE, MenusCategory.ENTITY_INTERACTION),
    EVENT_WORLD(Material.REDSTONE_BLOCK, Material.REDSTONE_ORE, NamedTextColor.RED, Material.RED_STAINED_GLASS_PANE, MenusCategory.WORLD_OTHER),
    STRUCTURE(Material.OXIDIZED_COPPER, Material.WAXED_OXIDIZED_CUT_COPPER, NamedTextColor.AQUA, Material.LIGHT_BLUE_STAINED_GLASS_PANE),
    FUNCTION(Material.LAPIS_BLOCK, Material.LAPIS_ORE, NamedTextColor.BLUE, Material.BLUE_STAINED_GLASS_PANE),
    METHOD(Material.EMERALD_BLOCK, Material.EMERALD_ORE, NamedTextColor.GREEN, Material.LIME_STAINED_GLASS_PANE);

    private final Material block;
    private final Material additionalBlock;
    private final NamedTextColor color;
    private final Material stainedPane;
    private final MenusCategory defaultCategory;

    /**
     * Creates instance of executor category.
     *
     * @param block first block.
     * @param additionalBlock second block.
     * @param color color of executor in chat.
     * @param stainedPane colored glass pane of executor.
     */
    ExecutorCategory(@NotNull Material block, @NotNull Material additionalBlock,
                     @NotNull NamedTextColor color, @NotNull Material stainedPane) {
        this(block, additionalBlock, color, stainedPane, MenusCategory.OTHER);
    }

    /**
     * Creates instance of executor category.
     *
     * @param block first block.
     * @param additionalBlock second block.
     * @param color color of executor in chat.
     * @param stainedPane colored glass pane of executor.
     * @param defaultCategory default category in menu.
     */
    ExecutorCategory(@NotNull Material block, @NotNull Material additionalBlock,
                     @NotNull NamedTextColor color, @NotNull Material stainedPane,
                     @NotNull MenusCategory defaultCategory) {
        this.block = block;
        this.additionalBlock = additionalBlock;
        this.color = color;
        this.stainedPane = stainedPane;
        this.defaultCategory = defaultCategory;
    }

    /**
     * Returns executor category by first block material.
     *
     * @param material first block material.
     * @return category, or null - if not found.
     */
    public static @Nullable ExecutorCategory getByMaterial(@NotNull Material material) {
        for (ExecutorCategory category : values()) {
            if (category.block == material) return category;
        }
        return null;
    }

    @Override
    public @NotNull MenusCategory getDefaultCategory() {
        return defaultCategory;
    }

    @Override
    public @NotNull Material getBlock() {
        return block;
    }

    @Override
    public @NotNull Material getStainedPane() {
        return stainedPane;
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
    public @NotNull Material getAdditionalBlock() {
        return additionalBlock;
    }

    @Override
    public @NotNull ItemStack getItem() {
        return createItem(block, 1, "items.developer." + name().toLowerCase().replace("_", "-"), name().toLowerCase());
    }

}
