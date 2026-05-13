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

package ua.mcchickenstudio.opencreative.coding.menus;

import net.kyori.adventure.title.Title;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;
import ua.mcchickenstudio.opencreative.coding.blocks.actions.Target;
import ua.mcchickenstudio.opencreative.menus.AbstractMenu;
import ua.mcchickenstudio.opencreative.settings.Sounds;

import java.time.Duration;

import static ua.mcchickenstudio.opencreative.utils.ItemUtils.*;
import static ua.mcchickenstudio.opencreative.utils.MessageUtils.*;

public final class ValueTargetSelectionMenu extends AbstractMenu {

    private final Player player;

    public ValueTargetSelectionMenu(@NotNull Player player) {
        super(1, getLocaleMessage("menus.developer.selection.title-values"));
        this.player = player;
    }

    @Override
    public void fillItems(Player player) {
        int slot = 0;
        for (Target target : Target.values()) {
            if (!target.isSupportsEventValue()) continue;
            setItem(slot, createItem(target.getIcon(), 1, "menus.developer.selection.items." + target.name().toLowerCase().replace("_", "-")));
            slot++;
        }
        if (slot < getSize()) {
            while (slot < getSize()) {
                setItem(slot, DECORATION_ITEM);
                slot++;
            }
        }
    }

    @Override
    public void onClick(@NotNull InventoryClickEvent event) {
        if (isPlayerClicked(event) && isClickedInMenuSlots(event)) {
            ItemStack item = event.getCurrentItem();
            event.setCancelled(true);
            if (item == null || item.equals(DECORATION_ITEM)) {
                return;
            }
            ItemStack itemInHand = player.getInventory().getItemInMainHand();
            Target target = Target.getByMaterial(item.getType());
            setPersistentData(itemInHand, getCodingTargetTypeKey(), target.name());
            ItemMeta meta = itemInHand.getItemMeta();
            meta.setLore(getLocaleItemDescription("menus.developer.variables.items.event-value.lore"));
            itemInHand.setItemMeta(meta);
            addLoreAtBegin(itemInHand, getLocaleMessage("menus.developer.event-values.target")
                    .replace("%target%", target.getLocaleName()));
            player.showTitle(Title.title(
                    toComponent(getLocaleMessage("world.dev-mode.set-target")), toComponent(target.getLocaleName()),
                    Title.Times.times(Duration.ofMillis(250), Duration.ofSeconds(2), Duration.ofMillis(750))
            ));
            player.closeInventory();
            Sounds.DEV_SET_TARGET.play(player);
        }
    }

    @Override
    public void onOpen(@NotNull InventoryOpenEvent event) {
    }
}
