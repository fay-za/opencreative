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

package ua.mcchickenstudio.opencreative.coding.placeholders;

import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import ua.mcchickenstudio.opencreative.coding.blocks.actions.Action;
import ua.mcchickenstudio.opencreative.coding.blocks.actions.ActionsHandler;

public final class PlayerPlaceholder extends KeyPlaceholder {

    public PlayerPlaceholder() {
        super("player", "player_uuid", "display_name");
    }

    @Override
    public @Nullable String parseKey(String key, ActionsHandler handler, Action action) {
        if (handler.getEvent().getSelection().getFirst() instanceof Player player) {
            if (!player.getWorld().equals(action.getExecutor().getPlanet().getWorld())) return null;
            switch (key) {
                case "player" -> {
                    return player.getName();
                }
                case "player_uuid" -> {
                    return player.getUniqueId().toString();
                }
                case "display_name" -> {
                    return player.getDisplayName();
                }
            }
        }
        return null;
    }

    @Override
    public @NotNull String getExtensionId() {
        return "default";
    }

    @Override
    public @NotNull String getName() {
        return "Player Placeholder";
    }

    @Override
    public @NotNull String getDescription() {
        return "Parses player placeholders";
    }
}
