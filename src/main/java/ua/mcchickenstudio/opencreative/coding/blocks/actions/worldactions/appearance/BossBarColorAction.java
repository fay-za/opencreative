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

import net.kyori.adventure.bossbar.BossBar;
import org.jetbrains.annotations.NotNull;
import ua.mcchickenstudio.opencreative.coding.arguments.Arguments;
import ua.mcchickenstudio.opencreative.coding.blocks.actions.ActionType;
import ua.mcchickenstudio.opencreative.coding.blocks.actions.Target;
import ua.mcchickenstudio.opencreative.coding.blocks.actions.worldactions.WorldAction;
import ua.mcchickenstudio.opencreative.coding.blocks.executors.Executor;

public final class BossBarColorAction extends WorldAction {
    public BossBarColorAction(Executor executor, Target target, int x, Arguments args) {
        super(executor, target, x, args);
    }

    @Override
    protected void execute() {
        if (!getArguments().pathExists("name")) {
            return;
        }
        String name = getArguments().getText("name", "boss", this);
        String colorString = getArguments().getText("color", "purple", this);
        BossBar.Color color = BossBar.Color.PURPLE;
        try {
            color = BossBar.Color.valueOf(colorString.toUpperCase());
        } catch (IllegalArgumentException ignored) {
        }
        BossBar bossBar = getPlanet().getTerritory().getBossBars().get(name.toLowerCase());
        if (bossBar != null) {
            bossBar.color(color);
        }
        getPlanet().getTerritory().getBossBars().put(name.toLowerCase(), bossBar);
    }

    @Override
    public @NotNull ActionType getActionType() {
        return ActionType.WORLD_BOSS_BAR_COLOR;
    }
}
