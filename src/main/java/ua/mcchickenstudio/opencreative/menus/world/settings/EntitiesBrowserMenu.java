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

import org.apache.commons.lang3.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.*;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import ua.mcchickenstudio.opencreative.menus.ListBrowserMenu;
import ua.mcchickenstudio.opencreative.menus.buttons.ParameterButton;
import ua.mcchickenstudio.opencreative.menus.world.WorldMenu;
import ua.mcchickenstudio.opencreative.planets.Planet;
import ua.mcchickenstudio.opencreative.settings.Sounds;
import ua.mcchickenstudio.opencreative.utils.MessageUtils;
import ua.mcchickenstudio.opencreative.utils.PlayerUtils;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;

import static ua.mcchickenstudio.opencreative.utils.ItemUtils.*;
import static ua.mcchickenstudio.opencreative.utils.MessageUtils.getLocaleMessage;

/**
 * This class represents a menu, that displays specified list of entities in world.
 * Player can teleport to entity or remove it.
 */
public final class EntitiesBrowserMenu extends ListBrowserMenu<Entity> implements WorldMenu {

    private final Planet planet;
    private final List<ParameterButton> buttons = new ArrayList<>();
    private final ItemStack REMOVE_ALL = createItem(Material.BARRIER, 1, "menus.entities-browser.items.remove-all");
    private final ItemStack BACK_TO_SETTINGS = createItem(Material.ARROW, 1, "menus.entities-browser.items.back");

    public EntitiesBrowserMenu(Player player, Planet planet) {
        super(player, getLocaleMessage("menus.entities-browser.title", false),
                PlacementLayout.BOTTOM_NO_DECORATION, new int[]{45, 48, 50}, new int[]{46, 52});
        this.planet = planet;
    }

    @Override
    protected ItemStack getElementIcon(Entity object) {
        if (object instanceof Entity entity) {
            return createEntityItem(entity);
        }
        return null;
    }

    private ItemStack createEntityItem(Entity entity) {
        ItemStack item = createItem(getEntityMaterial(entity), 1, "menus.entities-browser.items.entity");
        replacePlaceholderInLore(item, "%name%", entity.getName().substring(0, Math.min(20, entity.getName().length())));
        replacePlaceholderInLore(item, "%type%", StringUtils.capitalize(entity.getType().name().toLowerCase().replace('_', ' ')));
        replacePlaceholderInLore(item, "%x%", entity.getLocation().getBlockX());
        replacePlaceholderInLore(item, "%y%", entity.getLocation().getBlockY());
        replacePlaceholderInLore(item, "%z%", entity.getLocation().getBlockZ());
        setPersistentData(item, getItemTypeKey(), entity.getUniqueId().toString());
        return item;
    }

    private Material getEntityMaterial(Entity entity) {
        Material material = Material.getMaterial(entity.getType().name());
        if (material == null && entity.getType().isAlive()) {
            material = Material.getMaterial(entity.getType().name() + "_SPAWN_EGG");
        } else if (entity instanceof Item item) {
            material = item.getItemStack().getType();
        } else if (entity instanceof ItemDisplay item) {
            material = item.getItemStack().getType();
        } else if (entity instanceof TextDisplay) {
            material = Material.JUNGLE_SIGN;
        } else if (entity instanceof BlockDisplay) {
            material = Material.GRASS_BLOCK;
        }
        if (material != null && material.isItem()) {
            return material;
        }
        return Material.HEAVY_CORE;
    }

    @Override
    protected void fillOtherItems() {
        ParameterButton type = new ParameterButton(
                "all",
                List.of("all", "friendly", "monsters", "items", "transport", "decoration", "not-living"),
                "type",
                "menus.all-worlds",
                "menus.entities-browser.items.type",
                List.of(Material.HOPPER, Material.PIGLIN_HEAD, Material.ZOMBIE_HEAD, Material.POISONOUS_POTATO, Material.BIRCH_CHEST_BOAT, Material.PAINTING, Material.PUMPKIN_SEEDS)
        );
        buttons.add(type);
        setItem(47, createItem(Material.RED_STAINED_GLASS_PANE, 1));
        setItem(48, type.getItem());
        setItem(50, REMOVE_ALL);
        setItem(51, createItem(Material.RED_STAINED_GLASS_PANE, 1));
    }

    @Override
    protected void onCharmsBarClick(InventoryClickEvent event) {
        event.setCancelled(true);
        ItemStack item = event.getCurrentItem();
        if (item == null) {
            return;
        }
        for (ParameterButton button : buttons) {
            if (itemEquals(item, button.getItem(true))) {
                if (event.getRawSlot() == 48) {
                    button.next();
                    elements.clear();
                    switch (button.getCurrentChoice()) {
                        case 2 ->
                                elements.addAll(new ArrayList<>(getElements()).stream().filter(entity -> entity instanceof LivingEntity && (!(entity instanceof Monster))).toList());
                        case 3 ->
                                elements.addAll(new ArrayList<>(getElements()).stream().filter(entity -> entity instanceof Enemy).toList());
                        case 4 ->
                                elements.addAll(new ArrayList<>(getElements()).stream().filter(entity -> entity instanceof Item).toList());
                        case 5 ->
                                elements.addAll(new ArrayList<>(getElements()).stream().filter(entity -> entity instanceof Vehicle).toList());
                        case 6 ->
                                elements.addAll(new ArrayList<>(getElements()).stream().filter(entity -> entity instanceof Painting || entity instanceof ArmorStand || entity instanceof TextDisplay || entity instanceof ItemDisplay || entity instanceof BlockDisplay || entity instanceof ItemFrame).toList());
                        case 7 ->
                                elements.addAll(new ArrayList<>(getElements()).stream().filter(entity -> !(entity instanceof LivingEntity)).toList());
                        default -> elements.addAll(getElements());
                    }
                    fillElements(getCurrentPage());
                    fillArrowsItems(getCurrentPage());
                    setItem(48, button.getItem());
                    Sounds.MENU_ENTITIES_BROWSER_SORT.play(getPlayer());
                    return;
                }
            }
        }
        if (itemEquals(item, BACK_TO_SETTINGS) && planet.isOwner(getPlayer())) {
            new WorldSettingsMenu(planet, getPlayer()).open(getPlayer());
        } else if (itemEquals(item, REMOVE_ALL)) {
            if (elements.isEmpty()) return;
            int count = elements.size();
            for (Entity element : new ArrayList<>(elements)) {
                if (element instanceof Entity entity) {
                    elements.remove(entity);
                    entity.remove();
                }
            }
            for (Player p : planet.getPlayers()) {
                if (planet.getWorldPlayers().canBuild(p)) {
                    p.sendMessage(MessageUtils.getPlayerLocaleMessage("menus.entities-browser.removed-all", getPlayer()).replace("%count%", String.valueOf(count)));
                }
            }
            elements.removeIf(Entity::isDead);
            fillElements(getCurrentPage());
            fillArrowsItems(getCurrentPage());
        }
    }

    @Override
    protected void onElementClick(InventoryClickEvent event) {
        ItemStack currentItem = event.getCurrentItem();
        event.setCancelled(true);
        if (currentItem == null) {
            return;
        }
        if (!planet.isLoaded() || !(planet.getWorldPlayers().canBuild(getPlayer()))) {
            getPlayer().closeInventory();
            return;
        }
        String uuidString = getItemType(currentItem);
        if (uuidString.isEmpty()) {
            return;
        }
        UUID uuid = UUID.fromString(uuidString);
        Entity entity = Bukkit.getEntity(uuid);
        if (entity == null) {
            elements.removeIf(e -> e.getUniqueId().equals(uuid));
            elements.removeIf(Entity::isDead);
            fillElements(getCurrentPage());
            fillArrowsItems(getCurrentPage());
            return;
        }
        switch (event.getClick()) {
            case LEFT -> {
                if (PlayerUtils.isEntityInDevPlanet(getPlayer())) {
                    Sounds.PLAYER_FAIL.play(getPlayer());
                    return;
                }
                getPlayer().closeInventory();
                Sounds.WORLD_TELEPORT_TO_ENTITY.play(getPlayer());
                getPlayer().teleport(entity.getLocation());
            }
            case RIGHT -> {
                if (PlayerUtils.isEntityInDevPlanet(getPlayer())) {
                    Sounds.PLAYER_FAIL.play(getPlayer());
                    return;
                }
                Sounds.WORLD_TELEPORT_ENTITY_TO_ME.play(getPlayer());
                entity.teleport(getPlayer().getLocation());
                if (elements.removeIf(Entity::isDead)) {
                    fillElements(getCurrentPage());
                    fillArrowsItems(getCurrentPage());
                }
            }
            case SHIFT_LEFT -> {
                for (Player p : planet.getPlayers()) {
                    if (planet.getWorldPlayers().canBuild(p)) {
                        p.sendMessage(MessageUtils.getPlayerLocaleMessage("menus.entities-browser.removed", getPlayer())
                                .replace("%name%", entity.getName().substring(0, Math.min(20, entity.getName().length())))
                                .replace("%x%", String.valueOf(entity.getLocation().getBlockX()))
                                .replace("%y%", String.valueOf(entity.getLocation().getBlockY()))
                                .replace("%z%", String.valueOf(entity.getLocation().getBlockZ())));
                    }
                }
                Sounds.WORLD_REMOVE_ENTITY.play(getPlayer());
                elements.remove(entity);
                entity.remove();
                elements.removeIf(Entity::isDead);
                fillElements(getCurrentPage());
                fillArrowsItems(getCurrentPage());
                getPlayer().updateInventory();
            }
        }
    }

    @Override
    protected void fillArrowsItems(int currentPage) {
        if (elements.isEmpty()) {
            setItem(getNoElementsPageButtonSlot(), getNoElementsButton());
            setItem(getPreviousPageButtonSlot(), planet.isOwner(getPlayer()) ? BACK_TO_SETTINGS : DECORATION_ITEM);
            setItem(getNextPageButtonSlot(), DECORATION_ITEM);
            setItem(50, DECORATION_ITEM);
        } else {
            int maxPagesAmount = getPages();
            if (currentPage > maxPagesAmount || currentPage < 1) {
                this.setCurrentPage(1);
            }
            setItem(getPreviousPageButtonSlot(), currentPage > 1 ? getPreviousPageButton() : planet.isOwner(getPlayer()) ? BACK_TO_SETTINGS : DECORATION_ITEM);
            setItem(getNextPageButtonSlot(), currentPage < maxPagesAmount ? getNextPageButton() : DECORATION_ITEM);
            setItem(50, REMOVE_ALL);
        }
    }

    @Override
    public List<Entity> getElements() {
        if (!planet.isLoaded()) {
            return List.of();
        }
        List<Entity> entities = new ArrayList<>(planet.getTerritory().getWorld().getEntities().stream().filter(e -> !(e instanceof Player)).toList());
        entities.sort(Comparator.comparingInt(Entity::getTicksLived).reversed());
        return new ArrayList<>(entities);
    }

    @Override
    protected ItemStack getNextPageButton() {
        return replacePlaceholderInLore(createItem(Material.SPECTRAL_ARROW, getCurrentPage() + 1, "menus.entities-browser.items.next-page"), "%page%", getCurrentPage() + 1);
    }

    @Override
    protected ItemStack getPreviousPageButton() {
        return replacePlaceholderInLore(createItem(Material.ARROW, Math.max(1, getCurrentPage() - 1), "menus.entities-browser.items.previous-page"), "%page%", getCurrentPage() - 1);
    }

    @Override
    protected ItemStack getNoElementsButton() {
        return createItem(Material.BARRIER, 1, "menus.entities-browser.items.no-entities");
    }

    @Override
    public void onOpen(@NotNull InventoryOpenEvent event) {
        Sounds.MENU_OPEN_ENTITIES_BROWSER.play(getPlayer());
    }

    @Override
    public Planet getPlanet() {
        return planet;
    }
}
