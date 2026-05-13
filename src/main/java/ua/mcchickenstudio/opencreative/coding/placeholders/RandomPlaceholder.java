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

import java.util.List;
import java.util.Random;

public final class RandomPlaceholder extends KeyPlaceholder {

    public RandomPlaceholder() {
        super("random", "random_uuid");
    }

    @Override
    public @Nullable String parseKey(String key, ActionsHandler handler, Action action) {
        Player randomPlayer;
        List<Player> playerList = handler.getExecutor().getPlanet().getTerritory().getWorld().getPlayers();
        if (playerList.isEmpty()) return null;
        Random r = new Random();
        int i = r.nextInt(playerList.size());
        randomPlayer = playerList.get(i);
        if (randomPlayer != null) {
            if (key.equals("random")) {
                return randomPlayer.getName();
            } else if (key.equals("random_uuid")) {
                return randomPlayer.getUniqueId().toString();
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
        return "Random Placeholder";
    }

    @Override
    public @NotNull String getDescription() {
        return "Parses random placeholders";
    }
}
