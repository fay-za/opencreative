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

import net.kyori.adventure.title.Title;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.data.BlockData;
import org.bukkit.block.data.Directional;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import ua.mcchickenstudio.opencreative.OpenCreative;
import ua.mcchickenstudio.opencreative.coding.blocks.actions.ActionCategory;
import ua.mcchickenstudio.opencreative.coding.blocks.actions.ActionType;
import ua.mcchickenstudio.opencreative.planets.DevPlanet;
import ua.mcchickenstudio.opencreative.settings.Sounds;

import java.time.Duration;
import java.util.List;

import static ua.mcchickenstudio.opencreative.utils.BlockUtils.setSignLine;
import static ua.mcchickenstudio.opencreative.utils.ItemUtils.*;
import static ua.mcchickenstudio.opencreative.utils.MessageUtils.getLocaleMessage;
import static ua.mcchickenstudio.opencreative.utils.MessageUtils.toComponent;
import static ua.mcchickenstudio.opencreative.utils.PlayerUtils.translateBlockSign;

public final class ActionTypeSelectionMenu extends BlocksWithMenusCategoryMenu<ActionType> {

    private final ActionCategory action;
    private final String firstLine;

    public ActionTypeSelectionMenu(@NotNull Player player,
                                   @NotNull Location location,
                                   @NotNull ActionCategory action) {
        super(player, location, action.isCondition() ? "conditions" : "actions",
                action.name().toLowerCase(),
                action.getStainedPane(), action.getDefaultCategory());
        this.action = action;
        this.firstLine = null;
    }

    public ActionTypeSelectionMenu(@NotNull Player player,
                                   @NotNull Location location,
                                   @NotNull ActionCategory action,
                                   @Nullable String firstLine) {
        super(player, location, action.isCondition() ? "conditions" : "actions",
                action.name().toLowerCase(),
                action.getStainedPane(), action.getDefaultCategory());
        this.action = action;
        this.firstLine = firstLine;
    }

    @Override
    protected ItemStack getElementIcon(ActionType type) {
        return type.getIcon();
    }

    @Override
    protected void onElementClick(InventoryClickEvent event) {
        ItemStack item = event.getCurrentItem();
        event.setCancelled(true);
        if (item == null) return;
        if (item.getItemMeta() == null) return;
        DevPlanet devPlanet = OpenCreative.getPlanetsManager().getDevPlanet(getPlayer());
        Block codingBlock = signLocation.getBlock().getRelative(BlockFace.NORTH);
        if (signLocation.getWorld().getName().contains("dev") && devPlanet != null) {
            String typeString = getPersistentData(item, getCodingValueKey());
            ActionType actionType = null;
            try {
                actionType = ActionType.valueOf(typeString);
            } catch (Exception ignored) {
            }
            if (actionType == ActionType.REPEAT_WHILE || actionType == ActionType.REPEAT_WHILE_NOT) {
                new RepeatConditionSelectionMenu(getPlayer(), signLocation, actionType == ActionType.REPEAT_WHILE_NOT)
                        .open(getPlayer());
                return;
            }
            ActionCategory actionCategory = actionType == null ? null : actionType.getCategory();
            if (actionCategory != null) {
                devPlanet.addInsideCodeColumnChange(signLocation.getBlock().getRelative(BlockFace.NORTH).getLocation());
                if (!actionCategory.isCondition()) setSignLine(signLocation, 1, "");
                if (firstLine != null) setSignLine(signLocation, 1, firstLine);
                setSignLine(signLocation, 2, actionCategory.name().toLowerCase());
            }
            if (setSignLine(signLocation, 3, typeString.toLowerCase())) {
                devPlanet.addInsideCodeColumnChange(signLocation.getBlock().getRelative(BlockFace.NORTH).getLocation());
                translateBlockSign(signLocation.getBlock());
                getPlayer().closeInventory();
                getPlayer().showTitle(Title.title(
                        toComponent(getLocaleMessage("world.dev-mode.set-" + (action.isCondition() ? "conditions" : "actions"))), item.getItemMeta().displayName(),
                        Title.Times.times(Duration.ofMillis(750), Duration.ofSeconds(1), Duration.ofMillis(750))
                ));
                (action.isCondition() ? Sounds.DEV_SET_CONDITION : Sounds.DEV_SET_ACTION).play(event.getWhoClicked());
                event.getWhoClicked().swingMainHand();
            }
            /*
             Setting a chest block if action requires container.
             Executors don't have arguments, neither chests.
            */
            if (actionCategory != null) {
                Block containerBlock = codingBlock.getRelative(BlockFace.UP);
                if (containerBlock.getState() instanceof InventoryHolder container) {
                    if (devPlanet.isDropItems()) {
                        for (ItemStack chestItem : container.getInventory().getContents()) {
                            if (chestItem != null) {
                                if (chestItem.getItemMeta() == null || !chestItem.getItemMeta().getPersistentDataContainer().has(getCodingDoNotDropMeKey())) {
                                    containerBlock.getWorld().dropItem(containerBlock.getLocation(), chestItem);
                                }
                            }
                        }
                    }
                    containerBlock.setType(Material.AIR);
                }
                if (actionType.isChestRequired()) {
                    containerBlock.setType(devPlanet.getContainerMaterial());
                    BlockData blockData = containerBlock.getBlockData();
                    ((Directional) blockData).setFacing(BlockFace.SOUTH);
                    containerBlock.setBlockData(blockData);
                    getPlayer().spawnParticle(Particle.BLOCK, containerBlock.getLocation(), 1, 0, 0.5f, 0.5f, containerBlock.getBlockData());
                    Sounds.DEV_ACTION_WITH_CHEST.play(getPlayer());
                }
            }
        }
    }

    @Override
    public List<ActionType> getElements() {
        return ActionType.getActionsByCategories(action, currentCategory);
    }
}
