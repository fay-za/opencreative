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
import org.bukkit.Location;
import org.bukkit.WorldBorder;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import ua.mcchickenstudio.opencreative.coding.arguments.Arguments;
import ua.mcchickenstudio.opencreative.coding.blocks.actions.ActionType;
import ua.mcchickenstudio.opencreative.coding.blocks.actions.Target;
import ua.mcchickenstudio.opencreative.coding.blocks.actions.playeractions.PlayerAction;
import ua.mcchickenstudio.opencreative.coding.blocks.executors.Executor;
import ua.mcchickenstudio.opencreative.planets.PlanetPlayer;

public final class PlayerSetWorldBorderAction extends PlayerAction {
    public PlayerSetWorldBorderAction(Executor executor, Target target, int x, Arguments args) {
        super(executor, target, x, args);
    }

    @Override
    public void executePlayer(@NotNull Player player) {
        double radius = getArguments().getDouble("radius", (getWorld() == null ? 10d : getWorld().getWorldBorder().getSize()), this);
        int time = getArguments().getInt("time", 0, this);
        int warningDistance = getArguments().getInt("warning-distance", 5, this);
        int warningTime = getArguments().getInt("warning-time", 15, this);
        int safeDistance = getArguments().getInt("safe-distance", 5, this);
        WorldBorder border = Bukkit.createWorldBorder();
        border.setSize(radius, time);
        border.setWarningTime(warningTime);
        border.setWarningDistance(warningDistance);
        border.setDamageBuffer(safeDistance);
        Location center = getArguments().getLocation("center", player.getLocation(), this);
        border.setCenter(center);
        player.setWorldBorder(border);
        PlanetPlayer planetPlayer = getPlanet().getWorldPlayers().getPlanetPlayer(player);
        if (planetPlayer != null) {
            planetPlayer.setWorldSize(getArguments().pathExists("radius") ? radius : null);
        }
    }


    @Override
    public @NotNull ActionType getActionType() {
        return ActionType.PLAYER_SET_WORLD_BORDER;
    }
}
