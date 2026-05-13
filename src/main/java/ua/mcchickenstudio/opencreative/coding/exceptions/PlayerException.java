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

package ua.mcchickenstudio.opencreative.coding.exceptions;

import org.jetbrains.annotations.NotNull;

/**
 * <h1>PlayerException</h1>
 * This class represents a player exception, that has
 * nickname of player, who involved in exception. Used
 * to replace nickname placeholder.
 */
public abstract class PlayerException extends RuntimeException {

    private final @NotNull String playerName;

    public PlayerException(@NotNull String playerName, @NotNull String message) {
        super(message);
        this.playerName = playerName;
    }

    public @NotNull String getPlayerName() {
        return playerName;
    }

}
