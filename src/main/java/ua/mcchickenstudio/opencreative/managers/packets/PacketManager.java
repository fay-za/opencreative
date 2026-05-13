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

package ua.mcchickenstudio.opencreative.managers.packets;

import net.kyori.adventure.text.Component;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import ua.mcchickenstudio.opencreative.managers.Manager;

/**
 * <h1>PacketManager</h1>
 * This interface represents a packets handler, that
 * will send or modify incoming packets for players.
 * It contains special methods that cannot be done
 * using default Paper API.
 */
public interface PacketManager extends Manager {

    /**
     * Spawns a visual glowing block only for player
     * in specified location. Glowing block needs to
     * be invisible and not physical. As block entity
     * can be used {@link org.bukkit.entity.Shulker},
     * {@link org.bukkit.entity.BlockDisplay}, {@link org.bukkit.entity.FallingBlock}.
     *
     * @param player   player, that will see glowing block.
     * @param location location, where block will be seen.
     */
    void displayGlowingBlock(@NotNull Player player, @NotNull Location location);

    /**
     * Plays a chest opening animation for player.
     * <p>
     * If specified block is not chest, then animation
     * will be not played and will be ignored.
     *
     * @param player player, that can see this animation.
     * @param block  block where animation can be played.
     */
    void sendChestOpenAnimation(@NotNull Player player, @NotNull Block block);

    /**
     * Plays a chest closing animation for player.
     * <p>
     * If specified block is not chest, then animation
     * will be not played and will be ignored.
     *
     * @param player player, that can see this animation.
     * @param block  block where animation can be played.
     */
    void sendChestCloseAnimation(@NotNull Player player, @NotNull Block block);

    /**
     * Player's name in players list will be seen by receiver
     * with usual spectator's transparent gray color. Used to
     * separate players in same world and players in different
     * worlds.
     *
     * @param player   player to change display name.
     * @param receiver player that will see this change.
     */
    void displayAsSpectatorName(@NotNull Player player, @NotNull Player receiver);

    /**
     * Player's name in players list will be seen by receiver
     * as default player's name.
     *
     * @param player   player to change display name.
     * @param receiver player that will see this change.
     */
    void removeSpectatorName(@NotNull Player player, @NotNull Player receiver);

    /**
     * Shows fake phantom block for player.
     *
     * @param player   player to change display name.
     * @param location player that will see this change.
     * @param material type of block.
     */
    void showBlockForPlayer(@NotNull Player player, @NotNull Location location, @NotNull Material material);

}
