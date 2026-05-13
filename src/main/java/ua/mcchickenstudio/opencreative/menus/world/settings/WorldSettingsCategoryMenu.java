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

package ua.mcchickenstudio.opencreative.menus.world.settings;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import ua.mcchickenstudio.opencreative.OpenCreative;
import ua.mcchickenstudio.opencreative.menus.AbstractMenu;
import ua.mcchickenstudio.opencreative.menus.world.WorldMenu;
import ua.mcchickenstudio.opencreative.planets.Planet;
import ua.mcchickenstudio.opencreative.planets.PlanetInfo;
import ua.mcchickenstudio.opencreative.settings.Sounds;
import ua.mcchickenstudio.opencreative.utils.MessageUtils;

import static org.bukkit.Material.ARROW;
import static ua.mcchickenstudio.opencreative.utils.ItemUtils.*;
import static ua.mcchickenstudio.opencreative.utils.MessageUtils.getLocaleMessage;

public final class WorldSettingsCategoryMenu extends AbstractMenu implements WorldMenu {

    private final Planet planet;
    private final PlanetInfo.Category currentCategory;
    private final ItemStack BACK_ITEM = createItem(ARROW, 1, "menus.world-settings-categories.items.back");

    public WorldSettingsCategoryMenu(Planet planet) {
        super(6, MessageUtils.getLocaleMessage("menus.world-settings.title"));
        this.currentCategory = planet.getInformation().getCategory();
        this.planet = planet;
    }

    @Override
    public void fillItems(Player player) {
        setItem(10, createButton(PlanetInfo.Category.SANDBOX));
        setItem(12, createButton(PlanetInfo.Category.ADVENTURE));
        setItem(14, createButton(PlanetInfo.Category.STRATEGY));
        setItem(16, createButton(PlanetInfo.Category.ARCADE));
        setItem(28, createButton(PlanetInfo.Category.ROLEPLAY));
        setItem(30, createButton(PlanetInfo.Category.STORY));
        setItem(32, createButton(PlanetInfo.Category.SIMULATOR));
        setItem(34, createButton(PlanetInfo.Category.EXPERIMENT));
        setItem(45, BACK_ITEM);
        setItem(46, DECORATION_PANE_ITEM);
        setItem(47, createItem(Material.GREEN_STAINED_GLASS_PANE, 1));
        setItem(49, setPersistentData(
                createItem(currentCategory.getMaterial(), 1,
                        "menus.world-settings-categories.items." +
                                currentCategory.name().toLowerCase()),
                getItemTypeKey(), currentCategory.name()));
        setItem(51, createItem(Material.GREEN_STAINED_GLASS_PANE, 1));
        setItem(52, DECORATION_PANE_ITEM);
        setItem(53, DECORATION_PANE_ITEM);
    }

    @Override
    public void onClick(@NotNull InventoryClickEvent event) {
        event.setCancelled(true);
        if (!isPlayerClicked(event)) return;
        Planet planet = OpenCreative.getPlanetsManager().getPlanetByPlayer((Player) event.getWhoClicked());
        if (planet == null) return;
        if (!planet.isOwner(event.getWhoClicked().getName())) return;
        if (event.getCurrentItem() == null) return;
        if (event.getCurrentItem().getItemMeta() == null) return;
        if (!itemEquals(event.getCurrentItem(), BACK_ITEM)) {
            String categoryString = getPersistentData(event.getCurrentItem(), getItemTypeKey());
            if (categoryString.isEmpty()) return;
            try {
                PlanetInfo.Category category = PlanetInfo.Category.valueOf(categoryString);
                planet.getInformation().setCategory(category);
                event.getWhoClicked().sendMessage(getLocaleMessage("settings.world-category.changed").replace("%category%", category.getLocaleName()));
            } catch (IllegalArgumentException ignored) {
                return;
            }
            event.getWhoClicked().closeInventory();
            Sounds.WORLD_SETTINGS_CATEGORY_SET.play(event.getWhoClicked());
        } else {
            new WorldSettingsMenu(planet, (Player) event.getWhoClicked()).open((Player) event.getWhoClicked());
        }

    }

    @Override
    public Planet getPlanet() {
        return planet;
    }

    private ItemStack createButton(PlanetInfo.Category category) {
        if (category == currentCategory) return DECORATION_ITEM;
        return setPersistentData(
                createItem(category.getMaterial(), 1,
                        "menus.world-settings-categories.items." +
                                category.name().toLowerCase()),
                getItemTypeKey(), category.name());
    }

    @Override
    public void onOpen(@NotNull InventoryOpenEvent event) {
    }
}
