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

package ua.mcchickenstudio.opencreative.coding.menus.variables;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.title.Title;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;
import ua.mcchickenstudio.opencreative.coding.menus.MenusCategory;
import ua.mcchickenstudio.opencreative.coding.menus.blocks.ContentWithMenusCategoryMenu;
import ua.mcchickenstudio.opencreative.coding.values.EventValue;
import ua.mcchickenstudio.opencreative.coding.values.EventValues;
import ua.mcchickenstudio.opencreative.settings.Sounds;

import java.time.Duration;
import java.util.List;

import static ua.mcchickenstudio.opencreative.utils.ItemUtils.*;
import static ua.mcchickenstudio.opencreative.utils.MessageUtils.getLocaleMessage;
import static ua.mcchickenstudio.opencreative.utils.MessageUtils.toComponent;

/**
 * This class represents a menu where player can select event value
 * and assign it to current item in hand.
 */
public final class EventValueSelectionMenu extends ContentWithMenusCategoryMenu<EventValue> {

    public EventValueSelectionMenu(@NotNull Player player) {
        super(player, "event-values", getLocaleMessage("menus.developer.event-values.title", false),
                Material.ORANGE_STAINED_GLASS_PANE, MenusCategory.WORLD);
    }

    @Override
    protected ItemStack getElementIcon(EventValue value) {
        ItemStack icon = createItem(value.getDisplayIcon(), "menus.developer.event-values.items."
                + value.getID().toLowerCase().replace("_", "-"));
        setPersistentData(icon, getCodingValueKey(), "EVENT_VALUE");
        setPersistentData(icon, getCodingVariableTypeKey(), value.getID().toUpperCase());
        return icon;
    }

    @Override
    protected void onElementClick(InventoryClickEvent event) {
        ItemStack item = event.getCurrentItem();
        ItemStack itemInHand = getPlayer().getInventory().getItemInMainHand();
        event.setCancelled(true);
        if (item == null) return;
        if (item.getItemMeta() == null) return;
        ItemMeta meta = item.getItemMeta();
        if (meta == null) return;
        Component displayName = meta.displayName();
        if (displayName == null) return;
        ItemMeta handMeta = itemInHand.getItemMeta();
        handMeta.displayName(displayName);
        itemInHand.setItemMeta(handMeta);
        setPersistentData(itemInHand, getCodingValueKey(), "EVENT_VALUE");
        setPersistentData(itemInHand, getCodingVariableTypeKey(), getPersistentData(item, getCodingVariableTypeKey()));
        getPlayer().closeInventory();
        getPlayer().showTitle(Title.title(
                toComponent(getLocaleMessage("world.dev-mode.set-variable")), displayName,
                Title.Times.times(Duration.ofMillis(250), Duration.ofSeconds(2), Duration.ofMillis(750))
        ));
        Sounds.DEV_EVENT_VALUE_SET.play(event.getWhoClicked());
    }

    @Override
    public List<EventValue> getElements() {
        return EventValues.getInstance().getByCategories(currentCategory);
    }
}
