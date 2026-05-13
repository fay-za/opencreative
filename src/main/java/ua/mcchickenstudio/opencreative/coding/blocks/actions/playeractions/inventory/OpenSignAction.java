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

package ua.mcchickenstudio.opencreative.coding.blocks.actions.playeractions.inventory;

import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.block.sign.Side;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import ua.mcchickenstudio.opencreative.coding.arguments.Arguments;
import ua.mcchickenstudio.opencreative.coding.blocks.actions.ActionType;
import ua.mcchickenstudio.opencreative.coding.blocks.actions.Target;
import ua.mcchickenstudio.opencreative.coding.blocks.actions.playeractions.PlayerAction;
import ua.mcchickenstudio.opencreative.coding.blocks.executors.Executor;
import ua.mcchickenstudio.opencreative.coding.exceptions.TooManyOpenedMenusException;

public final class OpenSignAction extends PlayerAction {
    public OpenSignAction(Executor executor, Target target, int x, Arguments args) {
        super(executor, target, x, args);
    }

    @Override
    public void executePlayer(@NotNull Player player) {
        Location location = getArguments().getLocation("location", getPlanet().getTerritory().getSpawnLocation(), this);
        Block block = location.getBlock();
        if (!(block.getState() instanceof Sign sign)) return;
        String sideString = getArguments().getText("side", "front", this);
        Side side = (sideString.equals("back") ? Side.BACK : Side.FRONT);
        if (getPlanet().getLimits().cantOpenMenu(player)) {
            /*
             * This check prevents player from opening
             * too many menus, that can prevent from
             * quiting the game.
             */
            throw new TooManyOpenedMenusException(player.getName());
        }
        player.openSign(sign, side);
    }

    @Override
    public @NotNull ActionType getActionType() {
        return ActionType.PLAYER_OPEN_SIGN;
    }
}
