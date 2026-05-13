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

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.title.Title;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.NotNull;
import ua.mcchickenstudio.opencreative.menus.ListBrowserMenu;
import ua.mcchickenstudio.opencreative.planets.DevPlanet;
import ua.mcchickenstudio.opencreative.settings.Sounds;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

import static ua.mcchickenstudio.opencreative.utils.BlockUtils.getSignLine;
import static ua.mcchickenstudio.opencreative.utils.BlockUtils.setSignLine;
import static ua.mcchickenstudio.opencreative.utils.ItemUtils.*;
import static ua.mcchickenstudio.opencreative.utils.MessageUtils.getLocaleMessage;
import static ua.mcchickenstudio.opencreative.utils.MessageUtils.toComponent;
import static ua.mcchickenstudio.opencreative.utils.PlayerUtils.translateBlockSign;

public final class FunctionChooserMenu extends ListBrowserMenu<Location> {

    private final DevPlanet devPlanet;
    private final Location signLocation;

    public FunctionChooserMenu(Player player, DevPlanet planet, Location location) {
        super(player, getLocaleMessage("menus.developer.function-chooser.title"), PlacementLayout.LOCATION_CHOOSER);
        this.devPlanet = planet;
        this.signLocation = location;
    }

    @Override
    protected ItemStack getElementIcon(Location location) {
        Block signBlock = location.getBlock().getRelative(BlockFace.SOUTH);
        String line = getSignLine(signBlock.getLocation(), 3);
        if (line != null && !line.isEmpty()) {
            ItemStack itemStack = createItem(Material.LAPIS_LAZULI, 1, "menus.developer.function-chooser.items.function");
            ItemMeta meta = itemStack.getItemMeta();
            if (meta != null) {
                meta.setDisplayName(ChatColor.BLUE + line);
            }
            itemStack.setItemMeta(meta);
            setPersistentData(itemStack, getCodingLocationX(), location.getX());
            setPersistentData(itemStack, getCodingLocationY(), location.getY());
            setPersistentData(itemStack, getCodingLocationZ(), location.getZ());
            replacePlaceholderInLore(itemStack, "%x%", location.getX());
            replacePlaceholderInLore(itemStack, "%y%", location.getY());
            replacePlaceholderInLore(itemStack, "%z%", location.getZ());
            return itemStack;
        }
        return ItemStack.empty();
    }

    @Override
    protected void fillDecorationItems() {
        for (int slot = 0; slot <= 8; slot++) {
            setItem(slot, DECORATION_PANE_ITEM);
        }
        for (int slot = 45; slot <= 53; slot++) {
            setItem(slot, DECORATION_PANE_ITEM);
        }
    }

    @Override
    protected void fillOtherItems() {
    }

    @Override
    protected void onCharmsBarClick(InventoryClickEvent event) {
    }

    @Override
    protected void onElementClick(InventoryClickEvent event) {
        if (isPlayerClicked(event) && isClickedInMenuSlots(event)) {
            ItemStack item = event.getCurrentItem();
            if (item == null) return;
            if (item.getItemMeta() == null) return;
            String name = ChatColor.stripColor(item.getItemMeta().getDisplayName());
            event.getWhoClicked().closeInventory();
            if (event.isRightClick()) {
                PersistentDataContainer container = item.getItemMeta().getPersistentDataContainer();
                if (container.has(getCodingLocationX()) && container.has(getCodingLocationY()) && container.has(getCodingLocationZ())) {
                    try {
                        double x = container.get(getCodingLocationX(), PersistentDataType.DOUBLE);
                        double y = container.get(getCodingLocationY(), PersistentDataType.DOUBLE);
                        double z = container.get(getCodingLocationZ(), PersistentDataType.DOUBLE);
                        event.getWhoClicked().teleport(new Location(event.getWhoClicked().getWorld(), x, y, z + 2, 180, 0));
                    } catch (NullPointerException ignored) {}
                }
            } else {
                devPlanet.addInsideCodeColumnChange(signLocation.getBlock().getRelative(BlockFace.NORTH).getLocation());
                setSignLine(signLocation, 3, name);
                translateBlockSign(signLocation.getBlock());
                getPlayer().showTitle(Title.title(
                        toComponent(getLocaleMessage("menus.developer.function-chooser.chosen")), Component.text(name).color(NamedTextColor.BLUE),
                        Title.Times.times(Duration.ofMillis(250), Duration.ofSeconds(2), Duration.ofMillis(750))
                ));
                Sounds.DEV_SET_FUNCTION.play(event.getWhoClicked());
                event.getWhoClicked().swingMainHand();
            }
        }
        event.setCancelled(true);
    }

    @Override
    protected void fillArrowsItems(int currentPage) {
        if (elements.isEmpty()) {
            setItem(getNoElementsPageButtonSlot(), getNoElementsButton());
            setItem(getPreviousPageButtonSlot(), DECORATION_PANE_ITEM);
            setItem(getNextPageButtonSlot(), DECORATION_PANE_ITEM);
        } else {
            int maxPagesAmount = getPages();
            if (currentPage > maxPagesAmount || currentPage < 1) {
                currentPage = 1;
            }
            setItem(getPreviousPageButtonSlot(), currentPage > 1 ? getPreviousPageButton() : DECORATION_PANE_ITEM);
            setItem(getNextPageButtonSlot(), currentPage < maxPagesAmount ? getNextPageButton() : DECORATION_PANE_ITEM);
        }
    }

    @Override
    public List<Location> getElements() {
        return new ArrayList<>(devPlanet.getPlacedFunctions());
    }

    @Override
    protected ItemStack getNextPageButton() {
        return createItem(Material.SPECTRAL_ARROW, 1, "menus.developer.function-chooser.items.next-page");
    }

    @Override
    protected ItemStack getPreviousPageButton() {
        return createItem(Material.ARROW, 1, "menus.developer.function-chooser.items.previous-page");
    }

    @Override
    protected ItemStack getNoElementsButton() {
        return createItem(Material.BARRIER, 1, "menus.developer.function-chooser.items.no-elements");
    }

    @Override
    public void onOpen(@NotNull InventoryOpenEvent event) {

    }
}
