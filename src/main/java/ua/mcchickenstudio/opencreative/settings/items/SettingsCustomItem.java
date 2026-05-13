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

package ua.mcchickenstudio.opencreative.settings.items;

import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;
import ua.mcchickenstudio.opencreative.utils.ItemUtils;

import java.util.ArrayList;
import java.util.List;

import static ua.mcchickenstudio.opencreative.utils.ItemUtils.createItem;
import static ua.mcchickenstudio.opencreative.utils.MessageUtils.*;

/**
 * <h1>SettingsCustomItem</h1>
 * This class represents item, that will be changed
 * by modifiers: preset, data, translation, name, lore,
 * material, amount, glowing.
 */
public class SettingsCustomItem implements SettingsItem {

    private Items preset;
    private String data;
    private String translationKey;

    private String name;
    private String description;

    private Material material;
    private Integer amount;
    private Boolean glowing;

    /**
     * Sets bytes array data of item, that will be used for
     * recovering saved item.
     *
     * @param data bytes array of item.
     */
    public void setData(String data) {
        this.data = data;
    }

    /**
     * Sets preset of item, that will be used for
     * recovering prepared system item.
     *
     * @param preset type of system item.
     */
    public void setPreset(@NotNull Items preset) {
        this.preset = preset;
    }

    /**
     * Sets amount of item.
     *
     * @param amount amount of item.
     */
    public void setAmount(int amount) {
        this.amount = amount;
    }

    /**
     * Sets whether item should be glowing or not.
     *
     * @param glowing true - will glow, false - not.
     */
    public void setGlowing(boolean glowing) {
        this.glowing = glowing;
    }

    /**
     * Sets material of item.
     *
     * @param material material of item.
     */
    public void setMaterial(@NotNull Material material) {
        this.material = material;
    }

    /**
     * Sets display name of item.
     *
     * @param name display name of item.
     */
    public void setName(@NotNull String name) {
        this.name = name;
    }

    /**
     * Sets description of item.
     *
     * @param description lore of item.
     */
    public void setDescription(@NotNull String description) {
        this.description = description;
    }

    /**
     * Sets translation key from localization file (without "items."),
     * that will be used to set display name and description.
     *
     * @param translationKey translation key.
     */
    public void setTranslationKey(@NotNull String translationKey) {
        this.translationKey = translationKey;
    }

    @Override
    public @NotNull ItemStack getItem(@NotNull Player player) {
        ItemStack item = null;
        if (preset != null) {
            item = preset.get(player);
        } else if (data != null) {
            item = ItemUtils.loadItemFromByteArray(data);
        }

        if (material != null && material.isItem()) {
            if (item == null) {
                item = createItem(material, 1);
            }
            item.setType(material);
        } else if (item == null) {
            return new ItemStack(Material.AIR);
        }

        if (amount != null) item.setAmount(Math.clamp(amount, 1, 64));
        ItemMeta meta = item.getItemMeta();
        if (meta != null) {
            if (translationKey != null) {
                meta.setDisplayName(getLocaleItemName(translationKey + ".name"));
                meta.setLore(getLocaleItemDescription(translationKey + ".lore"));
            }
            if (name != null) {
                meta.displayName(toComponent(name));
            }
            if (description != null) {
                List<Component> lore = new ArrayList<>();
                for (String line : description.split("\n")) {
                    lore.add(toComponent(line));
                }
                meta.lore(lore);
            }
            if (glowing != null) {
                meta.setEnchantmentGlintOverride(glowing);
            }
            item.setItemMeta(meta);
        }
        return item;
    }
}
