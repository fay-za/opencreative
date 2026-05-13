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

/**
 * This class represents a disabled economy manager, that will be
 * used by default, if Vault will be not detected. It will not
 * do any money operation and will be disabled forever.
 */
public final class DisabledEconomy implements Economy {

    @Override
    public boolean depositMoney(OfflinePlayer offlinePlayer, Number money) {
        return false;
    }

    @Override
    public boolean withdrawMoney(OfflinePlayer offlinePlayer, Number money) {
        return false;
    }

    @Override
    public Number getBalance(OfflinePlayer offlinePlayer) {
        return 0;
    }

    @Override
    public void init() {
    }

    @Override
    public boolean isEnabled() {
        return false;
    }

    @Override
    public String getName() {
        return "Disabled Economy";
    }
}
