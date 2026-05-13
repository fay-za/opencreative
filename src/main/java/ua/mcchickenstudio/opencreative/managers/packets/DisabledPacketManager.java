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

/**
 * This class represents a disabled packet manager is used,
 * when other plugins implementation is not detected. It will
 * not do anything on methods usage.
 */
public final class DisabledPacketManager implements PacketManager {

    @Override
    public void displayGlowingBlock(@NotNull Player player, @NotNull Location location) {
    }

    @Override
    public void sendChestOpenAnimation(@NotNull Player player, @NotNull Block block) {
    }

    @Override
    public void sendChestCloseAnimation(@NotNull Player player, @NotNull Block block) {
    }

    @Override
    public void displayAsSpectatorName(@NotNull Player player, @NotNull Player receiver) {
    }

    @Override
    public void removeSpectatorName(@NotNull Player player, @NotNull Player receiver) {
    }

    @Override
    public void showBlockForPlayer(@NotNull Player player, @NotNull Location location, @NotNull Material material) {
    }

    @Override
    public void init() {
    }

    @Override
    public boolean isEnabled() {
        return false;
    }

    @Override
    public String getName() {
        return "Disabled Packet Manager";
    }
}
