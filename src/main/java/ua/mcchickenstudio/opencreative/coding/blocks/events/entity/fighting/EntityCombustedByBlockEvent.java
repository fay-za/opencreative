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

package ua.mcchickenstudio.opencreative.coding.blocks.events.entity.fighting;

import org.bukkit.block.Block;
import org.bukkit.event.Cancellable;
import org.bukkit.event.entity.EntityCombustByBlockEvent;
import org.jetbrains.annotations.NotNull;
import ua.mcchickenstudio.opencreative.coding.blocks.events.BlockEvent;
import ua.mcchickenstudio.opencreative.coding.blocks.events.WorldEvent;

public final class EntityCombustedByBlockEvent extends WorldEvent implements Cancellable, BlockEvent {

    private final EntityCombustByBlockEvent event;

    public EntityCombustedByBlockEvent(EntityCombustByBlockEvent event) {
        super(event.getEntity());
        this.event = event;
    }

    @Override
    public @NotNull Block getBlock() {
        Block block = event.getCombuster();
        return block == null ? event.getEntity().getLocation().getBlock() : block;
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
