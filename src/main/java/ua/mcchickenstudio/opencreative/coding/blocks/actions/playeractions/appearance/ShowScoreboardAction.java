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

package ua.mcchickenstudio.opencreative.coding.blocks.actions.playeractions.appearance;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.Scoreboard;
import org.jetbrains.annotations.NotNull;
import ua.mcchickenstudio.opencreative.coding.arguments.Arguments;
import ua.mcchickenstudio.opencreative.coding.blocks.actions.ActionType;
import ua.mcchickenstudio.opencreative.coding.blocks.actions.Target;
import ua.mcchickenstudio.opencreative.coding.blocks.actions.playeractions.PlayerAction;
import ua.mcchickenstudio.opencreative.coding.blocks.executors.Executor;

public final class ShowScoreboardAction extends PlayerAction {
    public ShowScoreboardAction(Executor executor, Target target, int x, Arguments args) {
        super(executor, target, x, args);
    }

    @Override
    public void executePlayer(@NotNull Player player) {
        Scoreboard scoreboard = Bukkit.getScoreboardManager().getMainScoreboard();
        if (getArguments().pathExists("scoreboard")) {
            String name = getArguments().getText("scoreboard", " ", this);
            scoreboard = getPlanet().getTerritory().getScoreboards().getScoreboard(name.toLowerCase());
            if (scoreboard == null) {
                scoreboard = Bukkit.getScoreboardManager().getMainScoreboard();
            }
        }
        player.setScoreboard(scoreboard);
    }

    @Override
    public @NotNull ActionType getActionType() {
        return ActionType.PLAYER_SHOW_SCOREBOARD;
    }
}
