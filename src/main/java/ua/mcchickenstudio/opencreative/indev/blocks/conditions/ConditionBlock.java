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

package ua.mcchickenstudio.opencreative.indev.blocks.conditions;

import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.event.player.PlayerInteractEvent;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import ua.mcchickenstudio.opencreative.coding.arguments.Arguments;
import ua.mcchickenstudio.opencreative.coding.blocks.actions.ActionsHandler;
import ua.mcchickenstudio.opencreative.indev.blocks.CodingBlock;
import ua.mcchickenstudio.opencreative.indev.blocks.actions.ActionBlock;

import java.util.List;

public abstract class ConditionBlock extends CodingBlock {

    public ConditionBlock(@NotNull String id, @NotNull Material mainBlock, @NotNull Material offBlock ) {
        super(id, mainBlock, offBlock);
    }

    public void execute(@Nullable Entity target, @NotNull ActionsHandler actionsHandler, @NotNull Arguments arguments, @NotNull List<ActionBlock> actions, @NotNull List<ActionBlock> reactions, boolean isOpposed) {
        if (check(target, actionsHandler, arguments) ^ isOpposed) {
            // execute actions
        } else {
            // execute reactions
        }
    }

    public abstract boolean check(@Nullable Entity target, @NotNull ActionsHandler actionsHandler, @NotNull Arguments arguments);

    @Override
    public void onSignClick(PlayerInteractEvent event) {

    }

}
