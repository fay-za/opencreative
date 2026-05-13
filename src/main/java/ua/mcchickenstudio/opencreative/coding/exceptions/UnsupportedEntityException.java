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

import org.bukkit.entity.Entity;
import org.jetbrains.annotations.NotNull;

/**
 * <h1>UnsupportedEntityException</h1>
 * This class represents an exception, that happens when
 * code tries to perform action, that won't work on
 * current entity and requires it to be the specified.
 */
public final class UnsupportedEntityException extends RuntimeException {

    private final Class<?> required;
    private final Class<? extends Entity> current;

    public UnsupportedEntityException(@NotNull Class<?> required,
                                      @NotNull Entity current) {
        super("Entity must be " + required.getSimpleName() +
                ", not " + current.getClass().getSimpleName() + ".");
        this.required = required;
        this.current = current.getClass();
    }

    public @NotNull Class<?> getRequired() {
        return required;
    }

    public @NotNull Class<? extends Entity> getCurrent() {
        return current;
    }

}
