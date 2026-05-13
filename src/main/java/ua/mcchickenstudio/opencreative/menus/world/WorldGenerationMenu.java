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

package ua.mcchickenstudio.opencreative.menus.world;

import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import ua.mcchickenstudio.opencreative.OpenCreative;
import ua.mcchickenstudio.opencreative.menus.AbstractMenu;
import ua.mcchickenstudio.opencreative.menus.buttons.ParameterButton;
import ua.mcchickenstudio.opencreative.settings.Sounds;
import ua.mcchickenstudio.opencreative.utils.world.WorldUtils;
import ua.mcchickenstudio.opencreative.utils.world.generators.*;

import java.util.*;

import static ua.mcchickenstudio.opencreative.utils.ItemUtils.createItem;
import static ua.mcchickenstudio.opencreative.utils.ItemUtils.itemEquals;
import static ua.mcchickenstudio.opencreative.utils.MessageUtils.getLocaleMessage;

public final class WorldGenerationMenu extends AbstractMenu {

    private final Player player;
    private final ParameterButton generatorButton;
    private final ParameterButton environmentButton;
    private final ParameterButton generateStructures;
    private ParameterButton biomePicker;
    private boolean ignoreLimit;
    private final ItemStack createButton = createItem(Material.PUFFERFISH_BUCKET, 1, "menus.world-creation.items.create");

    public WorldGenerationMenu(Player player, String generator, String environment, boolean generateStructures) {
        super(3, getLocaleMessage("menus.world-creation.title", false));
        this.player = player;
        this.generatorButton = new ParameterButton(generator,
                WorldGenerators.getInstance().getGeneratorsIDs(),
                "type", "menus.world-creation", "menus.world-creation.items.type",
                WorldGenerators.getInstance().getGeneratorsMaterials());
        this.environmentButton = new ParameterButton(environment, List.of("normal", "nether", "the_end"), "environment", "menus.world-creation", "menus.world-creation.items.environment", List.of(Material.GRASS_BLOCK, Material.NETHERRACK, Material.END_STONE));
        this.generateStructures = new ParameterButton(generateStructures, List.of(false, true), "generate-structures", "menus.world-creation", "menus.world-creation.items.generate-structures", List.of(Material.DECORATED_POT, Material.BOOKSHELF));
    }

    public WorldGenerationMenu(Player player) {
        this(player, "flat", "normal", true);
    }

    public void setIgnoreLimit(boolean ignoreLimit) {
        this.ignoreLimit = ignoreLimit;
    }

    @Override
    public void fillItems(Player player) {
        setItem(10, generatorButton.getItem());
        setItem(11, environmentButton.getItem());
        setItem(12, generateStructures.getItem());
        WorldGenerator generator = WorldGenerators.getInstance().getById(generatorButton.getCurrentValue().toString());
        updateBiomePicker(generator);
        setItem(7, createItem(Material.LIME_STAINED_GLASS_PANE, 1));
        setItem(16, createButton);
        setItem(25, createItem(Material.LIME_STAINED_GLASS_PANE, 1));
    }

    @Override
    public void onClick(@NotNull InventoryClickEvent event) {
        if (!isPlayerClicked(event) || !isClickedInMenuSlots(event)) {
            return;
        }
        if (itemEquals(DECORATION_ITEM, event.getCurrentItem())) {
            event.setCancelled(true);
            return;
        }
        event.setCancelled(true);
        switch (event.getRawSlot()) {
            case 10 -> {
                generatorButton.next();
                setItem(event.getRawSlot(), generatorButton.getItem());
                WorldGenerator generator = WorldGenerators.getInstance().getById(generatorButton.getCurrentValue().toString());
                if (generator != null) {
                    setItem(11, generator instanceof EnvironmentCapable ? environmentButton.getItem() : AIR_ITEM);
                    setItem(12, generator instanceof StructuresCapable ? generateStructures.getItem() : AIR_ITEM);
                    updateBiomePicker(generator);
                }
                Sounds.MENU_GENERATION_CHANGE.play(player);
            }
            case 11 -> {
                if (event.getCurrentItem() == null) return;
                environmentButton.next();
                setItem(event.getRawSlot(), environmentButton.getItem());
                Sounds.MENU_ENVIRONMENT_CHANGE.play(player);
                WorldGenerator generator = WorldGenerators.getInstance().getById(generatorButton.getCurrentValue().toString());
                if (generator != null) {
                    updateBiomePicker(generator);
                }
            }
            case 12 -> {
                if (event.getCurrentItem() == null) return;
                generateStructures.next();
                setItem(event.getRawSlot(), generateStructures.getItem());
                Sounds.MENU_GENERATE_STRUCTURES_CHANGE.play(player);
            }
            case 13 -> {
                if (event.getCurrentItem() == null) return;
                if (biomePicker == null) return;
                biomePicker.next();
                setItem(event.getRawSlot(), biomePicker.getItem());
                Sounds.MENU_BIOME_CHANGE.play(player);
            }
            case 16 -> {
                player.closeInventory();
                if (!OpenCreative.getStability().isFine()) {
                    player.sendMessage(getLocaleMessage("creative.stability.cannot"));
                    Sounds.PLAYER_FAIL.play(player);
                    return;
                }
                boolean notReachedWorldsLimit = OpenCreative.getPlanetsManager().getPlanetsByOwner(player).size() < OpenCreative.getSettings().getGroups().getGroup(player).getWorldsLimit();
                if (notReachedWorldsLimit || ignoreLimit) {
                    Sounds.WORLD_GENERATION.play(player);
                    player.closeInventory();
                    WorldGenerator generator = WorldGenerators.getInstance().getById(generatorButton.getCurrentValue().toString());
                    if (generator == null) return;
                    if (generator instanceof WorldTemplate template) {
                        OpenCreative.getPlanetsManager().createPlanet(player, WorldUtils.generateWorldID(), template);
                    } else {
                        World.Environment environment = World.Environment.valueOf(environmentButton.getCurrentValue().toString().toUpperCase());
                        int seed = new Random().nextInt();
                        boolean generateStructure = Boolean.parseBoolean(generateStructures.getCurrentValue().toString());
                        String biome = "";
                        if (biomePicker != null) biome = biomePicker.getCurrentValue().toString();
                        OpenCreative.getPlanetsManager().createPlanet(player, WorldUtils.generateWorldID(),
                                generator, environment, seed, generateStructure, biome);
                    }
                }
            }
        }
    }

    private void updateBiomePicker(@Nullable WorldGenerator generator) {
        if (generator instanceof BiomeChangeable changeable) {
            World.Environment environment = World.Environment.valueOf(environmentButton.getCurrentValue().toString().toUpperCase());
            Map<String, Material> biomes = changeable.getBiomes(environment);
            if (biomes.isEmpty()) {
                setItem(13, AIR_ITEM);
                return;
            }
            biomePicker = new ParameterButton("all", new LinkedList<>(biomes.keySet()),
                    "biome", "menus.world-creation",
                    "menus.world-creation.items.biome", new LinkedList<>(biomes.values()));
            setItem(13, biomePicker.getItem());
        } else {
            setItem(13, AIR_ITEM);
        }
    }

    @Override
    public void onOpen(@NotNull InventoryOpenEvent event) {
        Sounds.MENU_OPEN_GENERATION.play(player);
    }
}
