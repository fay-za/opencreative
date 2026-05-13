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

package ua.mcchickenstudio.opencreative.utils;

import net.kyori.adventure.resource.ResourcePackInfo;
import net.kyori.adventure.resource.ResourcePackRequest;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.title.Title;
import org.bukkit.*;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeInstance;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.block.sign.Side;
import org.bukkit.boss.KeyedBossBar;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.packs.ResourcePack;
import org.bukkit.permissions.PermissionAttachment;
import org.bukkit.potion.PotionEffect;
import org.bukkit.scheduler.BukkitRunnable;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import ua.mcchickenstudio.opencreative.OpenCreative;
import ua.mcchickenstudio.opencreative.events.player.PlayerLobbyEvent;
import ua.mcchickenstudio.opencreative.settings.Settings;
import ua.mcchickenstudio.opencreative.settings.Sounds;
import ua.mcchickenstudio.opencreative.settings.items.ItemsGroup;
import ua.mcchickenstudio.opencreative.utils.async.AsyncScheduler;
import ua.mcchickenstudio.opencreative.utils.hooks.HookUtils;

import java.net.URI;
import java.time.Duration;
import java.util.*;
import java.util.concurrent.TimeUnit;

import static ua.mcchickenstudio.opencreative.utils.ErrorUtils.sendPlayerErrorMessage;
import static ua.mcchickenstudio.opencreative.utils.ErrorUtils.sendWarningMessage;
import static ua.mcchickenstudio.opencreative.utils.MessageUtils.*;
import static ua.mcchickenstudio.opencreative.utils.world.WorldUtils.isDevPlanet;
import static ua.mcchickenstudio.opencreative.utils.world.WorldUtils.isPlanet;

/**
 * <h1>PlayerUtils</h1>
 * This class contains most used utilities for manipulating
 * with player and his data.
 */
public final class PlayerUtils {

    private final static Set<UUID> enabledSpying = new HashSet<>();
    private final static Map<UUID, PermissionAttachment> permissionAttachmentMap = new HashMap<>();

    /**
     * Clears player from modifications made by world
     * and resets his states, parameters and attributes.
     *
     * @param player player to clear.
     */
    public static void clearPlayer(Player player) {
        clearPlayer(player, true);
    }

    /**
     * Clears player from modifications made by world
     * and resets his states, parameters and attributes.
     *
     * @param player player to clear.
     * @param clearPermissions remove world mode permissions or not.
     */
    public static void clearPlayer(Player player, boolean clearPermissions) {
        clearPlayer(player, clearPermissions, true);
    }

    /**
     * Clears player from modifications made by world
     * and resets his states, parameters and attributes.
     *
     * @param player player to clear.
     * @param clearPermissions remove world mode permissions or not.
     * @param resetGameMode set game mode to adventure or not.
     */
    public static void clearPlayer(Player player, boolean clearPermissions, boolean resetGameMode) {
        if (resetGameMode) player.setGameMode(GameMode.ADVENTURE);
        if (clearPermissions) clearWorldModePermissions(player);
        player.closeInventory();
        if (OpenCreative.getSettings().getLobbySettings().shouldClearInventory(player.getWorld())) {
            player.getInventory().clear();
        }
        for (PotionEffect effect : player.getActivePotionEffects()) {
            player.removePotionEffect(effect.getType());
        }
        for (NamespacedKey recipe : new ArrayList<>(player.getDiscoveredRecipes())) {
            if (recipe.getKey().startsWith("oc_recipe_")) {
                player.undiscoverRecipe(recipe);
            }
        }
        resetAttributes(player);
        player.resetPlayerTime();
        player.resetPlayerWeather();
        player.resetCooldown();
        resetResourcePack(player);
        removePassengers(player);
        if (OpenCreative.getSettings().getLobbySettings().shouldResetViewDistance()) {
            player.setSimulationDistance(Bukkit.getSimulationDistance());
            player.setViewDistance(Math.min(player.getClientViewDistance(), Bukkit.getViewDistance()));
        }
        player.setWorldBorder(player.getWorld().getWorldBorder());
        player.stopAllSounds();
        for (Entity entity : player.getWorld().getEntities()) {
            player.showEntity(OpenCreative.getPlugin(), entity);
        }
        for (Player p : player.getWorld().getPlayers()) {
            player.showEntity(OpenCreative.getPlugin(), p);
        }
        player.setScoreboard(Bukkit.getScoreboardManager().getMainScoreboard());
        clearBossBars(player);
        HookUtils.clearPlayerHook(player);
        if (resetGameMode) player.setGameMode(GameMode.ADVENTURE);
    }

    public static void removePassengers(@NotNull Player player) {
        player.releaseLeftShoulderEntity();
        player.releaseRightShoulderEntity();
        for (Entity passenger : new ArrayList<>(player.getPassengers())) {
            player.eject();
        }
    }

    /**
     * Resets changeable attributes for player.
     *
     * @param player player to clear attributes.
     */
    public static void resetAttributes(Player player) {
        player.setFireTicks(0);
        player.setFreezeTicks(0);
        player.setNoDamageTicks(20);
        player.setMaximumNoDamageTicks(20);
        player.setArrowsInBody(0);
        player.setBeeStingersInBody(0);
        player.setExp(0);
        player.setLevel(0);
        player.setMaxHealth(20);
        player.setHealth(20);
        player.setFoodLevel(20);
        player.setFlying(false);
        player.setGliding(false);
        player.setFlySpeed(0.1f);
        player.setWalkSpeed(0.2f);
        player.setMaximumAir(300);
        player.setRemainingAir(player.getMaximumAir());
        player.setCanPickupItems(true);
        player.setGlowing(false);
        player.setSilent(false);
        player.setCollidable(true);
        player.setAI(true);
        player.setNoPhysics(false);
        player.setVisualFire(false);
        AttributeInstance movementSpeed = player.getAttribute(Attribute.GENERIC_MOVEMENT_SPEED);
        if (movementSpeed != null) movementSpeed.setBaseValue(0.1f);

        AttributeInstance scale = player.getAttribute(Attribute.GENERIC_SCALE);
        if (scale != null) {
            scale.setBaseValue(1);
        }

        AttributeInstance stepHeight = player.getAttribute(Attribute.GENERIC_STEP_HEIGHT);
        if (stepHeight != null) stepHeight.setBaseValue(0.6f);
    }

    /**
     * Clears and removes displayed boss bars for player.
     *
     * @param player player to clear boss bars.
     */
    public static void clearBossBars(Player player) {
        try {
            for (@NotNull Iterator<KeyedBossBar> it = Bukkit.getBossBars(); it.hasNext(); ) {
                KeyedBossBar bar = it.next();
                bar.removePlayer(player);
            }
            player.activeBossBars().forEach(player::hideBossBar);
        } catch (Exception ignored) {
        }
    }

    /**
     * Removes other resource packs and replaces with
     * server's resource pack, or vanilla.
     *
     * @param player player to reset resource pack.
     */
    public static void resetResourcePack(Player player) {
        if (!OpenCreative.getSettings().getLobbySettings().shouldResetResourcePack()) {
            return;
        }
        ResourcePack serverPack = Bukkit.getServerResourcePack();
        if (serverPack == null) {
            player.removeResourcePacks();
            return;
        }
        ResourcePackInfo serverPackInfo = ResourcePackInfo.resourcePackInfo()
                .uri(URI.create(serverPack.getUrl()))
                .hash(serverPack.getHash() == null ? "" : serverPack.getHash())
                .build();
        ResourcePackRequest request = ResourcePackRequest.resourcePackRequest()
                .packs(serverPackInfo)
                .required(Bukkit.isResourcePackRequired())
                .build();
        player.sendResourcePacks(request);
    }

    /**
     * Teleports player to lobby.
     *
     * @param player specified player to teleport.
     **/
    public static void teleportToLobby(Player player) {
        Location location = getLobbyLocation();
        player.eject();
        if (player.isDead()) {
            player.setRespawnLocation(location);
            if (OpenCreative.getPlugin().isEnabled()) {
                player.spigot().respawn();
            }
        }

        if (OpenCreative.getPlugin().isEnabled()) {
            player.teleportAsync(location).thenAccept(success -> {
                if (success) {
                    handleLobbyTeleport(player, location);
                    Bukkit.getScheduler().runTask(OpenCreative.getPlugin(), () -> {
                        PlayerLobbyEvent event = new PlayerLobbyEvent(player);
                        event.callEvent();
                    });
                }
            });
            return;
        } else if (!player.teleport(location)) {
            return;
        }
        handleLobbyTeleport(player, location);
        PlayerLobbyEvent event = new PlayerLobbyEvent(player);
        event.callEvent();
    }

    private static void handleLobbyTeleport(@NotNull Player player, @NotNull Location location) {
        clearPlayer(player, true, OpenCreative.getSettings().getLobbySettings().shouldResetGameMode(location.getWorld()));
        /* player.showTitle(Title.title(
                toComponent(getLocaleMessage("lobby.title")), toComponent(getLocaleMessage("lobby.subtitle")),
                Title.Times.times(Duration.ofSeconds(1), Duration.ofSeconds(3), Duration.ofSeconds(1))
        )); */
        player.sendMessage(toComponent(getLocaleMessage("lobby.message")));
        Sounds.LOBBY.play(player);
        // Sounds.LOBBY_MUSIC.play(player);

        ItemsGroup.LOBBY.setItems(player);
        player.getInventory().setHeldItemSlot(4);

        giveLobbyPermissions(player);
    }

    /**
     * Returns a lobby world, where player will get lobby items.
     *
     * @return lobby world.
     */
    public static World getLobbyWorld() {
        String spawnWorld = OpenCreative.getPlugin().getConfig().getString("lobby.world");
        if (spawnWorld == null || spawnWorld.isEmpty() || Bukkit.getWorld(spawnWorld) == null) {
            spawnWorld = "world";
        }
        return Bukkit.getWorld(spawnWorld);
    }

    /**
     * Returns a spawn location in lobby world, where players
     * will be connected to on server join.
     *
     * @return spawn location in lobby world.
     */
    public static Location getLobbyLocation() {
        World world = getLobbyWorld();
        world.setGameRule(GameRule.DO_LIMITED_CRAFTING, true);
        ConfigurationSection data = OpenCreative.getPlugin().getConfig().getConfigurationSection("lobby.spawn");
        if (data == null) {
            return world.getSpawnLocation();
        }
        Location location = world.getSpawnLocation();
        double x = data.getDouble("x", location.getX());
        double y = data.getDouble("y", location.getX());
        double z = data.getDouble("z", location.getX());
        float yaw = (float) data.getDouble("yaw", location.getYaw());
        float pitch = (float) data.getDouble("pitch", location.getPitch());
        return new Location(world, x, y, z, yaw, pitch);
    }

    /**
     * Checks if entity is probably in developers planet.
     * <p>
     * <b>Note: Can return true, even if planet will be not registered in base.</b>
     *
     * @param entity entity to check.
     * @return true - if entity is probably in developers planet, false - in planet world or lobby.
     */
    public static boolean isEntityInDevPlanet(@NotNull Entity entity) {
        return isDevPlanet(entity.getWorld());
    }

    /**
     * Checks if entity is in lobby.
     *
     * @param entity entity to check.
     * @return true - if entity in lobby, false - in planet or dev planet.
     */
    public static boolean isEntityInLobby(@NotNull Entity entity) {
        return getLobbyWorld().equals(entity.getWorld());
    }

    /**
     * Registers permission attachments in memory for all online players.
     */
    public static void loadPermissions() {
        permissionAttachmentMap.clear();
        for (Player player : Bukkit.getOnlinePlayers()) {
            loadPermissions(player);
        }
    }

    /**
     * Registers player's permission attachment to memory.
     *
     * @param player player to load permissions.
     */
    public static void loadPermissions(@NotNull Player player) {
        PermissionAttachment permissionAttachment = player.addAttachment(OpenCreative.getPlugin());
        permissionAttachmentMap.put(player.getUniqueId(), permissionAttachment);
    }

    /**
     * Removes stored player's permission attachment from memory.
     *
     * @param player player to remove attachment.
     */
    public static void removeFromPermissionsMap(@NotNull Player player) {
        clearWorldModePermissions(player);
        permissionAttachmentMap.remove(player.getUniqueId());
    }

    /**
     * Allows player to see local chat message from other worlds.
     *
     * @param player player, that will see messages.
     */
    public static boolean enableSpying(@NotNull Player player) {
        return enabledSpying.add(player.getUniqueId());
    }

    /**
     * Disallows player to see local chat message from other worlds.
     *
     * @param player player, that won't see messages anymore.
     */
    public static boolean disableSpying(@NotNull Player player) {
        return enabledSpying.remove(player.getUniqueId());
    }

    /**
     * Checks whether player can see local chat messages from other worlds.
     *
     * @param player player to check.
     * @return true - can see, false - not.
     */
    public static boolean isSpying(@NotNull Player player) {
        return enabledSpying.contains(player.getUniqueId());
    }

    /**
     * Returns set of players, who can see local chat messages
     * from other worlds.
     *
     * @return set of players, who can see local chat.
     */
    public static Set<Player> getPlayersWithEnabledSpying() {
        Set<Player> players = new HashSet<>();
        if (enabledSpying.isEmpty()) return players;
        for (UUID uuid : new HashSet<>(enabledSpying)) {
            Player player = Bukkit.getPlayer(uuid);
            if (player == null) {
                enabledSpying.remove(uuid);
                continue;
            }
            players.add(player);
        }
        return players;
    }

    /**
     * Sets player's permissions when they enter developers planet.
     *
     * @param player player to give permissions.
     */
    public static void giveDevPermissions(@NotNull Player player) {
        PermissionAttachment permissionAttachment = permissionAttachmentMap.get(player.getUniqueId());
        if (permissionAttachment == null) return;
        Set<String> perms = OpenCreative.getSettings().getGroups().getGroup(player).getDevPermissions();
        for (String permission : perms) {
            boolean negated = permission.startsWith("!");
            String node = negated ? permission.substring(1) : permission;
            permissionAttachment.setPermission(node, !negated);
        }
    }

    /**
     * Sets player's permissions when they enter planet in play mode.
     *
     * @param player player to give permissions.
     */
    public static void givePlayPermissions(@NotNull Player player) {
        PermissionAttachment permissionAttachment = permissionAttachmentMap.get(player.getUniqueId());
        if (permissionAttachment == null) return;
        Set<String> perms = OpenCreative.getSettings().getGroups().getGroup(player).getPlayPermissions();
        for (String permission : perms) {
            boolean negated = permission.startsWith("!");
            String node = negated ? permission.substring(1) : permission;
            permissionAttachment.setPermission(node, !negated);
        }
    }

    private static boolean containsWorldEditPermission(@NotNull Set<String> permissions) {
        for (String permission : permissions) {
            if (permission.startsWith("worldedit.")) {
                return true;
            }
        }
        return false;
    }

    /**
     * Sets player's permissions when they enter planet in build mode.
     *
     * @param player player to give permissions.
     */
    public static void giveBuildPermissions(@NotNull Player player) {
        PermissionAttachment permissionAttachment = permissionAttachmentMap.get(player.getUniqueId());
        if (permissionAttachment == null) return;
        Set<String> perms = OpenCreative.getSettings().getGroups().getGroup(player).getBuildPermissions();
        World world = player.getWorld();
        if (isPlanet(world) && containsWorldEditPermission(perms)) {
            permissionAttachment.setPermission("worldguard.region.bypass." + world.getName(), true);
        }
        for (String permission : perms) {
            boolean negated = permission.startsWith("!");
            String node = negated ? permission.substring(1) : permission;
            permissionAttachment.setPermission(node, !negated);
        }
    }

    /**
     * Sets player's permissions when they enter planet in play or build mode,
     * but not being as world builder, developer or owner.
     *
     * @param player player to give permissions.
     */
    public static void giveVisitorPermissions(@NotNull Player player) {
        PermissionAttachment permissionAttachment = permissionAttachmentMap.get(player.getUniqueId());
        if (permissionAttachment == null) return;
        Set<String> perms = OpenCreative.getSettings().getGroups().getGroup(player).getVisitorPermissions();
        for (String permission : perms) {
            boolean negated = permission.startsWith("!");
            String node = negated ? permission.substring(1) : permission;
            permissionAttachment.setPermission(node, !negated);
        }
    }

    /**
     * Sets player's permissions when they enter lobby.
     *
     * @param player player to give permissions.
     */
    public static void giveLobbyPermissions(@NotNull Player player) {
        PermissionAttachment permissionAttachment = permissionAttachmentMap.get(player.getUniqueId());
        if (permissionAttachment == null) return;
        Set<String> perms = OpenCreative.getSettings().getGroups().getGroup(player).getLobbyPermissions();
        for (String permission : perms) {
            boolean negated = permission.startsWith("!");
            String node = negated ? permission.substring(1) : permission;
            permissionAttachment.setPermission(node, !negated);
        }
    }

    /**
     * Removes player's permissions that he got in world in Build, Play, Dev mode, or in lobby.
     *
     * @param player player to remove permissions.
     */
    public static void clearWorldModePermissions(@NotNull Player player) {
        PermissionAttachment permissionAttachment = permissionAttachmentMap.get(player.getUniqueId());
        if (permissionAttachment == null) return;
        Map<String, Boolean> permissions = permissionAttachment.getPermissions();
        Set<Map.Entry<String, Boolean>> permissionsCopy = new HashSet<>(permissions.entrySet());
        for (Map.Entry<String, Boolean> entry : permissionsCopy) {
            String key = entry.getKey();
            permissionAttachment.unsetPermission(key);
        }
    }

    /**
     * Translates sign text on code block.
     *
     * @param block block with sign that will be translated.
     */
    public static void translateBlockSign(Block block) {
        if (!block.getType().toString().contains("SIGN")) return;
        Sign sign = (Sign) block.getState();
        AsyncScheduler.run(() -> {
            List<Component> newLines = new ArrayList<>();
            for (Component line : sign.getSide(Side.FRONT).lines()) {
                String content = ((TextComponent) line).content();
                String path = "blocks." + content;
                if (content.isEmpty()) {
                    newLines.add(Component.text(""));
                } else if (!messageExists(path)) {
                    newLines.add(Component.text(content));
                } else {
                    newLines.add(toComponent(getLocaleMessage(path, false)));
                }
            }
            AsyncScheduler.later(() -> {
                for (Player player : block.getLocation().getWorld().getPlayers()) {
                    player.sendSignChange(block.getLocation(), newLines);
                }
            }, AsyncScheduler.getScheduler(), 100, TimeUnit.MILLISECONDS);
        }, AsyncScheduler.getScheduler());
    }

    /**
     * Translates sign text on code block.
     *
     * @param block block with sign that will be translated.
     */
    public static void translateBlockSign(Block block, Player player) {
        if (block == null) return;
        if (!block.getType().toString().contains("SIGN")) return;
        List<Component> newLines = new ArrayList<>();
        Sign sign = (Sign) block.getState();
        for (Component line : sign.getSide(Side.FRONT).lines()) {
            String content = ((TextComponent) line).content();
            String path = "blocks." + content;
            if (content.isEmpty()) {
                newLines.add(Component.text(" "));
            } else if (!messageExists(path)) {
                newLines.add(Component.text(content));
            } else {
                newLines.add(toComponent(getLocaleMessage(path, false)));
            }
        }
        new BukkitRunnable() {
            @Override
            public void run() {
                player.sendSignChange(block.getLocation(), newLines);
            }
        }.runTaskLater(OpenCreative.getPlugin(), 10L);
    }

    public static void translateSigns(Player player, int radius) {
        if (radius <= 0) return;
        if (radius > 50) radius = 50;
        if (!player.isConnected()) return;
        int minX = player.getLocation().getBlockX() - radius;
        int maxX = player.getLocation().getBlockX() + radius;
        int minZ = player.getLocation().getBlockZ() - radius;
        int maxZ = player.getLocation().getBlockZ() + radius;
        int y = player.getLocation().getBlockY();
        for (int x = minX; x <= maxX; x++) {
            for (int z = minZ; z <= maxZ; z++) {
                Block block = player.getWorld().getBlockAt(x, y, z);
                if (block.getType().name().contains("WALL_SIGN")) {
                    translateBlockSign(block, player);
                }
            }
        }
    }

    public static void spawnGlowingBlock(Player player, Location location) {
        if (OpenCreative.getPacketManager().isEnabled()) {
            try {
                OpenCreative.getPacketManager().displayGlowingBlock(player, location);
            } catch (Exception error) {
                sendPlayerErrorMessage(player, "Failed to spawn glowing block", error);
            }
        }
    }

    public static void sendOpenedChestAnimation(Player player, Block block) {
        if (OpenCreative.getPacketManager().isEnabled()) {
            try {
                OpenCreative.getPacketManager().sendChestOpenAnimation(player, block);
            } catch (Exception error) {
                sendPlayerErrorMessage(player, "Failed to display opened chest animation", error);
            }
        }
    }

    public static void sendClosedChestAnimation(Player player, Block block) {
        if (OpenCreative.getPacketManager().isEnabled()) {
            try {
                OpenCreative.getPacketManager().sendChestCloseAnimation(player, block);
            } catch (Exception error) {
                sendPlayerErrorMessage(player, "Failed to display closed chest animation", error);
            }
        }
    }

    public static void hidePlayerInTab(Player spectator, Player receiver) {
        if (spectator == receiver) return;
        Settings.PlayerListChanger changer = OpenCreative.getSettings().getListChanger();
        if (changer == Settings.PlayerListChanger.SPECTATOR) {
            if (OpenCreative.getPacketManager().isEnabled()) {
                try {
                    OpenCreative.getPacketManager().displayAsSpectatorName(spectator, receiver);
                } catch (Exception error) {
                    sendWarningMessage("Failed to mark player " + spectator.getName() + "as hidden in tab", error);
                }
            } else {
                receiver.hidePlayer(OpenCreative.getPlugin(), spectator);
            }
        } else if (changer == Settings.PlayerListChanger.FULL) {
            receiver.hidePlayer(OpenCreative.getPlugin(), spectator);
        }
    }

    public static void showPlayerFromTab(Player spectator, Player receiver) {
        if (spectator == receiver) return;
        if (OpenCreative.getPacketManager().isEnabled() && OpenCreative.getSettings().getListChanger() == Settings.PlayerListChanger.SPECTATOR) {
            try {
                OpenCreative.getPacketManager().removeSpectatorName(spectator, receiver);
            } catch (Exception error) {
                sendWarningMessage("Failed send uncolored spectator name " + spectator.getName(), error);
            }
        } else {
            receiver.showPlayer(OpenCreative.getPlugin(), spectator);
        }
    }

    public static @Nullable UUID getUUIDFromText(@NotNull String text) {
        try {
            return UUID.fromString(text);
        } catch (Exception ignored) {
            return null;
        }
    }

}
