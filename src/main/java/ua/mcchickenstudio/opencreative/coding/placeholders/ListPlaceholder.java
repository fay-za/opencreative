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
import ua.mcchickenstudio.opencreative.coding.variables.VariableLink;
import ua.mcchickenstudio.opencreative.coding.variables.WorldVariable;
import ua.mcchickenstudio.opencreative.coding.variables.WorldVariables;

import java.util.List;

public final class ListPlaceholder extends KeyValuePlaceholder {

    public ListPlaceholder() {
        super("list", "list_local", "list_game", "list_global", "list_saved", "list_save");
    }

    @Override
    public @Nullable String parseKeyValue(String key, String value, ActionsHandler handler, Action action) {
        if (!value.contains(",")) return null;
        String[] split = value.split(",");
        if (split.length < 2) return null;
        String listName = split[0].strip();
        int listIndex;
        try {
            listIndex = Integer.parseInt(split[1].strip());
        } catch (Exception e) {
            return null;
        }
        if (listIndex < 1) return null;
        WorldVariables variables = action.getExecutor().getPlanet().getVariables();
        VariableLink link = switch (key) {
            case "list_local" -> new VariableLink(listName, VariableLink.VariableType.LOCAL);
            case "list", "list_global", "list_game" -> new VariableLink(listName, VariableLink.VariableType.GLOBAL);
            case "list_save", "list_saved" -> new VariableLink(listName, VariableLink.VariableType.SAVED);
            default -> null;
        };
        if (link == null) return null;
        WorldVariable variable = variables.getVariable(link, action);
        String replacement = "null! " + value + " - " + link.getVariableType().name();
        if (variable == null) return replacement;
        if (!(variable.getValue() instanceof List<?> list)) return replacement;
        if (listIndex > list.size()) return replacement;
        Object listValue = list.get(listIndex - 1);
        return String.valueOf(listValue)
                .substring(0, Math.min(100, String.valueOf(listValue).length()));
    }

    @Override
    public @NotNull String getExtensionId() {
        return "default";
    }

    @Override
    public @NotNull String getName() {
        return "List Placeholder";
    }

    @Override
    public @NotNull String getDescription() {
        return "Parses list elements placeholder %list(index_of_element)";
    }
}
