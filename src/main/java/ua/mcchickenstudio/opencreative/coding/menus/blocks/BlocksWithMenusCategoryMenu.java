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

package ua.mcchickenstudio.opencreative.coding.menus.blocks;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.BlockState;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import ua.mcchickenstudio.opencreative.coding.menus.MenusCategory;
import ua.mcchickenstudio.opencreative.menus.BlockMenu;

import static ua.mcchickenstudio.opencreative.utils.MessageUtils.getLocaleMessage;

/**
 * This class represents a menu where player can select type of coding block.
 * Every category of coding blocks has this menu.
 */
public abstract class BlocksWithMenusCategoryMenu<T> extends ContentWithMenusCategoryMenu<T> implements BlockMenu {

    protected Location signLocation;

    public BlocksWithMenusCategoryMenu(@NotNull Player player,
                                       @NotNull Location location,
                                       @NotNull String mainCategory,
                                       @NotNull String blockPath,
                                       @NotNull Material stainedPane,
                                       @NotNull MenusCategory defaultCategory) {
        super(player, mainCategory,
                ChatColor.stripColor(getLocaleMessage("blocks." + blockPath, false)),
                stainedPane, defaultCategory);
        this.signLocation = location;
    }

    public void setSignLocation(Location signLocation) {
        this.signLocation = signLocation;
    }

    @Override
    public @Nullable BlockState getBlockState() {
        return signLocation.getBlock().getState();
    }

    @Override
    public @Nullable Location getLocation() {
        return signLocation;
    }
}
