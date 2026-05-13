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

package ua.mcchickenstudio.opencreative.coding.blocks.actions.worldactions.appearance;

import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.scoreboard.Criteria;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Scoreboard;
import org.jetbrains.annotations.NotNull;
import ua.mcchickenstudio.opencreative.coding.arguments.Arguments;
import ua.mcchickenstudio.opencreative.coding.blocks.actions.ActionType;
import ua.mcchickenstudio.opencreative.coding.blocks.actions.Target;
import ua.mcchickenstudio.opencreative.coding.blocks.actions.worldactions.WorldAction;
import ua.mcchickenstudio.opencreative.coding.blocks.executors.Executor;

import static ua.mcchickenstudio.opencreative.utils.ErrorUtils.sendPlanetLimitWarningMessage;

public final class CreateScoreboardAction extends WorldAction {
    public CreateScoreboardAction(Executor executor, Target target, int x, Arguments args) {
        super(executor, target, x, args);
    }

    @Override
    protected void execute() {
        if (!getArguments().pathExists("name")) {
            return;
        }
        String name = getArguments().getText("name", "board", this);
        Component displayName = getArguments().getComponent("display-name", Component.text("Scoreboard"), this);
        try {
            int amount = getPlanet().getTerritory().getScoreboards().getAmount();
            if (amount >= getPlanet().getLimits().getScoreboardsLimit()) {
                sendPlanetLimitWarningMessage(this, "scoreboards",
                        amount, getPlanet().getLimits().getScoreboardsLimit());
                return;
            }
            Scoreboard scoreboard = getPlanet().getTerritory().getScoreboards().getScoreboard(name.toLowerCase());
            if (scoreboard != null) {
                Objective objective = scoreboard.getObjective("score");
                if (objective == null) {
                    objective = scoreboard.registerNewObjective("score", Criteria.DUMMY, displayName);
                }
                objective.displayName(displayName);
            } else {
                scoreboard = Bukkit.getScoreboardManager().getNewScoreboard();
                Objective objective = scoreboard.registerNewObjective("score", Criteria.DUMMY, displayName);
                objective.setDisplaySlot(DisplaySlot.SIDEBAR);
            }
            getPlanet().getTerritory().getScoreboards().registerScoreboard(name.toLowerCase(), scoreboard);
        } catch (Exception ignored) {
        }
    }

    @Override
    public @NotNull ActionType getActionType() {
        return ActionType.WORLD_CREATE_SCOREBOARD;
    }
}
