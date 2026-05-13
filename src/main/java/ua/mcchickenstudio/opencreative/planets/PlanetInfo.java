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

package ua.mcchickenstudio.opencreative.planets;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextReplacementConfig;
import net.kyori.adventure.text.format.TextDecoration;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.scheduler.BukkitRunnable;
import org.jetbrains.annotations.NotNull;
import ua.mcchickenstudio.opencreative.OpenCreative;
import ua.mcchickenstudio.opencreative.utils.ItemUtils;

import java.util.ArrayList;
import java.util.List;

import static ua.mcchickenstudio.opencreative.planets.Planet.Sharing.PUBLIC;
import static ua.mcchickenstudio.opencreative.utils.FileUtils.*;
import static ua.mcchickenstudio.opencreative.utils.ItemUtils.*;
import static ua.mcchickenstudio.opencreative.utils.MessageUtils.*;

/**
 * <h1>PlanetInfo</h1>
 * This class represents an information of planet. It contains
 * display name, description, custom ID, category, reputation
 * and icon of planet.
 *
 * <p>This information will be displayed in worlds browser
 * or in advertisement messages.
 * </p>
 */
public class PlanetInfo {

    private final Planet planet;

    private String displayName;
    private String description;
    private String customID;

    private int uniques;
    private int reputation;
    private Category category;
    private ItemStack icon;
    private boolean downloadable;
    private int online;

    public PlanetInfo(Planet planet) {
        this.planet = planet;
        loadInformation();
    }

    /**
     * Loads information from world's settings.
     */
    public void loadInformation() {
        FileConfiguration config = getPlanetConfig(planet);
        String name = "Unknown name";
        String description = "World data is corrupted,\\nplease report server admin\\nabout this world.";
        String customID = String.valueOf(planet.getId());
        Category category = Category.SANDBOX;
        ItemStack icon = new ItemStack(Material.REDSTONE);
        boolean downloadable = false;
        reputation = getPlayersFromPlanetList(planet, Planet.PlayersType.LIKED).size() - getPlayersFromPlanetList(planet, Planet.PlayersType.DISLIKED).size();
        uniques = getPlayersFromPlanetList(planet, Planet.PlayersType.UNIQUE).size();
        if (config.getString("name") != null) {
            name = config.getString("name");
        }
        if (config.getString("description") != null) {
            description = config.getString("description");
        }
        if (config.getString("customID") != null) {
            customID = config.getString("customID");
        }
        if (config.getString("category") != null) {
            try {
                category = Category.valueOf(config.getString("category"));
            } catch (Exception error) {
                category = Category.SANDBOX;
            }
        }
        if (config.get("icon") != null) {
            try {
                if (config.isString("icon")) {
                    Material material = Material.matchMaterial(config.getString("icon", ""));
                    if (material != null && material.isItem()) {
                        icon = new ItemStack(material, 1);
                    } else {
                        icon = ItemUtils.loadItemFromByteArray(config.getString("icon", ""));
                    }
                } else if (config.isConfigurationSection("icon")) {
                    ConfigurationSection section = config.getConfigurationSection("icon");
                    if (section != null) {
                        icon = ItemStack.deserialize(section.getValues(true));
                    }
                }
            } catch (Exception ignored) {
                icon = new ItemStack(Material.REDSTONE, 1);
            }
            if (icon.isEmpty()) icon = new ItemStack(Material.REDSTONE, 1);
        }
        if (config.getString("downloadable") != null) {
            downloadable = config.getBoolean("downloadable");
        }
        this.displayName = name;
        this.description = description;
        this.category = category;
        this.customID = customID;
        this.downloadable = downloadable;
        this.icon = icon;
    }

    /**
     * Updates icon in asynchronous task. Used to not
     * load the main thread.
     */
    public void updateIconAsync() {
        new BukkitRunnable() {
            @Override
            public void run() {
                updateIcon();
            }
        }.runTaskAsynchronously(OpenCreative.getPlugin());
        new BukkitRunnable() {
            @Override
            public void run() {
                if (!planet.isLoaded()) {
                    online = 0;
                    return;
                }
                online = planet.getPlayers().size();
            }
        }.runTask(OpenCreative.getPlugin());
    }

    /**
     * Updates icon with current planet's information.
     */
    public void updateIcon() {
        ItemStack item = icon.clone();
        ItemMeta meta = item.getItemMeta();
        meta.displayName(
                getLocaleComponent("menus.all-worlds.items.world.name")
                        .replaceText(TextReplacementConfig.builder()
                                .match("%planetName%")
                                .replacement(displayName()).build()));
        List<String> lore = new ArrayList<>();
        for (String loreLine : getLocaleItemDescription("menus.all-worlds.items.world.lore")) {
            if (loreLine.contains("%planetDescription%")) {
                String[] newLines = this.description.split("\\\\n");
                for (String newLine : newLines) {
                    lore.add(loreLine.replace("%planetDescription%", ChatColor.translateAlternateColorCodes('&', newLine)));
                }
            } else {
                lore.add(parsePlanetLines(this.planet, loreLine));
            }
        }
        item.setAmount((Math.max(planet.getOnline(), 1)));
        meta.setLore(lore);
        item.setItemMeta(meta);
        clearItemFlags(item);
        setPersistentData(item, getItemIdKey(), customID);
        icon = item;
    }

    /**
     * Removes custom ID from world.
     */
    public void resetCustomID() {
        this.customID = String.valueOf(planet.getId());
        removePlanetConfigParameter(planet, "customID");
    }

    /**
     * Returns text component of display name, that can be
     * used in item stacks or texts.
     *
     * @return display name of planet.
     */
    public Component displayName() {
        return LegacyComponentSerializer.legacySection().deserialize(displayName).decorationIfAbsent(TextDecoration.ITALIC, TextDecoration.State.FALSE);
    }

    /**
     * Returns text component of description, that can be
     * used in item stacks or texts.
     *
     * @return description of planet.
     */
    public Component description() {
        return LegacyComponentSerializer.legacySection().deserialize(description);
    }

    /**
     * Returns display name, that stores in
     * world's settings.
     *
     * @return display name of planet.
     */
    public String getDisplayName() {
        return displayName;
    }

    /**
     * Sets new display name of planet, that will be displayed
     * in worlds browser menu and advertisements.
     *
     * @param name new display name.
     */
    public void setDisplayName(String name) {
        this.displayName = name;
        setPlanetConfigParameter(planet, "name", name);
    }

    /**
     * Returns description, that stores in
     * world's settings.
     *
     * @return description of planet.
     */
    public String getDescription() {
        return description;
    }

    /**
     * Sets new description of planet, that will be displayed
     * in worlds browser menu.
     *
     * @param description new description.
     */
    public void setDescription(String description) {
        this.description = description;
        setPlanetConfigParameter(planet, "description", description);
    }

    /**
     * Returns category of planet.
     *
     * @return category of planet.
     */
    public Category getCategory() {
        return category;
    }

    /**
     * Sets category of planet, that will be displayed
     * in worlds browser menu.
     *
     * @param category new category.
     */
    public void setCategory(Category category) {
        this.category = category;
        setPlanetConfigParameter(planet, "category", category.toString());
    }

    /**
     * Returns text custom ID of planet,
     * that can be used with /join.
     *
     * @return custom ID of planet.
     */
    public String getCustomID() {
        return customID;
    }

    /**
     * Sets new text custom ID, that can be used to
     * join world with short /join command.
     *
     * @param customID new custom ID.
     */
    public void setCustomID(@NotNull String customID) {
        this.customID = customID;
        setPlanetConfigParameter(planet, "customID", customID);
    }

    /**
     * Returns icon of planet. If planet is closed,
     * it will have type of barrier.
     *
     * @return icon of planet
     */
    public ItemStack getIcon() {
        if (planet.getSharing() == PUBLIC) return icon;
        else {
            return icon.clone().withType(Material.BARRIER);
        }
    }

    /**
     * Sets new item stack as planet's icon. Name, lore
     * and enchantments will be removed from item.
     *
     * @param itemStack new icon.
     */
    public void setIcon(ItemStack itemStack) {
        ItemStack newIcon = clearItemMeta(itemStack.clone());
        newIcon.setAmount(1);
        if (ItemUtils.doesItemRequireSpecialData(newIcon)) {
            setPlanetConfigParameter(planet, "icon", ItemUtils.saveItemAsByteArray(newIcon));
        } else {
            setPlanetConfigParameter(planet, "icon", newIcon.getType().name());
        }
        this.icon = newIcon;
        updateIcon();
    }

    /**
     * Returns count of unique visitors.
     *
     * @return count of uniques.
     */
    public int getUniques() {
        return uniques;
    }

    /**
     * Sets count of unique visitors.
     *
     * @param uniques new count.
     */
    public void setUniques(int uniques) {
        this.uniques = uniques;
    }

    /**
     * Returns rating of planet. It's calculated
     * by subtracting likes count by count of dislikes.
     * It's displayed in worlds browser menu.
     *
     * @return reputation of planet.
     */
    public int getReputation() {
        return reputation;
    }

    /**
     * Sets rating of planet. It's displayed
     * in worlds browser menu.
     *
     * @param reputation new reputation.
     */
    public void setPlanetReputation(int reputation) {
        this.reputation = reputation;
    }

    /**
     * Checks if world can be used as template in
     * worlds generation menu.
     *
     * @return true - can be used as template, false - not.
     */
    public boolean isDownloadable() {
        return downloadable;
    }

    /**
     * Sets can be world used as template to generate
     * a new world.
     *
     * @param downloadable true - can be used, false - not.
     */
    public void setDownloadable(boolean downloadable) {
        this.downloadable = downloadable;
        setPlanetConfigParameter(planet, "downloadable", downloadable);
    }

    /**
     * Returns inaccurate amount of players in world, that changes
     * with entering or leaving the world, doesn't check real amount
     * of world.
     * <p>Useful to avoid lags by checking all world players.
     * @see Planet#getPlayers()
     *
     * @return inaccurate amount of players in world.
     */
    public int getAsyncOnline() {
        return online;
    }

    /**
     * Returns assigned planet.
     *
     * @return assigned planet.
     */
    public @NotNull Planet getPlanet() {
        return planet;
    }

    public enum Category {

        SANDBOX(Material.HORN_CORAL),
        ADVENTURE(Material.FIRE_CHARGE),
        STRATEGY(Material.ZOMBIE_HEAD),
        ARCADE(Material.HEART_OF_THE_SEA),
        ROLEPLAY(Material.CHERRY_SAPLING),
        STORY(Material.ENCHANTED_BOOK),
        SIMULATOR(Material.PUFFERFISH),
        EXPERIMENT(Material.NETHER_WART);

        private final Material material;

        Category(Material material) {
            this.material = material;
        }

        public Material getMaterial() {
            return material;
        }

        public String getLocaleName() {
            return getLocaleMessage("world.categories." + name().toLowerCase());
        }
    }
}
