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

package ua.mcchickenstudio.opencreative.coding.modules;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;
import ua.mcchickenstudio.opencreative.OpenCreative;
import ua.mcchickenstudio.opencreative.menus.ListBrowserMenu;
import ua.mcchickenstudio.opencreative.settings.Sounds;
import ua.mcchickenstudio.opencreative.utils.MessageUtils;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import static ua.mcchickenstudio.opencreative.utils.ItemUtils.*;
import static ua.mcchickenstudio.opencreative.utils.MessageUtils.getLocaleMessage;

public final class OwnModulesBrowserMenu extends ListBrowserMenu<Module> {

    private final List<Module> modules;

    private final ItemStack TUTORIAL = createItem(Material.ENDER_EYE, 1, "menus.own-modules.items.tutorial");
    private final ItemStack BACK_TO_ALL_MODULES = createItem(Material.MANGROVE_CHEST_BOAT, 1, "menus.own-modules.items.all-modules");

    public OwnModulesBrowserMenu(Player player) {
        super(player, getLocaleMessage("menus.own-modules.title", false), PlacementLayout.BOTTOM_NO_DECORATION,
                new int[]{45}, new int[]{45, 46, 52, 53});
        this.modules = new ArrayList<>(OpenCreative.getModuleManager().getPlayerModules(player.getUniqueId()));
        Comparator<Module> sortByOnline = Comparator.comparingLong(module -> module.getInformation().getCreationTime());
        this.modules.sort(sortByOnline);
    }

    @Override
    protected ItemStack getElementIcon(Module module) {
        ItemStack item = clearItemMeta(module.getInformation().getIcon().clone());
        ItemMeta meta = item.getItemMeta();
        meta.displayName(module.getInformation().displayName());
        List<String> lore = new ArrayList<>();
        for (String loreLine : MessageUtils.getLocaleItemDescription("menus.own-modules.items.module.lore")) {
            if (loreLine.contains("%moduleDescription%")) {
                String[] newLines = module.getInformation().getDescription().split("\\\\n");
                for (String newLine : newLines) {
                    lore.add(loreLine.replace("%moduleDescription%", ChatColor.translateAlternateColorCodes('&', newLine)));
                }
            } else {
                lore.add(MessageUtils.parseModuleLines(module, loreLine));
            }
        }
        meta.setLore(lore);
        item.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
        meta.addItemFlags(ItemFlag.HIDE_ARMOR_TRIM);
        meta.addItemFlags(ItemFlag.HIDE_DESTROYS);
        meta.addItemFlags(ItemFlag.HIDE_UNBREAKABLE);
        meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
        meta.addItemFlags(ItemFlag.HIDE_DYE);
        meta.addItemFlags(ItemFlag.HIDE_PLACED_ON);
        item.setItemMeta(meta);
        setPersistentData(item, getItemIdKey(), String.valueOf(module.getId()));
        return item;
    }

    @Override
    protected void fillOtherItems() {
        setItem(45, BACK_TO_ALL_MODULES);
        setItem(createItem(Material.GREEN_STAINED_GLASS_PANE, 1), 47, 51);
        setItem(49, TUTORIAL);
    }

    @Override
    protected void onCharmsBarClick(InventoryClickEvent event) {
        event.setCancelled(true);
        ItemStack item = event.getCurrentItem();
        if (item == null) {
            return;
        }
        if (itemEquals(item, BACK_TO_ALL_MODULES)) {
            new ModulesBrowserMenu(getPlayer()).open(getPlayer());
        }
    }

    @Override
    protected void onElementClick(InventoryClickEvent event) {
        ItemStack currentItem = event.getCurrentItem();
        event.setCancelled(true);
        if (currentItem == null) {
            return;
        }
        String moduleID = getPersistentData(currentItem, getItemIdKey());
        if (moduleID.isEmpty()) {
            return;
        }
        Module module = OpenCreative.getModuleManager().getModuleById(moduleID);
        if (module != null) {
            new ModuleSettingsMenu(module, getPlayer()).open(getPlayer());
        }
    }

    @Override
    protected void fillArrowsItems(int currentPage) {
        if (elements.isEmpty()) {
            setItem(getNoElementsPageButtonSlot(), getNoElementsButton());
            setItem(getPreviousPageButtonSlot(), BACK_TO_ALL_MODULES);
            setItem(getNextPageButtonSlot(), DECORATION_ITEM);
        } else {
            int maxPagesAmount = getPages();
            if (currentPage > maxPagesAmount || currentPage < 1) {
                currentPage = 1;
            }
            setItem(getPreviousPageButtonSlot(), currentPage > 1 ? getPreviousPageButton() : BACK_TO_ALL_MODULES);
            setItem(getNextPageButtonSlot(), currentPage < maxPagesAmount ? getNextPageButton() : DECORATION_ITEM);
        }
    }

    @Override
    public List<Module> getElements() {
        return new ArrayList<>(modules);
    }

    @Override
    protected ItemStack getNextPageButton() {
        return replacePlaceholderInLore(createItem(Material.SPECTRAL_ARROW, getCurrentPage() + 1, "menus.own-modules.items.next-page"), "%page%", getCurrentPage() + 1);
    }

    @Override
    protected ItemStack getPreviousPageButton() {
        return replacePlaceholderInLore(createItem(Material.ARROW, Math.max(1, getCurrentPage() - 1), "menus.own-modules.items.previous-page"), "%page%", getCurrentPage() - 1);
    }

    @Override
    protected ItemStack getNoElementsButton() {
        return createItem(Material.BARRIER, 1, "menus.own-modules.items.no-modules");
    }

    @Override
    public void onOpen(@NotNull InventoryOpenEvent event) {
        Sounds.MENU_OPEN_OWN_MODULES_BROWSER.play(event.getPlayer());
    }

}
