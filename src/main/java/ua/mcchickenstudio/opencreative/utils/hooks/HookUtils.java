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

package ua.mcchickenstudio.opencreative.utils.hooks;

import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import ua.mcchickenstudio.opencreative.OpenCreative;
import ua.mcchickenstudio.opencreative.managers.blocks.BlocksManager;
import ua.mcchickenstudio.opencreative.managers.blocks.VanillaBlocksManager;
import ua.mcchickenstudio.opencreative.managers.blocks.WorldEditManager;
import ua.mcchickenstudio.opencreative.managers.disguises.DisabledDisguises;
import ua.mcchickenstudio.opencreative.managers.disguises.DisguiseManager;
import ua.mcchickenstudio.opencreative.managers.disguises.LibsDisguises;
import ua.mcchickenstudio.opencreative.managers.packets.DisabledPacketManager;
import ua.mcchickenstudio.opencreative.managers.packets.PacketManager;
import ua.mcchickenstudio.opencreative.managers.packets.ProtocolLibManager;

import static ua.mcchickenstudio.opencreative.utils.ErrorUtils.sendCriticalErrorMessage;

public final class HookUtils {

    public static boolean isPlaceholderAPIEnabled = false;
    public static boolean isProtocolLibEnabled = false;
    public static boolean isVaultEnabled = false;
    public static boolean isLibsDisguisesEnabled = false;
    public static boolean isWorldEditEnabled = false;

    /**
     * Load hooks into other plugins for working with them. For example: Creative+ can hook into PlaceholderAPI.
     **/
    public static void loadHooks() {
        isPlaceholderAPIEnabled = isPluginEnabled("PlaceholderAPI");
        isProtocolLibEnabled = isPluginEnabled("ProtocolLib");
        isVaultEnabled = isPluginEnabled("Vault");
        isWorldEditEnabled = isPluginEnabled("WorldEdit");
        isLibsDisguisesEnabled = isPluginEnabled("LibsDisguises");
        OpenCreative.getPlugin().getLogger().info((isPlaceholderAPIEnabled ? "Successfully integrated to PlaceholderAPI: Added placeholders." : "Didn't detect PlaceholderAPI."));
        OpenCreative.getPlugin().getLogger().info((isProtocolLibEnabled ? "Successfully integrated to ProtocolLib: Added blocks effects and animations." : "Didn't detect ProtocolLib, some block effects will be not available."));
        OpenCreative.getPlugin().getLogger().info((isLibsDisguisesEnabled ? "Successfully integrated to LibsDisguises: Added morph actions." : "Didn't detect LibsDisguises, disguise actions will be not available."));
        OpenCreative.getPlugin().getLogger().info((isWorldEditEnabled ? "Successfully integrated to WorldEdit: Added out-of-borders limit." : "Didn't detect WorldEdit."));
        if (isPlaceholderAPIEnabled) {
            PAPIUtils.registerPlaceholder();
        }
    }

    public static boolean isPluginEnabled(String pluginName) {
        Plugin plugin = Bukkit.getPluginManager().getPlugin(pluginName);
        return plugin != null && plugin.isEnabled();
    }

    public static void clearEntitiesHook(World world) {
        try {
            if (OpenCreative.getDisguiseManager().isEnabled()) {
                for (Entity entity : world.getEntities()) {
                    OpenCreative.getDisguiseManager().clearDisguises(entity);
                }
            }
        } catch (Exception error) {
            sendCriticalErrorMessage("Failed to clear disguises in world " + world.getName(), error);
        }
    }

    public static void clearPlayerHook(Player player) {
        try {
            if (OpenCreative.getDisguiseManager().isEnabled()) {
                OpenCreative.getDisguiseManager().clearDisguises(player);
            }
        } catch (Exception error) {
            sendCriticalErrorMessage("Failed to clear disguise for player " + player.getName(), error);
        }
    }

    public static PacketManager getPacketManager() {
        if (isProtocolLibEnabled) {
            return new ProtocolLibManager();
        } else {
            return new DisabledPacketManager();
        }
    }

    public static BlocksManager getBlocks() {
        if (isWorldEditEnabled) {
            return new WorldEditManager();
        } else {
            return new VanillaBlocksManager();
        }
    }

    public static DisguiseManager getDisguises() {
        if (isLibsDisguisesEnabled) {
            return new LibsDisguises();
        } else {
            return new DisabledDisguises();
        }
    }
}
