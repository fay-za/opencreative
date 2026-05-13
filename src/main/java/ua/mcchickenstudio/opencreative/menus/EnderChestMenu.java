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

package ua.mcchickenstudio.opencreative.menus;

import net.kyori.adventure.key.Key;
import net.kyori.adventure.sound.Sound;
import org.bukkit.Location;
import org.bukkit.block.BlockState;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import ua.mcchickenstudio.opencreative.OpenCreative;
import ua.mcchickenstudio.opencreative.menus.world.WorldMenu;
import ua.mcchickenstudio.opencreative.planets.Planet;
import ua.mcchickenstudio.opencreative.planets.PlanetPlayer;
import ua.mcchickenstudio.opencreative.utils.PlayerUtils;

public final class EnderChestMenu extends AbstractMenu implements WorldMenu, BlockMenu {

    private final @NotNull Planet planet;
    private final @Nullable Location location;
    private final @Nullable BlockState blockState;

    public EnderChestMenu(@NotNull Planet planet, @Nullable Location location) {
        super(3, InventoryType.ENDER_CHEST.getDefaultTitle());
        this.planet = planet;
        this.location = location;
        this.blockState = location != null ? location.getBlock().getState() : null;
    }

    @Override
    public void fillItems(Player player) {
        PlanetPlayer planetPlayer = planet.getWorldPlayers().getPlanetPlayer(player);
        if (planetPlayer == null) return;
        int slot = 0;
        for (ItemStack item : planetPlayer.getSavedEnderChest()) {
            if (slot >= 27) return;
            setItem(slot, item);
            slot++;
        }
    }

    @Override
    public void onClick(@NotNull InventoryClickEvent event) {
    }

    @Override
    public void onOpen(@NotNull InventoryOpenEvent event) {
        if (location != null) {
            new BukkitRunnable() {
                @Override
                public void run() {
                    for (Player player : location.getWorld().getPlayers()) {
                        PlayerUtils.sendOpenedChestAnimation(player, location.getBlock());
                    }
                }
            }.runTaskLater(OpenCreative.getPlugin(), 10L);
        } else {
            event.getPlayer().playSound(Sound.sound(Key.key("block.ender_chest.open"), Sound.Source.BLOCK, 100f, 1f));
        }
    }

    @Override
    public void onClose(@NotNull InventoryCloseEvent event) {
        event.getPlayer().playSound(Sound.sound(Key.key("block.ender_chest.close"), Sound.Source.BLOCK, 100f, 1f));
        if (location != null) {
            for (Player player : location.getWorld().getPlayers()) {
                PlayerUtils.sendClosedChestAnimation(player, location.getBlock());
            }
        }
        if (!(event.getPlayer() instanceof Player player)) return;
        PlanetPlayer planetPlayer = planet.getWorldPlayers().getPlanetPlayer(player);
        if (planetPlayer == null) return;
        planetPlayer.saveEnderChest(getInventory().getContents());
        destroy();
    }

    @Override
    public @NotNull Planet getPlanet() {
        return planet;
    }

    @Override
    public @Nullable BlockState getBlockState() {
        return blockState;
    }

    @Override
    public @Nullable Location getLocation() {
        return location;
    }
}
