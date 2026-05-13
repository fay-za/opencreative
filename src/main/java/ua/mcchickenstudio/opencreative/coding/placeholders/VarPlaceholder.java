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

public final class VarPlaceholder extends KeyValuePlaceholder {

    public VarPlaceholder() {
        super("var", "var_local", "var_game", "var_global", "var_save", "var_saved");
    }

    @Override
    public @Nullable String parseKeyValue(String type, String name, ActionsHandler handler, Action action) {
        WorldVariables variables = action.getExecutor().getPlanet().getVariables();
        VariableLink link = switch (type) {
            case "var_local" -> new VariableLink(name, VariableLink.VariableType.LOCAL);
            case "var", "var_game", "var_global" -> new VariableLink(name, VariableLink.VariableType.GLOBAL);
            case "var_save", "var_saved" -> new VariableLink(name, VariableLink.VariableType.SAVED);
            default -> null;
        };
        if (link == null) return null;
        WorldVariable variable = variables.getVariable(link, action);
        String replacement = "null! " + name + " - " + link.getVariableType().name();
        if (variable != null) {
            replacement = String.valueOf(variable.getValue()).substring(0, Math.min(100, String.valueOf(variable.getValue()).length()));
        }
        return replacement;
    }

    @Override
    public @NotNull String getExtensionId() {
        return "default";
    }

    @Override
    public @NotNull String getName() {
        return "Variable Placeholder";
    }

    @Override
    public @NotNull String getDescription() {
        return "Parses variable placeholder %var(variable_name)";
    }
}
