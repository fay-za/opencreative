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

/**
 * <h1>SettingsSimpleItem</h1>
 * This class represents system item, that will be not modified.
 */
public class SettingsPresetItem implements SettingsItem {

    private final Items type;

    /**
     * Creates instance of preset item.
     *
     * @param type type of system item.
     */
    public SettingsPresetItem(@NotNull Items type) {
        this.type = type;
    }

    @Override
    public @NotNull ItemStack getItem(@NotNull Player player) {
        return type.get(player);
    }
}
