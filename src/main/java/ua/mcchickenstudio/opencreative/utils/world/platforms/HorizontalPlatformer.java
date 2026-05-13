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
 * <h1>HorizontalPlatformer</h1>
 * This class represents a platforms generator, that creates
 * and returns platforms in horizontal way.
 */
public final class HorizontalPlatformer extends DevPlatformer {

    public HorizontalPlatformer() {
        super("horizontal");
    }

    @Override
    public void setWorldBorder(@NotNull DevPlanet devPlanet) {

        World world = devPlanet.getWorld();
        world.getWorldBorder().setWarningDistance(0);

        world.getWorldBorder().setCenter(50, 50);
        world.getWorldBorder().setSize(120);

        DevPlatform platformZ = getFarPlatformByZ(devPlanet);
        DevPlatform platformX = getFarPlatformByX(devPlanet);
        double endZ = getPlatformEndLocation(platformZ).getBlockZ();
        double endX = getPlatformEndLocation(platformX).getBlockX();
        /*
         * We find center of world border by dividing
         * most far platform end coordinate by 2.
         * (the start coordinate is 0)
         */
        double centerZ = endZ / 2;
        double centerX = endX / 2;
        world.getWorldBorder().setCenter(centerX, centerZ);
        /*
         * We find size by subtracting most far
         * coordinate with center coordinate.
         */
        double size = ((Math.max(endX, endZ)) - (Math.max(centerX, centerZ))) * 2 + 20;
        world.getWorldBorder().setSize(size);

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
    public @Nullable DevPlatform getPlatformInLocation(@NotNull DevPlanet devPlanet, @NotNull Location location) {
        double x = location.getX();
        double z = location.getZ();
        for (DevPlatform platform : getPlatforms(devPlanet)) {
            Location begin = getPlatformBeginLocation(platform);
            Location end = getPlatformEndLocation(platform);
            if (x >= begin.getBlockX() && x <= end.getBlockX()) {
                if (z >= begin.getBlockZ() && z <= end.getBlockZ()) {
                    return platform;
                }
            }
        }
        return null;
    }

    @Override
    public @NotNull DevPlatform getFarPlatformByX(@NotNull DevPlanet devPlanet) {
        DevPlatform farPlatform = new DevPlatform(devPlanet, 1, 1);
        if (!devPlanet.isLoaded()) return farPlatform;
        for (int x = 2; x <= 5; x++) {
            DevPlatform current = new DevPlatform(devPlanet, x, 1);
            if (current.exists()) {
                farPlatform = current;
            }
        }
        return farPlatform;
    }

    @Override
    public @NotNull DevPlatform getFarPlatformByZ(@NotNull DevPlanet devPlanet) {
        DevPlatform farPlatform = new DevPlatform(devPlanet, 1, 1);
        if (!devPlanet.isLoaded()) return farPlatform;
        for (int z = 2; z <= 5; z++) {
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
        for (int x = 1; x <= getFarPlatformByX(devPlanet).getX(); x++) {
            for (int z = 1; z <= getFarPlatformByZ(devPlanet).getZ(); z++) {
                DevPlatform platform = new DevPlatform(devPlanet, x, z);
                if (platform.exists()) {
                    platforms.add(platform);
                }
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
                        Block block = platform.getWorld().getBlockAt(x, 0, z);
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
        int step = OpenCreative.getSettings().getCodingSettings().getHorizontalPlatformStep();
        return new Location(platform.getWorld(), (platform.getX() - 1) * step, 0, (platform.getZ() - 1) * step);
    }


    @Override
    public @NotNull Location getPlatformEndLocation(@NotNull DevPlatform platform) {
        return getPlatformBeginLocation(platform).clone().add(100, 0, 100);
    }

    @Override
    public @NotNull DevPlatform getNextAvailablePlatform(@NotNull DevPlanet planet) {
        int[][] platformCoordinates = {
                {2, 1}, {1, 2}, {2, 2}, {3, 1}, {1, 3}, {2, 3}, {3, 2}, {3, 3},
                {4, 1}, {1, 4}, {4, 2}, {2, 4}, {4, 3}, {3, 4}, {4, 4}
        };
        DevPlatform platform = null;
        for (int[] coords : platformCoordinates) {
            platform = new DevPlatform(planet, coords[0], coords[1]);
            if (!platform.exists()) {
                break;
            }
        }
        return platform;
    }

    @Override
    public int getCodingBlocksLimit(@NotNull DevPlanet planet) {
        return 46;
    }

    @Override
    public boolean notDependsOnHeight() {
        return true;
    }

    @Override
    public @NotNull String getName() {
        return "Horizontal Platforms Generator";
    }

    @Override
    public @NotNull String getDescription() {
        return "Builds coding platforms like plots";
    }

    @Override
    public @NotNull String getExtensionId() {
        return "default";
    }

}
