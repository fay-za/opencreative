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
import ua.mcchickenstudio.opencreative.OpenCreative;
import ua.mcchickenstudio.opencreative.settings.groups.Group;

import java.util.HashMap;
import java.util.UUID;

import static ua.mcchickenstudio.opencreative.utils.MessageUtils.getLocaleMessage;

/**
 * <h1>CooldownUtils</h1>
 * This class contains utils for checking and modifying player's cooldown.
 */
public final class CooldownUtils {

    private static final HashMap<UUID, Long> genericCommandCooldown = new HashMap<>();
    private static final HashMap<UUID, Long> advertisementCommandCooldown = new HashMap<>();
    private static final HashMap<UUID, Long> creativeChatCooldown = new HashMap<>();
    private static final HashMap<UUID, Long> worldChatCooldown = new HashMap<>();
    private static final HashMap<UUID, Long> modulesManipulationsCooldown = new HashMap<>();
    private static final HashMap<UUID, Long> blocksDuplicationCooldown = new HashMap<>();
    private static final HashMap<UUID, Long> worldDownloadCooldown = new HashMap<>();
    private static final HashMap<UUID, Long> prompterDownloadCooldown = new HashMap<>();

    /**
     * Returns cooldown timestamp for player, or 0 - if they don't have cooldown.
     *
     * @param player player to get timestamp of cooldown.
     * @param type type of cooldown.
     * @return timestamp, when cooldown will be ended; or 0 - if player doesn't have cooldown.
     */
    public static long getCooldownFromMap(@NotNull Player player, @NotNull CooldownType type) {

        HashMap<UUID, Long> cooldownMap = getCooldownMap(type);

        if (!(cooldownMap.containsKey(player.getUniqueId()))) return 0L;
        return cooldownMap.get(player.getUniqueId());
    }

    /**
     * Sets player's cooldown.
     *
     * @param player   Player to set cooldown.
     * @param cooldown Cooldown to set, in seconds.
     * @param type     Type of cooldown.
     **/
    public static void setCooldown(@NotNull Player player, int cooldown, @NotNull CooldownType type) {

        long cooldownInMillis = cooldown * 1000L;
        long currentTime = System.currentTimeMillis();
        long cooldownEndTime = currentTime + cooldownInMillis;

        HashMap<UUID, Long> cooldownMap = getCooldownMap(type);
        cooldownMap.put(player.getUniqueId(), cooldownEndTime);

    }

    /**
     * Returns player's cooldown.
     *
     * @param player Player for getting cooldown.
     * @param type   Type of cooldown.
     * @return cooldown - Remaining time for passing cooldown, in seconds. Returns 0, if player hasn't cooldown or player has bypass.
     **/
    public static int getCooldown(@NotNull Player player,
                                  @NotNull CooldownType type) {

        if (player.hasPermission("opencreative.cooldown.bypass")) return 0;
        long cooldownEndTime = getCooldownFromMap(player, type);
        if (cooldownEndTime == 0L) return 0;

        long currentTime = System.currentTimeMillis();

        if (cooldownEndTime < currentTime) {
            return 0;
        } else {
            return Math.round(cooldownEndTime - currentTime) / 1000;
        }
    }

    /**
     * Checks if player has cooldown. If not, sets it and returns true.
     * If cooldown is active, returns false.
     *
     * @param player Player to check and set cooldown
     * @param group  Group object for retrieving cooldown durations
     * @param type   Cooldown type to check/set
     * @return true if cooldown was not active and now set; false if still on cooldown
     */
    public static boolean checkAndSetCooldown(@NotNull Player player,
                                              @NotNull Group group,
                                              @NotNull CooldownType type) {
        int currentCooldown = getCooldown(player, type);
        if (currentCooldown > 0) return false;

        setCooldown(player, getGroupCooldown(type, group), type);
        return true;
    }

    /**
     * Checks if player has cooldown. If not, sets it and returns true.
     * If cooldown is active, returns false and sends cooldown message.
     *
     * @param player Player to check and set cooldown
     * @param group  Group object for retrieving cooldown durations
     * @param type   Cooldown type to check/set
     * @return true if cooldown was not active and now set; false if still on cooldown
     */
    public static boolean checkAndSetCooldownWithMessage(@NotNull Player player,
                                                         @NotNull Group group,
                                                         @NotNull CooldownType type) {
        if (!checkAndSetCooldown(player, group, type)) {
            player.sendMessage(getLocaleMessage("cooldown")
                    .replace("%cooldown%", String.valueOf(getCooldown(player, type))));
            return false;
        }
        return true;
    }

    /**
     * Checks if player has cooldown. If not, sets it and returns true.
     * If cooldown is active, returns false.
     *
     * @param player Player to check and set cooldown
     * @param type   Cooldown type to check/set
     * @return true if cooldown was not active and now set; false if still on cooldown
     */
    public static boolean checkAndSetCooldownWithMessage(@NotNull Player player,
                                                         @NotNull CooldownType type) {
        Group group = OpenCreative.getSettings().getGroups().getGroup(player);
        return checkAndSetCooldownWithMessage(player, group, type);
    }

    /**
     * Clears all player's cooldowns.
     *
     * @param player player to remove cooldowns.
     */
    public static void clearPlayerCooldowns(@NotNull Player player) {
        genericCommandCooldown.remove(player.getUniqueId());
        advertisementCommandCooldown.remove(player.getUniqueId());
        creativeChatCooldown.remove(player.getUniqueId());
        worldChatCooldown.remove(player.getUniqueId());
        modulesManipulationsCooldown.remove(player.getUniqueId());
        blocksDuplicationCooldown.remove(player.getUniqueId());
        worldDownloadCooldown.remove(player.getUniqueId());
        prompterDownloadCooldown.remove(player.getUniqueId());
    }

    private static HashMap<UUID, Long> getCooldownMap(@NotNull CooldownType type) {
        return switch (type) {
            case GENERIC_COMMAND -> genericCommandCooldown;
            case ADVERTISEMENT_COMMAND -> advertisementCommandCooldown;
            case CREATIVE_CHAT -> creativeChatCooldown;
            case WORLD_CHAT -> worldChatCooldown;
            case MODULE_MANIPULATION -> modulesManipulationsCooldown;
            case BLOCKS_DUPLICATION -> blocksDuplicationCooldown;
            case WORLD_DOWNLOAD -> worldDownloadCooldown;
            case PROMPTER_REQUEST -> prompterDownloadCooldown;
        };
    }

    private static int getGroupCooldown(@NotNull CooldownType type, @NotNull Group group) {
        return switch (type) {
            case GENERIC_COMMAND -> group.getGenericCommandCooldown();
            case ADVERTISEMENT_COMMAND -> group.getAdvertisementCooldown();
            case CREATIVE_CHAT -> group.getCreativeChatCooldown();
            case WORLD_CHAT -> group.getChatCooldown();
            case MODULE_MANIPULATION -> group.getModuleManipulationCooldown();
            case BLOCKS_DUPLICATION -> group.getBlocksDuplicationCooldown();
            case WORLD_DOWNLOAD -> group.getWorldDownloadCooldown();
            case PROMPTER_REQUEST -> group.getPrompterUsageCooldown();
        };
    }

    /**
     * Represents type of cooldown.
     */
    public enum CooldownType {
        /**
         * Cooldown of generic commands.
         */
        GENERIC_COMMAND,
        /**
         * World Advertisement cooldown.
         */
        ADVERTISEMENT_COMMAND,
        /**
         * Cooldown of sending message in global chat.
         */
        CREATIVE_CHAT,
        /**
         * Cooldown of sending message in world chat.
         */
        WORLD_CHAT,
        /**
         * Cooldown of creating or installing module.
         */
        MODULE_MANIPULATION,
        /**
         * Cooldown of duplicating coding blocks with manipulator.
         */
        BLOCKS_DUPLICATION,
        /**
         * Cooldown of downloading world with /world download.
         */
        WORLD_DOWNLOAD,
        /**
         * Cooldown of requesting code generation with /env make.
         */
        PROMPTER_REQUEST
    }
}
