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

import net.kyori.adventure.text.Component;
import org.bukkit.OfflinePlayer;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import ua.mcchickenstudio.opencreative.managers.Manager;

import java.util.List;

public interface TranslationManager extends Manager {

    @NotNull List<Translation> getTranslations();

    @Nullable Translation getTranslation(@NotNull String lang);

    boolean hasLocaleComponent(@NotNull String id, @NotNull String lang);

    @NotNull Component getLocaleComponent(@NotNull String id, @NotNull String lang);

    @NotNull Component getLocaleComponent(@NotNull String id, @NotNull String lang, @NotNull Component def);

    @NotNull Component getLocaleComponent(@NotNull String id, @NotNull OfflinePlayer player);

    @NotNull Component getLocaleComponent(@NotNull String id, @NotNull OfflinePlayer player, @NotNull Component def);

}
