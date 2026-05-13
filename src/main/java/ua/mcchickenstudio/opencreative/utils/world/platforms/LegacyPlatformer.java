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

package ua.mcchickenstudio.opencreative.utils.world.platforms;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Biome;
import org.bukkit.block.Block;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import ua.mcchickenstudio.opencreative.planets.DevPlanet;
import ua.mcchickenstudio.opencreative.planets.DevPlatform;
import ua.mcchickenstudio.opencreative.OpenCreative;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

/**
 * <h1>LegacyPlatformer</h1>
 * This class represents a platforms generator, that creates
 * and returns platforms in vertical way, but with a little bit
 * of fun for our closed-sourced competitors.
 */
public final class LegacyPlatformer extends DevPlatformer implements HasVisibleBorder {

    public LegacyPlatformer() {
        super("legacy");
    }

    @Override
    public void setWorldBorder(@NotNull DevPlanet devPlanet) {

        World world = devPlanet.getWorld();
        world.getWorldBorder().setWarningDistance(0);

        world.getWorldBorder().setCenter(50, 50);
        world.getWorldBorder().setSize(100);

    }

    @Override
    public @Nullable DevPlatform getPlatformInLocation(@NotNull DevPlanet devPlanet, @NotNull Location location) {
        double x = location.getX();
        double y = location.getY();
        double z = location.getZ();
        for (DevPlatform platform : getPlatforms(devPlanet)) {
            Location beginLocation = getPlatformBeginLocation(platform);
            int height = beginLocation.getBlockY();
            int begin = beginLocation.getBlockX();
            int end = platform.getEndCoordinate();
            if (x >= begin && x <= end) {
                if (z >= begin && z <= end) {
                    if (y >= height && y <= height + 3) {
                        return platform;
                    }
                }
            }
        }
        return null;
    }

    @Override
    public @Nullable Location getColumnBeginLocation(@NotNull DevPlanet devPlanet, @NotNull Location location) {
        DevPlatform platform = getPlatformInLocation(devPlanet, location);
        if (platform == null) return null;

        Location begin = getPlatformBeginLocation(platform);

        int executorX = begin.getBlockX() + 4;
        int relativeZ = location.getBlockZ() - begin.getBlockZ();

        if (relativeZ < 4) return null;
        if ((relativeZ % 4) != 0) return null;

        int executorIndex = relativeZ / 4;
        int executorZ = begin.getBlockZ() + (executorIndex * 4);

        if (executorZ >= getPlatformEndLocation(platform).getBlockZ()) return null;

        return new Location(platform.getWorld(), executorX, location.getY(), executorZ);
    }

    @Override
    public @NotNull DevPlatform getFarPlatformByX(@NotNull DevPlanet devPlanet) {
        // Floors are stacking on each other, so we return 1, 1.
        return new DevPlatform(devPlanet, 1, 1);
    }

    @Override
    public @NotNull DevPlatform getFarPlatformByZ(@NotNull DevPlanet devPlanet) {
        DevPlatform farPlatform = new DevPlatform(devPlanet, 1, 1);
        if (!devPlanet.isLoaded()) return farPlatform;
        for (int z = 2; z <= 25; z++) {
            DevPlatform current = new DevPlatform(devPlanet, 1, z);
            if (current.exists()) {
                farPlatform = current;
            }
        }
        return farPlatform;
    }

    @Override
    public @NotNull List<@NotNull DevPlatform> getPlatforms(@NotNull DevPlanet devPlanet) {
        List<DevPlatform> platforms = new ArrayList<>();
        if (!devPlanet.isLoaded()) return platforms;
        for (int z = 1; z <= getFarPlatformByZ(devPlanet).getZ(); z++) {
            DevPlatform platform = new DevPlatform(devPlanet, 1, z);
            if (platform.exists()) {
                platforms.add(platform);
            }
        }
        return platforms;
    }

    @Override
    public boolean claimPlatform(@NotNull DevPlanet devPlanet, @NotNull DevPlatform platform) {
        if (!devPlanet.isLoaded()) return false;
        if (platform.exists()) return false;
        buildPlatform(platform, DevPlanet.getDefaultFloorMaterial(), DevPlanet.getDefaultEventMaterial(),
            DevPlanet.getDefaultActionMaterial()).thenAccept((ignored) -> {
                Bukkit.getScheduler().runTask(OpenCreative.getPlugin(), () -> {
                    setWorldBorder(devPlanet);
                    devPlanet.displayWorldBorders();
                });
        });
        return true;
    }

    @Override
    public @NotNull CompletableFuture<Void> buildPlatform(@NotNull DevPlatform platform, Material floorMaterial, Material eventMaterial, Material actionMaterial) {
        CompletableFuture<Void> future = new CompletableFuture<>();
        Location begin = getPlatformBeginLocation(platform);
        Location end = getPlatformEndLocation(platform);
        int height = begin.getBlockY();
        int beginX = begin.getBlockX();
        int endX = end.getBlockX();
        int beginZ = begin.getBlockZ();
        int endZ = end.getBlockZ();
        int executorX = beginX + 4;
        CompletableFuture<Integer> built = OpenCreative.getBlocksManager().setBlocksType(begin, end, floorMaterial, 10300);
        built.thenAccept((changed) -> {
            Bukkit.getScheduler().runTask(OpenCreative.getPlugin(), () -> {
                if (Bukkit.getWorld(platform.getWorld().getName()) == null) {
                    return;
                }
                for (int x = beginX; x <= endX; x++) {
                    for (int z = beginZ; z <= endZ; z++) {
                        Block block = platform.getWorld().getBlockAt(x, height, z);
                        if (x == executorX && (z - beginZ) % 4 == 0 && z != beginZ && z != endZ) {
                            block.setType(eventMaterial);
                        } else if (x > executorX && (x - executorX) % 2 == 0 && x < endX - 2 && (z - beginZ) % 4 == 0 && z != beginZ && z != endZ) {
                            block.setType(actionMaterial);
                        }
                        block.setBiome(Biome.ICE_SPIKES);
                    }
                }
                future.complete(null);
            });
        });
        return future;
    }

    public @NotNull Location getPlatformBeginLocation(@NotNull DevPlatform platform) {
        int step = OpenCreative.getSettings().getCodingSettings().getLegacyPlatformStep();
        return new Location(platform.getWorld(), 0, (platform.getZ() - 1) * step, 0);
    }


    @Override
    public @NotNull Location getPlatformEndLocation(@NotNull DevPlatform platform) {
        return getPlatformBeginLocation(platform).clone().add(100, 0, 100);
    }

    @Override
    public @NotNull DevPlatform getNextAvailablePlatform(@NotNull DevPlanet planet) {
        DevPlatform platform = new DevPlatform(planet, 1, 1);
        for (int y = 1; y <= 25; y++) {
            platform = new DevPlatform(planet, 1, y);
            if (!platform.exists()) return platform;
        }
        return platform;
    }

    @Override
    public int getCodingBlocksLimit(@NotNull DevPlanet planet) {
        return 46;
    }

    @Override
    public boolean notDependsOnHeight() {
        return false;
    }

    @Override
    public @NotNull String getName() {
        return "Legacy Platforms Generator";
    }

    @Override
    public @NotNull String getDescription() {
        return "Builds coding platforms like legacy floors";
    }

    @Override
    public @NotNull String getExtensionId() {
        return "default";
    }

}
