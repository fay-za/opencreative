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

package ua.mcchickenstudio.opencreative.indev;

import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import ua.mcchickenstudio.opencreative.utils.ItemUtils;

import static ua.mcchickenstudio.opencreative.utils.MessageUtils.getLocaleItemDescription;
import static ua.mcchickenstudio.opencreative.utils.MessageUtils.getLocaleItemName;

public class ItemBuilder {

    private final ItemStack item;

    public ItemBuilder(ItemStack item) {
        this.item = item.clone();
    }

    public ItemBuilder(Material material) {
        this.item = ItemUtils.createItem(material,1);
    }

    public ItemBuilder translate(String path) {
        item.editMeta(meta -> {
            meta.setDisplayName(getLocaleItemName(path+".name"));
            meta.setLore(getLocaleItemDescription(path+".lore"));
        });
        return this;
    }

    public ItemBuilder amount(int amount) {
        item.setAmount(amount);
        return this;
    }

    public ItemBuilder data(NamespacedKey key, String value) {
        ItemUtils.setPersistentData(item, key, value);
        return this;
    }

    public ItemBuilder type(String value) {
        ItemUtils.setPersistentData(item, ItemUtils.getItemTypeKey(), value);
        return this;
    }

    public ItemBuilder parse(String placeholder, Object value) {
        ItemUtils.replacePlaceholderInName(item,placeholder,value);
        ItemUtils.replacePlaceholderInLore(item,placeholder,value);
        return this;
    }

    public ItemStack getItem() {
        return item;
    }
}
