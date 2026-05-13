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

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextReplacementConfig;
import net.kyori.adventure.text.format.TextDecoration;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.scheduler.BukkitRunnable;
import org.jetbrains.annotations.NotNull;
import ua.mcchickenstudio.opencreative.OpenCreative;
import ua.mcchickenstudio.opencreative.coding.CodeConfiguration;
import ua.mcchickenstudio.opencreative.planets.Planet;
import ua.mcchickenstudio.opencreative.utils.ItemUtils;

import java.util.ArrayList;
import java.util.List;

import static ua.mcchickenstudio.opencreative.utils.FileUtils.getModuleConfig;
import static ua.mcchickenstudio.opencreative.utils.FileUtils.setModuleConfigParameter;
import static ua.mcchickenstudio.opencreative.utils.ItemUtils.*;
import static ua.mcchickenstudio.opencreative.utils.MessageUtils.*;

public class ModuleInfo {

    private final Module module;

    private String displayName;
    private String description;
    private ItemStack icon;
    private int reputation;
    private int downloads;
    private long creationTime;
    private boolean isPublic;

    public ModuleInfo(@NotNull Module module) {
        this.module = module;
        loadInformation();
    }

    public @NotNull String getDisplayName() {
        return displayName;
    }

    /**
     * Sets new display name of module, that will be displayed
     * in modules browser menu.
     *
     * @param name new display name.
     */
    public void setDisplayName(String name) {
        this.displayName = name;
        setModuleConfigParameter(module, "name", name);
        updateIconAsync();
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

    public @NotNull String getDescription() {
        return description;
    }

    /**
     * Sets new description of module, that will be displayed
     * in worlds browser menu.
     *
     * @param description new description.
     */
    public void setDescription(String description) {
        this.description = description;
        setModuleConfigParameter(module, "description", description);
        updateIconAsync();
    }

    public @NotNull ItemStack getIcon() {
        if (module.getInformation().isPublic()) return icon;
        else {
            return icon.clone().withType(Material.BARRIER);
        }
    }

    /**
     * Sets new item stack as module's icon. Name, lore
     * and enchantments will be removed from item.
     *
     * @param itemStack new icon.
     */
    public void setIcon(ItemStack itemStack) {
        ItemStack newIcon = clearItemMeta(itemStack.clone());
        newIcon.setAmount(1);
        if (ItemUtils.doesItemRequireSpecialData(newIcon)) {
            setModuleConfigParameter(module, "icon", ItemUtils.saveItemAsByteArray(newIcon));
        } else {
            setModuleConfigParameter(module, "icon", newIcon.getType().name());
        }
        this.icon = newIcon;
        updateIcon();
    }

    public boolean isPublic() {
        return isPublic;
    }

    public void setPublic(boolean isPublic) {
        if (isPublic == this.isPublic) return;
        this.isPublic = isPublic;
        setModuleConfigParameter(module, "public", isPublic);
        updateIconAsync();
    }

    public long getCreationTime() {
        if (creationTime == 0) {
            return 1670573410000L;
        }
        return creationTime;
    }

    public int getReputation() {
        return reputation;
    }

    public int getDownloads() {
        return downloads;
    }

    private void loadInformation() {
        CodeConfiguration config = getModuleConfig(module);
        displayName = config.getString("name", "Unknown name");
        description = config.getString("description", "Unknown description");
        isPublic = config.getBoolean("public", true);
        icon = new ItemStack(Material.BARREL, 1);
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
            icon = new ItemStack(Material.BARREL, 1);
        }
        if (icon.isEmpty()) icon = new ItemStack(Material.BARREL, 1);
        setPersistentData(icon, getItemIdKey(), String.valueOf(module.getId()));
        creationTime = config.getLong("creation-time", 1670573410000L);
        reputation = config.getStringList("players.liked").size() - config.getStringList("players.disliked").size();
        downloads = config.getIntegerList("planets").size();
    }

    public void updateIcon() {
        ItemStack item = icon.clone();
        ItemMeta meta = item.getItemMeta();
        meta.displayName(
                getLocaleComponent("menus.modules.items.module.name")
                        .replaceText(TextReplacementConfig.builder()
                                .match("%moduleName%")
                                .replacement(displayName()).build()));
        List<String> lore = new ArrayList<>();
        for (String loreLine : getLocaleItemDescription("menus.modules.items.module.lore")) {
            if (loreLine.contains("%moduleDescription%")) {
                String[] newLines = this.description.split("\\\\n");
                for (String newLine : newLines) {
                    lore.add(loreLine.replace("%moduleDescription%", ChatColor.translateAlternateColorCodes('&', newLine)));
                }
            } else {
                lore.add(parseModuleLines(module, loreLine));
            }
        }
        item.setAmount(1);
        meta.setLore(lore);
        item.setItemMeta(meta);
        clearItemFlags(item);
        setPersistentData(item, getItemIdKey(), String.valueOf(module.getId()));
        icon = item;
    }

    public boolean wasDownloadedBefore(@NotNull Planet planet) {
        FileConfiguration config = getModuleConfig(module);
        List<Integer> planets = config.getIntegerList("planets");
        return planets.contains(planet.getId());
    }

    /**
     * Marks planet as module downloader.
     *
     * @param planet planet that has installed module.
     */
    public void addDownload(@NotNull Planet planet) {
        FileConfiguration config = getModuleConfig(module);
        List<Integer> planets = config.getIntegerList("planets");
        if (planets.contains(planet.getId())) {
            return;
        }
        planets.add(planet.getId());
        setModuleConfigParameter(module, "planets", planets);
        downloads = planets.size();
        updateIconAsync();
    }

    public boolean addLike(@NotNull Player player) {
        FileConfiguration config = getModuleConfig(module);
        List<String> likedUUIDs = config.getStringList("players.liked");
        if (likedUUIDs.contains(player.getUniqueId().toString())) return false;
        List<String> dislikedUUIDs = config.getStringList("players.disliked");
        if (dislikedUUIDs.contains(player.getUniqueId().toString())) return false;
        likedUUIDs.add(player.getUniqueId().toString());
        setModuleConfigParameter(module, "players.liked", likedUUIDs);
        reputation = likedUUIDs.size() - dislikedUUIDs.size();
        updateIconAsync();
        return true;
    }

    public boolean addDislike(@NotNull Player player) {
        FileConfiguration config = getModuleConfig(module);
        List<String> dislikedUUIDs = config.getStringList("players.disliked");
        if (dislikedUUIDs.contains(player.getUniqueId().toString())) return false;
        List<String> likedUUIDs = config.getStringList("players.liked");
        if (likedUUIDs.contains(player.getUniqueId().toString())) return false;
        dislikedUUIDs.add(player.getUniqueId().toString());
        setModuleConfigParameter(module, "players.disliked", dislikedUUIDs);
        reputation = likedUUIDs.size() - dislikedUUIDs.size();
        updateIconAsync();
        return true;
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
    }

}
