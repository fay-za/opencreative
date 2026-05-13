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

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import ua.mcchickenstudio.opencreative.coding.arguments.Arguments;
import ua.mcchickenstudio.opencreative.coding.blocks.actions.ActionType;
import ua.mcchickenstudio.opencreative.coding.blocks.actions.Target;
import ua.mcchickenstudio.opencreative.coding.blocks.actions.playeractions.PlayerAction;
import ua.mcchickenstudio.opencreative.coding.blocks.executors.Executor;
import ua.mcchickenstudio.opencreative.coding.exceptions.TooManyOpenedMenusException;
import ua.mcchickenstudio.opencreative.utils.Advancement;

public final class ShowAdvancementAction extends PlayerAction {
    public ShowAdvancementAction(Executor executor, Target target, int x, Arguments args) {
        super(executor, target, x, args);
    }

    @Override
    public void executePlayer(@NotNull Player player) {
        if (getPlanet().getLimits().cantOpenMenu(player)) {
            /*
             * This check prevents player from closing
             * too many menus, that can prevent from
             * quiting the game.
             */
            throw new TooManyOpenedMenusException(player.getName());
        }
        ItemStack itemStack = getArguments().getItem("icon", new ItemStack(Material.DIAMOND), this);
        Advancement.AdvancementStyle style = Advancement.AdvancementStyle.GOAL;
        String styleString = getArguments().getText("style", "goal", this);
        String title = getArguments().getText("title", "You got Advancement!", this);
        String message = getArguments().getText("message", "Good job.", this);
        try {
            style = Advancement.AdvancementStyle.valueOf(styleString.toUpperCase());
        } catch (IllegalArgumentException ignored) {
        }
        Advancement advancement = Advancement.make(itemStack, style, title, message);
        advancement.show(player);
    }

    @Override
    public @NotNull ActionType getActionType() {
        return ActionType.PLAYER_SHOW_ADVANCEMENT;
    }
}
