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

package ua.mcchickenstudio.opencreative.events.player;

import net.kyori.adventure.text.Component;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import ua.mcchickenstudio.opencreative.events.CreativeEvent;
import ua.mcchickenstudio.opencreative.planets.Planet;

/**
 * Called when player sends message.
 * <p>
 * If a World Chat event is cancelled, it will not send message.
 */
public class WorldChatEvent extends CreativeEvent implements Cancellable {

    private final Player player;
    private final String message;
    private final World world;
    private final Planet planet;

    private Component formattedMessage;
    private boolean cancelled;

    public WorldChatEvent(@NotNull Player player, @NotNull String message, @NotNull Component formattedMessage,
                          @NotNull World world, @Nullable Planet planet) {
        this.player = player;
        this.message = message;
        this.formattedMessage = formattedMessage;
        this.world = world;
        this.planet = planet;
    }

    public @NotNull Player getPlayer() {
        return player;
    }

    public @NotNull String getMessage() {
        return message;
    }

    public @NotNull World getWorld() {
        return world;
    }

    public @Nullable Planet getPlanet() {
        return planet;
    }

    public Component getFormattedMessage() {
        return formattedMessage;
    }

    @SuppressWarnings("unused")
    public void setFormattedMessage(Component formattedMessage) {
        this.formattedMessage = formattedMessage;
    }

    @Override
    public boolean isCancelled() {
        return cancelled;
    }

    @Override
    public void setCancelled(boolean cancel) {
        cancelled = cancel;
    }
}
