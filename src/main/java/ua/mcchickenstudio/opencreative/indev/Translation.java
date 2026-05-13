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

package ua.mcchickenstudio.opencreative.indev;

import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;

public class Translation {

    private final @NotNull String lang;
    private final @NotNull ItemStack icon;

    public Translation(@NotNull String lang, @NotNull ItemStack icon) {
        this.lang = lang;
        this.icon = icon;
    }

    private final @NotNull Map<@NotNull String, @NotNull String> messages = new HashMap<>();

    public @NotNull Map<@NotNull String, @NotNull String> getMessages() {
        return messages;
    }

    public @NotNull ItemStack getIcon() {
        return icon;
    }

    public @NotNull String getLang() {
        return lang;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof Translation translation) {
            return translation.lang.equals(lang);
        }
        return false;
    }

    @Override
    public int hashCode() {
        return lang.hashCode();
    }
}
