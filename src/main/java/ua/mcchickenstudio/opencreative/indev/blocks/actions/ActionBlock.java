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

package ua.mcchickenstudio.opencreative.indev.blocks.actions;

import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.event.player.PlayerInteractEvent;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import ua.mcchickenstudio.opencreative.coding.arguments.Arguments;
import ua.mcchickenstudio.opencreative.coding.blocks.actions.ActionsHandler;
import ua.mcchickenstudio.opencreative.indev.blocks.CodingBlock;

import java.util.Map;

public abstract class ActionBlock extends CodingBlock {

    public ActionBlock(@NotNull String id, @NotNull Material mainBlock, @NotNull Material offBlock ) {
        super(id, mainBlock, offBlock);
    }

    @Override
    public @Nullable WrappedAction createWrapped(@NotNull Map<String, Object> data) {
        //int x = (int) data.get("location.x");
        //int y = (int) data.get("location.y");
        //int z = (int) data.get("location.z");
        //Target target = Target.getByText((String) data.get("target"));
        return null;
        //return new WrappedAction(this, target, new Arguments(null), x, y, z);
    }

    public abstract void execute(@Nullable Entity target, @NotNull ActionsHandler actionsHandler, @NotNull Arguments arguments);

    @Override
    public void onSignClick(PlayerInteractEvent event) {

    }

}
