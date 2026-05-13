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

package ua.mcchickenstudio.opencreative.settings;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.jetbrains.annotations.NotNull;
import ua.mcchickenstudio.opencreative.OpenCreative;

import static ua.mcchickenstudio.opencreative.utils.ErrorUtils.sendCriticalErrorMessage;

/**
 * <h1>Requirements</h1>
 * This class represents a requirements for players
 * to change or to do something.
 */
public final class Requirements {

    private int worldCreationMinSeconds = 30;
    private int worldReputationMinSeconds = 300;

    private String customIdPattern = "^[a-zA-Zа-яА-Я0-9_]+$";
    private int customIdMinLength = 2;
    private int customIdMaxLength = 16;

    private int worldNameMinLength = 4;
    private int worldNameMaxLength = 30;

    private int worldDescriptionMinLength = 4;
    private int worldDescriptionMaxLength = 256;

    private int moduleNameMinLength = 4;
    private int moduleNameMaxLength = 30;

    private int moduleDescriptionMinLength = 4;
    private int moduleDescriptionMaxLength = 256;

    /**
     * Loads settings of coding from configuration.
     */
    public void load() {
        FileConfiguration config = OpenCreative.getPlugin().getConfig();
        ConfigurationSection section = config.getConfigurationSection("requirements");

        if (section == null) {
            sendCriticalErrorMessage("Can't load requirements, section `requirements` in config.yml is empty.");
            return;
        }

        worldCreationMinSeconds = section.getInt("world-creation.played-seconds", 30);
        worldReputationMinSeconds = section.getInt("world-reputation.creation-seconds", 300);

        customIdPattern = section.getString("world-custom-id.pattern", "^[a-zA-Zа-яА-Я0-9_]+$");
        customIdMinLength = section.getInt("world-custom-id.min-length", 2);
        customIdMaxLength = section.getInt("world-custom-id.max-length", 16);

        worldNameMinLength = section.getInt("world-name.min-length", 2);
        worldNameMaxLength = section.getInt("world-name.max-length", 16);

        worldDescriptionMinLength = section.getInt("world-description.min-length", 2);
        worldDescriptionMaxLength = section.getInt("world-description.max-length", 256);

        moduleNameMinLength = section.getInt("module-name.min-length", 2);
        moduleNameMaxLength = section.getInt("module-name.max-length", 16);

        moduleDescriptionMinLength = section.getInt("module-description.min-length", 2);
        moduleDescriptionMaxLength = section.getInt("module-description.max-length", 256);
    }

    /**
     * Returns pattern for world's custom ID.
     *
     * @return pattern of world custom ID.
     */
    public @NotNull String getCustomIdPattern() {
        return customIdPattern;
    }

    /**
     * Returns maximum length of world's custom ID.
     *
     * @return limit of world's custom ID length.
     */
    public int getCustomIdMaxLength() {
        return customIdMaxLength;
    }

    /**
     * Returns minimal length of world's custom ID.
     *
     * @return minimal length of custom ID.
     */
    public int getCustomIdMinLength() {
        return customIdMinLength;
    }

    /**
     * Returns maximum length of world's description.
     *
     * @return limit of world's description length.
     */
    public int getWorldDescriptionMaxLength() {
        return worldDescriptionMaxLength;
    }

    /**
     * Returns minimal length of world's description.
     *
     * @return minimal length of description.
     */
    public int getWorldDescriptionMinLength() {
        return worldDescriptionMinLength;
    }

    /**
     * Returns maximum length of world's name.
     *
     * @return limit of world's name length.
     */
    public int getWorldNameMaxLength() {
        return worldNameMaxLength;
    }

    /**
     * Returns minimal length of world's name.
     *
     * @return minimal length of name.
     */
    public int getWorldNameMinLength() {
        return worldNameMinLength;
    }

    /**
     * Returns how many seconds need to pass after player join
     * to be able to create a world.
     *
     * @return minimal amount of seconds to be able to create a world.
     */
    public int getWorldCreationMinSeconds() {
        return worldCreationMinSeconds;
    }

    //  Цьом

    /**
     * Returns how many seconds need to pass after world's
     * creation to allow players to like or dislike world.
     *
     * @return minimal amount of seconds to be able to like or dislike a world.
     */
    public int getWorldReputationMinSeconds() {
        return worldReputationMinSeconds;
    }

    /**
     * Returns maximum length of module's description.
     *
     * @return limit of module's description length.
     */
    public int getModuleDescriptionMaxLength() {
        return moduleDescriptionMaxLength;
    }

    /**
     * Returns minimal length of module's description.
     *
     * @return minimal length of description.
     */
    public int getModuleDescriptionMinLength() {
        return moduleDescriptionMinLength;
    }

    /**
     * Returns maximum length of module's name.
     *
     * @return limit of module's name length.
     */
    public int getModuleNameMaxLength() {
        return moduleNameMaxLength;
    }

    /**
     * Returns minimal length of module's name.
     *
     * @return minimal length of name.
     */
    public int getModuleNameMinLength() {
        return moduleNameMinLength;
    }

}
