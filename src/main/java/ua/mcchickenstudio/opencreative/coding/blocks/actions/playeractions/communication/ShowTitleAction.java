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

package ua.mcchickenstudio.opencreative.coding.blocks.actions.playeractions.communication;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.title.Title;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import ua.mcchickenstudio.opencreative.coding.arguments.Arguments;
import ua.mcchickenstudio.opencreative.coding.blocks.actions.ActionType;
import ua.mcchickenstudio.opencreative.coding.blocks.actions.Target;
import ua.mcchickenstudio.opencreative.coding.blocks.actions.playeractions.PlayerAction;
import ua.mcchickenstudio.opencreative.coding.blocks.executors.Executor;

import java.time.Duration;

public final class ShowTitleAction extends PlayerAction {
    public ShowTitleAction(Executor executor, Target target, int x, Arguments args) {
        super(executor, target, x, args);
    }

    @Override
    public void executePlayer(@NotNull Player player) {
        Component title = getArguments().getComponent("title", Component.text(""), this);
        Component subtitle = getArguments().getComponent("subtitle", Component.text(""), this);
        int fadeIn = getArguments().getInt("fade-in", 20, this);
        int stay = getArguments().getInt("stay", 60, this);
        int fadeOut = getArguments().getInt("stay-out", 10, this);
        /*
         * We multiply ticks by 50L to
         * convert them into milliseconds.
         * 1000 milliseconds = 1 second = 20 ticks.
         */
        player.showTitle(Title.title(
                title, subtitle,
                Title.Times.times(Duration.ofMillis(fadeIn * 50L), Duration.ofMillis(stay * 50L), Duration.ofMillis(fadeOut * 50L))
        ));
    }

    @Override
    public @NotNull ActionType getActionType() {
        return ActionType.PLAYER_SHOW_TITLE;
    }
}
