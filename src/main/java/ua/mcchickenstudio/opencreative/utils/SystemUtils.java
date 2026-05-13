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

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public final class SystemUtils {

    /**
     * Sets property in JVM.
     *
     * @param key   key of property.
     * @param value value of property.
     */
    public static void setSystemProperty(@NotNull String key, @NotNull String value) {
        try {
            System.getProperties().setProperty("opencreative." + key, value);
        } catch (Exception ignored) {
        }
    }

    /**
     * Returns value of property, or null
     *
     * @param key key of property.
     * @return value, or null - if not exists.
     */
    public static @Nullable String getSystemProperty(@NotNull String key) {
        try {
            return System.getProperties().getProperty("opencreative." + key);
        } catch (Exception ignored) {
            return null;
        }
    }

}
