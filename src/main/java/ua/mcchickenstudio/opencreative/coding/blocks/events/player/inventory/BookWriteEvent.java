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

import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.player.PlayerEditBookEvent;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import ua.mcchickenstudio.opencreative.coding.blocks.events.ItemEvent;
import ua.mcchickenstudio.opencreative.coding.blocks.events.WorldEvent;

public final class BookWriteEvent extends WorldEvent implements Cancellable, ItemEvent {

    private final PlayerEditBookEvent event;
    private final ItemStack oldBook;
    private final ItemStack newBook;

    public BookWriteEvent(Player player, PlayerEditBookEvent event) {
        super(player);
        this.event = event;
        this.oldBook = event.getPlayer().getActiveItem().clone();
        oldBook.setItemMeta(event.getPreviousBookMeta());
        this.newBook = event.getPlayer().getActiveItem().clone();
        newBook.setItemMeta(event.getNewBookMeta());
    }

    public ItemStack getNewBook() {
        return newBook;
    }

    @Override
    public boolean isCancelled() {
        return event.isCancelled();
    }

    @Override
    public void setCancelled(boolean cancelled) {
        event.setCancelled(cancelled);
    }

    @Override
    public @NotNull ItemStack getItem() {
        return oldBook;
    }
}
