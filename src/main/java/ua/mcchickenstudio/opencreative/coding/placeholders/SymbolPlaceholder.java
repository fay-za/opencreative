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

package ua.mcchickenstudio.opencreative.coding.placeholders;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import ua.mcchickenstudio.opencreative.coding.blocks.actions.Action;
import ua.mcchickenstudio.opencreative.coding.blocks.actions.ActionsHandler;

public final class SymbolPlaceholder extends KeyPlaceholder {

    public SymbolPlaceholder() {
        super("space", "empty", "new-line", "nl");
    }

    @Override
    public @Nullable String parseKey(String key, ActionsHandler handler, Action action) {
        return switch (key) {
            case "space" -> " ";
            case "empty" -> "";
            case "new-line", "nl" -> "\n";
            default -> null;
        };
    }

    @Override
    public @NotNull String getExtensionId() {
        return "default";
    }

    @Override
    public @NotNull String getName() {
        return "Symbol Placeholder";
    }

    @Override
    public @NotNull String getDescription() {
        return "Parses symbol placeholders";
    }
}
