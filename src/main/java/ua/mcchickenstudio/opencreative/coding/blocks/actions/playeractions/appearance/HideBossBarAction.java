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

import net.kyori.adventure.bossbar.BossBar;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import ua.mcchickenstudio.opencreative.coding.arguments.Arguments;
import ua.mcchickenstudio.opencreative.coding.blocks.actions.ActionType;
import ua.mcchickenstudio.opencreative.coding.blocks.actions.Target;
import ua.mcchickenstudio.opencreative.coding.blocks.actions.playeractions.PlayerAction;
import ua.mcchickenstudio.opencreative.coding.blocks.executors.Executor;

public final class HideBossBarAction extends PlayerAction {
    public HideBossBarAction(Executor executor, Target target, int x, Arguments args) {
        super(executor, target, x, args);
    }

    @Override
    public void executePlayer(@NotNull Player player) {
        if (getArguments().pathExists("bossbar")) {
            String name = getArguments().getText("bossbar", " ", this);
            BossBar bossBar = getPlanet().getTerritory().getBossBars().get(name.toLowerCase());
            if (bossBar != null) {
                player.hideBossBar(bossBar);
                return;
            }
        }
        player.activeBossBars().forEach(player::hideBossBar);
    }

    @Override
    public @NotNull ActionType getActionType() {
        return ActionType.PLAYER_HIDE_BOSS_BAR;
    }
}
