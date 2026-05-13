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

package ua.mcchickenstudio.opencreative.settings.items;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BookMeta;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import ua.mcchickenstudio.opencreative.coding.blocks.actions.ActionCategory;
import ua.mcchickenstudio.opencreative.coding.blocks.executors.ExecutorCategory;
import ua.mcchickenstudio.opencreative.utils.ItemUtils;

import static ua.mcchickenstudio.opencreative.utils.ItemUtils.createItem;
import static ua.mcchickenstudio.opencreative.utils.ItemUtils.setPersistentData;
import static ua.mcchickenstudio.opencreative.utils.MessageUtils.getBookPages;

/**
 * <h1>Items</h1>
 * This enum represents main items, that will be given to players
 * by doing something: joining the lobby, connecting to world
 * or entering to the coding world.
 * <p>
 * To get item, use {@link #get(Player)} ()} method.
 */
public enum Items {

    /**
     * For opening Worlds Browser menu in lobby.
     */
    GAMES(Material.COMPASS, "lobby", "worlds"),
    /**
     * For opening Own Worlds Browser menu in lobby.
     */
    OWN(Material.NETHER_STAR, "lobby", "own_worlds"),
    /**
     * For viewing Changelogs in lobby.
     */
    CHANGELOGS(Material.WRITTEN_BOOK, "lobby"),

    /**
     * For opening World Settings menu in world, as its owner.
     */
    WORLD_SETTINGS(Material.COMPASS, "developer", "world_settings"),
    /**
     * For changing speed of flight in coding world.
     */
    FLY_SPEED_CHANGER(Material.FEATHER, "developer"),
    /**
     * For moving lines and marking them in coding world.
     */
    LINES_CONTROLLER(Material.COMPARATOR, "developer", "lines_controller"),
    /**
     * For marking or unmarking condition as opposed.
     */
    ARROW_NOT(Material.ARROW, "developer"),
    /**
     * For marking or unmarking executor whether it should send debug messages.
     */
    DEBUG_TORCH(Material.REDSTONE_TORCH, "developer"),
    /**
     * For viewing coding tutorial in coding world.
     */
    CODING_BOOK(Material.WRITTEN_BOOK, "developer"),
    /**
     * For viewing values list in coding world.
     */
    VARIABLES(Material.IRON_INGOT, "developer"),

    EVENT_PLAYER(ExecutorCategory.EVENT_PLAYER),
    EVENT_WORLD(ExecutorCategory.EVENT_WORLD),
    EVENT_ENTITY(ExecutorCategory.EVENT_ENTITY),
    METHOD(ExecutorCategory.METHOD),
    FUNCTION(ExecutorCategory.FUNCTION),
    STRUCTURE(ExecutorCategory.STRUCTURE),

    PLAYER_ACTION(ActionCategory.PLAYER_ACTION),
    WORLD_ACTION(ActionCategory.WORLD_ACTION),
    ENTITY_ACTION(ActionCategory.ENTITY_ACTION),
    VARIABLE_ACTION(ActionCategory.VARIABLE_ACTION),
    STRUCTURE_ACTION(ActionCategory.STRUCTURE_ACTION),
    REPEAT_ACTION(ActionCategory.REPEAT_ACTION),
    CONTROLLER_ACTION(ActionCategory.CONTROLLER_ACTION),
    CONTROL_ACTION(ActionCategory.CONTROL_ACTION),
    SELECTION_ACTION(ActionCategory.SELECTION_ACTION),

    LAUNCH_FUNCTION_ACTION(ActionCategory.LAUNCH_FUNCTION_ACTION),
    LAUNCH_METHOD_ACTION(ActionCategory.LAUNCH_METHOD_ACTION),

    PLAYER_CONDITION(ActionCategory.PLAYER_CONDITION),
    WORLD_CONDITION(ActionCategory.WORLD_CONDITION),
    ENTITY_CONDITION(ActionCategory.ENTITY_CONDITION),
    ELSE_CONDITION(ActionCategory.ELSE_CONDITION),
    VARIABLE_CONDITION(ActionCategory.VARIABLE_CONDITION);

    private final Material material;
    private final String persistentData;
    private final String group;

    /**
     * Creates main item with material and prefix group in localization file.
     *
     * @param material material of item.
     * @param group    group in localization file (lobby, developer).
     */
    Items(@NotNull Material material, @NotNull String group, @NotNull String persistentData) {
        this.material = material;
        this.group = group;
        this.persistentData = persistentData;
    }

    /**
     * Creates main item with material and prefix group in localization file.
     *
     * @param material material of item.
     * @param group    group in localization file (lobby, developer).
     */
    Items(@NotNull Material material, @NotNull String group) {
        this.material = material;
        this.group = group;
        this.persistentData = null;
    }

    /**
     * Creates main item by action block.
     *
     * @param group group of action.
     */
    Items(@NotNull ActionCategory group) {
        this.material = group.getBlock();
        this.group = "developer";
        this.persistentData = null;
    }

    /**
     * Creates main item by executor block.
     *
     * @param group group of executor.
     */
    Items(@NotNull ExecutorCategory group) {
        this.material = group.getBlock();
        this.group = "developer";
        this.persistentData = null;
    }

    /**
     * Returns items enum by text.
     *
     * @param id id to get items enum.
     * @return items type, or null - if not exists.
     */
    public static @Nullable Items getById(@NotNull String id) {
        id = id.toUpperCase().replace("-", "_");
        switch (id) {
            // Aliases
            case "OWN_WORLDS", "OWN_GAMES", "OWN_WORLD", "MY_WORLDS" -> {
                return OWN;
            }
            case "WORLDS", "ALL_WORLDS", "ALL_GAMES" -> {
                return GAMES;
            }
            case "SETTINGS" -> {
                return WORLD_SETTINGS;
            }
        }
        for (Items type : Items.values()) {
            if (type.name().equals(id)) {
                return type;
            }
        }
        return null;
    }

    /**
     * Returns material of main item.
     *
     * @return material.
     */
    public @NotNull Material getMaterial() {
        return material;
    }

    /**
     * Creates and returns item, that can be given to player.
     *
     * @return item to give to player.
     */
    @SuppressWarnings({"unused", "ResultOfMethodCallIgnored"})
    public ItemStack get(@NotNull Player player) {
        String path = "items." + group + "." + name().toLowerCase().replace("_", "-");
        ItemStack item = createItem(material, 1, path);
        if (item.getItemMeta() instanceof BookMeta bookMeta) {
            bookMeta.pages(getBookPages(player, path + ".pages"));
            item.setItemMeta(bookMeta);
        }
        if (persistentData != null) {
            setPersistentData(item, ItemUtils.getItemTypeKey(), persistentData);
        }
        return item;
    }
}
