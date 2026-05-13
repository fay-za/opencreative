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

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.jetbrains.annotations.NotNull;
import ua.mcchickenstudio.opencreative.OpenCreative;
import ua.mcchickenstudio.opencreative.coding.arguments.Arguments;
import ua.mcchickenstudio.opencreative.coding.blocks.actions.ActionType;
import ua.mcchickenstudio.opencreative.coding.blocks.actions.Target;
import ua.mcchickenstudio.opencreative.coding.blocks.actions.playeractions.PlayerAction;
import ua.mcchickenstudio.opencreative.coding.blocks.events.player.world.PlayerPurchaseEvent;
import ua.mcchickenstudio.opencreative.coding.blocks.executors.Executor;
import ua.mcchickenstudio.opencreative.coding.exceptions.TooManyOpenedMenusException;
import ua.mcchickenstudio.opencreative.menus.ConfirmationMenu;
import ua.mcchickenstudio.opencreative.planets.Planet;
import ua.mcchickenstudio.opencreative.settings.Sounds;

import static ua.mcchickenstudio.opencreative.utils.MessageUtils.*;

public final class RequestPurchaseAction extends PlayerAction {
    public RequestPurchaseAction(Executor executor, Target target, int x, Arguments args) {
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
        String id = getArguments().getText("id", "example", this);
        String name = getArguments().getText("name", "Example", this);
        boolean save = getArguments().getBoolean("save", false, this);
        int price = getArguments().getInt("price", 100, this);
        Planet planet = OpenCreative.getPlanetsManager().getPlanetByPlayer(player);
        if (planet == null) return;
        new ConfirmationMenu(
                getLocaleMessage("menus.confirmation.request-money", false).replace("%name%", name),
                Material.GOLD_INGOT,
                getLocaleItemName("menus.confirmation.items.request-money.name")
                        .replace("%price%", String.valueOf(price))
                        .replace("%name%", name),
                getLocaleItemDescription("menus.confirmation.items.request-money.lore"),
                new BukkitRunnable() {
                    @Override
                    public void run() {
                        Planet currentPlanet = OpenCreative.getPlanetsManager().getPlanetByPlayer(player);
                        if (!planet.equals(currentPlanet) || !OpenCreative.getEconomy().isEnabled()) {
                            cancel();
                            return;
                        }
                        if (OpenCreative.getEconomy().getBalance(player).intValue() < price) {
                            player.sendMessage(getLocaleMessage("no-money").replace("%money%", String.valueOf(price)));
                        } else {
                            if (save) {
                                if (planet.getWorldPlayers().getPlanetPlayer(player).getPurchases().contains(id.toLowerCase())) {
                                    return;
                                } else {
                                    planet.getWorldPlayers().getPlanetPlayer(player).addPurchase(id.toLowerCase());
                                }
                            }
                            Sounds.WORLD_PURCHASE.play(player);
                            if (!planet.isOwner(player)) {
                                OpenCreative.getEconomy().withdrawMoney(player, price);
                                OpenCreative.getEconomy().depositMoney(Bukkit.getOfflinePlayer(planet.getOwner()), price);
                            }
                            new PlayerPurchaseEvent(player, id, name, price, save).callEvent();
                        }
                    }
                }).open(player);
    }

    @Override
    public @NotNull ActionType getActionType() {
        return ActionType.PLAYER_REQUEST_PURCHASE;
    }
}
