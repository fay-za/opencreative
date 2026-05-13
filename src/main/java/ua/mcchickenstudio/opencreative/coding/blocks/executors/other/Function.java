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

package ua.mcchickenstudio.opencreative.coding.blocks.executors.other;

import org.jetbrains.annotations.NotNull;
import ua.mcchickenstudio.opencreative.coding.blocks.executors.ExecutorCategory;
/**
 * <h1>Function</h1>
 * This class represents a function, that executes actions
 * in same actions handler.
 */
public final class Function extends NameableExecutor {

    public Function() {
        super("function", ExecutorCategory.FUNCTION);
    }

    @Override
    public @NotNull String getExtensionId() {
        return "default";
    }

    @Override
    public @NotNull String getName() {
        return "Function";
    }

    @Override
    public @NotNull String getDescription() {
        return "Adds actions in place, where it was called";
    }
}
