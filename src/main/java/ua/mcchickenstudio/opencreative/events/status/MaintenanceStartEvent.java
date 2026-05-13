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

package ua.mcchickenstudio.opencreative.events.status;

import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.Nullable;
import ua.mcchickenstudio.opencreative.events.CreativeEvent;

/**
 * Called when maintenance mode starts by command sender or other plugin.
 * <p>
 * If command sender is null, then maintenance mode was started by other plugin.
 */
public final class MaintenanceStartEvent extends CreativeEvent {

    private final @Nullable CommandSender sender;

    public MaintenanceStartEvent(@Nullable CommandSender sender) {
        this.sender = sender;
    }

    public @Nullable CommandSender getSender() {
        return sender;
    }
}
