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

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import ua.mcchickenstudio.opencreative.coding.variables.ValueType;
import ua.mcchickenstudio.opencreative.menus.AbstractMenu;
import ua.mcchickenstudio.opencreative.settings.Sounds;

import static ua.mcchickenstudio.opencreative.utils.ItemUtils.*;
import static ua.mcchickenstudio.opencreative.utils.MessageUtils.getLocaleMessage;

public final class VariablesMenu extends AbstractMenu {

    private final ItemStack TEXT_ITEM = createItem(Material.BOOK, 1, "menus.developer.variables.items.text");
    private final ItemStack NUMBER_ITEM = createItem(Material.SLIME_BALL, 1, "menus.developer.variables.items.number");
    private final ItemStack BOOLEAN_ITEM = createItem(Material.CLOCK, 1, "menus.developer.variables.items.boolean");
    private final ItemStack LOCATION_ITEM = createItem(Material.PAPER, 1, "menus.developer.variables.items.location");
    private final ItemStack VARIABLE_ITEM = createItem(Material.MAGMA_CREAM, 1, "menus.developer.variables.items.variable");
    private final ItemStack EVENT_VALUE_ITEM = createItem(Material.NAME_TAG, 1, "menus.developer.variables.items.event-value");
    private final ItemStack POTION_ITEM = createItem(Material.POTION, 1, "menus.developer.variables.items.potion");
    private final ItemStack PARTICLE_ITEM = createItem(Material.NETHER_STAR, 1, "menus.developer.variables.items.particle");
    private final ItemStack VECTOR_ITEM = createItem(Material.PRISMARINE_SHARD, 1, "menus.developer.variables.items.vector");

    public VariablesMenu() {
        super(1, getLocaleMessage("menus.developer.variables.title"));
    }

    @Override
    public void fillItems(Player player) {
        setItem(0, TEXT_ITEM);
        setItem(1, NUMBER_ITEM);
        setItem(2, LOCATION_ITEM);
        setItem(3, POTION_ITEM);
        setItem(4, PARTICLE_ITEM);
        setItem(5, EVENT_VALUE_ITEM);
        setItem(6, VARIABLE_ITEM);
        setItem(7, VECTOR_ITEM);
        setItem(8, BOOLEAN_ITEM);
    }

    @Override
    public void onClick(@NotNull InventoryClickEvent event) {
        if (!isClickedInMenuSlots(event) || !isPlayerClicked(event)) return;
        event.setCancelled(true);
        if (event.getCurrentItem() == null || itemEquals(event.getCurrentItem(), DECORATION_ITEM)) return;
        ItemStack currentItem = event.getCurrentItem().clone();
        if (currentItem.equals(VARIABLE_ITEM)) {
            setPersistentData(currentItem, getCodingVariableTypeKey(), "LOCAL");
        }
        ValueType valueType = ValueType.getByMaterial(currentItem.getType());
        if (valueType == null) valueType = ValueType.TEXT;
        setPersistentData(currentItem, getCodingValueKey(), valueType.name());
        event.getWhoClicked().getInventory().addItem(currentItem);
        event.setCursor(null);
        Sounds.DEV_TAKE_VALUE.play(event.getWhoClicked());
    }

    @Override
    public void onOpen(@NotNull InventoryOpenEvent event) {
        Sounds.MENU_OPEN_VALUES_BROWSER.play(event.getPlayer());
    }
}
