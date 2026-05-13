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

package ua.mcchickenstudio.opencreative.listeners.player;

import com.destroystokyo.paper.event.player.PlayerStartSpectatingEntityEvent;
import com.destroystokyo.paper.event.player.PlayerStopSpectatingEntityEvent;
import io.papermc.paper.event.player.PlayerNameEntityEvent;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.event.HoverEvent;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import net.kyori.adventure.title.Title;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.Sign;
import org.bukkit.block.sign.Side;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.SignChangeEvent;
import org.bukkit.event.hanging.HangingBreakByEntityEvent;
import org.bukkit.event.player.*;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.inventory.meta.SpawnEggMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;
import ua.mcchickenstudio.opencreative.OpenCreative;
import ua.mcchickenstudio.opencreative.coding.CodeConfiguration;
import ua.mcchickenstudio.opencreative.coding.CodeStorage;
import ua.mcchickenstudio.opencreative.coding.CodingBlockParser;
import ua.mcchickenstudio.opencreative.coding.CodingBlockPlacer;
import ua.mcchickenstudio.opencreative.coding.blocks.actions.ActionCategory;
import ua.mcchickenstudio.opencreative.coding.blocks.actions.ActionType;
import ua.mcchickenstudio.opencreative.coding.blocks.events.player.interaction.*;
import ua.mcchickenstudio.opencreative.coding.blocks.executors.ExecutorCategory;
import ua.mcchickenstudio.opencreative.coding.menus.TargetSelectionMenu;
import ua.mcchickenstudio.opencreative.coding.menus.ValueTargetSelectionMenu;
import ua.mcchickenstudio.opencreative.coding.menus.blocks.BlocksCategorySelectionMenu;
import ua.mcchickenstudio.opencreative.coding.menus.blocks.FunctionChooserMenu;
import ua.mcchickenstudio.opencreative.coding.menus.blocks.MethodChooserMenu;
import ua.mcchickenstudio.opencreative.coding.menus.blocks.SelectionActionsMenu;
import ua.mcchickenstudio.opencreative.coding.menus.layouts.ArgumentSlot;
import ua.mcchickenstudio.opencreative.coding.menus.layouts.Layout;
import ua.mcchickenstudio.opencreative.coding.menus.layouts.LayoutMaker;
import ua.mcchickenstudio.opencreative.coding.menus.variables.EventValuesCategorySelectionMenu;
import ua.mcchickenstudio.opencreative.coding.menus.variables.ParticlesMenu;
import ua.mcchickenstudio.opencreative.coding.menus.variables.PotionsMenu;
import ua.mcchickenstudio.opencreative.coding.menus.variables.VariablesMenu;
import ua.mcchickenstudio.opencreative.coding.variables.VariableLink;
import ua.mcchickenstudio.opencreative.menus.AbstractMenu;
import ua.mcchickenstudio.opencreative.menus.Menus;
import ua.mcchickenstudio.opencreative.menus.world.browsers.OwnWorldsBrowserMenu;
import ua.mcchickenstudio.opencreative.menus.world.browsers.WorldsCompassMenu;
import ua.mcchickenstudio.opencreative.menus.world.settings.WorldSettingsMenu;
import ua.mcchickenstudio.opencreative.planets.DevPlanet;
import ua.mcchickenstudio.opencreative.planets.DevPlatform;
import ua.mcchickenstudio.opencreative.planets.Planet;
import ua.mcchickenstudio.opencreative.planets.PlanetFlags;
import ua.mcchickenstudio.opencreative.settings.Sounds;
import ua.mcchickenstudio.opencreative.settings.groups.LimitType;
import ua.mcchickenstudio.opencreative.utils.CooldownUtils;
import ua.mcchickenstudio.opencreative.utils.ItemUtils;

import java.time.Duration;
import java.util.*;

import static ua.mcchickenstudio.opencreative.listeners.player.ChangedWorld.*;
import static ua.mcchickenstudio.opencreative.listeners.player.PlaceBlockListener.move;
import static ua.mcchickenstudio.opencreative.listeners.player.PlaceBlockListener.placeDebugTorch;
import static ua.mcchickenstudio.opencreative.utils.BlockUtils.*;
import static ua.mcchickenstudio.opencreative.utils.CooldownUtils.getCooldown;
import static ua.mcchickenstudio.opencreative.utils.CooldownUtils.setCooldown;
import static ua.mcchickenstudio.opencreative.utils.ItemUtils.*;
import static ua.mcchickenstudio.opencreative.utils.MessageUtils.*;
import static ua.mcchickenstudio.opencreative.utils.PlayerUtils.*;

public final class InteractListener implements Listener {

    private static VariableLink.VariableType getVariableType(ItemMeta meta) {
        char colorCode = 'c';
        String itemName = meta.getDisplayName();
        if (itemName.length() >= 2) {
            colorCode = itemName.charAt(1);
        }
        VariableLink.VariableType type = colorCode == 'a' ? VariableLink.VariableType.SAVED : colorCode == 'e' ? VariableLink.VariableType.GLOBAL : VariableLink.VariableType.LOCAL;
        if (type == VariableLink.VariableType.LOCAL) {
            type = VariableLink.VariableType.GLOBAL;
        } else if (type == VariableLink.VariableType.GLOBAL) {
            type = VariableLink.VariableType.SAVED;
        } else {
            type = VariableLink.VariableType.LOCAL;
        }
        return type;
    }

    public static String formatLocation(Location location) {
        double x = Math.round(location.getX() * 100.0) / 100.0;
        double y = Math.round(location.getY() * 100.0) / 100.0;
        double z = Math.round(location.getZ() * 100.0) / 100.0;
        float yaw = Math.round(location.getYaw() * 100.0f) / 100.0f;
        float pitch = Math.round(location.getPitch() * 100.0f) / 100.0f;
        return ChatColor.translateAlternateColorCodes('&', "&a" + x + " " + y + " " + z + " &7" + yaw + " " + pitch);
    }

    @EventHandler
    public void onInteraction(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        ItemStack fixedMain = ItemUtils.fixItem(player.getInventory().getItemInMainHand().clone());
        ItemStack fixedOff = ItemUtils.fixItem(player.getInventory().getItemInOffHand().clone());
        if (!fixedMain.equals(player.getInventory().getItemInMainHand())) {
            player.getInventory().setItemInMainHand(fixedMain);
        }
        if (!fixedOff.equals(player.getInventory().getItemInOffHand())) {
            player.getInventory().setItemInOffHand(fixedOff);
        }
        ItemStack currentItem = player.getInventory().getItemInMainHand();
        DevPlanet devPlanet = OpenCreative.getPlanetsManager().getDevPlanet(player);
        if (devPlanet == null) {
            setPaperLocation(event, player, currentItem);
            return;
        }
        Block clickedBlock = event.getClickedBlock();
        boolean doNotUseItem = false;
        if (clickedBlock != null) {
            if (clickedBlock.getType().toString().contains("WALL_SIGN")) {
                doNotUseItem = handleSignClick(event, player, currentItem, clickedBlock, devPlanet);
            } else if (clickedBlock.getState() instanceof InventoryHolder) {
                doNotUseItem = handleContainerClick(event, player, devPlanet, event.getClickedBlock());
            }
        }
        if (currentItem.getItemMeta() != null && !doNotUseItem) {
            handleCodingItemInteraction(event, player, currentItem, clickedBlock);
        }
    }

    /**
     * Handles player's coding item interaction, often with Right Click Air event.
     * Sets coding items types with persistent data, changes values (CLOCK, FEATHER),
     * changes types (MAGMA_CREAM), opens menus (NAME_TAG, NETHER_STAR, POTION),
     * sends a display name of item (BOOK, SLIME_BALL).
     */
    private void handleCodingItemInteraction(PlayerInteractEvent event, Player player, ItemStack currentItem, Block clickedBlock) {
        if (event.getHand() != EquipmentSlot.HAND) {
            return;
        }
        if (itemEquals(currentItem, createItem(Material.IRON_INGOT, 1, "items.developer.variables"))) {
            new VariablesMenu().open(player);
            return;
        }
        switch (currentItem.getType()) {
            case BOOK -> handleBookClick(event, player, currentItem);
            case SLIME_BALL -> handleSlimeBallClick(event, player, currentItem);
            case BLACK_DYE -> handleDyeClick(event, player, currentItem);
            case FEATHER -> handleFeatherInteraction(event, player, currentItem);
            case CLOCK -> handleClockInteraction(event, player, currentItem);
            case MAGMA_CREAM -> handleMagmaCreamInteraction(event, player, currentItem);
            case PAPER -> handlePaperInteraction(event, player, currentItem);
            case PRISMARINE_SHARD -> handlePrismarineShardClick(event, player, currentItem);
            case NAME_TAG -> {
                event.setCancelled(true);
                if (event.getAction() == Action.LEFT_CLICK_BLOCK || event.getAction() == Action.RIGHT_CLICK_BLOCK)
                    return;
                if (player.isSneaking()) {
                    new ValueTargetSelectionMenu(player).open(player);
                } else {
                    new EventValuesCategorySelectionMenu(player,
                            player.getInventory().getItemInMainHand()).open(player);
                }
            }
            case COMPARATOR -> {
                if (clickedBlock != null) {
                    handleComparatorInteraction(event, player, clickedBlock);
                }
            }
            case NETHER_STAR -> {
                event.setCancelled(true);
                if (event.getAction() == Action.LEFT_CLICK_BLOCK || event.getAction() == Action.RIGHT_CLICK_BLOCK)
                    return;
                if (player.isSneaking()) {
                    String particleType = getPersistentData(currentItem, getCodingParticleTypeKey());
                    if (particleType.isEmpty()) return;
                    try {
                        Particle particle = Particle.valueOf(particleType.toUpperCase());
                        Vector direction = player.getLocation().getDirection().normalize().multiply(1.5);
                        Location particleLocation = player.getLocation().add(direction).add(0, 1, 0);
                        player.spawnParticle(particle, particleLocation, 1);
                    } catch (Exception ignored) {
                    }
                } else {
                    new ParticlesMenu(player).open(player);
                }
            }
            case POTION, GLASS_BOTTLE, LINGERING_POTION, SPLASH_POTION -> {
                event.setCancelled(true);
                if (event.getAction() == Action.LEFT_CLICK_BLOCK || event.getAction() == Action.RIGHT_CLICK_BLOCK)
                    return;
                if (player.isSneaking() && currentItem.getType() != Material.GLASS_BOTTLE) {
                    if (player.hasCooldown(currentItem.getType())) return;
                    player.setCooldown(currentItem.getType(), 10);
                    try {
                        PotionMeta potionMeta = (PotionMeta) currentItem.getItemMeta();
                        List<PotionEffect> effects = new ArrayList<>();
                        if (potionMeta.getBasePotionType() != null) {
                            effects.addAll(potionMeta.getBasePotionType().getPotionEffects());
                        }
                        if (potionMeta.hasCustomEffects()) {
                            effects.addAll(potionMeta.getCustomEffects());
                        }
                        for (PotionEffect potionEffect : effects) {
                            if (player.hasPotionEffect(potionEffect.getType())) {
                                player.removePotionEffect(potionEffect.getType());
                            } else {
                                player.addPotionEffect(potionEffect);
                            }
                        }
                    } catch (Exception ignored) {
                    }
                } else {
                    new PotionsMenu(player, currentItem.getType()).open(player);
                }
            }
        }
    }

    /**
     * Handles event, when player clicks coding container block, like chest or barrel.
     * Used for creating and opening layout menus of action.
     *
     * @return true - opened container inventory, false - not opened.
     */
    private boolean handleContainerClick(PlayerInteractEvent event, Player player, DevPlanet devPlanet, Block clickedBlock) {
        if (!(clickedBlock.getState() instanceof InventoryHolder holder)) return false;
        if (event.getAction() == Action.LEFT_CLICK_BLOCK) {
            ItemStack item = player.getInventory().getItemInMainHand();
            if (item.isEmpty()) return true;
            if (getItemType(item).equals("lines_controller")) {
                Sounds.DEV_NOT_ALLOWED.play(player);
                return true;
            }
            ActionType action = ActionType.getType(clickedBlock.getRelative(BlockFace.DOWN));
            if (action == null) return true;
            if (action.getCategory() == ActionCategory.SELECTION_ACTION || action == ActionType.REPEAT_WHILE || action == ActionType.REPEAT_WHILE_NOT) {
                String selectionAction = getSignLine(clickedBlock.getRelative(BlockFace.DOWN).getRelative(BlockFace.SOUTH).getLocation(), (byte) 3);
                if (selectionAction == null || selectionAction.isEmpty()) return true;
                action = ActionType.getType(selectionAction);
                if (action == null) return true;
            }
            if (action.getArgumentsSlots().length == 0) return true;
            int maximumSlots = 0;
            List<Integer> ignored = new ArrayList<>();
            for (ArgumentSlot argument : action.getArgumentsSlots()) {
                if (argument.isParameter()) ignored.add(maximumSlots);
                maximumSlots += argument.getListSize();
            }
            Inventory inventory = holder.getInventory();
            for (int slot = 0; slot < maximumSlots; slot++) {
                if (slot >= inventory.getSize()) {
                    break;
                }
                if (ignored.contains(slot)) continue;
                if (inventory.getItem(slot) == null) {
                    inventory.setItem(slot, item);
                    player.getInventory().setItemInMainHand(null);
                    Sounds.DEV_INSERTED_IN_CONTAINER.play(player);
                    Layout layout = devPlanet.getOpenedMenu(inventory.getLocation());
                    if (layout != null && slot < layout.getArgsSlots().size()) {
                        layout.setItem(layout.getArgsSlots().get(slot), item);
                    }
                    devPlanet.addInsideCodeColumnChange(clickedBlock.getRelative(BlockFace.DOWN).getLocation());
                    return true;
                }
            }
            Sounds.DEV_NOT_ALLOWED.play(player);
            return true;
        }
        if (event.getAction() != Action.RIGHT_CLICK_BLOCK) return false;
        if (player.isSneaking()) return true;
        if (getItemType(player.getInventory().getItemInMainHand()).equals("lines_controller")) {
            event.setCancelled(true);
            Sounds.DEV_NOT_ALLOWED.play(player);
            return false;
        }
        Block actionBlock = clickedBlock.getRelative(BlockFace.DOWN);
        Block signBlock = actionBlock.getRelative(BlockFace.SOUTH);
        if (!(signBlock.getState() instanceof Sign sign)) return false;
        if (sign.getSide(Side.FRONT).lines().size() < 3) return false;
        String type = sign.getSide(Side.FRONT).getLine(2);
        try {
            ActionType action = ActionType.valueOf(type.toUpperCase());
            Layout layout = devPlanet.getOpenedMenu(clickedBlock.getLocation());
            event.setCancelled(true);
            if (!action.isChestRequired()) {
                Menus.onBlockDestroy(clickedBlock.getLocation());
                if (devPlanet.isDropItems() && clickedBlock.getState() instanceof InventoryHolder container) {
                    for (ItemStack item : container.getInventory().getContents()) {
                        if (item != null) {
                            if (item.getItemMeta() == null || !item.getItemMeta().getPersistentDataContainer().has(getCodingDoNotDropMeKey())) {
                                clickedBlock.getWorld().dropItem(clickedBlock.getLocation(), item);
                            }
                        }
                    }
                }
                clickedBlock.getWorld().spawnParticle(Particle.EXPLOSION, clickedBlock.getLocation(), 1);
                clickedBlock.setType(Material.AIR);
                return true;
            }
            if (layout == null) {
                layout = new LayoutMaker(action, clickedBlock);
                layout.open(player);
                devPlanet.registerOpenedMenu(clickedBlock.getLocation(), layout);
            } else {
                player.openInventory(layout.getInventory());
            }
            return true;
        } catch (IllegalArgumentException e) {
            player.sendActionBar(getLocaleComponent("coding-error.unknown-layout"));
            event.setCancelled(false);
            return true;
        }
    }

    /**
     * Handles event, when player clicks WALL_SIGN block.
     * Used for opening menus (executor, action type selection),
     * renaming (function, cycle), changing time (cycle),
     * reversing condition (if conditions).
     */
    private boolean handleSignClick(PlayerInteractEvent event, Player player, ItemStack currentItem, Block clickedBlock, DevPlanet devPlanet) {
        event.setCancelled(true);
        if (event.getAction() == Action.LEFT_CLICK_BLOCK) {
            translateBlockSign(clickedBlock);
        }
        if (currentItem.getType() == Material.COMPARATOR) {
            return false;
        }
        Block mainBlock = clickedBlock.getRelative(BlockFace.NORTH);
        ExecutorCategory mainBlockCategory = ExecutorCategory.getByMaterial(mainBlock.getType());
        ActionCategory actionBlockCategory = ActionCategory.getByMaterial(mainBlock.getType());
        if (currentItem.getType() == Material.ARROW && event.getHand() == EquipmentSlot.HAND) {
            if (actionBlockCategory != null && actionBlockCategory.isCondition() || actionBlockCategory == ActionCategory.SELECTION_ACTION) {
                /*
                 * We cancel changing NOT in selection action,
                 * when sign doesn't have specified condition type
                 * in third sign line, because we can't select
                 * ALL PLAYERS with NOT parameter, it's useless.
                 */
                if (actionBlockCategory == ActionCategory.SELECTION_ACTION && isSignLineEmpty(clickedBlock.getLocation(), (byte) 3)) {
                    return false;
                }
                if (actionBlockCategory == ActionCategory.ELSE_CONDITION) {
                    return false;
                }
                devPlanet.addInsideCodeColumnChange(clickedBlock.getRelative(BlockFace.NORTH).getLocation());
                if (isSignLineEmpty(clickedBlock.getLocation(), (byte) 1)) {
                    setSignLine(clickedBlock.getLocation(), (byte) 1, "not");
                    Sounds.DEV_CONDITION_NOT.play(player);
                } else {
                    setSignLine(clickedBlock.getLocation(), (byte) 1, "");
                    Sounds.DEV_CONDITION_DEFAULT.play(player);
                }
                translateBlockSign(clickedBlock);
            } else if (actionBlockCategory == ActionCategory.REPEAT_ACTION) {
                String type = getSignLine(clickedBlock.getLocation(), 1);
                if (type == null) return false;
                ActionType actionType = ActionType.getType(type);
                if (actionType == ActionType.REPEAT_WHILE) {
                    devPlanet.addInsideCodeColumnChange(clickedBlock.getLocation());
                    setSignLine(clickedBlock.getLocation(), (byte) 1, ActionType.REPEAT_WHILE_NOT.name().toLowerCase());
                    Sounds.DEV_CONDITION_NOT.play(player);
                    translateBlockSign(clickedBlock);
                } else if (actionType == ActionType.REPEAT_WHILE_NOT) {
                    devPlanet.addInsideCodeColumnChange(clickedBlock.getLocation());
                    setSignLine(clickedBlock.getLocation(), (byte) 1, ActionType.REPEAT_WHILE.name().toLowerCase());
                    Sounds.DEV_CONDITION_DEFAULT.play(player);
                    translateBlockSign(clickedBlock);
                } else {
                    return false;
                }
            }
        } else if (currentItem.getType() == Material.REDSTONE_TORCH && event.getHand() == EquipmentSlot.HAND) {
            if (mainBlockCategory == null) return false;
            Block torchBlock = mainBlock.getRelative(BlockFace.WEST);
            devPlanet.addChangedColumn(mainBlock.getLocation());
            if (torchBlock.getType() == Material.REDSTONE_WALL_TORCH) {
                torchBlock.setType(Material.AIR);
                Sounds.DEV_UNSET_DEBUG_TORCH.play(player);
            } else {
                placeDebugTorch(torchBlock.getLocation());
                Sounds.DEV_SET_DEBUG_TORCH.play(player);
            }
        } else if (player.isSneaking() && actionBlockCategory != null) {
            if (actionBlockCategory == ActionCategory.SELECTION_ACTION) {
                String selectionAction = getSignLine(clickedBlock.getLocation(), (byte) 4);
                switch (selectionAction) {
                    case "selection_set" -> {
                        setSignLine(clickedBlock.getLocation(), (byte) 4, "selection_add");
                        Sounds.DEV_ACTION_TARGET.play(player);
                    }
                    case "selection_add" -> {
                        setSignLine(clickedBlock.getLocation(), (byte) 4, "selection_remove");
                        Sounds.DEV_ACTION_TARGET.play(player);
                    }
                    case null, default -> {
                        setSignLine(clickedBlock.getLocation(), (byte) 4, "selection_set");
                        Sounds.DEV_ACTION_TARGET.play(player);
                    }
                }
                translateBlockSign(clickedBlock);
            } else if (actionBlockCategory != ActionCategory.ELSE_CONDITION) {
                new TargetSelectionMenu(clickedBlock.getLocation()).open(player);
            }
        } else {
            AbstractMenu menu = null;
            final Location clickedLocation = clickedBlock.getLocation();
            if (mainBlockCategory != null) {
                menu = switch (mainBlockCategory) {
                    case EVENT_WORLD, EVENT_ENTITY, EVENT_PLAYER ->
                            new BlocksCategorySelectionMenu(player, clickedLocation, mainBlockCategory);
                    default -> null;
                };
            } else if (actionBlockCategory != null) {
                menu = switch (actionBlockCategory) {
                    case PLAYER_ACTION, PLAYER_CONDITION, VARIABLE_ACTION,
                         VARIABLE_CONDITION, ENTITY_ACTION, ENTITY_CONDITION,
                         WORLD_ACTION, WORLD_CONDITION, CONTROL_ACTION,
                         REPEAT_ACTION, STRUCTURE_ACTION ->
                            new BlocksCategorySelectionMenu(player, clickedLocation, actionBlockCategory);
                    case SELECTION_ACTION -> new SelectionActionsMenu(player, clickedLocation);
                    case LAUNCH_FUNCTION_ACTION -> new FunctionChooserMenu(player, devPlanet, clickedLocation);
                    case LAUNCH_METHOD_ACTION -> new MethodChooserMenu(player, devPlanet, clickedLocation);
                    default -> null;
                };
            }
            if (menu != null) {
                menu.open(player);
                return true;
            } else if (mainBlockCategory == ExecutorCategory.STRUCTURE) {
                if (event.getAction().isRightClick()) {
                    ItemStack item = event.getPlayer().getInventory().getItemInMainHand();

                    if (!item.isEmpty() && item.hasItemMeta()) {
                        String displayName = ChatColor.stripColor(item.getItemMeta().getDisplayName());

                        if (displayName != null && displayName.length() < 15) {
                            setSignLine(clickedBlock.getLocation(), (byte) 3, displayName);

                            Sounds.DEV_CYCLE_NAMED.play(player);

                            translateBlockSign(clickedBlock);
                        }
                    } else {
                        player.sendMessage(ChatColor.AQUA + "Tip: " + ChatColor.GRAY + "Hold a renamed item to name this structure!");
                    }
                }
            } else if (mainBlockCategory == ExecutorCategory.FUNCTION || mainBlockCategory == ExecutorCategory.METHOD) {
                ItemStack item = event.getPlayer().getInventory().getItemInMainHand();
                if (!item.isEmpty() && item.hasItemMeta()) {
                    String displayName = ChatColor.stripColor(item.getItemMeta().getDisplayName());
                    if (displayName.length() < 15) {
                        setSignLine(clickedBlock.getLocation(), (byte) 3, displayName);
                        (mainBlockCategory == ExecutorCategory.FUNCTION ?
                                Sounds.DEV_FUNCTION_NAMED : Sounds.DEV_METHOD_NAMED).play(player);
                        translateBlockSign(clickedBlock);
                    }
                }
            }
        }
        return false;
    }

    private void handleDyeClick(PlayerInteractEvent event, Player player, ItemStack currentItem) {
        if (event.getAction() == Action.RIGHT_CLICK_AIR) {
            ItemMeta meta = currentItem.getItemMeta();
            if (meta == null || !meta.hasDisplayName()) {
                return;
            }
            Component displayName = meta.displayName();
            if (displayName != null) {
                player.sendMessage(displayName.hoverEvent(HoverEvent.showText(toComponent(getLocaleMessage("world.dev-mode.click-to-copy")))).clickEvent(ClickEvent.suggestCommand(ChatColor.stripColor(meta.getDisplayName()))));
                player.swingMainHand();
            }
        }
    }

    private void handleSlimeBallClick(PlayerInteractEvent event, Player player, ItemStack currentItem) {
        if (event.getAction() == Action.RIGHT_CLICK_AIR) {
            ItemMeta meta = currentItem.getItemMeta();
            if (meta == null || !meta.hasDisplayName()) {
                return;
            }
            Component displayName = meta.displayName();
            if (displayName != null) {
                player.sendMessage(displayName.hoverEvent(HoverEvent.showText(toComponent(getLocaleMessage("world.dev-mode.click-to-copy")))).clickEvent(ClickEvent.suggestCommand(ChatColor.stripColor(meta.getDisplayName()))));
                setPersistentData(currentItem, getCodingValueKey(), "NUMBER");
            }
        }
    }

    private void handleBookClick(PlayerInteractEvent event, Player player, ItemStack currentItem) {
        if (event.getAction() == Action.RIGHT_CLICK_AIR) {
            ItemMeta meta = currentItem.getItemMeta();
            if (meta == null || !meta.hasDisplayName()) {
                return;
            }
            Component displayName = meta.displayName();
            if (displayName != null) {
                player.sendMessage(displayName.hoverEvent(HoverEvent.showText(toComponent(getLocaleMessage("world.dev-mode.click-to-copy")))).clickEvent(ClickEvent.suggestCommand(meta.getDisplayName().replace("§", "&"))));
                setPersistentData(currentItem, getCodingValueKey(), "TEXT");
                player.swingMainHand();
            }
        }
    }

    private void handlePrismarineShardClick(PlayerInteractEvent event, Player player, ItemStack currentItem) {
        if (event.getAction() == Action.RIGHT_CLICK_AIR) {
            ItemMeta meta = currentItem.getItemMeta();
            if (meta == null || !meta.hasDisplayName()) return;
            if (player.isSneaking()) {
                String vectorString = ChatColor.stripColor(meta.getDisplayName());
                String[] coords = vectorString.split(" ");
                if (coords.length == 3) {
                    try {
                        double x, y, z;
                        x = Double.parseDouble(coords[0]);
                        y = Double.parseDouble(coords[1]);
                        z = Double.parseDouble(coords[2]);
                        player.setVelocity(new Vector(x, y, z));
                    } catch (Exception ignored) {}
                }
            } else {
                Component displayName = meta.displayName();
                if (displayName != null) {
                    player.sendMessage(displayName.hoverEvent(HoverEvent.showText(toComponent(getLocaleMessage("world.dev-mode.click-to-copy")))).clickEvent(ClickEvent.suggestCommand(ChatColor.stripColor(meta.getDisplayName()))));
                    setPersistentData(currentItem, getCodingValueKey(), "VECTOR");
                    player.swingMainHand();
                }
            }
        }
    }

    private void handleComparatorInteraction(PlayerInteractEvent event, Player player, Block clickedBlock) {
        if (clickedBlock.getType().name().contains("WALL_SIGN")) {
            clickedBlock = clickedBlock.getRelative(BlockFace.NORTH);
        }
        DevPlanet devPlanet = OpenCreative.getPlanetsManager().getDevPlanet(player);
        if (devPlanet == null) return;
        if (ActionCategory.getByMaterial(clickedBlock.getType()) != null) {
            if (event.getAction() == Action.RIGHT_CLICK_BLOCK) {
                if (move(clickedBlock.getRelative(BlockFace.WEST).getLocation(), BlockFace.EAST)) {
                    Sounds.DEV_MOVE_BLOCKS_RIGHT.play(player);
                } else {
                    Sounds.DEV_NOT_ALLOWED.play(player);
                }
                devPlanet.addInsideCodeColumnChange(clickedBlock.getLocation());
            } else if (event.getAction() == Action.LEFT_CLICK_BLOCK) {
                if (move(clickedBlock.getRelative(-2, 0, 0).getLocation(), BlockFace.WEST)) {
                    Sounds.DEV_MOVE_BLOCKS_LEFT.play(player);
                } else {
                    Sounds.DEV_NOT_ALLOWED.play(player);
                }
                devPlanet.addInsideCodeColumnChange(clickedBlock.getLocation());
            }
        } else if (ExecutorCategory.getByMaterial(clickedBlock.getType()) != null) {
            if (event.getAction() != Action.LEFT_CLICK_BLOCK && event.getAction() != Action.RIGHT_CLICK_BLOCK) {
                return;
            }
            if (player.hasCooldown(Material.COMPARATOR)) {
                return;
            }
            player.setCooldown(Material.COMPARATOR, 20);
            Location location = clickedBlock.getLocation();
            Set<Location> locations = devPlanet.getMarkedExecutors(player);
            int limit = OpenCreative.getSettings().getGroups().getGroup(player).getLimit(LimitType.SELECTED_LINES_AMOUNT).calculateLimit(1);
            if (locations.contains(location)) {
                devPlanet.unselectMarkedExecutor(player, location);
                player.sendActionBar(getLocaleMessage("menus.developer.manipulator.unmarked")
                        .replace("%amount%", String.valueOf(devPlanet.getMarkedExecutors(player).size()))
                        .replace("%limit%", String.valueOf(limit)));
                Sounds.DEV_UNMARK_EXECUTOR.play(player);
            } else {
                if (locations.size() >= limit) {
                    player.sendActionBar(getLocaleMessage("menus.developer.manipulator.limit")
                            .replace("%amount%", String.valueOf(devPlanet.getMarkedExecutors(player).size())));
                    Sounds.DEV_NOT_ALLOWED.play(player);
                    return;
                }
                devPlanet.markExecutorAsSelected(player, location);
                player.sendActionBar(getLocaleMessage("menus.developer.manipulator.marked")
                        .replace("%amount%", String.valueOf(devPlanet.getMarkedExecutors(player).size()))
                        .replace("%limit%", String.valueOf(limit)));
                Sounds.DEV_MARK_EXECUTOR.play(player);
            }
        } else {
            DevPlatform platform = devPlanet.getPlatformInLocation(clickedBlock.getLocation());
            if (platform == null) return;
            if (clickedBlock.getType() != platform.getEventMaterial()) return;
            if (player.hasCooldown(Material.COMPARATOR)) {
                return;
            }
            player.setCooldown(Material.COMPARATOR, 20);
            Set<Location> markedExecutors = devPlanet.getMarkedExecutors(player);
            if (markedExecutors.isEmpty()) {
                player.sendActionBar(getLocaleComponent("menus.developer.manipulator.not-selected"));
                Sounds.DEV_NOT_ALLOWED.play(player);
                return;
            }
            if (getCooldown(player, CooldownUtils.CooldownType.BLOCKS_DUPLICATION) > 0) {
                player.sendMessage(getLocaleMessage("cooldown").replace("%cooldown%", String.valueOf(getCooldown(player, CooldownUtils.CooldownType.BLOCKS_DUPLICATION))));
                return;
            }
            setCooldown(player, OpenCreative.getSettings().getGroups().getGroup(player)
                    .getBlocksDuplicationCooldown(), CooldownUtils.CooldownType.BLOCKS_DUPLICATION);
            CodeStorage temporary = new CodeConfiguration();
            new CodingBlockParser(devPlanet, true).parseExecutors(devPlanet, temporary, new LinkedHashSet<>(markedExecutors));
            devPlanet.clearMarkedExecutors(player);
            ConfigurationSection section = temporary.getSection("code.blocks");
            if (section == null) return;
            CodingBlockPlacer.CodePlacementResult result = new CodingBlockPlacer(devPlanet).placeCodingLines(devPlanet, section, clickedBlock.getLocation());
            if (result.getType() == CodingBlockPlacer.CodePlacementResult.Type.NOT_ENOUGH_SPACE) {
                player.sendMessage(getLocaleMessage("environment.duplication.few-space")
                        .replace("%required%", String.valueOf(markedExecutors.size())));
                Sounds.DEV_NOT_ALLOWED.play(player);
            } else if (result.getType() == CodingBlockPlacer.CodePlacementResult.Type.ERROR) {
                player.sendMessage(getLocaleMessage("environment.duplication.error"));
                for (Location placedExecutor : result.getPlacedColumns()) {
                    devPlanet.addChangedColumn(placedExecutor);
                }
                Sounds.PLAYER_ERROR.play(player);
            } else {
                player.sendMessage(getLocaleMessage("environment.duplication.success"));
                for (Location placedExecutor : result.getPlacedColumns()) {
                    devPlanet.addChangedColumn(placedExecutor);
                }
                Sounds.DEV_BLOCKS_DUPLICATED.play(player);
            }
        }
    }

    private void handlePaperInteraction(PlayerInteractEvent event, Player player, ItemStack currentItem) {
        if (event.getAction() == Action.LEFT_CLICK_AIR && !player.hasCooldown(currentItem.getType())) {
            Planet planet = OpenCreative.getPlanetsManager().getPlanetByPlayer(player);
            if (planet != null) {
                addPlayerWithLocation(player);
                player.teleport(planet.getTerritory().getSpawnLocation());
                player.getInventory().setItemInMainHand(currentItem); // Fix for Multi-Verse Inventories
                Sounds.DEV_LOCATION_TELEPORT.play(player);
                player.setCooldown(currentItem.getType(), 60);
            }
        }
    }

    private void handleMagmaCreamInteraction(PlayerInteractEvent event, Player player, ItemStack currentItem) {
        /*
         * We use RIGHT_CLICK_AIR to prevent
         * accidental value change on container click.
         */
        if (event.getAction() != Action.RIGHT_CLICK_AIR) {
            return;
        }
        if (currentItem.getItemMeta() == null || !currentItem.getItemMeta().hasDisplayName()) {
            return;
        }
        event.setCancelled(true);
        ItemMeta meta = currentItem.getItemMeta();
        VariableLink.VariableType type = getVariableType(meta);
        meta.setDisplayName(type.getColor() + ChatColor.stripColor(meta.getDisplayName()));
        player.showTitle(Title.title(
                meta.displayName(), Component.text(type.getLocalized()),
                Title.Times.times(Duration.ofSeconds(0), Duration.ofSeconds(2), Duration.ofSeconds(1))
        ));
        currentItem.setItemMeta(meta);
        setPersistentData(currentItem, getCodingValueKey(), "VARIABLE");
        setPersistentData(currentItem, getCodingVariableTypeKey(), type.name());
        Sounds.DEV_VARIABLE_CHANGE.play(player);
        player.swingMainHand();
        player.sendMessage(Component.text(meta.getDisplayName())
                .clickEvent(ClickEvent.suggestCommand(ChatColor.stripColor(meta.getDisplayName()))));
    }

    private void handleFeatherInteraction(PlayerInteractEvent event, Player player, ItemStack currentItem) {
        if (currentItem.getItemMeta() == null || !currentItem.getItemMeta().hasDisplayName()) {
            return;
        }
        if (!currentItem.getItemMeta().getDisplayName().equals(getLocaleItemName("items.developer.fly-speed-changer.name"))) {
            return;
        }
        currentItem.setAmount((currentItem.getAmount() > 3 ? 1 : currentItem.getAmount() + 1));
        float currentSpead = currentItem.getAmount() + 1;
        float speed = ((currentSpead - 1) / 9) * (1 - 0.1f);
        if (speed > 1) {
            speed = currentSpead * 0.1f;
        }
        player.setFlying(true);
        player.setFlySpeed(speed);
        player.sendActionBar(getLocaleMessage("world.dev-mode.changed-fly-speed").replace("%speed%", String.valueOf(currentItem.getAmount())));
        Sounds.DEV_FLY_SPEED_CHANGE.play(player);
    }

    private void handleClockInteraction(PlayerInteractEvent event, Player player, ItemStack currentItem) {
        /*
         * We use RIGHT_CLICK_AIR to prevent
         * accidental value change on container click.
         */
        if (event.getAction() != Action.RIGHT_CLICK_AIR) {
            return;
        }
        boolean value = false;
        ItemMeta meta = currentItem.getItemMeta();
        if (meta != null && meta.hasDisplayName()) {
            value = meta.getDisplayName().contains("true");
        }
        String displayName = ChatColor.translateAlternateColorCodes('&', !value ? "&atrue" : "&cfalse");
        setDisplayName(currentItem, displayName);
        (!value ? Sounds.DEV_BOOLEAN_TRUE : Sounds.DEV_BOOLEAN_FALSE).play(player);
        player.swingMainHand();
        setPersistentData(currentItem, getCodingValueKey(), "BOOLEAN");
        player.showTitle(Title.title(
                toComponent(getLocaleMessage("world.dev-mode.set-variable")), Component.text(displayName),
                Title.Times.times(Duration.ofMillis(250), Duration.ofSeconds(2), Duration.ofMillis(750))
        ));
    }

    private void setPaperLocation(PlayerInteractEvent event, Player player, ItemStack currentItem) {
        Planet planet = OpenCreative.getPlanetsManager().getPlanetByPlayer(player);
        if (isPlayerWithLocation(player) && planet != null && !planet.getWorldPlayers().canBuild(player)) {
            event.setCancelled(true);
        }
        if (currentItem.getType() != Material.PAPER || !isPlayerWithLocation(player) || player.hasCooldown(currentItem.getType())) {
            return;
        }
        if (event.getAction().isRightClick()) {
            Block clickedBlock = event.getClickedBlock();
            Location location = player.getLocation();
            if (clickedBlock != null) {
                location = clickedBlock.getLocation();
            }
            String locationString = formatLocation(location);
            setDisplayName(currentItem, locationString);
            player.showTitle(Title.title(
                    toComponent(getLocaleMessage("world.dev-mode.set-variable")), Component.text(locationString),
                    Title.Times.times(Duration.ofMillis(250), Duration.ofSeconds(2), Duration.ofMillis(750))
            ));
            setPersistentData(currentItem, getCodingValueKey(), "LOCATION");
            spawnGlowingBlock(player, location);
            Sounds.DEV_LOCATION_SET.play(player);
        } else if (event.getAction() == Action.LEFT_CLICK_AIR) {
            if (planet != null && planet.getDevPlanet().isLoaded()) {
                player.setCooldown(currentItem.getType(), 60);
                player.teleportAsync(getOldLocationPlayerWithLocation(player)).thenAccept(success -> {
                    Sounds.DEV_LOCATION_TELEPORT_BACK.play(player);
                    player.getInventory().setItemInMainHand(currentItem); // Fix for Multi-Verse Inventories
                    for (Player developer : planet.getDevPlanet().getWorld().getPlayers()) {
                        WorldBorder border = Bukkit.createWorldBorder();
                        border.setCenter(planet.getDevPlanet().getWorld().getWorldBorder().getCenter());
                        border.setSize(planet.getDevPlanet().getWorld().getWorldBorder().getSize() * 5);
                        developer.setWorldBorder(border);
                    }
                    new BukkitRunnable() {
                        @Override
                        public void run() {
                            translateSigns(player, 10);
                        }
                    }.runTaskLater(OpenCreative.getPlugin(), 5L);
                });
            }
        }
    }

    @EventHandler
    public void onCompass(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        ItemStack currentItem = player.getInventory().getItemInMainHand();
        if (!event.getAction().isRightClick()) {
            return;
        }
        if (player.hasCooldown(currentItem.getType())) {
            return;
        }
        Planet planet = OpenCreative.getPlanetsManager().getPlanetByPlayer(player);
        if (isEntityInLobby(player)) {
            if (getItemType(currentItem).equals("worlds")) {
                // Opens recommended worlds menus.
                if (OpenCreative.getSettings().isMaintenance() && !player.hasPermission("opencreative.maintenance.bypass")) {
                    player.sendMessage(getLocaleMessage("maintenance"));
                    return;
                }
                if (OpenCreative.getStability().isVeryBad() && !player.hasPermission("opencreative.stability.bypass")) {
                    player.sendMessage(getLocaleMessage("creative.stability.cannot"));
                    return;
                }
                player.setCooldown(currentItem.getType(), 60);
                new WorldsCompassMenu().open(player);
            } else if (getItemType(currentItem).equals("own_worlds")) {
                // Opens player's worlds menus.
                if (OpenCreative.getSettings().isMaintenance() && !player.hasPermission("opencreative.maintenance.bypass")) {
                    player.sendMessage(getLocaleMessage("maintenance"));
                    return;
                }
                if (OpenCreative.getStability().isVeryBad() && !player.hasPermission("opencreative.stability.bypass")) {
                    player.sendMessage(getLocaleMessage("creative.stability.cannot"));
                    return;
                }
                player.setCooldown(currentItem.getType(), 60);
                new OwnWorldsBrowserMenu(player).open(player);
            }
        } else if (planet != null && getItemType(currentItem).equals("world_settings")) {
            // Opens world settings menus.
            if (OpenCreative.getSettings().isMaintenance() && !player.hasPermission("opencreative.maintenance.bypass")) {
                player.sendMessage(getLocaleMessage("maintenance"));
                return;
            }
            if (OpenCreative.getStability().isVeryBad() && !player.hasPermission("opencreative.stability.bypass")) {
                player.sendMessage(getLocaleMessage("creative.stability.cannot"));
                return;
            }
            if (planet.isOwner(player)) {
                player.setCooldown(currentItem.getType(), 60);
                new WorldSettingsMenu(planet, player).open(player);
            }
        }
    }

    @EventHandler
    public void onSignEdit(SignChangeEvent event) {
        Planet planet = OpenCreative.getPlanetsManager().getPlanetByPlayer(event.getPlayer());
        if (planet != null) {
            if (OpenCreative.getSettings().shouldLogSignEdits()) {
                PlainTextComponentSerializer plainSerializer = PlainTextComponentSerializer.plainText();
                StringBuilder content = new StringBuilder();
                for (Component line : event.lines()) {
                    content.append(plainSerializer.serialize(line));
                }
                if (content.isEmpty()) {
                    new ChangedSignEvent(event.getPlayer(), event).callEvent();
                    return;
                }
                OpenCreative.getPlugin().getLogger().info("[SIGN: " + planet.getId() + (isEntityInDevPlanet(event.getPlayer()) ? "dev" : "")
                        + "] Sign edited by " + event.getPlayer().getName() + ": " + content
                        + " (" + event.getBlock().getX() + ", " + event.getBlock().getY() + ", " + event.getBlock().getZ() + ")");
            }
            new ChangedSignEvent(event.getPlayer(), event).callEvent();
        }
    }

    @EventHandler
    public void onUsing(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        if (isEntityInLobby(player) && OpenCreative.getSettings().getLobbySettings().isSpawningMobsDisallowed()
                && !player.hasPermission("opencreative.lobby.spawning-mobs.bypass")) {
            ItemStack item = event.getItem();
            if (item == null) return;
            if (!(item.getItemMeta() instanceof SpawnEggMeta) && item.getType() != Material.ITEM_FRAME
                    && item.getType() != Material.GLOW_ITEM_FRAME && item.getType() != Material.PAINTING) {
                return;
            }
            event.setCancelled(true);
            player.sendActionBar(getLocaleMessage("not-for-lobby"));
            return;
        }
        Planet planet = OpenCreative.getPlanetsManager().getPlanetByPlayer(player);
        if (planet == null) {
            return;
        }
        if (OpenCreative.getPlanetsManager().getDevPlanet(player) != null) {
            return;
        }
        switch (event.getAction()) {
            case LEFT_CLICK_AIR -> new LeftClickEvent(player, event).callEvent();
            case LEFT_CLICK_BLOCK -> {
                new LeftClickEvent(player, event).callEvent();
                new BlockInteractionEvent(player, event).callEvent();
            }
            case PHYSICAL -> new WorldInteractEvent(player, event).callEvent();
            case RIGHT_CLICK_AIR -> {
                if (event.getHand() == EquipmentSlot.HAND) {
                    new RightClickEvent(player, event).callEvent();
                }
            }
            case RIGHT_CLICK_BLOCK -> {
                if (event.getHand() == EquipmentSlot.HAND) {
                    new RightClickEvent(player, event).callEvent();
                    new BlockInteractionEvent(player, event).callEvent();
                }
            }
        }
        if (event.getClickedBlock() == null) {
            return;
        }
        if (event.getAction() == Action.RIGHT_CLICK_BLOCK || event.getAction() == Action.PHYSICAL) {
            if (planet.getWorldPlayers().canBuild(player)) {
                return;
            }
            switch (planet.getFlagValue(PlanetFlags.PlanetFlag.BLOCK_INTERACT)) {
                // Disallow every block interact.
                case 2 -> {
                    player.sendActionBar(getLocaleMessage("world.cant-block-interact"));
                    event.setCancelled(true);
                }
                // Disallow changing comparator, repeater, note block.
                case 3 -> {
                    if (event.getClickedBlock().getType() == Material.COMPARATOR || event.getClickedBlock().getType() == Material.REPEATER || event.getClickedBlock().getType() == Material.NOTE_BLOCK) {
                        player.sendActionBar(getLocaleMessage("world.cant-block-interact"));
                        event.setCancelled(true);
                    }
                }
                // Disallow changing doors and chests.
                case 4 -> {
                    if (event.getClickedBlock().getType() == Material.CHEST || event.getClickedBlock().getType().name().contains("DOOR")) {
                        player.sendActionBar(getLocaleMessage("world.cant-block-interact"));
                        event.setCancelled(true);
                    }
                }
                // Allow interacting only with buttons, plates, levers
                case 5 -> {
                    if (!(event.getClickedBlock().getType().name().contains("BUTTON") || event.getClickedBlock().getType().name().contains("PRESSURE_PLATE") || event.getClickedBlock().getType() == Material.LEVER)) {
                        player.sendActionBar(getLocaleMessage("world.cant-block-interact"));
                        event.setCancelled(true);
                    }
                }
            }
        }
    }

    @EventHandler
    public void onMobClick(PlayerInteractEntityEvent event) {
        Player player = event.getPlayer();
        Planet planet = OpenCreative.getPlanetsManager().getPlanetByPlayer(player);
        if (planet != null) {
            if (planet.getFlagValue(PlanetFlags.PlanetFlag.MOB_INTERACT) == 2 && !planet.getWorldPlayers().canBuild(player)) {
                // Disallow entire mob interaction.
                event.getPlayer().sendActionBar(getLocaleMessage("world.cant-mob-interact"));
                event.setCancelled(true);
            }
            if (planet.getFlagValue(PlanetFlags.PlanetFlag.MOB_INTERACT) == 3 && !planet.getWorldPlayers().canBuild(player)) {
                // Disallow changing item frames and armor stands.
                if (event.getRightClicked().getType().name().contains("FRAME") || event.getRightClicked().getType() == EntityType.ARMOR_STAND) {
                    event.getPlayer().sendActionBar(getLocaleMessage("world.cant-mob-interact"));
                    event.setCancelled(true);
                }
            }
        }
    }

    @EventHandler
    public void onMobClick(PlayerInteractAtEntityEvent event) {
        Player player = event.getPlayer();
        Planet planet = OpenCreative.getPlanetsManager().getPlanetByPlayer(player);
        if (planet != null) {
            if (event.getHand() == EquipmentSlot.HAND) {
                new MobInteractionEvent(player, event).callEvent();
            }
            if (planet.getFlagValue(PlanetFlags.PlanetFlag.MOB_INTERACT) == 2 && !planet.getWorldPlayers().canBuild(player)) {
                // Disallow entire mob interaction.
                event.getPlayer().sendActionBar(getLocaleMessage("world.cant-mob-interact"));
                event.setCancelled(true);
            }
            if (planet.getFlagValue(PlanetFlags.PlanetFlag.MOB_INTERACT) == 3 && !planet.getWorldPlayers().canBuild(player)) {
                // Disallow changing item frames and armor stands.
                if (event.getRightClicked().getType().name().contains("FRAME") || event.getRightClicked().getType() == EntityType.ARMOR_STAND) {
                    event.getPlayer().sendActionBar(getLocaleMessage("world.cant-mob-interact"));
                    event.setCancelled(true);
                }
            }
        }
    }

    @EventHandler
    public void onHang(HangingBreakByEntityEvent event) {
        if (!(event.getRemover() instanceof Player player)) return;
        if (isEntityInLobby(player) && OpenCreative.getSettings().getLobbySettings().isDestroyingBlocksDisallowed()
                && !player.hasPermission("opencreative.lobby.destroying-blocks.bypass")) {
            event.setCancelled(true);
            player.sendActionBar(getLocaleMessage("not-for-lobby"));
            return;
        }
        Planet planet = OpenCreative.getPlanetsManager().getPlanetByPlayer(player);
        if (planet != null) {
            new MobInteractionEvent(player, event).callEvent();
            if (planet.getFlagValue(PlanetFlags.PlanetFlag.MOB_INTERACT) == 2 && !planet.getWorldPlayers().canBuild(player)) {
                player.sendActionBar(getLocaleMessage("world.cant-mob-interact"));
                event.setCancelled(true);
            }
            if (planet.getFlagValue(PlanetFlags.PlanetFlag.MOB_INTERACT) == 3 && !planet.getWorldPlayers().canBuild(player)) {
                if (event.getEntity().getType().name().contains("FRAME")) {
                    player.sendActionBar(getLocaleMessage("world.cant-mob-interact"));
                    event.setCancelled(true);
                }
            }
        }
    }

    @EventHandler
    public void onFishing(PlayerFishEvent event) {
        if (event.getCaught() == null) {
            return;
        }
        if (event.getCaught().getType() == EntityType.ITEM) {
            Planet planet = OpenCreative.getPlanetsManager().getPlanetByPlayer(event.getPlayer());
            if (planet != null) new FishEvent(event.getPlayer(), event).callEvent();
        }
    }

    @EventHandler
    public void onSpectating(PlayerStartSpectatingEntityEvent event) {
        Planet planet = OpenCreative.getPlanetsManager().getPlanetByPlayer(event.getPlayer());
        if (planet != null) new StartSpectatingEvent(event.getPlayer()).callEvent();
    }

    @EventHandler
    public void onSpectatingStop(PlayerStopSpectatingEntityEvent event) {
        Planet planet = OpenCreative.getPlanetsManager().getPlanetByPlayer(event.getPlayer());
        if (planet != null) new StopSpectatingEvent(event.getPlayer()).callEvent();
    }

    @EventHandler
    public void onBedInteract(PlayerBedEnterEvent event) {
        Planet planet = OpenCreative.getPlanetsManager().getPlanetByPlayer(event.getPlayer());
        if (planet != null) new BedEnterEvent(event.getPlayer(), event).callEvent();
    }

    @EventHandler
    public void onBedInteract(PlayerBedLeaveEvent event) {
        Planet planet = OpenCreative.getPlanetsManager().getPlanetByPlayer(event.getPlayer());
        if (planet != null) new BedLeaveEvent(event.getPlayer(), event).callEvent();
    }

    @EventHandler
    public void onBucketFill(PlayerBucketFillEvent event) {
        Planet planet = OpenCreative.getPlanetsManager().getPlanetByPlayer(event.getPlayer());
        if (planet != null) new BucketFillEvent(event.getPlayer(), event).callEvent();
    }

    @EventHandler
    public void onBucketEmpty(PlayerBucketEmptyEvent event) {
        Planet planet = OpenCreative.getPlanetsManager().getPlanetByPlayer(event.getPlayer());
        if (planet != null) new BucketEmptyEvent(event.getPlayer(), event).callEvent();
    }

    @EventHandler
    public void onBucketEntity(PlayerBucketEntityEvent event) {
        Planet planet = OpenCreative.getPlanetsManager().getPlanetByPlayer(event.getPlayer());
        if (planet != null) new BucketEntityEvent(event.getPlayer(), event).callEvent();
    }

    @EventHandler
    public void onEntityRename(PlayerNameEntityEvent event) {
        if (event.getName() == null) return;
        String text = PlainTextComponentSerializer.plainText().serialize(event.getName());
        int limit = OpenCreative.getSettings().getItemFixerSettings().getMaxEntityNameLength();
        if (text.length() > limit) {
            event.setName(PlainTextComponentSerializer.plainText()
                    .deserialize(text.substring(0, limit)));
        }
    }
}
