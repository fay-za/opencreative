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
import org.bukkit.command.CommandSender;
import org.bukkit.event.Cancellable;
import org.jetbrains.annotations.NotNull;
import ua.mcchickenstudio.opencreative.commands.ChatCommand;
import ua.mcchickenstudio.opencreative.events.CreativeEvent;

/**
 * Called when player or console sends message in creative chat.
 * <p>
 * If a Creative Chat event is cancelled, it will not send a message.
 *
 * @see ChatCommand
 */
public class CreativeChatEvent extends CreativeEvent implements Cancellable {

    private final CommandSender sender;
    private final String message;
    private Component formattedMessage;
    private boolean cancelled;

    public CreativeChatEvent(@NotNull CommandSender sender, @NotNull String message, @NotNull Component formattedMessage) {
        this.sender = sender;
        this.message = message;
        this.formattedMessage = formattedMessage;
    }

    public @NotNull Component getFormattedMessage() {
        return formattedMessage;
    }

    @SuppressWarnings("unused")
    public void setFormattedMessage(@NotNull Component formattedMessage) {
        this.formattedMessage = formattedMessage;
    }

    public @NotNull String getMessage() {
        return message;
    }

    public @NotNull CommandSender getSender() {
        return sender;
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
