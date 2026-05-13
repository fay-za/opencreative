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

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import ua.mcchickenstudio.opencreative.OpenCreative;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static ua.mcchickenstudio.opencreative.utils.ItemUtils.getCodingDoNotDropMeKey;
import static ua.mcchickenstudio.opencreative.utils.ItemUtils.setPersistentData;

/**
 * <h1>ItemsGroup</h1>
 * This enum represents OpenCreative+ items, that will be given
 * on some player actions: joining the lobby, connecting to world,
 * entering the coding world.
 * <p>
 * Groups (or kits) contain default item pairs (slots and items),
 * but they can be overridden by changing settings in config.
 * <p>
 * To set player's inventory to items from group, use {@link #setItems(Player)} method.
 */
public enum ItemsGroup {

    /**
     * Lobby items, that will be given, when player joins the lobby.
     * Should contain world browsers items.
     */
    LOBBY(new ItemPair(1, Items.CHANGELOGS), new ItemPair(4, Items.GAMES), new ItemPair(6, Items.OWN)),

    /**
     * Items, that will be given, when world's owner enters build mode.
     * Should contain world settings item and maybe some build tools?
     */
    BUILD_OWNER(new ItemPair(9, Items.WORLD_SETTINGS)),
    /**
     * Items, that will be given, when world's owner enters play mode.
     */
    PLAY_OWNER(new ItemPair(9, Items.WORLD_SETTINGS)),
    /**
     * Coding blocks and other coding items, that will be given, when
     * world's developer enters the coding world.
     */
    CODING(new ItemPair(1, Items.EVENT_PLAYER), new ItemPair(2, Items.PLAYER_ACTION), new ItemPair(3, Items.PLAYER_CONDITION),
            new ItemPair(4, Items.ELSE_CONDITION), new ItemPair(8, Items.CODING_BOOK), new ItemPair(9, Items.VARIABLES),
            new ItemPair(10, Items.STRUCTURE), new ItemPair(11, Items.REPEAT_ACTION), new ItemPair(12, Items.CONTROLLER_ACTION),
            new ItemPair(13, Items.CONTROL_ACTION), new ItemPair(14, Items.VARIABLE_ACTION), new ItemPair(15, Items.VARIABLE_CONDITION),
            new ItemPair(18, Items.FLY_SPEED_CHANGER), new ItemPair(19, Items.EVENT_WORLD), new ItemPair(20, Items.WORLD_CONDITION),
            new ItemPair(21, Items.WORLD_ACTION), new ItemPair(22, Items.METHOD), new ItemPair(23, Items.LAUNCH_METHOD_ACTION),
            new ItemPair(24, Items.SELECTION_ACTION), new ItemPair(26, Items.DEBUG_TORCH), new ItemPair(27, Items.LINES_CONTROLLER),
            new ItemPair(28, Items.EVENT_ENTITY), new ItemPair(29, Items.ENTITY_CONDITION), new ItemPair(30, Items.ENTITY_ACTION),
            new ItemPair(33, Items.STRUCTURE_ACTION), new ItemPair(31, Items.FUNCTION), new ItemPair(32, Items.LAUNCH_FUNCTION_ACTION), new ItemPair(36, Items.ARROW_NOT)),

    /**
     * Coding blocks and other coding items, that will be given, when
     * world's owner enters the coding world. Should contain world settings item.
     */
    CODING_OWNER(new ItemPair(1, Items.EVENT_PLAYER), new ItemPair(2, Items.PLAYER_ACTION), new ItemPair(3, Items.PLAYER_CONDITION),
            new ItemPair(4, Items.ELSE_CONDITION), new ItemPair(8, Items.VARIABLES), new ItemPair(9, Items.WORLD_SETTINGS),
            new ItemPair(10, Items.STRUCTURE), new ItemPair(11, Items.REPEAT_ACTION), new ItemPair(12, Items.CONTROLLER_ACTION),
            new ItemPair(13, Items.CONTROL_ACTION), new ItemPair(14, Items.VARIABLE_ACTION), new ItemPair(15, Items.VARIABLE_CONDITION),
            new ItemPair(17, Items.FLY_SPEED_CHANGER), new ItemPair(18, Items.CODING_BOOK), new ItemPair(19, Items.EVENT_WORLD),
            new ItemPair(20, Items.WORLD_CONDITION), new ItemPair(21, Items.WORLD_ACTION), new ItemPair(22, Items.METHOD),
            new ItemPair(23, Items.LAUNCH_METHOD_ACTION), new ItemPair(24, Items.SELECTION_ACTION), new ItemPair(26, Items.DEBUG_TORCH),
            new ItemPair(27, Items.LINES_CONTROLLER), new ItemPair(28, Items.EVENT_ENTITY), new ItemPair(29, Items.ENTITY_CONDITION),
            new ItemPair(30, Items.ENTITY_ACTION), new ItemPair(31, Items.FUNCTION), new ItemPair(32, Items.LAUNCH_FUNCTION_ACTION),
            new ItemPair(33, Items.STRUCTURE_ACTION), new ItemPair(36, Items.ARROW_NOT));

    private final List<ItemPair> pairs;

    /**
     * Creates instance of items group with default pairs.
     *
     * @param defaultPairs pairs of slots and items.
     */
    ItemsGroup(@NotNull ItemPair... defaultPairs) {
        this.pairs = List.of(defaultPairs);
    }

    /**
     * Returns items group by text.
     *
     * @param id id to get items group.
     * @return items group, or null - if not exists.
     */
    public static @Nullable ItemsGroup getById(@NotNull String id) {
        for (ItemsGroup group : ItemsGroup.values()) {
            if (group.name().equals(id)) {
                return group;
            }
        }
        return null;
    }

    /**
     * Returns list of pairs with slots and items.
     *
     * @return pairs of items.
     */
    public @NotNull List<ItemPair> getPairs() {
        return pairs;
    }

    /**
     * Returns list of items, created for player to give manually.
     * Empty items are not included.
     *
     * @param player player to get items.
     * @return list of items.
     */
    public @NotNull List<ItemStack> getItems(@NotNull Player player) {
        List<ItemStack> list = new ArrayList<>();
        SettingsItemsGroup group = OpenCreative.getSettings().getItemsGroups().get(this);
        if (group == null) {
            for (ItemPair pair : pairs) {
                ItemStack item = pair.item().get(player);
                if (this == LOBBY) {
                    setPersistentData(item, getCodingDoNotDropMeKey(), "1");
                }
                if (item.isEmpty()) continue;
                list.add(item);
            }
            return list;
        }
        Map<Integer, SettingsItem> items = group.getItems();
        for (int slot : items.keySet()) {
            ItemStack item = items.get(slot).getItem(player);
            if (this == LOBBY) {
                setPersistentData(item, getCodingDoNotDropMeKey(), "1");
            }
            if (item.isEmpty()) continue;
            list.add(item);
        }
        return list;
    }

    /**
     * Sets items in player inventory.
     * <p>
     * Recommended to clear player's inventory before doing this.
     *
     * @param player player to set items.
     */
    public void setItems(@NotNull Player player) {
        SettingsItemsGroup group = OpenCreative.getSettings().getItemsGroups().get(this);
        if (group == null) {
            for (ItemPair pair : pairs) {
                ItemStack item = pair.item().get(player);
                if (this == LOBBY) {
                    setPersistentData(item, getCodingDoNotDropMeKey(), "1");
                }
                player.getInventory().setItem(pair.slot() - 1, item);
            }
            return;
        }
        Map<Integer, SettingsItem> items = group.getItems();
        for (int slot : items.keySet()) {
            ItemStack item = items.get(slot).getItem(player);
            if (this == LOBBY) {
                setPersistentData(item, getCodingDoNotDropMeKey(), "1");
            }
            player.getInventory().setItem(slot - 1, item);
        }
    }

    /**
     * Sets items in player inventory, if player doesn't have them.
     * <p>
     * Recommended to clear player's inventory before doing this.
     *
     * @param player player to set items.
     */
    public void setItemsIfAbsent(@NotNull Player player) {
        SettingsItemsGroup group = OpenCreative.getSettings().getItemsGroups().get(this);
        if (group == null) {
            for (ItemPair pair : pairs) {
                ItemStack item = pair.item().get(player);
                if (!player.getInventory().contains(item, 1)) player.getInventory().setItem(pair.slot() - 1, item);
            }
            return;
        }
        Map<Integer, SettingsItem> items = group.getItems();
        for (int slot : items.keySet()) {
            ItemStack item = items.get(slot).getItem(player);
            if (!player.getInventory().contains(item, 1)) player.getInventory().setItem(slot - 1, item);
        }
    }

    /**
     * Removes items from player inventory, if player has them.
     *
     * @param player player to remove items.
     */
    public void removeItems(@NotNull Player player) {
        SettingsItemsGroup group = OpenCreative.getSettings().getItemsGroups().get(this);
        if (group == null) {
            for (ItemPair pair : pairs) {
                ItemStack item = pair.item().get(player);
                player.getInventory().removeItemAnySlot(item);
            }
            return;
        }
        Map<Integer, SettingsItem> items = group.getItems();
        for (int slot : items.keySet()) {
            ItemStack item = items.get(slot).getItem(player);
            player.getInventory().removeItemAnySlot(item);
        }
    }

    /**
     * Gives player an item, that was got from items group.
     *
     * @param player player to give item.
     * @param slot   slot of item.
     * @return true - if given, false - if item not exists by slot.
     */
    public boolean giveItem(@NotNull Player player, int slot) {
        SettingsItemsGroup group = OpenCreative.getSettings().getItemsGroups().get(this);
        if (group == null) {
            for (ItemPair pair : pairs) {
                if (pair.slot() == slot) {
                    player.getInventory().addItem(pair.item().get(player));
                    return true;
                }
            }
            return false;
        }
        Map<Integer, SettingsItem> items = group.getItems();
        SettingsItem item = items.get(slot);
        if (item == null) return false;
        player.getInventory().addItem(item.getItem(player));
        return true;
    }
}
