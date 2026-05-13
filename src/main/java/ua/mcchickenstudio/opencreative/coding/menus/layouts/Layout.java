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

package ua.mcchickenstudio.opencreative.coding.menus.layouts;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.BlockState;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;
import ua.mcchickenstudio.opencreative.OpenCreative;
import ua.mcchickenstudio.opencreative.coding.blocks.actions.ActionType;
import ua.mcchickenstudio.opencreative.coding.variables.ValueType;
import ua.mcchickenstudio.opencreative.menus.AbstractMenu;
import ua.mcchickenstudio.opencreative.menus.buttons.ParameterButton;
import ua.mcchickenstudio.opencreative.planets.DevPlanet;
import ua.mcchickenstudio.opencreative.settings.Sounds;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static ua.mcchickenstudio.opencreative.utils.ItemUtils.*;
import static ua.mcchickenstudio.opencreative.utils.PlayerUtils.sendClosedChestAnimation;
import static ua.mcchickenstudio.opencreative.utils.PlayerUtils.sendOpenedChestAnimation;

/**
 * <h1>Layout</h1>
 * This class represents a inventory menu, that opens
 * when player clicks on coding container to fill arguments.
 *
 * @see LayoutMaker
 */
public abstract class Layout extends AbstractMenu {

    private final Block containerBlock;
    private final Set<Player> viewers = new HashSet<>();

    protected final ActionType actionType;
    protected final List<Integer> argsSlots = new ArrayList<>();
    protected final List<ParameterButton> parameterButtons = new ArrayList<>();
    protected final ArgumentSlot[] requiredSlots;

    private final InventoryHolder containerHolder;

    private int currentSlot = 0;

    /**
     * Creates a coding container layout menu.
     *
     * @param rows           amount of rows in layout.
     * @param actionType     type of action, that has arguments.
     * @param containerBlock container block.
     */
    public Layout(int rows, @NotNull ActionType actionType, @NotNull Block containerBlock) {
        super(rows, ChatColor.stripColor(actionType.getLocaleName()));
        this.actionType = actionType;
        this.containerBlock = containerBlock;
        this.requiredSlots = actionType.getArgumentsSlots();
        if (containerBlock.getState() instanceof InventoryHolder holder) {
            containerHolder = holder;
        } else {
            containerHolder = null;
        }
    }

    @Override
    public void fillItems(Player player) {
        fillArgumentItems();
    }

    /**
     * Fills menu with argument glasses and values.
     *
     * @see #setArgSlot(int, int...)
     * @see #setGlass(int, int...)
     * @see #setArgSlotHorizontal(int, int)
     * @see #setArgSlotVertical(int, int)
     */
    protected abstract void fillArgumentItems();

    /**
     * Returns argument value item from coding container.
     *
     * @param slot slot of item inside coding container.
     * @return value item, or empty item.
     */
    protected @NotNull ItemStack getArgumentValueFromContainer(int slot) {
        if (containerHolder == null) return ItemStack.empty();
        if (slot < 0 || slot >= containerHolder.getInventory().getContents().length) {
            // If slot is illegal
            return ItemStack.empty();
        }
        ItemStack item = containerHolder.getInventory().getContents()[slot];
        return item != null ? item : ItemStack.empty();
    }

    @Override
    public void onClick(@NotNull InventoryClickEvent event) {
        if (!isClickedInMenuSlots(event) || !isPlayerClicked(event)) {
            return;
        }
        ItemStack currentItem = event.getCursor();
        if (argsSlots.contains(event.getRawSlot())) {
            ItemStack argItem = inventory.getItem(event.getRawSlot());
            for (ParameterButton parameter : parameterButtons) {
                if (itemEquals(argItem, parameter.getItem())) {
                    event.setCancelled(true);
                    if (getValueType(currentItem) == ValueType.VARIABLE) {
                        inventory.setItem(event.getRawSlot(), currentItem);
                        Sounds.DEV_VARIABLE_PARAMETER.play(event.getWhoClicked());
                    } else {
                        parameter.next();
                        Sounds.DEV_NEXT_PARAMETER.play(event.getWhoClicked());
                        inventory.setItem(event.getRawSlot(), parameter.getItem());
                    }
                }
            }
        } else {
            event.setCancelled(true);
        }
    }

    @Override
    public void onOpen(@NotNull InventoryOpenEvent event) {
        viewers.add((Player) event.getPlayer());
        (containerBlock.getType() == Material.BARREL ? Sounds.DEV_OPEN_BARREL : Sounds.DEV_OPEN_CHEST).play(event.getPlayer());
        for (Player onlinePlayer : event.getPlayer().getWorld().getPlayers()) {
            sendOpenedChestAnimation(onlinePlayer, containerBlock);
        }
    }

    @Override
    public final void onClose(@NotNull InventoryCloseEvent event) {
        saveArgumentsItems();
        (containerBlock.getType() == Material.BARREL ? Sounds.DEV_CLOSED_BARREL : Sounds.DEV_CLOSED_CHEST).play(event.getPlayer());
        viewers.remove((Player) event.getPlayer());
        if (viewers.isEmpty()) {
            DevPlanet devPlanet = OpenCreative.getPlanetsManager().getDevPlanet((Player) event.getPlayer());
            if (devPlanet != null) {
                devPlanet.unregisterOpenedMenu(containerBlock.getLocation());
                for (Player onlinePlayer : event.getPlayer().getWorld().getPlayers()) {
                    sendClosedChestAnimation(onlinePlayer, containerBlock);
                }
            }
            destroy();
        }
    }

    /**
     * Saves arguments items into coding container.
     */
    private void saveArgumentsItems() {
        if (containerHolder == null) {
            // If container is destroyed, not saving items.
            return;
        }
        DevPlanet devPlanet = OpenCreative.getPlanetsManager().getDevPlanet(containerBlock.getWorld());
        if (devPlanet != null) {
            devPlanet.addInsideCodeColumnChange(containerBlock.getRelative(BlockFace.DOWN).getLocation());
        }
        int chestSlot = 0;
        for (int argSlot : argsSlots) {
            ItemStack argItem = inventory.getItem(argSlot);
            containerHolder.getInventory().setItem(chestSlot, argItem);
            for (ParameterButton rb : parameterButtons) {
                if (argItem == null) continue;
                ItemStack itemStack = argItem.clone();
                for (ItemFlag flag : itemStack.getItemFlags()) {
                    itemStack.removeItemFlags(flag);
                }
                if (itemStack.equals(rb.getItem(true))) {
                    if (itemStack.hasItemMeta()) {
                        ItemMeta itemMeta;
                        if (rb.getCurrentValue() instanceof Integer) {
                            itemStack.setType(Material.SLIME_BALL);
                            itemMeta = itemStack.getItemMeta();
                            itemMeta.setDisplayName(ChatColor.translateAlternateColorCodes('&', "&a") + rb.getCurrentValue() + ".0");
                        } else if (rb.getCurrentValue() instanceof Boolean) {
                            boolean value = (boolean) rb.getCurrentValue();
                            itemStack.setType(Material.CLOCK);
                            itemMeta = itemStack.getItemMeta();
                            itemMeta.setDisplayName(ChatColor.translateAlternateColorCodes('&', "&" + (value ? "a" : "c") + value));
                        } else {
                            itemStack.setType(Material.BOOK);
                            itemMeta = itemStack.getItemMeta();
                            itemMeta.setDisplayName(rb.getCurrentValue().toString());
                        }
                        itemMeta.lore(null);
                        itemStack.setItemMeta(itemMeta);
                        ValueType valueType = ValueType.getByMaterial(itemStack.getType());
                        if (valueType == null) valueType = ValueType.TEXT;
                        setPersistentData(itemStack, getCodingValueKey(), valueType.name());
                        setPersistentData(itemStack, getCodingDoNotDropMeKey(), "1");
                        containerHolder.getInventory().setItem(chestSlot, itemStack);
                    }
                }
            }
            chestSlot++;
        }
        containerBlock.getState().update(true);
    }

    /**
     * Sets argument slot with vertical glass panes in slot.
     *
     * @param argNumber number of argument.
     * @param slot      slot to put item.
     */
    protected void setArgSlotVertical(int argNumber, int slot) {
        ArgumentSlot argumentSlot = requiredSlots[argNumber - 1];
        ItemStack glassItem = argumentSlot.getVarType().getGlassItem(actionType, argumentSlot.getPath());
        setItem(glassItem, slot - 9, slot + 9);
        setArgSlot(argNumber, slot);
    }

    /**
     * Sets argument slot with horizontal glass panes in slot.
     *
     * @param argNumber number of argument.
     * @param slot      slot to put item.
     */
    protected void setArgSlotHorizontal(int argNumber, int slot) {
        ArgumentSlot argumentSlot = requiredSlots[argNumber - 1];
        ItemStack glassItem = argumentSlot.getVarType().getGlassItem(actionType, argumentSlot.getPath());
        setItem(glassItem, slot - 1, slot + 1);
        setArgSlot(argumentSlot, slot);
    }

    /**
     * Sets argument slot with vertical and horizontal glass panes in slot.
     *
     * @param argNumber number of argument.
     * @param slot      slot to put item.
     */
    protected void setArgSlotCross(int argNumber, int slot) {
        ArgumentSlot argumentSlot = requiredSlots[argNumber - 1];
        ItemStack glassItem = argumentSlot.getVarType().getGlassItem(actionType, argumentSlot.getPath());
        setItem(glassItem, slot - 9, slot - 1, slot + 1, slot + 9);
        setArgSlot(argumentSlot, slot);
    }

    /**
     * Sets glass pane in slot.
     *
     * @param argNumber number of argument.
     * @param slots     slots to put glass pane.
     */
    protected void setGlass(int argNumber, int... slots) {
        ArgumentSlot argumentSlot = requiredSlots[argNumber - 1];
        setItem(argumentSlot.getVarType().getGlassItem(actionType, argumentSlot.getPath()), slots);
    }

    /**
     * Sets argument slot in slot.
     *
     * @param argNumber number of argument.
     * @param slots     slots to put argument slot.
     */
    protected void setArgSlot(int argNumber, int... slots) {
        ArgumentSlot argumentSlot = requiredSlots[argNumber - 1];
        setArgSlot(argumentSlot, slots);
    }

    /**
     * Sets argument slot in slot.
     *
     * @param argumentSlot required argument.
     * @param slots        slots to put argument slot.
     */
    private void setArgSlot(@NotNull ArgumentSlot argumentSlot, int... slots) {
        for (int slot : slots) {
            ItemStack contentItem = containerHolder == null ? ItemStack.empty() : getArgumentValueFromContainer(currentSlot++);
            if (argumentSlot.isParameter()) {
                Object value = "";
                if (!contentItem.isEmpty() && contentItem.hasItemMeta()) {
                    String display = ChatColor.stripColor(contentItem.getItemMeta().getDisplayName());
                    if (contentItem.getType() == Material.SLIME_BALL) {
                        value = Integer.parseInt(display.replace(".0", ""));
                    } else if (contentItem.getType() == Material.CLOCK) {
                        value = Boolean.parseBoolean(display);
                    } else {
                        value = display;
                    }
                }
                ParameterButton rb = createParamButton((ParameterSlot) argumentSlot, value);
                if (!contentItem.isEmpty() && getValueType(contentItem) == ValueType.VARIABLE) {
                    setItem(contentItem, slot);
                } else {
                    setItem(rb.getItem(), slot);
                }
                parameterButtons.add(rb);
            } else {
                setItem(contentItem, slot);
            }
            argsSlots.add(slot);
        }
    }

    /**
     * Returns list of slots, items from will be saved
     * into coding container after closing menu.
     *
     * @return list of slots with argument items.
     */
    public @NotNull List<Integer> getArgsSlots() {
        return argsSlots;
    }

    /**
     * Creates and returns parameter button.
     *
     * @param parameter parameter with info.
     * @param value     current value.
     * @return parameter button.
     */
    protected @NotNull ParameterButton createParamButton(@NotNull ParameterSlot parameter, Object value) {
        String path = "items.developer." + (actionType.isCondition() ? "conditions" : "actions") + "." + actionType.name().toLowerCase().replace("_", "-") + ".arguments." + parameter.getPath();
        return new ParameterButton(value, parameter.getValues(), parameter.getPath(), "items.developer", path, parameter.getIcons());
    }

    /**
     * Returns list of slots, that will be used to display
     * a row of items in more beautiful place.
     *
     * @param count count of items in row (1-9)
     * @param row   row (1-6)
     * @return list of centered slots.
     */
    protected @NotNull List<Integer> getCentredSlots(int count, int row) {
        List<Integer> slots = new ArrayList<>();
        switch (count) {
            case 1:
                slots.add((row * 9 - 5));
                break;
            case 2:
                slots.add((row * 9 - 7));
                slots.add((row * 9 - 3));
                break;
            case 3:
                slots.add((row * 9 - 8));
                slots.add((row * 9 - 5));
                slots.add((row * 9 - 2));
                break;
            case 4:
                slots.add((row * 9 - 8));
                slots.add((row * 9 - 6));
                slots.add((row * 9 - 4));
                slots.add((row * 9 - 2));
                break;
            case 5:
                slots.add((row * 9 - 9));
                slots.add((row * 9 - 7));
                slots.add((row * 9 - 5));
                slots.add((row * 9 - 3));
                slots.add((row * 9 - 1));
                break;
            case 6:
                slots.add((row * 9 - 8));
                slots.add((row * 9 - 7));
                slots.add((row * 9 - 6));
                slots.add((row * 9 - 4));
                slots.add((row * 9 - 3));
                slots.add((row * 9 - 2));
                break;
            case 7:
                slots.add((row * 9 - 8));
                slots.add((row * 9 - 7));
                slots.add((row * 9 - 6));
                slots.add((row * 9 - 5));
                slots.add((row * 9 - 4));
                slots.add((row * 9 - 3));
                slots.add((row * 9 - 2));
                break;
            case 8:
                slots.add((row * 9 - 9));
                slots.add((row * 9 - 8));
                slots.add((row * 9 - 7));
                slots.add((row * 9 - 6));
                slots.add((row * 9 - 4));
                slots.add((row * 9 - 3));
                slots.add((row * 9 - 2));
                slots.add((row * 9 - 1));
                break;
            default:
                slots.add((row * 9 - 9));
                slots.add((row * 9 - 8));
                slots.add((row * 9 - 7));
                slots.add((row * 9 - 6));
                slots.add((row * 9 - 5));
                slots.add((row * 9 - 4));
                slots.add((row * 9 - 3));
                slots.add((row * 9 - 2));
                slots.add((row * 9 - 1));
                break;
        }
        return slots;
    }

    /**
     * Returns set of players, who have opened this menu.
     *
     * @return set of menu viewers.
     */
    public @NotNull Set<Player> getViewers() {
        return viewers;
    }

}
