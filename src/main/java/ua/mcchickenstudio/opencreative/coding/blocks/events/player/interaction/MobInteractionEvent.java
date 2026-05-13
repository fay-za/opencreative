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

package ua.mcchickenstudio.opencreative.coding.blocks.events.player.interaction;

import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.hanging.HangingBreakByEntityEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.jetbrains.annotations.NotNull;
import ua.mcchickenstudio.opencreative.coding.blocks.events.WorldEvent;
import ua.mcchickenstudio.opencreative.coding.blocks.events.player.fighting.KillerVictimEvent;

public final class MobInteractionEvent extends WorldEvent implements KillerVictimEvent, Cancellable {

    private final Cancellable event;
    private final Entity entity;

    public MobInteractionEvent(Player player, PlayerInteractEntityEvent event) {
        super(player);
        this.event = event;
        this.entity = event.getRightClicked();
    }

    public MobInteractionEvent(Player player, HangingBreakByEntityEvent event) {
        super(player);
        this.event = event;
        this.entity = event.getEntity();
    }

    public Entity getEntity() {
        return entity;
    }

    @Override
    public boolean isCancelled() {
        return event.isCancelled();
    }

    @Override
    public void setCancelled(boolean cancelled) {
        event.setCancelled(cancelled);
    }

    @Override
    public @NotNull Entity getVictim() {
        return entity;
    }

    @Override
    public @NotNull Entity getKiller() {
        return getSelection().getFirst();
    }
}
