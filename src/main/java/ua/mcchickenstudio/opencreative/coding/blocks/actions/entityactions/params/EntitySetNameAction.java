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

package ua.mcchickenstudio.opencreative.coding.blocks.actions.entityactions.params;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import ua.mcchickenstudio.opencreative.OpenCreative;
import ua.mcchickenstudio.opencreative.coding.arguments.Arguments;
import ua.mcchickenstudio.opencreative.coding.blocks.actions.ActionType;
import ua.mcchickenstudio.opencreative.coding.blocks.actions.Target;
import ua.mcchickenstudio.opencreative.coding.blocks.actions.entityactions.EntityAction;
import ua.mcchickenstudio.opencreative.coding.blocks.executors.Executor;
import ua.mcchickenstudio.opencreative.coding.exceptions.TooLongTextException;

import java.util.List;

public final class EntitySetNameAction extends EntityAction {
    public EntitySetNameAction(Executor executor, Target target, int x, Arguments args) {
        super(executor, target, x, args);
    }

    @Override
    public void executeEntity(@NotNull Entity entity) {
        if (entity instanceof Player) return;
        if (!getArguments().pathExists("name")) {
            entity.customName(null);
            return;
        }
        List<Component> components = getArguments().getComponentList("name", this);
        TextComponent.Builder builder = Component.text();
        for (Component component : components) {
            builder.append(component);
        }
        Component name = builder.build();
        String plainText = PlainTextComponentSerializer.plainText().serialize(name);
        int limit = OpenCreative.getSettings().getItemFixerSettings().getMaxEntityNameLength();
        if (plainText.length() > limit) {
            throw new TooLongTextException(limit);
        }
        entity.customName(name);
    }

    @Override
    public @NotNull ActionType getActionType() {
        return ActionType.ENTITY_SET_CUSTOM_NAME;
    }
}
