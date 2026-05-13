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

package ua.mcchickenstudio.opencreative.coding.blocks.events.player.world;

import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import ua.mcchickenstudio.opencreative.coding.blocks.events.WorldEvent;
import ua.mcchickenstudio.opencreative.coding.placeholders.KeyPlaceholder;
import ua.mcchickenstudio.opencreative.coding.placeholders.KeyValuePlaceholder;

public final class ChatEvent extends WorldEvent implements Cancellable {

    private final String message;
    private boolean cancelled;
    private boolean handledByCode;

    public ChatEvent(Player player, String message) {
        super(player);
        this.message = filter(message);
    }

    private String filter(String string) {
        string = string.replace("\\n", " ");
        string = KeyPlaceholder.getPatternPlaceholder().matcher(string).replaceAll(" ");
        return KeyValuePlaceholder.getPattern().matcher(string).replaceAll(" ");
    }

    public boolean isHandledByCode() {
        return handledByCode;
    }

    public void setHandledByCode(boolean handledByCode) {
        this.handledByCode = handledByCode;
    }

    public String getMessage() {
        return message;
    }

    @Override
    public boolean isCancelled() {
        return cancelled;
    }

    @Override
    public void setCancelled(boolean cancelled) {
        this.cancelled = cancelled;
    }
}
