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

/**
 * <h1>CollectionWithCollectionException</h1>
 * This class represents an exception, that happens when
 * code tries to save list in list, map in list, map in map.
 */
public final class CollectionWithCollectionException extends RuntimeException {

    public CollectionWithCollectionException(Class<?> first, Class<?> second) {
        super("Adding collection (" + first.getSimpleName() + ") to collection (" + second.getSimpleName() + ") is not supported.");
    }

}
