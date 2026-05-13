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

package ua.mcchickenstudio.opencreative.coding.blocks.events.world.blocks;

import com.destroystokyo.paper.event.block.AnvilDamagedEvent;
import org.bukkit.block.Block;
import org.bukkit.event.Cancellable;
import org.jetbrains.annotations.NotNull;
import ua.mcchickenstudio.opencreative.coding.blocks.events.BlockEvent;
import ua.mcchickenstudio.opencreative.coding.blocks.events.WorldEvent;
import ua.mcchickenstudio.opencreative.planets.Planet;

public final class BlockAnvilDamagedEvent extends WorldEvent implements BlockEvent, Cancellable {

    private final AnvilDamagedEvent event;
    private final Block anvil;

    public BlockAnvilDamagedEvent(Planet planet, AnvilDamagedEvent event, Block anvil) {
        super(planet, anvil);
        this.event = event;
        this.anvil = anvil;
    }

    @Override
    public @NotNull Block getBlock() {
        return anvil;
    }

    @Override
    public boolean isCancelled() {
        return event.isCancelled();
    }

    @Override
    public void setCancelled(boolean cancelled) {
        event.setCancelled(cancelled);
    }
}
