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

package ua.mcchickenstudio.opencreative.managers.economy;

import org.bukkit.OfflinePlayer;
import ua.mcchickenstudio.opencreative.managers.Manager;

/**
 * <h1>Economy</h1>
 * This interface represents economy manager
 * that has operations with server's economy,
 * like taking, giving and getting player's money.
 */
public interface Economy extends Manager {

    /**
     * Deposits money to player for some reason, examples:
     * gets world liked, some player bought in his world.
     *
     * @param offlinePlayer Player to give money.
     * @param money         Amount of money to give.
     * @return true - if successfully taken, false - failed.
     */
    boolean depositMoney(OfflinePlayer offlinePlayer, Number money);

    /**
     * Takes money from player balance, examples:
     * buys something in world.
     *
     * @param offlinePlayer Player to take his money.
     * @param money         Amount of money to take.
     * @return true - if successfully taken, false - failed.
     */
    boolean withdrawMoney(OfflinePlayer offlinePlayer, Number money);

    /**
     * Returns an amount of money that player has currently on his balance.
     *
     * @param offlinePlayer Player to check balance.
     * @return Amount of money on player's balance.
     */
    Number getBalance(OfflinePlayer offlinePlayer);
}
