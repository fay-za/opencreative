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

package ua.mcchickenstudio.opencreative.coding.blocks.actions.playeractions.communication;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import ua.mcchickenstudio.opencreative.coding.arguments.Arguments;
import ua.mcchickenstudio.opencreative.coding.blocks.actions.ActionType;
import ua.mcchickenstudio.opencreative.coding.blocks.actions.Target;
import ua.mcchickenstudio.opencreative.coding.blocks.actions.playeractions.PlayerAction;
import ua.mcchickenstudio.opencreative.coding.blocks.executors.Executor;
import ua.mcchickenstudio.opencreative.coding.exceptions.TooLongTextException;

import java.util.List;

public final class ShowActionbarAction extends PlayerAction {
    public ShowActionbarAction(Executor executor, Target target, int x, Arguments args) {
        super(executor, target, x, args);
    }

    @Override
    public void executePlayer(@NotNull Player player) {
        String separator = getArguments().getText("type", "new-line", this);
        List<Component> components = getArguments().getComponentList("actionbar", this);
        TextComponent.Builder builder = Component.text();
        Component separatorComponent = separator.equals("join-spaces") ? Component.space() : Component.empty();
        for (int i = 0; i < components.size(); i++) {
            builder.append(components.get(i));
            if (i != components.size() - 1) {
                builder.append(separatorComponent);
            }
        }
        Component actionbar = builder.build();
        String plainText = PlainTextComponentSerializer.plainText().serialize(actionbar);
        if (plainText.length() > 1024) {
            throw new TooLongTextException(1024);
        }
        player.sendActionBar(actionbar);
    }


    @Override
    public @NotNull ActionType getActionType() {
        return ActionType.PLAYER_SHOW_ACTIONBAR;
    }
}
