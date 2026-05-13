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

package ua.mcchickenstudio.opencreative.managers.packets;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.ProtocolManager;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.wrappers.*;
import it.unimi.dsi.fastutil.ints.IntArrayList;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.jetbrains.annotations.NotNull;
import ua.mcchickenstudio.opencreative.OpenCreative;
import ua.mcchickenstudio.opencreative.utils.world.cache.ChunkPacketListener;

import java.util.*;

import static com.comphenix.protocol.PacketType.Play.Server.*;
import static com.comphenix.protocol.wrappers.EnumWrappers.PlayerInfoAction.UPDATE_GAME_MODE;

/**
 * This class represents an implementation of ProtocolLib
 * for packets actions.
 */
public final class ProtocolLibManager implements PacketManager {

    private ProtocolManager manager;

    @Override
    public void init() {
        manager = ProtocolLibrary.getProtocolManager();
        manager.addPacketListener(new ChunkPacketListener(OpenCreative.getPlugin()));
    }

    @Override
    public void displayGlowingBlock(@NotNull Player player, @NotNull Location location) {
        World world = player.getWorld();
        UUID uuid = UUID.randomUUID();
        int id = 300;
        if (location.getX() == location.getBlockX() && location.getZ() == location.getBlockZ()) {
            location.add(0.5, 0, 0.5);
        }
        PacketContainer spawnEntityPacket = getSpawnFallingBlockPacket(id, uuid, location);
        PacketContainer entityDataPacket = getFallingBlockDataPacket(id);
        PacketContainer createTeamPacket = getTeamCreationPacket(uuid, ChatColor.GREEN);
        PacketContainer hideGlowingPacket = getRemoveEntityPacket(id);
        manager.sendServerPacket(player, createTeamPacket);
        manager.sendServerPacket(player, spawnEntityPacket);
        manager.sendServerPacket(player, entityDataPacket);
        new BukkitRunnable() {
            @Override
            public void run() {
                if (player.getWorld() == world) {
                    manager.sendServerPacket(player, hideGlowingPacket);
                    manager.sendServerPacket(player, getTeamDeletionPacket());
                }
            }
        }.runTaskLater(OpenCreative.getPlugin(), 60L);
    }

    @Override
    public void sendChestOpenAnimation(@NotNull Player player, @NotNull Block block) {
        Bukkit.getScheduler().runTaskAsynchronously(OpenCreative.getPlugin(), () -> {
            PacketContainer blockActionPacket = manager.createPacket(BLOCK_ACTION);
            blockActionPacket.getBlockPositionModifier().write(0, new BlockPosition(block.getLocation().toVector()));
            blockActionPacket.getIntegers().write(0, 1);
            blockActionPacket.getIntegers().write(1, 1);
            manager.sendServerPacket(player, blockActionPacket);
        });
    }

    @Override
    public void sendChestCloseAnimation(@NotNull Player player, @NotNull Block block) {
        Bukkit.getScheduler().runTaskAsynchronously(OpenCreative.getPlugin(), () -> {
            PacketContainer blockActionPacket = manager.createPacket(BLOCK_ACTION);
            blockActionPacket.getBlockPositionModifier().write(0, new BlockPosition(block.getLocation().toVector()));
            blockActionPacket.getIntegers().write(0, 1);
            blockActionPacket.getIntegers().write(1, 0);
            manager.sendServerPacket(player, blockActionPacket);
        });
    }

    @Override
    public void displayAsSpectatorName(@NotNull Player player, @NotNull Player receiver) {
        PacketContainer packet = ProtocolLibrary.getProtocolManager().createPacket(PacketType.Play.Server.PLAYER_INFO);
        packet.getPlayerInfoActions().write(0, EnumSet.of(UPDATE_GAME_MODE));
        packet.getPlayerInfoDataLists().write(1, Collections.singletonList(new PlayerInfoData(
                new WrappedGameProfile(player.getUniqueId(), player.getName()),
                player.getPing(),
                EnumWrappers.NativeGameMode.SPECTATOR,
                WrappedChatComponent.fromText(player.getName())
        )));
        manager.sendServerPacket(receiver, packet);
    }

    @Override
    public void removeSpectatorName(@NotNull Player player, @NotNull Player receiver) {
        if (player.getGameMode() == GameMode.SPECTATOR) return;
        PacketContainer packet = ProtocolLibrary.getProtocolManager().createPacket(PLAYER_INFO);
        packet.getPlayerInfoActions().write(0, EnumSet.of(UPDATE_GAME_MODE));
        packet.getPlayerInfoDataLists().write(1, Collections.singletonList(new PlayerInfoData(
                new WrappedGameProfile(player.getUniqueId(), player.getName()),
                player.getPing(),
                EnumWrappers.NativeGameMode.valueOf(player.getGameMode().name()),
                WrappedChatComponent.fromText(player.getName())
        )));
        manager.sendServerPacket(receiver, packet);
    }

    @Override
    public void showBlockForPlayer(@NotNull Player player, @NotNull Location location, @NotNull Material material) {
        PacketContainer packet = ProtocolLibrary.getProtocolManager().createPacket(PacketType.Play.Server.BLOCK_CHANGE);
        packet.getBlockPositionModifier().write(0, new BlockPosition(location.getBlockX(), location.getBlockY(), location.getBlockZ()));
        packet.getBlockData().write(0, WrappedBlockData.createData(material));
        ProtocolLibrary.getProtocolManager().sendServerPacket(player, packet);
    }

    @Override
    public boolean isEnabled() {
        return manager != null;
    }

    @Override
    public String getName() {
        return "ProtocolLib Packet Manager";
    }

    private PacketContainer getRemoveEntityPacket(int id) {
        PacketContainer hideGlowingPacket = manager.createPacket(ENTITY_DESTROY);
        hideGlowingPacket.getModifier().write(0, new IntArrayList(new int[]{id}));
        return hideGlowingPacket;
    }

    private PacketContainer getTeamCreationPacket(UUID uuid, ChatColor color) {
        PacketContainer createTeamPacket = manager.createPacket(SCOREBOARD_TEAM);
        createTeamPacket.getIntegers().write(0, 0);
        createTeamPacket.getStrings().write(0, "oc_block_display");
        createTeamPacket.getOptionalTeamParameters().write(0,
                Optional.of(WrappedTeamParameters.newBuilder()
                        .displayName(WrappedChatComponent.fromText("oc_block_display"))
                        .prefix(WrappedChatComponent.fromText("oc"))
                        .suffix(WrappedChatComponent.fromText("oc"))
                        .nametagVisibility("never")
                        .collisionRule("never")
                        .color(EnumWrappers.ChatFormatting.fromBukkit(color))
                        .build()));
        createTeamPacket.getSpecificModifier(Collection.class).write(0, Collections.singletonList(uuid.toString()));
        return createTeamPacket;
    }

    private PacketContainer getTeamDeletionPacket() {
        PacketContainer deletionTeamPacket = manager.createPacket(SCOREBOARD_TEAM);
        deletionTeamPacket.getIntegers().write(0, 1);
        deletionTeamPacket.getStrings().write(0, "oc_block_display");
        return deletionTeamPacket;
    }

    private PacketContainer getFallingBlockDataPacket(int id) {
        PacketContainer entityDataPacket = manager.createPacket(PacketType.Play.Server.ENTITY_METADATA);
        entityDataPacket.getIntegers().write(0, id);
        WrappedDataWatcher watcher = new WrappedDataWatcher();
        watcher.setByte(0, (byte) (0x20 | 0x40), true); // Glowing and Invisible
        watcher.setInteger(16, 2, true);
        watcher.setBoolean(5, true, true); // No gravity

        List<WrappedDataValue> wrappedDataValueList = new ArrayList<>();
        for (final WrappedWatchableObject entry : watcher.getWatchableObjects()) {
            if (entry == null) continue;
            final WrappedDataWatcher.WrappedDataWatcherObject watcherObject = entry.getWatcherObject();
            wrappedDataValueList.add(
                    new WrappedDataValue(
                            watcherObject.getIndex(),
                            watcherObject.getSerializer(),
                            entry.getRawValue()
                    )
            );
        }
        entityDataPacket.getDataValueCollectionModifier().write(0, wrappedDataValueList);
        return entityDataPacket;
    }

    private PacketContainer getSpawnFallingBlockPacket(int id, UUID uuid, Location location) {
        PacketContainer spawnEntityPacket = manager.createPacket(SPAWN_ENTITY);
        spawnEntityPacket.getIntegers().write(0, id);
        spawnEntityPacket.getUUIDs().write(0, uuid);
        spawnEntityPacket.getEntityTypeModifier().write(0, EntityType.SLIME);
        spawnEntityPacket.getDoubles()
                .write(0, location.getX())
                .write(1, location.getY())
                .write(2, location.getZ());
        //spawnEntityPacket.getModifier().write(12,1);
        return spawnEntityPacket;
    }
}
