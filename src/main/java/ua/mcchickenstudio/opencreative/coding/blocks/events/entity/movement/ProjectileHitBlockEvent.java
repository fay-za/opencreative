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

package ua.mcchickenstudio.opencreative.coding.blocks.events.entity.movement;

import org.bukkit.block.Block;
import org.bukkit.event.Cancellable;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.jetbrains.annotations.NotNull;
import ua.mcchickenstudio.opencreative.coding.blocks.events.BlockEvent;
import ua.mcchickenstudio.opencreative.coding.blocks.events.WorldEvent;

public final class ProjectileHitBlockEvent extends WorldEvent implements Cancellable, BlockEvent {

    private final ProjectileHitEvent event;
    private final Block block;

    public ProjectileHitBlockEvent(ProjectileHitEvent event, @NotNull Block block) {
        super(event.getEntity());
        this.event = event;
        this.block = block;
    }

    @Override
    public @NotNull Block getBlock() {
        return block;
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
