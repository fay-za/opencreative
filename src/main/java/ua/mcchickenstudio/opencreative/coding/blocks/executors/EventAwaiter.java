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

package ua.mcchickenstudio.opencreative.coding.blocks.executors;

import org.bukkit.event.Cancellable;
import org.jetbrains.annotations.NotNull;
import ua.mcchickenstudio.opencreative.coding.blocks.events.WorldEvent;

/**
 * <h1>EventAwaiter</h1>
 * This interface is used for executors, that can be
 * launched when some world event happens.
 */
public interface EventAwaiter {

    /**
     * Returns event class, that can run executor
     * on calling that event.
     *
     * @return event class, that will call executor.
     */
    @NotNull Class<? extends WorldEvent> getEventClass();

    /**
     * Checks whether event can be canceled.
     *
     * @return true - can be canceled, false - not.
     */
    default boolean isCancellable() {
        return (Cancellable.class.isAssignableFrom(getEventClass()));
    }

}
