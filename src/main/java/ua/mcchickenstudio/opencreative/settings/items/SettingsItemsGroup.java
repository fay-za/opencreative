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

import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;

/**
 * <h1>SettingsItemsGroup</h1>
 * This class represents custom items group, that will be
 * used for overriding pairs of items.
 */
public class SettingsItemsGroup {

    private final Map<Integer, SettingsItem> items = new HashMap<>();

    /**
     * Sets settings item in slot.
     *
     * @param slot slot of settings item.
     * @param item settings item.
     */
    public void setItem(int slot, @NotNull SettingsItem item) {
        items.put(slot, item);
    }

    /**
     * Returns map of slots and settings items.
     *
     * @return map of slots and settings items.
     */
    public Map<Integer, SettingsItem> getItems() {
        return items;
    }

    /**
     * Removes settings item by slot.
     *
     * @param slot slot to remove item.
     */
    public void removeItem(int slot) {
        items.remove(slot);
    }
}
