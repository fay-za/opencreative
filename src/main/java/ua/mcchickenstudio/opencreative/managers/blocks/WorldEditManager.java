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

package ua.mcchickenstudio.opencreative.managers.blocks;

import com.sk89q.worldedit.EditSession;
import com.sk89q.worldedit.MaxChangedBlocksException;
import com.sk89q.worldedit.WorldEdit;
import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldedit.entity.BaseEntity;
import com.sk89q.worldedit.event.extent.EditSessionEvent;
import com.sk89q.worldedit.extent.AbstractDelegateExtent;
import com.sk89q.worldedit.extent.Extent;
import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldedit.regions.CuboidRegion;
import com.sk89q.worldedit.regions.Region;
import com.sk89q.worldedit.util.eventbus.Subscribe;
import com.sk89q.worldedit.world.World;
import com.sk89q.worldedit.world.biome.BiomeType;
import com.sk89q.worldedit.world.block.*;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import ua.mcchickenstudio.opencreative.OpenCreative;
import ua.mcchickenstudio.opencreative.planets.Planet;
import ua.mcchickenstudio.opencreative.utils.SystemUtils;
import ua.mcchickenstudio.opencreative.utils.hooks.HookUtils;
import ua.mcchickenstudio.opencreative.wanders.Wander;

import java.util.concurrent.CompletableFuture;

import static ua.mcchickenstudio.opencreative.utils.world.WorldUtils.isLobbyWorld;

/**
 * <h1>WorldEditManager</h1>
 * This class represents a manager, that controls changing
 * many blocks, using WorldEdit.
 */
public final class WorldEditManager implements BlocksManager {

    @Override
    public @NotNull CompletableFuture<Integer> setBlocksType(@NotNull Location first, @NotNull Location second, @NotNull Material material, int limit) {
        World world = BukkitAdapter.adapt(first.getWorld());
        CompletableFuture<Integer> future = new CompletableFuture<>();
        // Checks whether its FastAsyncWorldEdit or classic WorldEdit
        if (HookUtils.isPluginEnabled("FastAsyncWorldEdit")) {
            Bukkit.getScheduler().runTaskAsynchronously(OpenCreative.getPlugin(), () -> {
                setBlocks(world, future, first, second, material, limit);
            });
        } else {
            setBlocks(world, future, first, second, material, limit);
        }
        return future;
    }

    private void setBlocks(@NotNull World world, @NotNull CompletableFuture<Integer> future,
                           @NotNull Location first, @NotNull Location second,
                           @NotNull Material material, int limit) {
        Region selection = new CuboidRegion(world, BlockVector3.at(
                first.getBlockX(), first.getBlockY(), first.getBlockZ()),
                BlockVector3.at(second.getBlockX(), second.getBlockY(), second.getBlockZ()));
        try (EditSession editSession = WorldEdit.getInstance().newEditSessionBuilder().world(world).maxBlocks(limit).build()) {
            BlockState blockState = BukkitAdapter.adapt(material.createBlockData());
            future.complete(editSession.setBlocks(selection, blockState));
        } catch (MaxChangedBlocksException error) {
            future.complete(limit);
        }
    }

    @Override
    public void init() {
        if (SystemUtils.getSystemProperty("worldedit.registered") != null) {
            return;
        }
        SystemUtils.setSystemProperty("worldedit.registered", "true");
        Object onSessionEvent = new Object() {
            @Subscribe
            public void onEditSessionEvent(EditSessionEvent event) {
                if (event.getStage() != EditSession.Stage.BEFORE_HISTORY) return;
                if (event.getActor() == null) return;
                org.bukkit.World bukkitWorld = BukkitAdapter.adapt(event.getWorld());
                Planet planet = OpenCreative.getPlanetsManager().getPlanetByWorld(bukkitWorld);
                if (planet == null) {
                    if (isLobbyWorld(bukkitWorld) && OpenCreative.getSettings().getLobbySettings().isWorldEditDisallowed()
                            && !event.getActor().hasPermission("opencreative.lobby.world-edit.bypass")) {
                        event.setExtent(new DisallowedExtent(event.getExtent()));
                    }
                    return;
                }
                CommandSender sender = BukkitAdapter.adapt(event.getActor());
                if (sender instanceof Player player) {
                    if (planet.getWorldPlayers().canBuild(player)) {
                        event.setExtent(new PlanetExtent(planet, event.getExtent(), player));
                    } else {
                        event.setExtent(new DisallowedExtent(event.getExtent()));
                    }
                    return;
                }
                event.setExtent(new PlanetExtent(planet, event.getExtent()));
            }
        };
        WorldEdit.getInstance().getEventBus().register(onSessionEvent);
    }

    @Override
    public boolean isEnabled() {
        return WorldEdit.getInstance() != null;
    }

    @Override
    public String getName() {
        return "WorldEdit Blocks Manager";
    }

    static class PlanetExtent extends AbstractDelegateExtent {

        private static final BlockState AIRSTATE = BlockTypes.AIR.getDefaultState();
        private static final BaseBlock AIRBASE = BlockTypes.AIR.getDefaultState().toBaseBlock();
        private final Planet planet;
        private final Player player;

        public PlanetExtent(Planet planet, Extent extent) {
            super(extent);
            this.planet = planet;
            this.player = null;
        }

        public PlanetExtent(Planet planet, Extent extent, Player player) {
            super(extent);
            this.planet = planet;
            this.player = player;
        }

        @SuppressWarnings("unchecked")
        @Override
        public boolean setBlock(BlockVector3 location, BlockStateHolder block) {
            boolean result = planet.getWorld().getWorldBorder().isInside(
                    new Location(planet.getWorld(),
                            location.x(),
                            location.y(),
                            location.z())
            ) && super.setBlock(location, block);
            if (result && player != null) {
                BlockType type = block.getBlockType();
                if (type.equals(BlockTypes.LAVA) || type.equals(BlockTypes.TNT) || type.equals(BlockTypes.AIR)) {
                    Wander wander = OpenCreative.getWander(player);
                    if (type.equals(BlockTypes.LAVA)) {
                        wander.getGriefStats().addLavaPlacementsAmount(1);
                    } else if (type.equals(BlockTypes.TNT)) {
                        wander.getGriefStats().addTntPlacementsAmount(1);
                    } else if (type.equals(BlockTypes.AIR)) {
                        wander.getGriefStats().addDestroyedBlocksAmount(1);
                    }
                }
            }
            return result;
        }

        @Override
        public com.sk89q.worldedit.entity.Entity createEntity(com.sk89q.worldedit.util.Location location, BaseEntity entity) {
            if (planet.getWorld().getWorldBorder().isInside(
                    new Location(planet.getWorld(),
                            location.x(),
                            location.y(),
                            location.z())
            )) {
                super.createEntity(location, entity);
            }
            return null;
        }

        @Override
        public boolean setBiome(BlockVector3 location, BiomeType biome) {
            return planet.getWorld().getWorldBorder().isInside(
                    new Location(planet.getWorld(),
                            location.x(),
                            location.y(),
                            location.z())
            ) && super.setBiome(location, biome);
        }

        @Override
        public BlockState getBlock(BlockVector3 location) {
            if (planet.getWorld().getWorldBorder().isInside(
                    new Location(planet.getWorld(),
                            location.x(),
                            location.y(),
                            location.z())
            )) {
                return super.getBlock(location);
            }
            return AIRSTATE;
        }

        @Override
        public BaseBlock getFullBlock(BlockVector3 location) {
            if (planet.getWorld().getWorldBorder().isInside(
                    new Location(planet.getWorld(),
                            location.x(),
                            location.y(),
                            location.z())
            )) {
                return super.getFullBlock(location);
            }
            return AIRBASE;
        }
    }

    static class DisallowedExtent extends AbstractDelegateExtent {

        public static final BlockState AIRSTATE = BlockTypes.AIR.getDefaultState();
        public static final BaseBlock AIRBASE = BlockTypes.AIR.getDefaultState().toBaseBlock();

        public DisallowedExtent(Extent extent) {
            super(extent);
        }

        @Override
        public boolean setBlock(BlockVector3 location, BlockStateHolder block) {
            return false;
        }

        @Override
        public com.sk89q.worldedit.entity.Entity createEntity(com.sk89q.worldedit.util.Location location, BaseEntity entity) {
            return null;
        }

        @Override
        public boolean setBiome(BlockVector3 location, BiomeType biome) {
            return false;
        }

        @Override
        public BlockState getBlock(BlockVector3 location) {
            return AIRSTATE;
        }

        @Override
        public BaseBlock getFullBlock(BlockVector3 location) {
            return AIRBASE;
        }
    }
}
