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
import net.kyori.adventure.text.format.TextColor;
import org.bukkit.Material;
import org.bukkit.Registry;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.potion.PotionType;
import org.jetbrains.annotations.NotNull;
import ua.mcchickenstudio.opencreative.menus.ListBrowserMenu;
import ua.mcchickenstudio.opencreative.settings.Sounds;

import java.util.Arrays;
import java.util.List;

import static ua.mcchickenstudio.opencreative.utils.ItemUtils.createItem;
import static ua.mcchickenstudio.opencreative.utils.MessageUtils.*;

public final class PotionsMenu extends ListBrowserMenu<PotionEffectType> {

    private final Material potionMaterial;

    public PotionsMenu(Player player, Material material) {
        super(player, getLocaleMessage("menus.developer.potions-list.title"), PlacementLayout.VALUE_CHOOSER);
        if (material != Material.POTION && material != Material.LINGERING_POTION && material != Material.SPLASH_POTION) {
            material = Material.POTION;
        }
        potionMaterial = material;
    }

    @Override
    protected ItemStack getElementIcon(PotionEffectType type) {
        ItemStack itemStack = new ItemStack(potionMaterial, 1);
        PotionMeta meta = (PotionMeta) itemStack.getItemMeta();
        PotionEffectType potionType = Registry.POTION_EFFECT_TYPE.get(type.key());
        if (potionType == null) return null;
        String name = type.key().asMinimalString().toLowerCase();
        if (!messageExists("menus.developer.potions-list.potions." + name)) {
            meta.displayName(Component.translatable("effect.minecraft." + name)
                    .color(TextColor.color(type.getColor().asRGB())));
        } else {
            meta.displayName(getLocaleComponent("menus.developer.potions-list.potions." + name, false));
        }
        meta.setBasePotionType(PotionType.WATER);
        meta.addCustomEffect(new PotionEffect(potionType, 3600, 0), true);
        meta.setColor(type.getColor());
        itemStack.setItemMeta(meta);
        return itemStack;
    }

    @Override
    protected void fillDecorationItems() {
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
            if (event.getCurrentItem() == null) return;
            event.getWhoClicked().getInventory().setItemInMainHand(event.getCurrentItem());
            Sounds.DEV_POTION_SET.play(event.getWhoClicked());
        }
        event.setCancelled(true);
    }

    @Override
    public List<PotionEffectType> getElements() {
        return Arrays.asList(PotionEffectType.values());
    }

    @Override
    protected ItemStack getNextPageButton() {
        return createItem(Material.SPECTRAL_ARROW, 1, "menus.developer.potions-list.items.next-page");
    }

    @Override
    protected ItemStack getPreviousPageButton() {
        return createItem(Material.ARROW, 1, "menus.developer.potions-list.items.previous-page");
    }

    @Override
    protected ItemStack getNoElementsButton() {
        return createItem(Material.BARRIER, 1, "menus.developer.potions-list.items.no-elements");
    }

    @Override
    public void onOpen(@NotNull InventoryOpenEvent event) {
    }
}
