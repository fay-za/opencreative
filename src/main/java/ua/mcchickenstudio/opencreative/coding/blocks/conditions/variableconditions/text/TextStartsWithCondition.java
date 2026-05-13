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

package ua.mcchickenstudio.opencreative.coding.blocks.conditions.variableconditions.text;

import org.bukkit.ChatColor;
import org.jetbrains.annotations.NotNull;
import ua.mcchickenstudio.opencreative.coding.arguments.Arguments;
import ua.mcchickenstudio.opencreative.coding.blocks.actions.Action;
import ua.mcchickenstudio.opencreative.coding.blocks.actions.ActionType;
import ua.mcchickenstudio.opencreative.coding.blocks.actions.Target;
import ua.mcchickenstudio.opencreative.coding.blocks.conditions.variableconditions.VariableCondition;
import ua.mcchickenstudio.opencreative.coding.blocks.executors.Executor;

import java.util.List;

public final class TextStartsWithCondition extends VariableCondition {
    public TextStartsWithCondition(Executor executor, Target target, int x, Arguments args, List<Action> actions, List<Action> reactions, boolean isOpposed) {
        super(executor, target, x, args, actions, reactions, isOpposed);
    }

    @Override
    public boolean check() {
        if (!getArguments().pathExists("text") || !getArguments().pathExists("start")) {
            return false;
        }
        String text = getArguments().getText("text", "", this);
        List<String> starts = getArguments().getTextList("start", this);
        boolean ignoreColors = getArguments().getBoolean("ignore-colors", false, this);
        boolean ignoreCaps = getArguments().getBoolean("ignore-caps", false, this);
        if (ignoreColors) text = ChatColor.stripColor(text);
        if (ignoreCaps) text = text.toLowerCase();
        for (String start : starts) {
            if (ignoreColors) {
                start = ChatColor.stripColor(start);
            }
            if (ignoreCaps) {
                start = start.toLowerCase();
            }
            if (text.startsWith(start)) return true;
        }
        return false;
    }

    @Override
    public @NotNull ActionType getActionType() {
        return ActionType.IF_VAR_TEXT_STARTS_WITH;
    }
}
