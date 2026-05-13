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

package ua.mcchickenstudio.opencreative.events.planet;

import org.bukkit.World;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import ua.mcchickenstudio.opencreative.planets.Planet;
import ua.mcchickenstudio.opencreative.utils.world.generators.WorldGenerator;

/**
 * Called when planet's world is created.
 */
public class PlanetCreationEvent extends PlanetEvent {

    private final Player player;
    private final WorldGenerator generator;
    private final World.Environment environment;
    private final boolean generateStructures;
    private final long seed;

    public PlanetCreationEvent(@NotNull Planet planet, @NotNull Player player,
                               @NotNull WorldGenerator generator, @NotNull World.Environment environment,
                               long seed, boolean generateStructures) {
        super(planet);
        this.player = player;
        this.generator = generator;
        this.environment =  environment;
        this.seed = seed;
        this.generateStructures = generateStructures;
    }

    public @NotNull Player getPlayer() {
        return player;
    }

    public @NotNull World.Environment getEnvironment() {
        return environment;
    }

    public @NotNull WorldGenerator getGenerator() {
        return generator;
    }

    public long getSeed() {
        return seed;
    }

    public boolean shouldGenerateStructures() {
        return generateStructures;
    }
}
