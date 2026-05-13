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

import me.libraryaddict.disguise.DisguiseAPI;
import me.libraryaddict.disguise.DisguiseConfig;
import me.libraryaddict.disguise.disguisetypes.*;
import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.jetbrains.annotations.NotNull;
import ua.mcchickenstudio.opencreative.utils.hooks.HookUtils;

public final class LibsDisguises implements DisguiseManager {

    @Override
    public void disguiseAsPlayer(@NotNull Entity entity, @NotNull String skin, @NotNull String nickname) {
        try {
            PlayerDisguise disguise = PlayerDisguise.class
                    .getDeclaredConstructor(String.class, String.class)
                    .newInstance(nickname, skin);
            disguise.setEntity(entity);
            disguise.setNotifyBar(DisguiseConfig.NotifyBar.NONE);
            disguise.startDisguise();
        } catch (Exception ignored) {}
    }

    @Override
    public void setDisguiseDisplayName(@NotNull Entity entity, @NotNull String displayName) {
        Disguise disguise = DisguiseAPI.getDisguise(entity);
        if (disguise == null) return;
        if (disguise instanceof PlayerDisguise mobDisguise) {
            mobDisguise.setName(displayName);
        }
    }

    @Override
    public void disguiseAsEntity(@NotNull Entity entity, @NotNull EntityType type) {
        MobDisguise mobDisguise = new MobDisguise(DisguiseType.getType(type));
        mobDisguise.setEntity(entity);
        mobDisguise.setNotifyBar(DisguiseConfig.NotifyBar.NONE);
        mobDisguise.startDisguise();
    }

    @Override
    public void disguiseAsBlock(@NotNull Entity entity, @NotNull Material material) {
        MiscDisguise disguise = new MiscDisguise(DisguiseType.FALLING_BLOCK, material);
        disguise.setEntity(entity);
        disguise.setNotifyBar(DisguiseConfig.NotifyBar.NONE);
        disguise.startDisguise();
    }

    @Override
    public void clearDisguises(@NotNull Entity entity) {
        Disguise disguise = DisguiseAPI.getDisguise(entity);
        if (disguise == null) return;
        disguise.stopDisguise();
    }

    @Override
    public void init() {
    }

    @Override
    public boolean isEnabled() {
        return HookUtils.isLibsDisguisesEnabled;
    }

    @Override
    public String getName() {
        return "LibsDisguises";
    }
}
