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

package ua.mcchickenstudio.opencreative.coding.menus;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import static ua.mcchickenstudio.opencreative.utils.ItemUtils.createItem;
import static ua.mcchickenstudio.opencreative.utils.ItemUtils.getItemType;

public enum MenusCategory {

    FIGHTING(Material.NETHERITE_SWORD),
    INTERACTION(Material.GRASS_BLOCK),
    INVENTORY(Material.DARK_OAK_CHEST_BOAT),
    MOVEMENT(Material.CHAINMAIL_BOOTS),
    BLOCKS(Material.BRICKS),
    WORLD(Material.CHAIN_COMMAND_BLOCK),
    ENTITY(Material.VILLAGER_SPAWN_EGG),
    COMMUNICATION(Material.JUNGLE_SIGN),
    STATE(Material.ANVIL),
    LINES(Material.CALIBRATED_SCULK_SENSOR),
    APPEARANCE(Material.ARMOR_STAND),
    EVENTS(Material.END_CRYSTAL),
    PARAMS(Material.APPLE),
    PLAYER(Material.PLAYER_HEAD),
    OTHER(Material.PUMPKIN_SEEDS),

    CUSTOM(Material.DIAMOND),
    STRUCTURE_ONLY(Material.GOLD_INGOT),
    MANIPULATIONS(Material.IRON_INGOT),

    ENTITY_STATE(Material.ANVIL),
    ENTITY_PARAMS(Material.APPLE),
    ENTITY_MOVEMENT(Material.NETHERITE_BOOTS),
    ENTITY_INTERACTION(Material.DARK_OAK_CHEST_BOAT),
    ENTITY_FIGHTING(Material.NETHERITE_SWORD),
    ENTITY_APPEARANCE(Material.ARMOR_STAND),
    ENTITY_INVENTORY(Material.DARK_OAK_CHEST_BOAT),

    WORLD_OTHER(Material.AMETHYST_CLUSTER),
    WORLD_INVENTORY(Material.MANGROVE_CHEST_BOAT),
    WORLD_BLOCKS(Material.CAMPFIRE),

    TEXT_OPERATIONS(Material.BOOK),
    NUMBER_OPERATIONS(Material.SLIME_BALL),
    LOCATION_OPERATIONS(Material.PAPER),
    ITEM_OPERATIONS(Material.GLOW_ITEM_FRAME),
    LIST_OPERATIONS(Material.BOOKSHELF),
    MAP_OPERATIONS(Material.CHEST_MINECART),
    VECTOR_OPERATIONS(Material.PRISMARINE_SHARD),

    REPEATS(Material.PRISMARINE_CRYSTALS),
    CONTROLLER(Material.COMPASS);

    private final Material material;

    MenusCategory(Material material) {
        this.material = material;
    }

    public static MenusCategory getByIcon(ItemStack icon) {
        if (icon == null) return null;
        String type = getItemType(icon);
        for (MenusCategory category : values()) {
            if (category.name().equalsIgnoreCase(type)) {
                return category;
            }
        }
        return null;
    }

    public ItemStack getItem(String blockCategory) {
        return createItem(material, 1, "items.developer.categories." + blockCategory + "." + this.name().toLowerCase(), this.name().toLowerCase());
    }

}
