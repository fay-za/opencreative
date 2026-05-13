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

package ua.mcchickenstudio.opencreative.wanders;

import com.google.gson.*;
import com.google.gson.annotations.Since;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.OfflinePlayer;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import ua.mcchickenstudio.opencreative.indev.Links;

import java.io.*;
import java.util.*;

import static ua.mcchickenstudio.opencreative.utils.ErrorUtils.sendDebugError;
import static ua.mcchickenstudio.opencreative.utils.FileUtils.getWanderJsonFile;
import static ua.mcchickenstudio.opencreative.utils.MessageUtils.getLocaleMessage;

/**
 * <h1>OfflineWander</h1>
 * This class represents wander, that could be offline
 * or online. Wander is a player, who plays on planets.
 * He has nickname, description, gender, favorite
 * worlds and last played world.
 */
public class OfflineWander {

    @Since(5.6)
    protected final @NotNull UUID uuid;
    @Since(5.6)
    protected @Nullable String name;
    @Since(5.6)
    protected @Nullable String description;
    @Since(5.6)
    protected @Nullable Gender gender;
    @Since(5.6)
    protected @Nullable Set<Integer> favoriteWorlds;
    @Since(5.6)
    protected int lastPlayedWorldId = -1;
    @Since(6.0)
    protected int visits = 0;
    @Since(6.0)
    protected Links links;
    @Since(5.6)
    protected boolean hideHints;
    @Since(5.8)
    protected @Nullable List<UUID> friends;
    @Since(5.8)
    protected Location lastLocation;

    /**
     * Constructor of offline wander by player's unique ID.
     *
     * @param uuid unique ID of player.
     */
    public OfflineWander(@NotNull UUID uuid) {
        this.uuid = uuid;
        loadInfo();
    }

    /**
     * Constructor of offline wander by offline player.
     *
     * @param offlinePlayer offline player.
     */
    public OfflineWander(@NotNull OfflinePlayer offlinePlayer) {
        this.uuid = offlinePlayer.getUniqueId();
        loadInfo();
    }

    /**
     * Returns offline player associated with wander.
     *
     * @return offline player.
     */
    public @NotNull OfflinePlayer getOfflinePlayer() {
        return Bukkit.getOfflinePlayer(uuid);
    }

    /**
     * Checks whether wander is playing on server.
     *
     * @return true - player is online, false - offline.
     */
    public boolean isOnline() {
        return Bukkit.getPlayer(uuid) != null;
    }

    /**
     * Returns unique ID of wander.
     *
     * @return unique ID of player.
     */
    public @NotNull UUID getUniqueId() {
        return uuid;
    }

    /**
     * Returns custom name of wander, or null - if not set.
     *
     * @return name of wander, or null.
     */
    public @Nullable String getName() {
        return name;
    }

    /**
     * Returns profile's description, or null - if not set.
     *
     * @return description, or null.
     */
    public @Nullable String getDescription() {
        return description;
    }

    /**
     * Returns profile's gender, or null - if not set.
     *
     * @return gender, or null.
     */
    public @Nullable Gender getGender() {
        return gender;
    }

    /**
     * Returns last location of wander (where player was playing
     * before leaving the server), or null - if not saved.
     *
     * @return last location, or null.
     */
    public @Nullable Location getLastLocation() {
        return lastLocation;
    }

    /**
     * Returns link of social media by ID,
     * or null - if not set.
     *
     * @param id type of social media.
     * @return link, or null.
     */
    public @Nullable String getLink(@NotNull String id) {
        if (links == null) return null;
        return links.getLink(id);
    }

    /**
     * Sets link of social media by ID.
     *
     * @param id type of social media.
     * @param link link to set.
     */
    public void setLink(@NotNull String id, @NotNull String link) {
        if (links == null) {
            this.links = new Links();
        }
        links.setLink(id, link);
        saveData();
    }

    /**
     * Removes social media link by ID.
     *
     * @param id type of social media.
     */
    public void removeLink(@NotNull String id) {
        if (links == null) return;
        links.setLink(id, null);
        saveData();
    }

    /**
     * Returns ID of last visited planet by player,
     * or -1 - if not saved.
     *
     * @return last visited planet's ID, or -1.
     */
    public int getLastPlayedWorldId() {
        return lastPlayedWorldId;
    }

    /**
     * Returns amount of all visited worlds
     * by player.
     *
     * @return amount of visited worlds.
     */
    public int getVisits() {
        return visits;
    }

    /**
     * Checks whether hints should
     * be hidden from player.
     *
     * @return true - should be hidden, false - show them.
     */
    public boolean shouldHideHints() {
        return hideHints;
    }

    /**
     * Returns set of favorite worlds IDs,
     * marked by player.
     *
     * @return set of favorite worlds IDs.
     */
    public @NotNull Set<Integer> getFavoriteWorlds() {
        return favoriteWorlds != null ? favoriteWorlds : Set.of();
    }

    /**
     * Returns set of friends UUIDs,
     * picked by player.
     *
     * @return list of friends UUIDs.
     */
    public @NotNull List<UUID> getFriends() {
        return friends != null ? friends : List.of();
    }

    /**
     * Sets last visited world ID.
     *
     * @param lastPlayedWorldId id of visited planet.
     */
    public void setLastPlayedWorldId(int lastPlayedWorldId) {
        this.lastPlayedWorldId = lastPlayedWorldId;
        saveData();
    }

    /**
     * Sets amount of visits.
     *
     * @param visits new amount to set.
     */
    public void setVisits(int visits) {
        this.visits = visits;
        saveData();
    }

    /**
     * Sets profile's custom name.
     *
     * @param name new name to set.
     */
    public void setName(@NotNull String name) {
        this.name = name;
        saveData();
    }

    /**
     * Sets profile's description.
     *
     * @param description new description to set.
     */
    public void setDescription(@NotNull String description) {
        this.description = description;
        saveData();
    }

    /**
     * Adds specified UUID to friends list.
     *
     * @param friendUUID unique ID of friend.
     * @return true - was added, false - already added,
     * or friend's UUID is same with wander.
     */
    public boolean addFriend(@NotNull UUID friendUUID) {
        if (uuid.equals(friendUUID)) {
            return false;
        }
        if (getFriends().contains(friendUUID)) {
            return false;
        }
        if (friends == null) friends = new ArrayList<>();
        friends.add(friendUUID);
        saveData();
        return true;
    }

    /**
     * Removes specified UUID from friends list.
     *
     * @param friendUUID unique ID of friend.
     * @return true - was removed, false - not removed,
     * or friend's UUID is same with wander.
     */
    public boolean removeFriend(@NotNull UUID friendUUID) {
        if (uuid.equals(friendUUID)) {
            return false;
        }
        if (friends == null || !getFriends().contains(friendUUID)) {
            return false;
        }
        friends.remove(friendUUID);
        saveData();
        return true;
    }

    /**
     * Adds specified planet's ID to favorite worlds.
     *
     * @param worldId id of planet.
     * @return true - was added, false - already added.
     */
    public boolean addFavoriteWorld(int worldId) {
        if (getFavoriteWorlds().contains(worldId)) {
            return false;
        }
        if (favoriteWorlds == null) favoriteWorlds = new HashSet<>();
        favoriteWorlds.add(worldId);
        saveData();
        return true;
    }

    /**
     * Removes specified planet's ID from favorite worlds.
     *
     * @param worldId id of planet.
     * @return true - was removed, false - not added yet.
     */
    public boolean removeFavoriteWorld(int worldId) {
        if (favoriteWorlds == null || !getFavoriteWorlds().contains(worldId)) {
            return false;
        }
        favoriteWorlds.remove(worldId);
        saveData();
        return true;
    }

    /**
     * Sets profile's gender.
     *
     * @param gender new gender to set.
     * @return true - was changed, false - its already current gender.
     */
    public boolean setGender(@NotNull Gender gender) {
        if (this.gender == gender) {
            return false;
        }
        this.gender = gender;
        saveData();
        return true;
    }

    /**
     * Clears all data and removes json file.
     *
     * @return true - was removed, false - failde to remove.
     */
    public boolean clearData() {
        File jsonFile = getWanderJsonFile(this, false);
        if (jsonFile == null || !jsonFile.exists()) {
            return false;
        }
        this.name = null;
        this.description = null;
        this.gender = null;
        this.lastPlayedWorldId = -1;
        this.getFavoriteWorlds().clear();
        this.lastLocation = null;
        try {
            return jsonFile.delete();
        } catch (Exception error) {
            sendDebugError("Can't delete wander file: " + uuid, error);
            return false;
        }
    }

    /**
     * Loads information about wander from disk.
     */
    public void loadInfo() {
        File jsonFile = getWanderJsonFile(this, false);
        if (jsonFile == null || !jsonFile.exists()) {
            return;
        }
        try (Reader reader = new FileReader(jsonFile)) {
            Gson gson = new Gson();
            JsonObject json = gson.fromJson(reader, JsonObject.class);

            if (json.has("name")) {
                this.name = json.get("name").getAsString();
            }

            if (json.has("description")) {
                this.description = json.get("description").getAsString();
            }

            if (json.has("gender")) {
                this.gender = Gender.getGender(json.get("gender").getAsString());
            }

            if (json.has("favoriteWorlds")) {
                JsonArray arr = json.getAsJsonArray("favoriteWorlds");
                this.favoriteWorlds = new HashSet<>();
                for (JsonElement el : arr) {
                    this.favoriteWorlds.add(el.getAsInt());
                }
            }

            if (json.has("friends")) {
                JsonArray arr = json.getAsJsonArray("friends");
                this.friends = new ArrayList<>();
                for (JsonElement el : arr) {
                    try {
                        UUID friend = UUID.fromString(el.getAsString());
                        this.friends.add(friend);
                    } catch (Exception ignored) {
                    }
                }
            }

            if (json.has("lastPlayedWorldId")) {
                this.lastPlayedWorldId = json.get("lastPlayedWorldId").getAsInt();
            }

            if (json.has("hideHints")) {
                this.hideHints = json.get("hideHints").getAsBoolean();
            }

            if (json.has("lastLocation")) {
                JsonObject lastLoc = json.get("lastLocation").getAsJsonObject();
                double x = lastLoc.get("x").getAsDouble();
                double y = lastLoc.get("y").getAsDouble();
                double z = lastLoc.get("z").getAsDouble();
                float yaw = lastLoc.get("yaw").getAsFloat();
                float pitch = lastLoc.get("pitch").getAsFloat();
                this.lastLocation = new Location(null, x, y, z, yaw, pitch);
            }

            if (json.has("socialLinks") && json.get("socialLinks").isJsonObject()) {
                links = new Links();
                JsonObject socialLinks = json.getAsJsonObject("socialLinks");
                for (Map.Entry<String, JsonElement> entry : socialLinks.entrySet()) {
                    String type = entry.getKey();
                    String value = entry.getValue().getAsString();
                    links.setLink(type, value);
                }
            }

        } catch (Exception error) {
            sendDebugError("Failed to load info for wander " + uuid, error);
        }
    }

    /**
     * Saves wander's information to disk.
     */
    public void saveData() {
        try {
            if (!shouldSaveData()) {
                return;
            }
            File jsonFile = getWanderJsonFile(this, true);
            if (jsonFile == null) {
                return;
            }
            JsonObject json = getJsonObject();
            try (Writer writer = new FileWriter(jsonFile)) {
                Gson gson = new GsonBuilder().setPrettyPrinting().create();
                gson.toJson(json, writer);
            }
        } catch (Exception error) {
            sendDebugError("Failed to save wander data: " + uuid, error);
        }
    }

    /**
     * Checks whether data should be saved.
     *
     * @return true - should be saved, false - can be not saved.
     */
    private boolean shouldSaveData() {
        return favoriteWorlds != null || description != null || gender != null || lastPlayedWorldId != -1;
    }

    /**
     * Returns Json object for saving.
     *
     * @return json object to save.
     */
    private JsonObject getJsonObject() {
        JsonObject json = new JsonObject();
        json.addProperty("uuid", uuid.toString());
        if (name != null) json.addProperty("name", name);
        if (description != null) json.addProperty("description", description);
        if (gender != null) json.addProperty("gender", gender.name());
        JsonArray favoriteWorlds = new JsonArray();
        if (this.favoriteWorlds != null) {
            for (int id : this.favoriteWorlds) {
                favoriteWorlds.add(id);
            }
        }
        if (!favoriteWorlds.isEmpty()) json.add("favoriteWorlds", favoriteWorlds);
        if (lastPlayedWorldId != -1) json.addProperty("lastPlayedWorldId", lastPlayedWorldId);
        if (visits > 0) json.addProperty("visits", visits);
        if (hideHints) json.addProperty("hideHints", true);
        if (lastLocation != null) {
            JsonObject lastLoc = new JsonObject();
            lastLoc.addProperty("x", lastLocation.getX());
            lastLoc.addProperty("y", lastLocation.getY());
            lastLoc.addProperty("z", lastLocation.getZ());
            lastLoc.addProperty("yaw", lastLocation.getYaw());
            lastLoc.addProperty("pitch", lastLocation.getPitch());
            json.add("lastLocation", lastLoc);
        }
        if (links != null && !links.getLinks().isEmpty()) {
            JsonObject socialLinks = new JsonObject();
            for (String type : links.getLinks().keySet()) {
                socialLinks.addProperty(type, links.getLinks().get(type));
            }
            json.add("socialLinks", socialLinks);
        }
        return json;
    }

    public enum Gender {

        MALE,
        FEMALE,
        NON_BINARY,
        UNKNOWN;

        public static @NotNull Gender getGender(@NotNull String text) {
            for (Gender gender : values()) {
                if (gender.name().equalsIgnoreCase(text)) {
                    return gender;
                }
            }
            return UNKNOWN;
        }

        public final @NotNull String getLocaleName() {
            return getLocaleMessage("profiles.genders." + name().toLowerCase().replace("_", "-"), false);
        }
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof OfflineWander wander) {
            return uuid.equals(wander.uuid);
        }
        return false;
    }

    @Override
    public int hashCode() {
        return uuid.hashCode();
    }

    @Override
    public String toString() {
        return uuid.toString();
    }
}
