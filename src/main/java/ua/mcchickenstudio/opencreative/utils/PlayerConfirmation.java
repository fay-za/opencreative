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

package ua.mcchickenstudio.opencreative.utils;

import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * <h1>PlayerConfirmation</h1>
 * This enum stores all confirmation types for
 * players, when they have to confirm their action.
 */
public enum PlayerConfirmation {

    /**
     * When world's owner changes world's name.
     */
    WORLD_NAME_CHANGE,
    /**
     * When world's owner changes world's description.
     */
    WORLD_DESCRIPTION_CHANGE,
    /**
     * When world's owner changes world's custom id.
     */
    WORLD_CUSTOM_ID_CHANGE,
    /**
     * When player searches worlds by id.
     */
    FIND_PLANETS_BY_ID,
    /**
     * When player searches worlds by name.
     */
    FIND_PLANETS_BY_NAME,
    /**
     * When player searches worlds by owner.
     */
    FIND_PLANETS_BY_OWNER,
    /**
     * When world's owner transfers ownership to other player.
     */
    TRANSFER_OWNERSHIP,
    /**
     * When new world's owner accepts taking ownership of world.
     */
    GET_OWNERSHIP,
    /**
     * When module's owner changes module's display name.
     */
    MODULE_NAME_CHANGE,
    /**
     * When module's owner changes module's description.
     */
    MODULE_DESCRIPTION_CHANGE,
    /**
     * When player changes profile's description.
     */
    PROFILE_DESCRIPTION,
    /**
     * When player changes profile's social link.
     */
    PROFILE_SOCIAL_CHANGE;

    private static final Map<UUID, ConfirmationValue> confirmations = new HashMap<>();

    /**
     * Sets player's confirmation state to specified type.
     *
     * @param player player to set confirmation.
     * @param confirmation confirmation type to set.
     */
    public static void setConfirmation(@NotNull Player player, @NotNull PlayerConfirmation confirmation) {
        setConfirmation(player, confirmation, null);
    }

    /**
     * Sets player's confirmation state to specified type.
     *
     * @param player player to set confirmation.
     * @param confirmation confirmation type to set.
     * @param data additional data.
     */
    public static void setConfirmation(@NotNull Player player, @NotNull PlayerConfirmation confirmation, @Nullable Object data) {
        confirmations.put(player.getUniqueId(), new ConfirmationValue(confirmation, data));
    }

    /**
     * Returns player's confirmation type, or null.
     *
     * @param player player to get confirmation type.
     * @return confirmation type, or null - if not found.
     */
    public static @Nullable PlayerConfirmation getConfirmation(@NotNull Player player) {
        ConfirmationValue value = confirmations.get(player.getUniqueId());
        if (value == null) return null;
        return value.confirmation;
    }

    /**
     * Returns data from player's confirmation state, or empty text "".
     *
     * @param player player to get confirmation additional data.
     * @return additional data or empty text "", if not exists.
     */
    public static @NotNull Object getConfirmationData(@NotNull Player player) {
        ConfirmationValue value = confirmations.get(player.getUniqueId());
        if (value == null) return "";
        if (value.data == null) return "";
        return value.data;
    }

    /**
     * Removes any confirmation state from player.
     *
     * @param player player to clear confirmations.
     */
    public static void clearConfirmations(@NotNull Player player) {
        confirmations.remove(player.getUniqueId());
    }

    /**
     * Checks whether player has confirmation state.
     *
     * @param player player to check.
     * @return true - player is in confirmation process, false - not.
     */
    public static boolean hasConfirmation(@NotNull Player player) {
        return confirmations.containsKey(player.getUniqueId());
    }

    /**
     * This record represents confirmation value, that stores
     * type of confirmation and additional data.
     *
     * @param confirmation type of confirmation.
     * @param data additional data.
     */
    public record ConfirmationValue(@NotNull PlayerConfirmation confirmation, @Nullable Object data) {}

}
