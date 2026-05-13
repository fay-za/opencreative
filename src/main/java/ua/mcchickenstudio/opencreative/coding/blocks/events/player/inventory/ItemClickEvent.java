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

package ua.mcchickenstudio.opencreative.coding.blocks.events.player.inventory;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import ua.mcchickenstudio.opencreative.coding.blocks.events.ItemEvent;
import ua.mcchickenstudio.opencreative.coding.blocks.events.WorldEvent;
import ua.mcchickenstudio.opencreative.coding.blocks.events.player.ClickedInventoryEvent;

public final class ItemClickEvent extends WorldEvent implements Cancellable, ItemEvent, ClickedInventoryEvent {

    private final InventoryClickEvent event;
    private final ItemStack item;
    private final ClickType click;
    private final int slot;
    private final ItemStack cursor;

    public ItemClickEvent(Player player, InventoryClickEvent event) {
        super(player);
        this.event = event;
        ItemStack currentItem = event.getCurrentItem();
        this.item = currentItem == null ? new ItemStack(Material.AIR) : currentItem;
        this.cursor = event.getCursor();
        this.click = event.getClick();
        this.slot = event.getSlot();
    }

    @Override
    public @Nullable Inventory getClickedInventory() {
        return event.getClickedInventory();
    }

    @Override
    public @NotNull ItemStack getItem() {
        return item;
    }

    public ClickType getClick() {
        return click;
    }

    public int getSlot() {
        return slot;
    }

    public ItemStack getCursor() {
        return cursor;
    }

    @Override
    public boolean isCancelled() {
        return event.isCancelled();
    }

    @Override
    public void setCancelled(boolean cancelled) {
        event.setCancelled(cancelled);
    }
}
