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

package ua.mcchickenstudio.opencreative.managers.disguises;

import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.jetbrains.annotations.NotNull;

public final class DisabledDisguises implements DisguiseManager {

    @Override
    public void disguiseAsPlayer(@NotNull Entity entity, @NotNull String skin, @NotNull String nickname) {}

    @Override
    public void setDisguiseDisplayName(@NotNull Entity entity, @NotNull String displayName) {}

    @Override
    public void disguiseAsEntity(@NotNull Entity entity, @NotNull EntityType type) {}

    @Override
    public void disguiseAsBlock(@NotNull Entity entity, @NotNull Material material) {}

    @Override
    public void clearDisguises(@NotNull Entity entity) {}

    @Override
    public void init() {}

    @Override
    public boolean isEnabled() {
        return false;
    }

    @Override
    public String getName() {
        return "Disabled Disguises";
    }
}
