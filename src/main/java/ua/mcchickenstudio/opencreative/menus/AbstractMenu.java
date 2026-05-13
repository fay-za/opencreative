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

package ua.mcchickenstudio.opencreative.menus;

import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import static ua.mcchickenstudio.opencreative.utils.ErrorUtils.sendPlayerErrorMessage;
import static ua.mcchickenstudio.opencreative.utils.ItemUtils.createItem;

/**
 * <h1>AbstractMenu</h1>
 * This class represents a menu with set items. It has
 * methods to check player's click, open and close inventory
 * events.
 */
public abstract class AbstractMenu implements InventoryMenu {

    protected final long creationTime;
    protected final ItemStack AIR_ITEM = new ItemStack(Material.AIR);
    protected final ItemStack NO_PERMS_ITEM = createItem(Material.RED_STAINED_GLASS, 1);
    protected final ItemStack DISABLED_ITEM = createItem(Material.RED_STAINED_GLASS, 1, "items.disabled");
    protected final ItemStack DECORATION_ITEM = createItem(Material.LIGHT_GRAY_STAINED_GLASS, 1);
    protected final ItemStack DECORATION_PANE_ITEM = createItem(Material.GRAY_STAINED_GLASS_PANE, 1);
    protected Inventory inventory;
    private int rows;
    private String title;

    public AbstractMenu(int rows, String title) {
        this.rows = rows;
        this.title = title;
        this.creationTime = System.currentTimeMillis();
    }

    public void setItem(int slot, ItemStack item) {
        slot = Math.clamp(0, slot, getSize());
        if (item == null) item = ItemStack.empty();
        getInventory().setItem(slot, item);
    }

    public void setItem(ItemStack item, int... slots) {
        for (int slot : slots) {
            setItem(slot, item);
        }
    }

    public @NotNull ItemStack getItem(int slot) {
        if (slot < 0 || slot >= getInventory().getSize()) return AIR_ITEM.clone();
        ItemStack item = getInventory().getItem(slot);
        return item == null ? AIR_ITEM.clone() : item;
    }

    public @NotNull Inventory getInventory() {
        rows = Math.clamp(1, rows, 6);
        if (inventory == null || inventory.getSize() != rows * 9) {
            inventory = Bukkit.createInventory(this, rows * 9, Component.text(this.title));
        }
        return inventory;
    }

    public void open(@NotNull Player player) {
        Menus.addMenu(this);
        try {
            inventory = getInventory();
            fillItems(player);
            player.openInventory(inventory);
        } catch (Exception e) {
            sendPlayerErrorMessage(player, "Failed to open AbstractMenu with title " + title + ". ", e);
        }
    }

    public abstract void fillItems(Player player);

    public abstract void onClick(@NotNull InventoryClickEvent event);

    public abstract void onOpen(@NotNull InventoryOpenEvent event);

    protected final boolean isClickedInMenuSlots(InventoryClickEvent event) {
        if (event.getClickedInventory() == null) return false;
        if (event.getInventory().getHolder() == null) return false;
        return event.getInventory().getHolder().equals(event.getClickedInventory().getHolder());
    }

    protected final boolean isPlayerClicked(InventoryClickEvent event) {
        return (event.getWhoClicked() instanceof Player);
    }

    public int getSize() {
        return rows * 9;
    }

    public int getRows() {
        return rows;
    }

    protected void setRows(int rows) {
        this.rows = rows;
        if (inventory != null && inventory.getSize() != rows * 9) {
            ItemStack[] oldItems = inventory.getContents();
            inventory = Bukkit.createInventory(this, rows * 9, Component.text(this.title));
            for (int slot = 0; slot < oldItems.length; slot++) {
                if (slot >= inventory.getSize()) break;
                inventory.setItem(slot, oldItems[slot]);
            }
        }
    }

    protected int reverse(int slot) {
        // 0  1  2  3   4    5  6  7  8
        // 9 10 11 12  13   14 15 16 17
        while (slot > 8) {
            slot = slot - 9;
        }
        return 8 - slot;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    @Override
    public long getCreationTime() {
        return creationTime;
    }

    protected boolean isNotEmpty(ItemStack item) {
        return (item != null && item.getType() != Material.AIR);
    }

}
