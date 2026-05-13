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

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import ua.mcchickenstudio.opencreative.OpenCreative;

import static ua.mcchickenstudio.opencreative.utils.ErrorUtils.sendCriticalErrorMessage;

/**
 * <h1>ItemFixerSettings</h1>
 * This class represents a settings of item fixer.
 */
public final class ItemFixerSettings {

    private boolean enabled = true;

    private boolean removeAttributes = true;
    private boolean removeBossSpawnEggs = true;
    private boolean removeCustomSpawnEggs = true;
    private boolean removeClickableBooks = true;
    private boolean clearCommandBlocksData = true;

    private int maxEnchantLevel = 10;
    private int entitiesMaxAmount = 3;
    private int maxBookPagesAmount = 50;
    private int maxEntityNameLength = 48;
    private int containerBigItemsLimit = 3;
    private int maxPersistentDataSize = 2048;
    private int displayNameMaxLength = 64;
    private int loreLineMaxLength = 100;
    private int loreLinesMaxAmount = 25;

    /**
     * Loads settings of item fixer from configuration.
     */
    public void load() {
        FileConfiguration config = OpenCreative.getPlugin().getConfig();
        ConfigurationSection section = config.getConfigurationSection("item-fixer");
        if (section == null) {
            sendCriticalErrorMessage("Can't load item fixer, section `item-fixer` in config.yml is empty.");
            return;
        }

        enabled = section.getBoolean("enabled", true);

        // Remove or ignore
        removeAttributes = section.getBoolean("remove-attribute-modifiers", true);
        removeClickableBooks = section.getBoolean("remove-clickable-in-books", true);
        removeCustomSpawnEggs = section.getBoolean("remove-custom-spawn-eggs", true);
        removeBossSpawnEggs = section.getBoolean("remove-boss-spawn-eggs", true);
        clearCommandBlocksData = section.getBoolean("clear-command-blocks-data", true);

        // Internal limits
        maxEntityNameLength = section.getInt("entity-name-max-length", 48);
        maxPersistentDataSize = section.getInt("persistent-data-max-size", 2048);
        entitiesMaxAmount = section.getInt("entities-max-amount", 3);
        maxBookPagesAmount = section.getInt("books-pages-max-amount", 50);
        containerBigItemsLimit = section.getInt("container-big-items-max-amount", 3);
        loreLinesMaxAmount = section.getInt("container-lore-lines-max-amount", 25);

        // Display limits
        maxEnchantLevel = section.getInt("max-enchantment-level", 10);
        displayNameMaxLength = section.getInt("display-name-max-length", 64);
        loreLineMaxLength = section.getInt("lore-line-max-length", 100);
    }

    /**
     * Returns maximum amount of heavy items inside container (chest, shulker).
     *
     * @return limit of big  in container.
     */
    public int getContainerBigItemsLimit() {
        return containerBigItemsLimit;
    }

    /**
     * Returns maximum amount of pages in a book.
     *
     * @return limit of pages in book.
     */
    public int getMaxBookPagesAmount() {
        return maxBookPagesAmount;
    }

    /**
     * Returns maximum level of enchantment in item.
     *
     * @return maximum level of enchantment.
     */
    public int getMaxEnchantLevel() {
        return maxEnchantLevel;
    }

    /**
     * Checks whether attribute modifiers should be removed from item.
     *
     * @return true - will be removed, false - not.
     */
    public boolean isRemoveAttributes() {
        return removeAttributes;
    }

    /**
     * Checks whether clickable components should be removed from book's content.
     *
     * @return true - will be removed, false - not.
     */
    public boolean isRemoveClickableBooks() {
        return removeClickableBooks;
    }

    /**
     * Checks whether custom eggs should be destroyed, because they may contain
     * malicious entity data.
     *
     * @return true - will be removed, false - not.
     */
    public boolean isRemoveCustomSpawnEggs() {
        return removeCustomSpawnEggs;
    }

    /**
     * Checks whether boss spawn eggs (for ender dragon and wither) should
     * be destroyed, because they're often used to grief worlds.
     *
     * @return true - will be removed, false - not.
     */
    public boolean isRemoveBossSpawnEggs() {
        return removeBossSpawnEggs;
    }

    /**
     * Returns maximum length of entity's name.
     *
     * @return limit of entity's name length.
     */
    public int getMaxEntityNameLength() {
        return maxEntityNameLength;
    }

    /**
     * Returns maximum size (symbols amount) of persistent data container.
     *
     * @return limit of persistent data container's size.
     */
    public int getMaxPersistentDataSize() {
        return maxPersistentDataSize;
    }

    /**
     * Returns maximum amount of entities inside block, that can
     * spawn a lot of entities.
     *
     * @return limit of entities inside block.
     */
    public int getEntitiesMaxAmount() {
        return entitiesMaxAmount;
    }

    /**
     * Returns maximum length of item's display name.
     *
     * @return limit of display name's length.
     */
    public int getDisplayNameMaxLength() {
        return displayNameMaxLength;
    }

    /**
     * Returns maximum length of item's lore one line length.
     *
     * @return limit of item lore line's length
     */
    public int getLoreLineMaxLength() {
        return loreLineMaxLength;
    }

    /**
     * Returns maximum amount of lines in item's lore.
     *
     * @return limit of item lore lines amount.
     */
    public int getLoreLinesMaxAmount() {
        return loreLinesMaxAmount;
    }

    /**
     * Checks whether command blocks data should be cleared.
     *
     * @return true - will be removed, false - not.
     */
    public boolean isClearCommandBlocksData() {
        return clearCommandBlocksData;
    }

    /**
     * Checks whether item fixer is enabled.
     *
     * @return true - enabled, false - disabled.
     */
    public boolean isEnabled() {
        return enabled;
    }
}
