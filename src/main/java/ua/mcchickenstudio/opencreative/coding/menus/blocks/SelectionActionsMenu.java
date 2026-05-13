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

package ua.mcchickenstudio.opencreative.coding.menus.blocks;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import ua.mcchickenstudio.opencreative.OpenCreative;
import ua.mcchickenstudio.opencreative.coding.blocks.actions.ActionCategory;
import ua.mcchickenstudio.opencreative.coding.blocks.actions.Target;
import ua.mcchickenstudio.opencreative.coding.menus.layouts.Layout;
import ua.mcchickenstudio.opencreative.menus.AbstractMenu;
import ua.mcchickenstudio.opencreative.planets.DevPlanet;
import ua.mcchickenstudio.opencreative.settings.Sounds;

import static ua.mcchickenstudio.opencreative.utils.BlockUtils.setSignLine;
import static ua.mcchickenstudio.opencreative.utils.ItemUtils.createItem;
import static ua.mcchickenstudio.opencreative.utils.ItemUtils.itemEquals;
import static ua.mcchickenstudio.opencreative.utils.MessageUtils.getLocaleMessage;
import static ua.mcchickenstudio.opencreative.utils.PlayerUtils.translateBlockSign;

public final class SelectionActionsMenu extends AbstractMenu {

    private final Player player;
    private final Location signLocation;

    private final ItemStack varCondition = createItem(Material.OBSIDIAN, 1, "items.developer.variable-condition");
    private final ItemStack playerCondition = createItem(Material.OAK_PLANKS, 1, "items.developer.player-condition");
    private final ItemStack entityCondition = createItem(Material.BRICKS, 1, "items.developer.entity-condition");

    private final ItemStack defaultItem = createItem(Target.DEFAULT.getIcon(), 1, "menus.developer.selection.items.default");
    private final ItemStack allPlayers = createItem(Target.ALL_PLAYERS.getIcon(), 1, "menus.developer.selection.items.all-players");
    private final ItemStack allEntities = createItem(Target.ALL_ENTITIES.getIcon(), 1, "menus.developer.selection.items.all-entities");
    private final ItemStack randomTarget = createItem(Target.RANDOM_TARGET.getIcon(), 1, "menus.developer.selection.items.random-target");
    private final ItemStack randomPlayer = createItem(Target.RANDOM_PLAYER.getIcon(), 1, "menus.developer.selection.items.random-player");
    private final ItemStack victim = createItem(Target.VICTIM.getIcon(), 1, "menus.developer.selection.items.victim");
    private final ItemStack killer = createItem(Target.KILLER.getIcon(), 1, "menus.developer.selection.items.killer");
    private final ItemStack lastSpawned = createItem(Target.LAST_SPAWNED.getIcon(), 1, "menus.developer.selection.items.last-spawned");

    public SelectionActionsMenu(Player player, Location location) {
        super(5, getLocaleMessage("blocks.selection_action", false));
        this.player = player;
        this.signLocation = location;
    }

    @Override
    public void fillItems(Player player) {
        setItem(10, defaultItem);
        setItem(12, randomTarget);
        setItem(13, randomPlayer);
        setItem(16, playerCondition);

        setItem(19, allPlayers);
        setItem(24, lastSpawned);
        setItem(25, entityCondition);

        setItem(28, allEntities);
        setItem(30, killer);
        setItem(31, victim);
        setItem(34, varCondition);
    }

    @Override
    public void onClick(@NotNull InventoryClickEvent event) {
        if (!isClickedInMenuSlots(event) || !isPlayerClicked(event)) {
            return;
        }
        event.setCancelled(true);
        ItemStack currentItem = event.getCurrentItem();
        if (currentItem == null) return;
        if (itemEquals(currentItem, playerCondition)) {
            new BlocksCategorySelectionMenu(player, signLocation, ActionCategory.PLAYER_CONDITION).open(player);
        } else if (itemEquals(currentItem, entityCondition)) {
            new BlocksCategorySelectionMenu(player, signLocation, ActionCategory.ENTITY_CONDITION).open(player);
        } else if (itemEquals(currentItem, varCondition)) {
            new BlocksCategorySelectionMenu(player, signLocation, ActionCategory.VARIABLE_CONDITION).open(player);
        } else if (itemEquals(currentItem, allPlayers)) {
            setLine("all_players");
            Sounds.DEV_SET_TARGET.play(player);
            event.getWhoClicked().swingMainHand();
            player.closeInventory();
        } else if (itemEquals(currentItem, randomPlayer)) {
            setLine("random_player");
            Sounds.DEV_SET_TARGET.play(player);
            event.getWhoClicked().swingMainHand();
            player.closeInventory();
        } else if (itemEquals(currentItem, killer)) {
            setLine("killer");
            Sounds.DEV_SET_TARGET.play(player);
            event.getWhoClicked().swingMainHand();
            player.closeInventory();
        } else if (itemEquals(currentItem, victim)) {
            setLine("victim");
            Sounds.DEV_SET_TARGET.play(player);
            event.getWhoClicked().swingMainHand();
            player.closeInventory();
        } else if (itemEquals(currentItem, randomTarget)) {
            setLine("random_target");
            Sounds.DEV_SET_TARGET.play(player);
            event.getWhoClicked().swingMainHand();
            player.closeInventory();
        } else if (itemEquals(currentItem, allEntities)) {
            setLine("all_entities");
            Sounds.DEV_SET_TARGET.play(player);
            event.getWhoClicked().swingMainHand();
            player.closeInventory();
        } else if (itemEquals(currentItem, defaultItem)) {
            setLine("default");
            Sounds.DEV_SET_TARGET.play(player);
            event.getWhoClicked().swingMainHand();
            player.closeInventory();
        } else if (itemEquals(currentItem, lastSpawned)) {
            setLine("last_spawned");
            Sounds.DEV_SET_TARGET.play(player);
            event.getWhoClicked().swingMainHand();
            player.closeInventory();
        }
    }

    private void setLine(String text) {
        setSignLine(signLocation, 1, "");
        setSignLine(signLocation, 2, text);
        setSignLine(signLocation, 3, "");
        translateBlockSign(signLocation.getBlock());
        Block containerBlock = signLocation.getBlock().getRelative(BlockFace.UP).getRelative(BlockFace.NORTH);
        DevPlanet devPlanet = OpenCreative.getPlanetsManager().getDevPlanet(signLocation.getWorld());
        if (devPlanet == null) {
            containerBlock.setType(Material.AIR);
            return;
        }
        devPlanet.addInsideCodeColumnChange(signLocation.getBlock().getRelative(BlockFace.NORTH).getLocation());
        Layout layout = devPlanet.getOpenedMenu(containerBlock.getLocation());
        if (layout != null) {
            for (Player viewer : layout.getViewers()) {
                viewer.closeInventory();
            }
            devPlanet.unregisterOpenedMenu(containerBlock.getLocation());
        }
        containerBlock.setType(Material.AIR);
    }

    @Override
    public void onOpen(@NotNull InventoryOpenEvent event) {

    }
}
