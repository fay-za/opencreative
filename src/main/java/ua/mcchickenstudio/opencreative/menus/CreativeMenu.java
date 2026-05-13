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

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import ua.mcchickenstudio.opencreative.OpenCreative;

import static ua.mcchickenstudio.opencreative.utils.ItemUtils.createItem;
import static ua.mcchickenstudio.opencreative.utils.ItemUtils.getItemType;
import static ua.mcchickenstudio.opencreative.utils.MessageUtils.getLocaleMessage;

public final class CreativeMenu extends AbstractMenu {

    private final ItemStack RELOAD_ITEM = createItem(Material.STRUCTURE_VOID, 1, "menus.creative.items.reload", "reload");
    private final ItemStack RESET_LOCALE_ITEM = createItem(Material.KNOWLEDGE_BOOK, 1, "menus.creative.items.reset-locale", "reset-locale");
    private final ItemStack CHECK_UPDATES = createItem(Material.ENDER_EYE, 1, "menus.creative.items.check-updates", "check-updates");

    private final ItemStack LIST_CORRUPTED = createItem(Material.TNT_MINECART, 1, "menus.creative.items.list-corrupted", "list-corrupted");
    private final ItemStack LIST_DEPRECATED = createItem(Material.HOPPER_MINECART, 1, "menus.creative.items.list-deprecated", "list-deprecated");
    private final ItemStack LIST_LOADED = createItem(Material.CHEST_MINECART, 1, "menus.creative.items.list-loaded", "list-loaded");

    private final ItemStack MAINTENANCE_START = createItem(Material.OBSERVER, 1, "menus.creative.items.maintenance-start", "maintenance-start");
    private final ItemStack MAINTENANCE_END = createItem(Material.DROPPER, 1, "menus.creative.items.maintenance-end", "maintenance-end");

    private final ItemStack DEBUG_ENABLE = createItem(Material.REDSTONE_TORCH, 1, "menus.creative.items.debug-enable", "debug-enable");
    private final ItemStack DEBUG_DISABLE = createItem(Material.SOUL_TORCH, 1, "menus.creative.items.debug-disable", "debug-disable");

    public CreativeMenu() {
        super(6, getLocaleMessage("menus.creative.title", false)
                .replace("%version%", OpenCreative.getVersion())
                .replace("%codename%", OpenCreative.getCodename()));
    }

    @Override
    public void fillItems(Player player) {
        setItem(49, createItem(Material.BEACON, 1, "menus.creative.items.info"));
        setItem(createItem(Material.LIGHT_BLUE_STAINED_GLASS_PANE, 1), 47, 51);
        setItem(DECORATION_PANE_ITEM, 45, 46, 52, 53);
        setItem(10, player.hasPermission("opencreative.reload") ? RELOAD_ITEM : DECORATION_ITEM);
        setItem(12, player.hasPermission("opencreative.resetlocale") ? RESET_LOCALE_ITEM : DECORATION_ITEM);
        setItem(14, player.hasPermission("opencreative.updates.check") ? CHECK_UPDATES : DECORATION_ITEM);
        setItem(16, player.hasPermission("opencreative.maintenance") ? (OpenCreative.getSettings().isMaintenance() ? MAINTENANCE_END : MAINTENANCE_START) : DECORATION_ITEM);
        setItem(28, player.hasPermission("opencreative.list.loaded") ? LIST_LOADED : DECORATION_ITEM);
        setItem(30, player.hasPermission("opencreative.list.corrupted") ? LIST_CORRUPTED : DECORATION_ITEM);
        setItem(32, player.hasPermission("opencreative.list.deprecated") ? LIST_DEPRECATED : DECORATION_ITEM);
        setItem(34, player.hasPermission("opencreative.debug") ? (OpenCreative.getSettings().isDebug() ? DEBUG_DISABLE : DEBUG_ENABLE) : DECORATION_ITEM);
    }

    @Override
    public void onClick(@NotNull InventoryClickEvent event) {
        ItemStack clickedItem = event.getCurrentItem();
        event.setCancelled(true);
        if (!isClickedInMenuSlots(event)) return;
        if (clickedItem == null) return;
        Player player = (Player) event.getWhoClicked();
        String type = getItemType(clickedItem);
        if (!type.isEmpty()) player.closeInventory();
        switch (type) {
            case "reload" -> player.performCommand("creative reload");
            case "reset-locale" -> player.performCommand("creative resetlocale");
            case "check-updates" -> player.performCommand("creative update");
            case "maintenance-start" -> player.performCommand("creative maintenance start");
            case "maintenance-end" -> player.performCommand("creative maintenance end");
            case "debug-enable" -> player.performCommand("creative debug enable");
            case "debug-disable" -> player.performCommand("creative debug disable");
            case "list-loaded" -> player.performCommand("creative list");
            case "list-deprecated" -> player.performCommand("creative deprecated");
            case "list-corrupted" -> player.performCommand("creative corrupted");
        }
    }

    @Override
    public void onOpen(@NotNull InventoryOpenEvent event) {
    }
}
