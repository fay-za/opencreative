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

package ua.mcchickenstudio.opencreative.coding.blocks.actions.playeractions.params;

import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeInstance;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import ua.mcchickenstudio.opencreative.coding.arguments.Arguments;
import ua.mcchickenstudio.opencreative.coding.blocks.actions.ActionType;
import ua.mcchickenstudio.opencreative.coding.blocks.actions.Target;
import ua.mcchickenstudio.opencreative.coding.blocks.actions.playeractions.PlayerAction;
import ua.mcchickenstudio.opencreative.coding.blocks.executors.Executor;

import static ua.mcchickenstudio.opencreative.utils.ErrorUtils.sendCodingDebugLog;

public final class SetHealthAction extends PlayerAction {
    public SetHealthAction(Executor executor, Target target, int x, Arguments args) {
        super(executor, target, x, args);
    }

    public void executePlayer(@NotNull Player player) {
        if (player.isDead()) {
            sendCodingDebugLog(getPlanet(), "Can't set player's health, player is dead.");
            return;
        }
        boolean add = getArguments().getBoolean("add", false, this);
        double health = getArguments().getDouble("health", 20.0d, this);
        if (add) {
            health = health + player.getHealth();
        }
        AttributeInstance maxHealth = player.getAttribute(Attribute.GENERIC_MAX_HEALTH);
        if (maxHealth != null && health > maxHealth.getValue()) {
            health = maxHealth.getValue();
        }
        player.setHealth(health);
    }

    @Override
    public @NotNull ActionType getActionType() {
        return ActionType.PLAYER_SET_HEALTH;
    }
}
