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

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Entity;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import ua.mcchickenstudio.opencreative.coding.blocks.actions.Action;
import ua.mcchickenstudio.opencreative.coding.blocks.actions.ActionsHandler;

public final class TargetPlaceholder extends KeyPlaceholder {

    public TargetPlaceholder() {
        super("selected", "selected_uuid", "target", "target_uuid", "targets", "selection");
    }

    @Override
    public @Nullable String parseKey(String key, ActionsHandler handler, Action action) {
        Entity entity = action.getEntity();
        return switch (key) {
            case "selected", "target" -> entity == null ? null : entity.getName();
            case "selected_uuid", "target_uuid" -> entity == null ? null : entity.getUniqueId().toString();
            case "targets", "selection" ->
                    String.join(", ", action.getHandler().getSelectedTargets().stream().map(CommandSender::getName).toList());
            default -> null;
        };
    }

    @Override
    public @NotNull String getExtensionId() {
        return "default";
    }

    @Override
    public @NotNull String getName() {
        return "Target Placeholder";
    }

    @Override
    public @NotNull String getDescription() {
        return "Parses target placeholders";
    }
}
