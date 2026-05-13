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

package ua.mcchickenstudio.opencreative.settings;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import ua.mcchickenstudio.opencreative.OpenCreative;
import ua.mcchickenstudio.opencreative.managers.economy.DisabledEconomy;
import ua.mcchickenstudio.opencreative.managers.economy.VaultEconomy;
import ua.mcchickenstudio.opencreative.utils.hooks.HookUtils;

public final class EconomySettings {

    /**
     * Loads settings of economy from configuration.
     */
    public void load() {
        FileConfiguration config = OpenCreative.getPlugin().getConfig();
        ConfigurationSection section = config.getConfigurationSection("economy");

        String economyType;
        if (section == null) {
            OpenCreative.getPlugin().getConfig().set("economy.type", "auto");
            OpenCreative.getPlugin().saveConfig();
            economyType = "auto";
        } else {
            economyType = section.getString("type", "auto");
        }

        OpenCreative.setEconomy(new DisabledEconomy());
        if (economyType.equalsIgnoreCase("vault")) {
            setupVault();
        } else {
            if (HookUtils.isVaultEnabled) {
                setupVault();
            } else {
                OpenCreative.getPlugin().getLogger().info("Didn't detect Vault, action Request Purchase and like rewards will be not available.");
            }
        }
    }

    private void setupVault() {
        if (HookUtils.isVaultEnabled) {
            OpenCreative.getPlugin().getLogger().info("Successfully integrated to Vault: Economy actions are working.");
            OpenCreative.setEconomy(new VaultEconomy());
            OpenCreative.getEconomy().init();
        } else {
            OpenCreative.getPlugin().getLogger().info("Didn't detect Vault, action Request Purchase and like rewards will be not available.");
        }
    }

}
