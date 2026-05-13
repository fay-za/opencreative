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

package ua.mcchickenstudio.opencreative.coding.blocks.actions.playeractions.inventory;

import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.Container;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import ua.mcchickenstudio.opencreative.coding.arguments.Arguments;
import ua.mcchickenstudio.opencreative.coding.blocks.actions.ActionType;
import ua.mcchickenstudio.opencreative.coding.blocks.actions.Target;
import ua.mcchickenstudio.opencreative.coding.blocks.actions.playeractions.PlayerAction;
import ua.mcchickenstudio.opencreative.coding.blocks.executors.Executor;
import ua.mcchickenstudio.opencreative.coding.exceptions.TooManyOpenedMenusException;

public final class OpenContainerAction extends PlayerAction {
    public OpenContainerAction(Executor executor, Target target, int x, Arguments args) {
        super(executor, target, x, args);
    }

    @Override
    public void executePlayer(@NotNull Player player) {
        Location location = getArguments().getLocation("location", getPlanet().getTerritory().getSpawnLocation(), this);
        boolean save = getArguments().getBoolean("save", true, this);

        Block block = location.getBlock();
        Inventory inventory;

        if (block.getState() instanceof Container container) {
            if (save) {
                inventory = container.getInventory();
            } else {
                inventory = copyInventory(container.getInventory(), container.customName());
            }
            if (getPlanet().getLimits().cantOpenMenu(player)) {
                throw new TooManyOpenedMenusException(player.getName());
            }
            player.openInventory(inventory);
        } else if (block.getType() == Material.ENDER_CHEST) {
            if (save) {
                inventory = player.getEnderChest();
            } else {
                inventory = copyInventory(player.getEnderChest(), null);
            }
            if (getPlanet().getLimits().cantOpenMenu(player)) {
                /*
                 * This check prevents player from opening
                 * too many menus, that can prevent from
                 * quiting the game.
                 */
                throw new TooManyOpenedMenusException(player.getName());
            }
            player.openInventory(inventory);
        } else if (block.getType() == Material.CRAFTING_TABLE) {
            player.openWorkbench(location, false);
        } else if (block.getType() == Material.ANVIL || block.getType() == Material.DAMAGED_ANVIL || block.getType() == Material.CHIPPED_ANVIL) {
            player.openAnvil(location, false);
        } else if (block.getType() == Material.CARTOGRAPHY_TABLE) {
            player.openCartographyTable(location, false);
        } else if (block.getType() == Material.ENCHANTING_TABLE) {
            player.openEnchanting(location, false);
        } else if (block.getType() == Material.LOOM) {
            player.openLoom(location, false);
        } else if (block.getType() == Material.GRINDSTONE) {
            player.openGrindstone(location, false);
        } else if (block.getType() == Material.SMITHING_TABLE) {
            player.openSmithingTable(location, false);
        } else if (block.getType() == Material.STONECUTTER) {
            player.openStonecutter(location, false);
        }
    }

    @Override
    public @NotNull ActionType getActionType() {
        return ActionType.PLAYER_OPEN_CONTAINER;
    }

    private Inventory copyInventory(Inventory inventory, Component customName) {
        Component title = inventory.getType().defaultTitle();
        if (customName != null) {
            title = customName;
        }
        Inventory copiedInventory = Bukkit.createInventory(null, inventory.getSize(), title);
        for (byte slot = 0; slot < inventory.getSize(); slot++) {
            if (slot >= inventory.getContents().length) {
                break;
            }
            ItemStack item = inventory.getItem(slot);
            if (item != null) {
                copiedInventory.setItem(slot, item);
            }
        }
        return copiedInventory;
    }
}
