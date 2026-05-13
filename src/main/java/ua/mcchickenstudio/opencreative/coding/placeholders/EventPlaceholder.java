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

package ua.mcchickenstudio.opencreative.coding.placeholders;

import org.bukkit.entity.Entity;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import ua.mcchickenstudio.opencreative.coding.blocks.actions.Action;
import ua.mcchickenstudio.opencreative.coding.blocks.actions.ActionsHandler;
import ua.mcchickenstudio.opencreative.coding.blocks.events.WorldEvent;
import ua.mcchickenstudio.opencreative.coding.blocks.events.player.fighting.KillerVictimEvent;

public final class EventPlaceholder extends KeyPlaceholder {

    public EventPlaceholder() {
        super("killer", "damager", "killer_uuid", "damager_uuid", "victim", "victim_uuid", "shooter", "shooter_uuid", "event");
    }

    @Override
    public @Nullable String parseKey(String key, ActionsHandler handler, Action action) {
        WorldEvent worldEvent = handler.getEvent();
        Entity killer = null;
        Entity victim = null;
        if (worldEvent instanceof KillerVictimEvent event) {
            killer = event.getKiller();
            victim = event.getVictim();
        }
        return switch (key) {
            case "killer", "damager", "shooter" -> killer != null ? killer.getName() : null;
            case "killer_uuid", "damager_uuid", "shooter_uuid" ->
                    killer != null ? killer.getUniqueId().toString() : null;
            case "victim" -> victim != null ? victim.getName() : null;
            case "victim_uuid" -> victim != null ? victim.getUniqueId().toString() : null;
            case "event" ->
                    handler.getEvent() == null ? "null" : handler.getEvent().getClass().getSimpleName().toLowerCase();
            default -> null;
        };
    }

    @Override
    public @NotNull String getExtensionId() {
        return "default";
    }

    @Override
    public @NotNull String getName() {
        return "Event Placeholder";
    }

    @Override
    public @NotNull String getDescription() {
        return "Parses event placeholders";
    }
}
