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

package ua.mcchickenstudio.opencreative.coding.blocks.events.player.fighting;

import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.jetbrains.annotations.NotNull;
import ua.mcchickenstudio.opencreative.coding.blocks.events.WorldEvent;

public final class MobDamagesPlayerEvent extends WorldEvent implements KillerVictimEvent, Cancellable {

    private final EntityDamageByEntityEvent event;
    private final Entity damager;
    private final Player victim;

    public MobDamagesPlayerEvent(Player player, EntityDamageByEntityEvent event) {
        super(player);
        this.event = event;
        this.damager = event.getDamager();
        this.victim = (Player) event.getEntity();
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
    public @NotNull Player getVictim() {
        return victim;
    }

    @Override
    public @NotNull Entity getKiller() {
        return damager;
    }
}
