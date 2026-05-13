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

package ua.mcchickenstudio.opencreative.indev.blocks.executors.player;

import org.bukkit.Material;
import org.jetbrains.annotations.NotNull;
import ua.mcchickenstudio.opencreative.coding.blocks.events.WorldEvent;
import ua.mcchickenstudio.opencreative.indev.blocks.ViewableTypedBlock;
import ua.mcchickenstudio.opencreative.indev.blocks.executors.ExecutorBlock;

public abstract class PlayerExecutor extends ExecutorBlock implements ViewableTypedBlock {

    private final String type;

    public PlayerExecutor(@NotNull String type) {
        super("player_event", Material.DIAMOND_BLOCK, Material.DIAMOND_ORE);
        this.type = type;
    }

    public abstract Class<? extends PlayerEvent> getPlayerEventClass();

    @Override
    public Class<? extends WorldEvent> getEventClass() {
        return getPlayerEventClass();
    }

    @Override
    public @NotNull String getType() {
        return "player_"+type;
    }
}
