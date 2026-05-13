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

package ua.mcchickenstudio.opencreative.coding;

import org.jetbrains.annotations.NotNull;

/**
 * <h1>ExtensionContent</h1>
 * This interface is used in additional content,
 * that can be added by developers to OpenCreative+.
 *
 * @see ua.mcchickenstudio.opencreative.coding.placeholders.Placeholder
 * @see ua.mcchickenstudio.opencreative.coding.blocks.executors.Executor
 * @see ua.mcchickenstudio.opencreative.coding.values.EventValue
 */
public interface ExtensionContent {

    /**
     * Returns lower-cased simple id of extension.
     * Will be used to identify author of content.
     * <p>ID "default" is reserved for OpenCreative+ developers.
     *
     * @return id of extension.
     */
    @NotNull String getExtensionId();

    /**
     * Returns name of extension content.
     * Will be displayed in list of extension
     * content.
     *
     * @return name of content.
     */
    @NotNull String getName();

    /**
     * Returns description of extension content.
     * Describes purpose of new additional content.
     *
     * @return description of content.
     */
    @NotNull String getDescription();

}
