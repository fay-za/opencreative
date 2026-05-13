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

package ua.mcchickenstudio.opencreative.utils.world;

import org.bukkit.*;
import org.bukkit.entity.*;
import org.bukkit.inventory.meta.FireworkMeta;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import ua.mcchickenstudio.opencreative.OpenCreative;
import ua.mcchickenstudio.opencreative.utils.FileUtils;
import ua.mcchickenstudio.opencreative.utils.PlayerUtils;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

/**
 * <h1>WorldUtils</h1>
 * This class contains methods for changing
 * or manipulating worlds, like checking if
 * world is a planet.
 */
public final class WorldUtils {

    private final static NamespacedKey DO_NOT_HURT_ANYONE = new NamespacedKey(OpenCreative.getPlugin(), "oc_do_not_damage");

    /**
     * Returns key, that will be used in entities, that
     * mustn't damage any entity.
     *
     * @return namespaced key of not hurting anyone.
     */
    public static NamespacedKey getDoNotHurtAnyoneKey() {
        return DO_NOT_HURT_ANYONE;
    }

    /**
     * Summons fireworks around players in world.
     * They won't damage players or entities.
     *
     * @param world  world to summon.
     * @param amount amount of fireworks.
     */
    public static void summonFireworks(@NotNull World world, int amount) {
        Random random = new Random();
        for (int i = 0; i < amount; i++) {
            for (Player player : world.getPlayers()) {
                int randomOffsetX = random.nextInt(-5, 6);
                int randomOffsetZ = random.nextInt(-5, 6);
                int detonateTicks = random.nextInt(30, 51);
                Color color = Color.fromRGB(random.nextInt(256), random.nextInt(256), random.nextInt(256));
                Color fadeColor = Color.fromRGB(random.nextInt(256), random.nextInt(256), random.nextInt(256));
                FireworkEffect.Type type = FireworkEffect.Type.values()[random.nextInt(FireworkEffect.Type.values().length)];
                FireworkEffect effect = FireworkEffect.builder()
                        .flicker(true)
                        .withColor(color)
                        .withFade(fadeColor)
                        .with(type)
                        .trail(true)
                        .build();
                Location location = player.getLocation();
                location.add(randomOffsetX, 1, randomOffsetZ);
                if (!location.getBlock().isPassable()) {
                    location = location.getWorld().getHighestBlockAt(location).getLocation();
                }
                Firework firework = world.spawn(location, Firework.class);
                firework.getPersistentDataContainer().set(DO_NOT_HURT_ANYONE, PersistentDataType.BYTE, (byte) 1);
                FireworkMeta meta = firework.getFireworkMeta();
                meta.addEffect(effect);
                firework.setFireworkMeta(meta);
                firework.setTicksToDetonate(detonateTicks);
            }
        }
    }

    /**
     * Summons fireworks around players in world.
     * They won't damage players or entities.
     *
     * @param times  how many times summon fireworks.
     * @param period period of time, that should pass before every time.
     */
    public static void summonFireworks(int times, int period) {
        final int[] summoned = {0};
        Bukkit.getScheduler().runTaskTimer(OpenCreative.getPlugin(), (task) -> {
            if (summoned[0] >= times) {
                task.cancel();
                return;
            }
            for (World world : Bukkit.getWorlds()) {
                if (isLobbyWorld(world)) {
                    summonFireworks(world, 5);
                } else if (isDevPlanet(world)) {
                    summonFireworks(world, 4);
                } else if (isPlanet(world)) {
                    summonFireworks(world, 3);
                }
            }
            summoned[0]++;
        }, 0L, period);
    }

    /**
     * Returns planet id from world's name
     * by splitting it and removing path to
     * planets folder.
     *
     * @param world world to get id.
     * @return planet id.
     */
    public static @NotNull String getPlanetIdFromName(@NotNull World world) {
        return world.getName()
                .replace(Bukkit.getServer().getWorldContainer() + "/", "")
                .replace("planets/planet", "");
    }

    /**
     * Rounds location numbers: x, y, z, yaw, pitch.
     *
     * @param location location to round.
     * @return location with rounded coordinates.
     */
    public static @NotNull Location roundLocation(@NotNull Location location) {
        double x = Math.round(location.getX() * 100.0) / 100.0;
        double y = Math.round(location.getY() * 100.0) / 100.0;
        double z = Math.round(location.getZ() * 100.0) / 100.0;
        float yaw = (float) (Math.round(location.getYaw() * 100.0) / 100.0);
        float pitch = (float) (Math.round(location.getPitch() * 100.0) / 100.0);
        location.set(x, y, z);
        location.setYaw(yaw);
        location.setPitch(pitch);
        return location;
    }

    /**
     * Converts text coordinate to numeric double.
     * <p>
     * If it starts with ~ symbol, it will add specified value
     * to default value.
     * <p>
     * If it fails to get number from text, it will return
     * default value.
     *
     * @param text         text with number to convert.
     * @param defaultValue default value.
     * @return coordinate double value.
     */
    public static double fromTextToCoordinate(@NotNull String text, double defaultValue) {
        try {
            if (text.startsWith("~")) {
                return text.equals("~") ? defaultValue : defaultValue + Double.parseDouble(text.substring(1));
            } else {
                return Double.parseDouble(text);
            }
        } catch (Exception ignored) {
            return defaultValue;
        }
    }

    /**
     * Converts location to map with x, y, z, yaw, pitch values.
     *
     * @param location location to convert to map.
     * @return map with x, y, z, yaw, pitch values.
     */
    public static @NotNull Map<String, Double> fromLocationToMap(@NotNull Location location) {
        Map<String, Double> map = new HashMap<>();
        map.put("x", location.getX());
        map.put("y", location.getY());
        map.put("z", location.getZ());
        map.put("yaw", (double) location.getYaw());
        map.put("pitch", (double) location.getPitch());
        return map;
    }

    /**
     * Checks if the world is a planet by
     * checking whether it's name contains
     * a typical planets folder path.
     *
     * @param world world to check.
     * @return true - should be a planet, false - not a planet.
     */
    public static boolean isPlanet(@NotNull World world) {
        return isPlanet(world.getName());
    }

    /**
     * Checks if the world is a planet by
     * checking whether it's name contains
     * a typical planets folder path.
     *
     * @param worldName name of world to check.
     * @return true - should be a planet, false - not a planet.
     */
    public static boolean isPlanet(@NotNull String worldName) {
        return worldName.contains("planets/planet");
    }

    /**
     * Checks if the world is a dev planet
     * by checking whether it's name ends
     * with "dev" word.
     *
     * @param worldName world to check.
     * @return true - should be a dev planet, false - not a dev planet.
     */
    public static boolean isDevPlanet(@NotNull String worldName) {
        return worldName.contains("planets/planet") && worldName.endsWith("dev");
    }

    /**
     * Checks if the world is a planet, or
     * dev planet, or lobby world.
     *
     * @param world world to check.
     * @return true - it's spawn, planet, dev planet world, false - not plugin's world.
     */
    public static boolean isOpenCreativeWorld(@NotNull World world) {
        return isLobbyWorld(world) || isPlanet(world) || isDevPlanet(world);
    }

    /**
     * Checks if entity is a hostile one,
     * that can attack player.
     *
     * @param entity entity to check.
     * @return true - entity is angry, false - entity is friendly or null.
     */
    public static boolean isEntityHostile(@Nullable Entity entity) {
        return entity instanceof Enemy || entity instanceof Boss;
    }

    /**
     * Checks if the world is a dev planet
     * by checking whether it's name ends
     * with "dev" word.
     *
     * @param world world to check.
     * @return true - should be a dev planet, false - not a dev planet.
     */
    public static boolean isDevPlanet(@NotNull World world) {
        return isDevPlanet(world.getName());
    }

    /**
     * Checks if the world is a lobby world,
     * where players will be teleported on
     * server connection or by /spawn command.
     *
     * @param world world to check.
     * @return true - it's lobby world, false - not lobby world.
     */
    public static boolean isLobbyWorld(@NotNull World world) {
        return world.equals(PlayerUtils.getLobbyWorld());
    }

    /**
     * Returns a unique numeric ID for new planet.
     * It gets a last numeric ID from config and
     * increases it while checking is number free.
     * Immediately will replace last numeric ID
     * with result.
     *
     * @return unique numeric ID.
     */
    public static int generateWorldID() {
        int newWorldID = OpenCreative.getPlugin().getConfig().getInt("last-world-id", 1);
        while (true) {
            newWorldID++;
            File planetFolder = new File(FileUtils.getPlanetFolderPath(newWorldID));
            File devPlanetFolder = new File(FileUtils.getDevPlanetFolderPath(newWorldID));
            if (!planetFolder.exists() && !devPlanetFolder.exists()) {
                OpenCreative.getPlugin().getConfig().set("last-world-id", newWorldID);
                OpenCreative.getPlugin().saveConfig();
                return newWorldID;
            }
        }
    }

}

