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

package ua.mcchickenstudio.opencreative.coding.blocks.actions.variableactions.text;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import org.jetbrains.annotations.NotNull;
import ua.mcchickenstudio.opencreative.coding.arguments.Arguments;
import ua.mcchickenstudio.opencreative.coding.blocks.actions.ActionType;
import ua.mcchickenstudio.opencreative.coding.blocks.actions.Target;
import ua.mcchickenstudio.opencreative.coding.blocks.actions.variableactions.VariableAction;
import ua.mcchickenstudio.opencreative.coding.blocks.executors.Executor;
import ua.mcchickenstudio.opencreative.coding.exceptions.TooLongTextException;
import ua.mcchickenstudio.opencreative.coding.variables.VariableLink;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class ParseNumberAction extends VariableAction {

    private final static Pattern INT_PATTERN = Pattern.compile("^-?[0-9]*$");
    private final static Pattern FLOAT_PATTERN = Pattern.compile("^-?[0-9]*\\.?[0-9]+([eE][-+]?[0-9]+)?$");

    public ParseNumberAction(Executor executor, Target target, int x, Arguments args) {
        super(executor, target, x, args);
    }

    @Override
    protected void execute() {
        VariableLink link = getArguments().getVariableLink("variable", this);
        Component component = getArguments().getComponent("text", Component.text("0.0"), this);
        String text = PlainTextComponentSerializer.plainText().serialize(component);
        if (text.length() > 1024) {
            throw new TooLongTextException(1024);
        }
        text = text.replaceAll("[^-0-9.]", "");

        Matcher floatMatcher = FLOAT_PATTERN.matcher(text);
        if (floatMatcher.find()) {
            setVarValue(link, Double.parseDouble(floatMatcher.group()));
            return;
        }
        Matcher intMatcher = INT_PATTERN.matcher(text);
        if (intMatcher.find()) {
            setVarValue(link, Long.parseLong(intMatcher.group()));
        }
    }

    @Override
    public @NotNull ActionType getActionType() {
        return ActionType.VAR_PARSE_NUMBER;
    }
}
