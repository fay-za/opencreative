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

package ua.mcchickenstudio.opencreative.utils.world.generators;

import org.bukkit.Material;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import static ua.mcchickenstudio.opencreative.utils.ErrorUtils.sendDebug;

/**
 * <h1>WorldGenerators</h1>
 * This class represents world generators registry, that
 * contains all generators, that can be used for creating
 * or loading worlds.
 * <p>
 * To add your own world generator, create a class, that
 * extends {@link WorldGenerator} or {@link AbstractFlatGenerator}, then register
 * it with {@link WorldGenerators#registerWorldGenerator(WorldGenerator)} method.
 */
public final class WorldGenerators {

    private static WorldGenerators instance;
    private final List<WorldGenerator> generators = new LinkedList<>();

    /**
     * Returns instance of world generators controller class.
     *
     * @return instance of world generators.
     */
    public synchronized static @NotNull WorldGenerators getInstance() {
        if (instance == null) {
            instance = new WorldGenerators();
        }
        return instance;
    }

    /**
     * Registers world generator, that can be used for world generation.
     *
     * @param generator world generator to register.
     */
    public void registerWorldGenerator(@NotNull WorldGenerator generator) {
        WorldGenerator existing = getById(generator.getID());
        if (existing != null) {
            sendDebug("[GENERATORS] Can't register world generator " + generator.getName() + " (from " + generator.getExtensionId() + "), "
                    + "because there's already registered world generator " + existing.getName() + " (from " + existing.getExtensionId() + ") "
                    + "with same ID: " + generator.getID());
            return;
        }
        sendDebug("[GENERATORS] Registered world generator: " + generator.getName() + " (from " + generator.getExtensionId() + ")");
        generators.add(generator);
    }

    /**
     * Registers world generators, that can be used for world generation.
     *
     * @param generator world generators to register.
     */
    public void registerWorldGenerator(@NotNull WorldGenerator... generator) {
        for (WorldGenerator value : generator) {
            registerWorldGenerator(value);
        }
    }

    /**
     * Unregisters world generator if list contains it.
     *
     * @param generator world generator to unregister.
     */
    @SuppressWarnings("unused")
    public void unregisterWorldGenerator(@NotNull WorldGenerator generator) {
        generators.remove(generator);
    }

    /**
     * Unregisters all world generators.
     */
    public void clearWorldGenerators() {
        generators.clear();
    }

    /**
     * Returns a copy of list that contains all registered world generators.
     *
     * @return world generators list.
     */
    public @NotNull List<WorldGenerator> getWorldGenerators() {
        return new ArrayList<>(generators);
    }

    /**
     * Returns a list of all registered generators IDs.
     *
     * @return generators ID list.
     */
    public @NotNull List<Object> getGeneratorsIDs() {
        List<Object> list = new ArrayList<>();
        for (WorldGenerator generator : generators) {
            list.add(generator.getID());
        }
        return list;
    }

    /**
     * Returns a list of all registered generators materials.
     *
     * @return generators materials list.
     */
    public @NotNull List<Material> getGeneratorsMaterials() {
        List<Material> list = new ArrayList<>();
        for (WorldGenerator generator : generators) {
            list.add(generator.getDisplayIcon().getType());
        }
        return list;
    }

    /**
     * Returns world generator from registry by specified id
     * if it exists, otherwise will return null.
     *
     * @param id id to get world generator.
     * @return world generator - if exists, or null - not exists.
     */
    public @Nullable WorldGenerator getById(@NotNull String id) {
        for (WorldGenerator generator : generators) {
            if (generator.getID().equals(id)) {
                return generator;
            }
        }
        return null;
    }

}
