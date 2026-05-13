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

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.plugin.RegisteredServiceProvider;

/**
 * Implementation of Vault economy,
 * the most used on servers.
 */
public final class VaultEconomy implements Economy {

    private net.milkbowl.vault.economy.Economy vaultEconomy;

    @Override
    public boolean depositMoney(OfflinePlayer offlinePlayer, Number money) {
        return vaultEconomy.depositPlayer(offlinePlayer, money.doubleValue()).transactionSuccess();
    }

    @Override
    public boolean withdrawMoney(OfflinePlayer offlinePlayer, Number money) {
        return vaultEconomy.withdrawPlayer(offlinePlayer, money.doubleValue()).transactionSuccess();
    }

    @Override
    public Number getBalance(OfflinePlayer offlinePlayer) {
        return vaultEconomy.getBalance(offlinePlayer);
    }

    @Override
    public void init() {
        RegisteredServiceProvider<net.milkbowl.vault.economy.Economy> rsp = Bukkit.getServicesManager().getRegistration(net.milkbowl.vault.economy.Economy.class);
        if (rsp != null) {
            vaultEconomy = rsp.getProvider();
        }
    }

    @Override
    public boolean isEnabled() {
        return vaultEconomy != null && vaultEconomy.isEnabled();
    }

    @Override
    public String getName() {
        return "Vault Economy";
    }
}
