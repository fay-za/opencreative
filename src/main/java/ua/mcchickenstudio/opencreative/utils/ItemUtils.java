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

package ua.mcchickenstudio.opencreative.utils;

import io.papermc.paper.persistence.PersistentDataContainerView;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.block.CommandBlock;
import org.bukkit.block.EntityBlockStorage;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.*;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.util.io.BukkitObjectInputStream;
import org.bukkit.util.io.BukkitObjectOutputStream;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.yaml.snakeyaml.external.biz.base64Coder.Base64Coder;
import ua.mcchickenstudio.opencreative.OpenCreative;
import ua.mcchickenstudio.opencreative.coding.variables.ValueType;
import ua.mcchickenstudio.opencreative.settings.items.ItemFixerSettings;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static ua.mcchickenstudio.opencreative.utils.ErrorUtils.sendDebug;
import static ua.mcchickenstudio.opencreative.utils.ErrorUtils.sendDebugError;
import static ua.mcchickenstudio.opencreative.utils.MessageUtils.getLocaleItemDescription;
import static ua.mcchickenstudio.opencreative.utils.MessageUtils.getLocaleItemName;

/**
 * <h1>ItemUtils</h1>
 * This class represents an utils for changing items.
 */
public final class ItemUtils {

    private final static NamespacedKey ITEM_ID_KEY = new NamespacedKey(OpenCreative.getPlugin(), "oc_item_id");
    private final static NamespacedKey ITEM_TYPE_KEY = new NamespacedKey(OpenCreative.getPlugin(), "oc_item_type");
    private final static NamespacedKey ITEM_ENTITY_INVISIBLE = new NamespacedKey(OpenCreative.getPlugin(), "oc_item_entity_invisible");

    private final static NamespacedKey CODING_VALUE_KEY = new NamespacedKey(OpenCreative.getPlugin(), "oc_value_type");
    private final static NamespacedKey CODING_PARTICLE_TYPE_KEY = new NamespacedKey(OpenCreative.getPlugin(), "oc_particle_type");
    private final static NamespacedKey CODING_VARIABLE_TYPE_KEY = new NamespacedKey(OpenCreative.getPlugin(), "oc_variable_type");
    private final static NamespacedKey CODING_TARGET_TYPE_KEY = new NamespacedKey(OpenCreative.getPlugin(), "oc_target_type");
    private final static NamespacedKey CODING_DO_NOT_DROP_ME_KEY = new NamespacedKey(OpenCreative.getPlugin(), "oc_do_not_drop_me");
    private final static NamespacedKey CODING_LOCATION_X = new NamespacedKey(OpenCreative.getPlugin(), "oc_loc_x");
    private final static NamespacedKey CODING_LOCATION_Y = new NamespacedKey(OpenCreative.getPlugin(), "oc_loc_y");
    private final static NamespacedKey CODING_LOCATION_Z = new NamespacedKey(OpenCreative.getPlugin(), "oc_loc_z");

    public static @NotNull NamespacedKey getCodingValueKey() {
        return CODING_VALUE_KEY;
    }

    public static @NotNull NamespacedKey getCodingVariableTypeKey() {
        return CODING_VARIABLE_TYPE_KEY;
    }

    public static @NotNull NamespacedKey getCodingDoNotDropMeKey() {
        return CODING_DO_NOT_DROP_ME_KEY;
    }

    public static @NotNull NamespacedKey getCodingLocationX() {
        return CODING_LOCATION_X;
    }

    public static @NotNull NamespacedKey getCodingLocationY() {
        return CODING_LOCATION_Y;
    }

    public static @NotNull NamespacedKey getCodingLocationZ() {
        return CODING_LOCATION_Z;
    }

    public static @NotNull NamespacedKey getCodingParticleTypeKey() {
        return CODING_PARTICLE_TYPE_KEY;
    }

    public static @NotNull NamespacedKey getItemTypeKey() {
        return ITEM_TYPE_KEY;
    }

    public static @NotNull NamespacedKey getItemIdKey() {
        return ITEM_ID_KEY;
    }

    public static @NotNull NamespacedKey getCodingTargetTypeKey() {
        return CODING_TARGET_TYPE_KEY;
    }

    public static @NotNull NamespacedKey getItemEntityInvisible() {
        return ITEM_ENTITY_INVISIBLE;
    }

    public static @NotNull ItemStack setPersistentData(@NotNull ItemStack item, @NotNull NamespacedKey key, @NotNull String value) {
        ItemMeta meta = item.getItemMeta();
        PersistentDataContainer container = meta.getPersistentDataContainer();
        container.set(key, PersistentDataType.STRING, value);
        item.setItemMeta(meta);
        return item;
    }

    public static @NotNull ItemStack setPersistentData(@NotNull ItemStack item, @NotNull NamespacedKey key, double value) {
        ItemMeta meta = item.getItemMeta();
        PersistentDataContainer container = meta.getPersistentDataContainer();
        container.set(key, PersistentDataType.DOUBLE, value);
        item.setItemMeta(meta);
        return item;
    }

    /**
     * Returns item stack with name, description found in localization file and persistent data.
     **/
    public static @NotNull ItemStack createItem(@NotNull ItemStack item, @NotNull String localizationPath, @NotNull String persistentData) {

        ItemStack itemStack = clearItemFlags(item.clone());
        ItemMeta itemMeta = itemStack.getItemMeta();
        itemMeta.setDisplayName(getLocaleItemName(localizationPath + ".name"));
        itemMeta.setLore(getLocaleItemDescription(localizationPath + ".lore"));
        itemStack.setItemMeta(itemMeta);
        setPersistentData(itemStack, getItemTypeKey(), persistentData);
        return itemStack;

    }

    /**
     * Returns item stack with name, description found in localization file and persistent data.
     **/
    public static @NotNull ItemStack createItem(@NotNull Material material, int amount,
                                                @NotNull String localizationPath, @NotNull String persistentData) {

        amount = Math.max(1, amount);
        if (!material.isItem()) material = Material.REDSTONE;
        ItemStack itemStack = createItem(material, amount);
        ItemMeta itemMeta = itemStack.getItemMeta();
        itemMeta.setDisplayName(getLocaleItemName(localizationPath + ".name"));
        itemMeta.setLore(getLocaleItemDescription(localizationPath + ".lore"));
        itemStack.setItemMeta(itemMeta);
        setPersistentData(itemStack, getItemTypeKey(), persistentData);
        return itemStack;

    }

    /**
     * Returns item stack with name and description found in localization file.
     **/
    public static @NotNull ItemStack createItem(@NotNull Material material, int amount, @NotNull String localizationPath) {

        amount = Math.max(1, amount);
        if (!material.isItem()) material = Material.REDSTONE;
        ItemStack itemStack = createItem(material, amount);
        ItemMeta itemMeta = itemStack.getItemMeta();
        itemMeta.setDisplayName(getLocaleItemName(localizationPath + ".name"));
        itemMeta.setLore(getLocaleItemDescription(localizationPath + ".lore"));
        itemStack.setItemMeta(itemMeta);
        return itemStack;

    }

    /**
     * Returns item stack with name and description found in localization file.
     **/
    public static @NotNull ItemStack createItem(@NotNull ItemStack item, @NotNull String localizationPath) {

        ItemStack itemStack = clearItemFlags(item.clone());
        ItemMeta itemMeta = itemStack.getItemMeta();
        itemMeta.setDisplayName(getLocaleItemName(localizationPath + ".name"));
        itemMeta.setLore(getLocaleItemDescription(localizationPath + ".lore"));
        itemStack.setItemMeta(itemMeta);
        return itemStack;

    }

    /**
     * Returns item stack with name and description found in localization file.
     **/
    public static @NotNull ItemStack createItem(@NotNull Material material, int amount,
                                       @NotNull String localizationPath, Object value) {

        ItemStack itemStack = createItem(material, amount, localizationPath);
        ItemMeta meta = getOrCreateItemMeta(itemStack);
        PersistentDataContainer container = itemStack.getItemMeta().getPersistentDataContainer();
        container.set(CODING_VALUE_KEY, PersistentDataType.BYTE, (Byte) value);
        itemStack.setItemMeta(meta);

        return itemStack;

    }

    public static @NotNull ItemStack createItem(@NotNull Material material, int amount) {

        ItemStack itemStack = new ItemStack(material, amount);
        ItemMeta itemMeta = getOrCreateItemMeta(itemStack);
        itemMeta.setDisplayName(" ");
        itemStack.setItemMeta(itemMeta);
        return clearItemFlags(itemStack);

    }

    public static @NotNull ItemStack clearItemFlags(@NotNull ItemStack itemStack) {
        ItemMeta meta = itemStack.getItemMeta();
        meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES,
                ItemFlag.HIDE_ADDITIONAL_TOOLTIP,
                ItemFlag.HIDE_ARMOR_TRIM,
                ItemFlag.HIDE_DESTROYS,
                ItemFlag.HIDE_UNBREAKABLE,
                ItemFlag.HIDE_ENCHANTS,
                ItemFlag.HIDE_DYE,
                ItemFlag.HIDE_PLACED_ON,
                ItemFlag.HIDE_STORED_ENCHANTS,
                ItemFlag.HIDE_ITEM_SPECIFICS
        );
        if (!meta.hasAttributeModifiers()) {
            meta.addAttributeModifier(Attribute.GENERIC_ARMOR, new AttributeModifier(new NamespacedKey(OpenCreative.getPlugin(), "hide_attributes"), 0.0d, AttributeModifier.Operation.ADD_NUMBER));
        }
        itemStack.setItemMeta(meta);
        return itemStack;
    }

    public static @NotNull ItemStack clearItemMeta(@NotNull ItemStack itemStack) {
        ItemMeta meta = getOrCreateItemMeta(itemStack);
        meta.displayName(null);
        meta.lore(null);
        meta.removeEnchantments();
        Set<NamespacedKey> persistentKeys = new HashSet<>(meta.getPersistentDataContainer().getKeys());
        for (NamespacedKey key : persistentKeys) {
            meta.getPersistentDataContainer().remove(key);
        }
        itemStack.setItemMeta(meta);
        return itemStack;
    }

    public static boolean itemEquals(ItemStack itemStack, ItemStack itemStack2) {
        if (itemStack == null || itemStack2 == null) return false;
        if (itemStack.getType() != itemStack2.getType()) return false;
        if (itemStack.getItemMeta() != null && itemStack.getItemMeta() != null) {
            if (!itemStack.getItemMeta().getDisplayName().equalsIgnoreCase(itemStack2.getItemMeta().getDisplayName())) {
                return false;
            }
            return (!itemStack.getItemMeta().hasLore() || !itemStack2.getItemMeta().hasLore()) || (itemStack.getItemMeta().getLore().equals(itemStack2.getItemMeta().getLore()));
        }
        return true;
    }

    public static @NotNull ItemStack replacePlaceholderInName(@NotNull ItemStack item,
                                                              @NotNull String placeholder,
                                                              @NotNull Object value) {
        ItemMeta meta = item.getItemMeta();
        if (meta != null) {
            meta.setDisplayName(meta.getDisplayName().replace(placeholder, value.toString()));
            item.setItemMeta(meta);
        }
        return item;
    }

    public static @NotNull ItemStack replacePlaceholderInLore(@NotNull ItemStack item,
                                                     @NotNull String placeholder,
                                                     @NotNull Object value) {
        ItemMeta meta = item.getItemMeta();
        if (meta != null && meta.getLore() != null) {
            List<String> newLore = new ArrayList<>();
            List<String> oldLore = meta.getLore();
            for (String loreLine : oldLore) {
                newLore.add(loreLine.replace(placeholder, value.toString()));
            }
            meta.setLore(newLore);
            item.setItemMeta(meta);
        }
        return item;
    }

    public static @NotNull ItemStack setDisplayName(@NotNull ItemStack item, @Nullable String displayName) {
        if (item.hasItemMeta()) {
            ItemMeta meta = item.getItemMeta();
            meta.setDisplayName(displayName);
            item.setItemMeta(meta);
        }
        return item;
    }

    public static @NotNull ItemStack setLore(@NotNull ItemStack item, @Nullable List<String> lore) {
        if (item.hasItemMeta()) {
            ItemMeta meta = item.getItemMeta();
            meta.setLore(lore);
            item.setItemMeta(meta);
        }
        return item;
    }

    public static @NotNull ItemStack addLoreAtBegin(@NotNull ItemStack item,
                                                    @NotNull String loreLine) {
        if (loreLine.isEmpty()) return item;
        if (item.hasItemMeta()) {
            ItemMeta meta = item.getItemMeta();
            List<String> lore = new ArrayList<>();
            lore.add(loreLine);
            if (meta.hasLore()) {
                lore.addAll(meta.getLore());
            }
            meta.setLore(lore);
            item.setItemMeta(meta);
        }
        return item;
    }

    public static @NotNull ItemStack addLoreAtEnd(@NotNull ItemStack item,
                                                  @NotNull String loreLine) {
        if (loreLine.isEmpty()) return item;
        if (item.hasItemMeta()) {
            ItemMeta meta = item.getItemMeta();
            List<String> lore = new ArrayList<>();
            if (meta.hasLore()) {
                lore = meta.getLore();
            }
            lore.add(loreLine);
            meta.setLore(lore);
            item.setItemMeta(meta);
        }
        return item;
    }

    public static @NotNull ValueType getValueType(@NotNull ItemStack item) {
        String typeString = getPersistentData(item, getCodingValueKey());
        try {
            return ValueType.valueOf(typeString);
        } catch (IllegalArgumentException e) {
            return ValueType.ITEM;
        }
    }

    public static @NotNull String getItemType(@NotNull ItemStack item) {
        return getPersistentData(item, getItemTypeKey());
    }

    public static @NotNull String getPersistentData(@NotNull ItemStack item, @NotNull NamespacedKey key) {
        if (item.getItemMeta() == null) {
            return "";
        }
        PersistentDataContainer container = item.getItemMeta().getPersistentDataContainer();
        if (!container.has(key)) {
            return "";
        }
        String dataType = container.get(key, PersistentDataType.STRING);
        if (dataType == null) {
            return "";
        }
        return dataType;
    }

    public static @NotNull ItemStack getItemWithIgnoreData(@NotNull ItemStack item,
                                                           boolean removeAmount, boolean removeName,
                                                           boolean removeLore, boolean removeFlags,
                                                           boolean removeEnchantments, boolean removeMaterial,
                                                           boolean removeDurability) {
        ItemStack newItem = item.clone();
        ItemMeta meta = newItem.getItemMeta();
        if (removeAmount) {
            newItem.setAmount(1);
        }
        if (meta != null) {
            if (removeName) {
                meta.displayName(Component.text(""));
            }
            if (removeLore) {
                meta.lore(new ArrayList<>());
            }
            if (removeDurability) {
                if (meta instanceof Damageable damageable) {
                    damageable.setDamage(0);
                }
            }
            newItem.setItemMeta(meta);
        }
        if (removeMaterial) {
            newItem.setType(Material.DIAMOND);
        }
        if (removeEnchantments) {
            newItem.removeEnchantments();
        }
        if (removeFlags) {
            for (ItemFlag flag : newItem.getItemFlags()) {
                newItem.removeItemFlags(flag);
            }
        }
        return newItem;
    }

    public static @NotNull ItemMeta getOrCreateItemMeta(@NotNull ItemStack item) {
        return item.hasItemMeta() ? item.getItemMeta() : Bukkit.getItemFactory().getItemMeta(item.getType());
    }

    /**
     * Removes bad things from item: enchants with big level,
     * attribute modifiers, books with a lot of pages,
     * containers with containers; or replaces items with air.
     *
     * @param item item to fix.
     */
    public static ItemStack fixItem(@NotNull ItemStack item) {
        ItemFixerSettings settings = OpenCreative.getSettings().getItemFixerSettings();
        if (!settings.isEnabled()) return item;
        return fixItem(item,
                settings.getDisplayNameMaxLength(),
                settings.getLoreLineMaxLength(),
                settings.getLoreLinesMaxAmount(),
                settings.getMaxEnchantLevel(),
                settings.getMaxBookPagesAmount(),
                settings.isRemoveClickableBooks(),
                settings.getContainerBigItemsLimit(),
                settings.getEntitiesMaxAmount(),
                settings.isRemoveCustomSpawnEggs(),
                settings.isRemoveBossSpawnEggs(),
                settings.isRemoveAttributes(),
                settings.isClearCommandBlocksData()
        );
    }

    /**
     * Removes bad things from item: enchants with big level,
     * attribute modifiers, books with a lot of pages,
     * containers with containers; or replaces items with air.
     *
     * @param item                   item to fix.
     * @param displayNameMaxLength   maximum length of item's display name.
     * @param loreLineMaxLength      maximum length of item's lore line.
     * @param loreLinesLimit         maximum amount of item's lore lines.
     * @param maxEnchantLevel        maximum enchant level.
     * @param bookPagesLimit         limit of books pages.
     * @param removeClickableBooks   remove clickable components in books or not.
     * @param containerBigItemsLimit limit of big items (books, containers) in container.
     * @param entitiesLimit          limit of entities in entities blocks storage (beehives).
     * @param removeCustomEggs       removes custom spawn eggs.
     * @param removeBossEggs         removes boss spawn eggs.
     * @param removeAttributes       removes attribute modifiers (scale).
     * @param clearCommandBlocksData removes commands from command blocks.
     */
    @SuppressWarnings("deprecation")
    public static @NotNull ItemStack fixItem(@NotNull ItemStack item,
                                    int displayNameMaxLength,
                                    int loreLineMaxLength,
                                    int loreLinesLimit,
                                    int maxEnchantLevel,
                                    int bookPagesLimit, boolean removeClickableBooks,
                                    int containerBigItemsLimit, int entitiesLimit, boolean removeCustomEggs,
                                    boolean removeBossEggs, boolean removeAttributes, boolean clearCommandBlocksData) {
        try {
            ItemMeta meta = item.getItemMeta();
            if (meta == null) return item;
            if (meta.hasEnchants()) {
                int enchantsLimit = 15;
                if (meta.getEnchants().size() > enchantsLimit) {
                    sendDebug("[ITEMS] Destroyed item with too many enchantments.");
                    item.setType(Material.AIR);
                    item.setItemMeta(null);
                } else {
                    List<Enchantment> badEnchants = new ArrayList<>();
                    for (Enchantment enchant : meta.getEnchants().keySet()) {
                        if (meta.getEnchantLevel(enchant) > maxEnchantLevel) {
                            badEnchants.add(enchant);
                        }
                    }
                    if (!badEnchants.isEmpty()) {
                        for (Enchantment enchantment : badEnchants) {
                            meta.removeEnchant(enchantment);
                        }
                        item.setItemMeta(meta);
                        sendDebug("[ITEMS] Cleared enchants");
                    }
                }
            }
            if (meta.hasAttributeModifiers() && meta.getAttributeModifiers() != null && removeAttributes) {
                Set<Attribute> attributes = meta.getAttributeModifiers().keySet();
                int attributesLimit = 20;
                if (meta.getEnchants().size() > attributesLimit) {
                    sendDebug("[ITEMS] Destroyed item with too many attributes.");
                    item.setType(Material.AIR);
                    item.setItemMeta(null);
                    return item;
                }
                for (Attribute attribute : attributes) {
                    if (attribute != Attribute.GENERIC_ARMOR) {
                        meta.removeAttributeModifier(attribute);
                        sendDebug("[ITEMS] Cleared attributes");
                    }
                }
            }
            if (!meta.getPersistentDataContainer().isEmpty()) {
                int persistentDataKeysLimit = 15;
                if (meta.getPersistentDataContainer().getKeys().size() > persistentDataKeysLimit) {
                    sendDebug("[ITEMS] Destroyed item with too many persistent data keys.");
                    item.setType(Material.AIR);
                    item.setItemMeta(null);
                    return item;
                }
                if (getPersistentDataAmount(item) > OpenCreative.getSettings().getItemFixerSettings().getMaxPersistentDataSize()) {
                    for (NamespacedKey key : item.getPersistentDataContainer().getKeys()) {
                        meta.getPersistentDataContainer().remove(key);
                    }
                    item.setItemMeta(meta);
                    sendDebug("[ITEMS] Cleared persistent data");
                }
            }
            if (meta.hasDisplayName()) {
                String displayName = meta.getDisplayName(); // Using legacy, because components are heavy :(
                if (!displayName.isEmpty()) {
                    if (ChatColor.stripColor(displayName).length() > displayNameMaxLength) {
                        displayName = displayName.substring(0, displayNameMaxLength);
                        meta.setDisplayName(displayName);
                        item.setItemMeta(meta);
                        sendDebug("[ITEMS] Fixed too long display name");
                    }
                }
            }
            if (meta.hasLore()) {
                List<String> lore = item.getLore(); // Using legacy, because components are heavy :(
                if (lore != null) {
                    if (lore.size() > loreLinesLimit) {
                        meta.lore(null);
                        item.setItemMeta(meta);
                        sendDebug("[ITEMS] Fixed too many lore lines");
                    }
                    List<String> newLore = new ArrayList<>(lore);
                    int loreIndex = 0;
                    boolean needsToChange = false;
                    for (String line : lore) {
                        if (ChatColor.stripColor(line).length() > loreLineMaxLength) {
                            needsToChange = true;
                            line = line.substring(0, loreLineMaxLength);
                            newLore.set(loreIndex, line);
                            sendDebug("[ITEMS] Fixed too long lore line");
                        }
                        loreIndex++;
                    }
                    if (needsToChange) {
                        meta.setLore(newLore);
                        item.setItemMeta(meta);
                    }
                }
            }
            switch (meta) {
                case BlockStateMeta blockMeta when blockMeta.getBlockState() instanceof InventoryHolder holder -> {
                    // If item is Chest or Shulker
                    int insideBadItems = 0;
                    ItemStack[] items = holder.getInventory().getContents();
                    for (ItemStack insideItem : items) {
                        if (insideItem == null) continue;
                        insideBadItems += getInsideBadItemsAmount(insideItem, containerBigItemsLimit);
                        if (insideBadItems >= containerBigItemsLimit) {
                            item.setType(Material.AIR);
                            item.setItemMeta(null);
                            sendDebug("[ITEMS] Destroyed container with a lot of items");
                            break;
                        }
                    }
                }
                case BlockStateMeta blockMeta when blockMeta.getBlockState() instanceof CommandBlock block -> {
                    if (clearCommandBlocksData) {
                        if (!block.getCommand().isEmpty()) block.setCommand(null);
                        blockMeta.setBlockState(block);
                        item.setItemMeta(blockMeta);
                    }
                }
                case BlockStateMeta blockMeta when blockMeta.getBlockState() instanceof EntityBlockStorage<?> storage -> {
                    if (storage.getEntityCount() > entitiesLimit) {
                        item.setType(Material.AIR);
                        item.setItemMeta(null);
                        sendDebug("[ITEMS] Destroyed entities block storage with a lot of entities");
                    }
                }
                case BookMeta book -> {
                    if (book.pages().size() > bookPagesLimit) {
                        item.setType(Material.AIR);
                        item.setItemMeta(null);
                        sendDebug("[ITEMS] Destroyed book with a lot of pages");
                    } else if (removeClickableBooks) {
                        List<Component> pages = book.pages();
                        boolean cleared = false;
                        for (int i = 0; i < pages.size(); i++) {
                            Component component = pages.get(i);
                            if (component.clickEvent() != null) cleared = true;
                            component = component.clickEvent() != null ? component.clickEvent(null) : component;
                            book.page(i + 1, component);
                        }
                        if (cleared) {
                            sendDebug("[ITEMS] Cleared book with clickable components");
                            item.setItemMeta(book);
                        }
                    }
                }
                case SpawnEggMeta egg when removeCustomEggs || removeBossEggs -> {
                    if (egg.getCustomSpawnedType() != null) {
                        item.setType(Material.AIR);
                        item.setItemMeta(null);
                        sendDebug("[ITEMS] Destroyed custom spawn egg");
                    }
                    if (removeBossEggs) {
                        if (item.getType() == Material.ENDER_DRAGON_SPAWN_EGG
                                || item.getType() == Material.WITHER_SPAWN_EGG) {
                            item.setType(Material.AIR);
                            item.setItemMeta(null);
                            sendDebug("[ITEMS] Destroyed boss spawn egg");
                        }
                    }
                }
                default -> {
                }
            }
        } catch (Exception exception) {
            sendDebugError("[ITEMS] Can't fix item: " + item, exception);
        }
        return item;
    }

    public static int getInsideBadItemsAmount(@NotNull ItemStack item, int limit) {
        if (item.getItemMeta() instanceof BookMeta) {
            return 1;
        }
        if (!(item.getItemMeta() instanceof BlockStateMeta blockMeta))  {
            return 0;
        }
        if (!(blockMeta.getBlockState() instanceof InventoryHolder holder)) {
            return 0;
        }
        int insideContainers = 1;
        ItemStack[] items = holder.getInventory().getContents();
        for (ItemStack insideItem : items) {
            if (insideItem == null) continue;
            insideContainers += getInsideBadItemsAmount(insideItem, 1);
            if (insideContainers >= limit) break;
        }
        if (insideContainers >= 1) {
            item.setType(Material.AIR);
            item.setItemMeta(null);
            sendDebug("[ITEMS] Destroyed container with a lot of items");
        }
        return insideContainers;
    }

    public static int getPersistentDataAmount(@NotNull ItemStack item) {
        int length = 0;
        int limit = OpenCreative.getSettings().getItemFixerSettings().getMaxPersistentDataSize();
        PersistentDataContainerView container = item.getPersistentDataContainer();
        for (NamespacedKey key : container.getKeys()) {
            /*
             * We do that, because we can't access the type
             * of persistent data directly.
             */
            length += key.getKey().length();
            if (container.has(key, PersistentDataType.STRING)) {
                String val = container.get(key, PersistentDataType.STRING);
                if (val != null) length += val.length();
            } else if (container.has(key, PersistentDataType.BYTE_ARRAY)) {
                byte[] val = container.get(key, PersistentDataType.BYTE_ARRAY);
                if (val != null) length += val.length;
            } else if (container.has(key, PersistentDataType.INTEGER)) {
                length += Integer.BYTES;
            } else if (container.has(key, PersistentDataType.LONG)) {
                length += Long.BYTES;
            } else if (container.has(key, PersistentDataType.BYTE)) {
                length += Byte.BYTES;
            } else if (container.has(key, PersistentDataType.BYTE_ARRAY)) {
                byte[] val = container.get(key, PersistentDataType.BYTE_ARRAY);
                if (val != null) length += val.length;
            } else if (container.has(key, PersistentDataType.DOUBLE)) {
                length += Double.BYTES;
            } else if (container.has(key, PersistentDataType.FLOAT)) {
                length += Float.BYTES;
            } else if (container.has(key, PersistentDataType.SHORT)) {
                length += Short.BYTES;
            }
            if (length >= limit) break;
        }
        return length;
    }

    /**
     * Saves item as byte array in text.
     *
     * @param item item to serialize.
     * @return string of byte array, that can be used in configs, or empty text "".
     */
    public static @NotNull String saveItemAsByteArray(@Nullable ItemStack item) {
        try {
            if (item == null || item.isEmpty()) return "";
            ByteArrayOutputStream arrayOutputStream = new ByteArrayOutputStream();
            BukkitObjectOutputStream objectOutputStream = new BukkitObjectOutputStream(arrayOutputStream);
            objectOutputStream.writeObject(item);
            return Base64Coder.encodeLines(arrayOutputStream.toByteArray());
        } catch (Exception error) {
            sendDebugError("Failed to serialize item to bytes string: " + item, error);
            return "";
        }
    }

    /**
     * Loads item from byte array in text.
     *
     * @param text text with item's byte array to deserialize.
     * @return item from byte array, or empty (air x0) item.
     */
    public static @NotNull ItemStack loadItemFromByteArray(@NotNull String text) {
        try {
            if (text.isEmpty()) return ItemStack.empty();
            ByteArrayInputStream arrayInputStream = new ByteArrayInputStream(Base64Coder.decodeLines(text));
            BukkitObjectInputStream objectInputStream = new BukkitObjectInputStream(arrayInputStream);
            return (ItemStack) objectInputStream.readObject();
        } catch (Exception error) {
            sendDebugError("Failed to deserialize item from bytes string: " + text, error);
            return ItemStack.empty();
        }
    }

    /**
     * Checks whether item should be saved as bytes array instead of just material.
     *
     * @param item item to check.
     * @return true - item can have special data, so it should be saved as bytes array
     * to contain all information, false - can be saved as material.
     */
    public static boolean doesItemRequireSpecialData(@NotNull ItemStack item) {
        if (item.getItemMeta() instanceof SkullMeta) {
            return true;
        }
        if (item.getItemMeta() instanceof BannerMeta banner) {
            if (!banner.getPatterns().isEmpty()) return true;
        }
        if (item.getItemMeta() instanceof ShieldMeta shield) {
            if (shield.getBaseColor() != null) return true;
        }
        if (item.getItemMeta() instanceof LeatherArmorMeta armor) {
            if (armor.isDyed()) return true;
        }
        if (item.getItemMeta() instanceof ArmorMeta armor) {
            if (armor.hasTrim()) return true;
        }
        if (item.getItemMeta() instanceof PotionMeta potion) {
            return potion.hasColor();
        }
        return false;
    }

}
