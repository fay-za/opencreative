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

package ua.mcchickenstudio.opencreative.coding.blocks.actions.worldactions.blocks;

import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.block.sign.Side;
import org.jetbrains.annotations.NotNull;
import ua.mcchickenstudio.opencreative.coding.arguments.Arguments;
import ua.mcchickenstudio.opencreative.coding.blocks.actions.ActionType;
import ua.mcchickenstudio.opencreative.coding.blocks.actions.Target;
import ua.mcchickenstudio.opencreative.coding.blocks.actions.worldactions.WorldAction;
import ua.mcchickenstudio.opencreative.coding.blocks.executors.Executor;

public final class SetSignLineAction extends WorldAction {
    public SetSignLineAction(Executor executor, Target target, int x, Arguments args) {
        super(executor, target, x, args);
    }

    @Override
    protected void execute() {
        Location location = getArguments().getLocation("location", getPlanet().getTerritory().getSpawnLocation(), this);
        int number = getArguments().getInt("number", 1, this);
        Block block = location.getBlock();

        if (!(block.getState() instanceof Sign sign)) return;

        String text = getArguments().getText("text", "", this);
        String sideString = getArguments().getText("side", "front", this);
        Side side = (sideString.equals("back") ? Side.BACK : Side.FRONT);

        if (number <= 0 || number > sign.getSide(side).lines().size()) number = 1;

        if (getPlanet().getLimits().cantModifyBlock(this)) {
            return;
        }

        sign.getSide(side).setLine(number - 1, text);
        sign.update();

        getPlanet().getLimits().clearModifiedBlocks();
    }

    @Override
    public @NotNull ActionType getActionType() {
        return ActionType.WORLD_SET_SIGN_LINE;
    }
}
