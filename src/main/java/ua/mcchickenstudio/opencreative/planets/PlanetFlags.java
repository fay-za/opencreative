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

package ua.mcchickenstudio.opencreative.planets;

import org.bukkit.GameRule;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import ua.mcchickenstudio.opencreative.utils.FileUtils;

import java.util.EnumMap;
import java.util.Map;

public class PlanetFlags {

    private final Planet planet;
    private final Map<PlanetFlag, Byte> flags = new EnumMap<>(PlanetFlag.class);

    public PlanetFlags(Planet planet) {
        this.planet = planet;
    }

    public void clear() {
        flags.clear();
    }

    public void setFlag(PlanetFlag planetFlag, byte value) {
        flags.put(planetFlag, value);
        FileUtils.setPlanetConfigParameter(planet, planetFlag.getConfigPath(), value);
    }

    public void loadFlags() {
        FileConfiguration configuration = FileUtils.getPlanetConfig(planet);
        for (PlanetFlag flag : PlanetFlag.values()) {
            String configValue = configuration.getString(flag.getConfigPath());
            if (configValue == null || configValue.isEmpty()) {
                flags.put(flag, flag.getDefaultValue());
            } else {
                byte value = flag.getDefaultValue();
                try {
                    value = Byte.parseByte(configValue);
                } catch (NumberFormatException ignored) {
                }
                if (value < 1 || value > 10) value = 1;
                flags.put(flag, value);
            }
        }

    }

    public byte getFlagValue(PlanetFlag flag) {
        if (flags.containsKey(flag)) {
            return (flags.get(flag));
        } else {
            setFlag(flag, flag.getDefaultValue());
            return flag.getDefaultValue();
        }
    }

    public Map<PlanetFlag, Byte> getFlags() {
        return flags;
    }

    public enum PlanetFlag {

        PLAYER_DAMAGE("player-damage", Material.TOTEM_OF_UNDYING, (byte) 1, (byte) 5),
        DAY_CYCLE("day-cycle", Material.CLOCK, (byte) 1, (byte) 4, GameRule.DO_DAYLIGHT_CYCLE),
        JOIN_MESSAGES("join-messages", Material.OAK_SIGN, (byte) 1, (byte) 2),
        FIRE_SPREAD("fire-spread", Material.CAMPFIRE, (byte) 1, (byte) 2, GameRule.DO_FIRE_TICK),
        WEATHER("weather", Material.WATER_BUCKET, (byte) 1, (byte) 3, GameRule.DO_WEATHER_CYCLE),
        BLOCK_INTERACT("block-interact", Material.CHEST, (byte) 1, (byte) 5),
        MOB_INTERACT("mob-interact", Material.VILLAGER_SPAWN_EGG, (byte) 1, (byte) 3),
        MOB_LOOT("mob-loot", Material.FEATHER, (byte) 1, (byte) 2, GameRule.DO_MOB_LOOT),
        MOB_SPAWN("mob-spawn", Material.PIG_SPAWN_EGG, (byte) 1, (byte) 5, GameRule.DO_MOB_SPAWNING),
        NATURAL_REGENERATION("natural-regeneration", Material.POTION, (byte) 1, (byte) 2, GameRule.NATURAL_REGENERATION),
        BLOCK_CHANGING("block-changing", Material.ICE, (byte) 1, (byte) 2),
        BLOCK_EXPLOSION("block-explosion", Material.TNT, (byte) 1, (byte) 2, GameRule.MOB_GRIEFING),
        LIKE_MESSAGES("like-messages", Material.KNOWLEDGE_BOOK, (byte) 1, (byte) 2),
        DEATH_MESSAGES("death-messages", Material.WITHER_SKELETON_SKULL, (byte) 1, (byte) 2),
        KEEP_INVENTORY("keep-inventory", Material.CHEST_MINECART, (byte) 1, (byte) 2, GameRule.KEEP_INVENTORY),
        IMMEDIATE_RESPAWN("immediate-respawn", Material.SKELETON_SKULL, (byte) 1, (byte) 2, GameRule.DO_IMMEDIATE_RESPAWN),
        LOCATOR_BAR("locator-bar", Material.RECOVERY_COMPASS, (byte) 1, (byte) 2),
        WORLD_BORDERS("world-borders", Material.LIGHT_BLUE_STAINED_GLASS, (byte) 1, (byte) 4);

        private final String configPath;
        private final byte defaultValue;
        private final byte maxChoices;
        private final GameRule<?> gameRule;
        private final Material material;

        PlanetFlag(String configPath, Material icon, byte defaultValue, byte maxChoices) {
            this.configPath = configPath;
            this.defaultValue = defaultValue;
            this.maxChoices = maxChoices;
            this.gameRule = null;
            this.material = icon;
        }

        PlanetFlag(String configPath, Material icon, byte defaultValue, byte maxChoices, GameRule<?> gameRule) {
            this.configPath = configPath;
            this.defaultValue = defaultValue;
            this.maxChoices = maxChoices;
            this.gameRule = gameRule;
            this.material = icon;
        }

        public String getConfigPath() {
            return "flags." + configPath;
        }

        public byte getDefaultValue() {
            return defaultValue;
        }

        public byte getMaxChoices() {
            return maxChoices;
        }

        public Material getMaterial() {
            return material;
        }

    }

}
