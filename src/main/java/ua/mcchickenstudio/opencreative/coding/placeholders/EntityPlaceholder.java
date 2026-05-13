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
import ua.mcchickenstudio.opencreative.coding.blocks.events.player.interaction.MobInteractionEvent;

public final class EntityPlaceholder extends KeyPlaceholder {

    public EntityPlaceholder() {
        super("entity", "entity_uuid");
    }

    @Override
    public @Nullable String parseKey(String key, ActionsHandler handler, Action action) {
        switch (key) {
            case "entity" -> {
                if (handler.getEvent() instanceof MobInteractionEvent event) {
                    return event.getEntity().getName();
                } else if (handler.getEvent().getSelection().getFirst() instanceof Entity entity) {
                    if (!entity.getWorld().equals(action.getExecutor().getPlanet().getWorld())) return null;
                    return entity.getName();
                }
            }
            case "entity_uuid" -> {
                if (handler.getEvent() instanceof MobInteractionEvent event) {
                    return event.getEntity().getUniqueId().toString();
                } else if (handler.getEvent().getSelection().getFirst() instanceof Entity entity) {
                    if (!entity.getWorld().equals(action.getExecutor().getPlanet().getWorld())) return null;
                    return entity.getUniqueId().toString();
                }
            }
        }
        return null;
    }

    @Override
    public @NotNull String getExtensionId() {
        return "default";
    }

    @Override
    public @NotNull String getName() {
        return "Entity Placeholder";
    }

    @Override
    public @NotNull String getDescription() {
        return "Parses entity placeholders";
    }
}
