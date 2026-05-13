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

package ua.mcchickenstudio.opencreative.listeners.player;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.*;
import org.bukkit.block.data.Directional;
import org.bukkit.block.data.Powerable;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import ua.mcchickenstudio.opencreative.OpenCreative;
import ua.mcchickenstudio.opencreative.coding.blocks.actions.ActionCategory;
import ua.mcchickenstudio.opencreative.coding.blocks.events.player.interaction.PlaceBlockEvent;
import ua.mcchickenstudio.opencreative.coding.blocks.executors.ExecutorCategory;
import ua.mcchickenstudio.opencreative.coding.menus.layouts.Layout;
import ua.mcchickenstudio.opencreative.planets.DevPlanet;
import ua.mcchickenstudio.opencreative.planets.DevPlatform;
import ua.mcchickenstudio.opencreative.planets.Planet;
import ua.mcchickenstudio.opencreative.settings.Sounds;
import ua.mcchickenstudio.opencreative.utils.ItemUtils;

import java.util.HashSet;
import java.util.Set;

import static ua.mcchickenstudio.opencreative.utils.BlockUtils.copySignData;
import static ua.mcchickenstudio.opencreative.utils.MessageUtils.getLocaleComponent;
import static ua.mcchickenstudio.opencreative.utils.MessageUtils.getLocaleMessage;
import static ua.mcchickenstudio.opencreative.utils.PlayerUtils.isEntityInLobby;
import static ua.mcchickenstudio.opencreative.utils.PlayerUtils.translateBlockSign;

public final class PlaceBlockListener implements Listener {

    public static void placeDevBlock(Location location, Material material, Material additionalBlockMaterial, Material signMaterial, String signText) {
        Block block = location.getBlock();
        block.setType(material);
        Block eastBlock = block.getRelative(BlockFace.EAST);
        eastBlock.setType(additionalBlockMaterial);
        if (eastBlock.getType() == Material.PISTON) {
            Directional data = (Directional) eastBlock.getBlockData();
            data.setFacing(BlockFace.EAST);
            eastBlock.setBlockData(data);
            Block farEastBlock = eastBlock.getRelative(BlockFace.EAST).getRelative(BlockFace.EAST);
            move(eastBlock.getLocation(), BlockFace.EAST);
            if (farEastBlock.isEmpty()) {
                farEastBlock.setType(Material.PISTON);
                if (farEastBlock.getBlockData() instanceof Directional directional) {
                    directional.setFacing(BlockFace.WEST);
                    farEastBlock.setBlockData(directional);
                }
            }

        }

        Block wallSign = block.getRelative(BlockFace.SOUTH);
        wallSign.setType(signMaterial);

        Sign sign = (Sign) wallSign.getState();
        sign.setLine(1, signText);
        if (block.getType() == Material.OXIDIZED_COPPER) {
            sign.setLine(2, "");
        }
        if (block.getType() == Material.PURPUR_BLOCK) {
            sign.setLine(1, "");
            sign.setLine(3, "selection_set");
        }
        sign.update();

        translateBlockSign(wallSign);

        Directional data = (Directional) wallSign.getBlockData();
        data.setFacing(BlockFace.SOUTH);
        wallSign.setBlockData(data);
    }

    public static void placeDebugTorch(@NotNull Location location) {
        Block block = location.getBlock();
        block.setType(Material.REDSTONE_WALL_TORCH);
        if (block.getBlockData() instanceof Directional directional) {
            directional.setFacing(BlockFace.WEST);
            block.setBlockData(directional);
        }
        if (block.getBlockData() instanceof Powerable powerable) {
            powerable.setPowered(true);
            block.setBlockData(powerable);
        }
    }

    public static boolean move(Location location, BlockFace face) {
        DevPlanet devPlanet = OpenCreative.getPlanetsManager().getDevPlanet(location.getWorld());
        if (devPlanet == null) return false;
        DevPlatform platform = devPlanet.getPlatformInLocation(location);
        if (platform == null) return false;
        Location begin = devPlanet.getDevPlatformer().getPlatformBeginLocation(platform);
        Location end = devPlanet.getDevPlatformer().getPlatformEndLocation(platform);
        if (face == BlockFace.EAST) {
            /*
             Moves blocks to right
             */
            if (location.getX() >= end.getBlockX() - 4) return false;
            Set<Block> movedBlocks = new HashSet<>();
            for (double x = end.getBlockX() - 5; x > location.getX(); x--) {
                Block oldBlock = location.getWorld().getBlockAt((int) x, location.getBlockY(), location.getBlockZ());
                if (oldBlock.getType() == Material.AIR) continue;
                if (!movedBlocks.contains(oldBlock)) {
                    Block newBlock = location.getWorld().getBlockAt((int) x + 2, location.getBlockY(), location.getBlockZ());
                    if (!newBlock.isEmpty()) {
                        oldBlock.getRelative(BlockFace.UP).setType(Material.AIR);
                        oldBlock.getRelative(BlockFace.SOUTH).setType(Material.AIR);
                        oldBlock.setType(Material.AIR);
                        continue;
                    }
                    moveCodingBlock(oldBlock, newBlock);
                    movedBlocks.add(newBlock);
                }
            }
            Location farPiston = end.clone().add(-3, 1, 0);
            farPiston.setZ(location.getZ());
            if (farPiston.getBlock().getType() == Material.PISTON) {
                if (!farPiston.getBlock().getRelative(BlockFace.WEST).isEmpty()) {
                    if (farPiston.getBlock().getBlockData() instanceof Directional directional) {
                        directional.setFacing(BlockFace.EAST);
                        farPiston.getBlock().setBlockData(directional);
                    }
                }
            }
        } else if (face == BlockFace.WEST) {
            /*
             Moves blocks to left.
             */
            if (location.getX() <= begin.getBlockX() + 5) return false;
            if (!location.getBlock().isEmpty()) return false;
            if (!location.getBlock().getRelative(BlockFace.EAST).isEmpty()) return false;
            Set<Block> movedBlocks = new HashSet<>();
            for (double x = location.getX() + 1; x < end.getBlockX(); x++) {
                Block oldBlock = location.getWorld().getBlockAt((int) x, location.getBlockY(), location.getBlockZ());
                if (oldBlock.getType() == Material.AIR) continue;
                if (!movedBlocks.contains(oldBlock)) {
                    Block newBlock = location.getWorld().getBlockAt((int) x - 2, location.getBlockY(), location.getBlockZ());
                    moveCodingBlock(oldBlock, newBlock);
                    movedBlocks.add(oldBlock);
                }
            }
        }
        return true;
    }

    /**
     * Moves coding block with wall sign and container to next block.
     *
     * @param oldBlock Block to move and replace with air.
     * @param newBlock Block where to move.
     */
    public static void moveCodingBlock(Block oldBlock, Block newBlock) {
        Block oldSignBlock = oldBlock.getRelative(BlockFace.SOUTH);
        Block oldContainerBlock = oldBlock.getRelative(BlockFace.UP);

        Block newSignBlock = newBlock.getRelative(BlockFace.SOUTH);
        Block newContainerBlock = newBlock.getRelative(BlockFace.UP);

        newBlock.setType(oldBlock.getType());
        newBlock.setBlockData(oldBlock.getBlockData());

        if (oldSignBlock.getType().toString().contains("WALL_SIGN")) {
            newSignBlock.setType(oldSignBlock.getType());
            copySignData((Sign) oldSignBlock.getState(), (Sign) newSignBlock.getState());
            translateBlockSign(newSignBlock);
        }
        if (oldContainerBlock.getState() instanceof InventoryHolder container) {
            newContainerBlock.setType(oldContainerBlock.getType());
            newContainerBlock.setBlockData(oldContainerBlock.getBlockData());
            DevPlanet devPlanet = OpenCreative.getPlanetsManager().getDevPlanet(oldContainerBlock.getWorld());
            if (devPlanet != null) {
                Layout layout = devPlanet.getOpenedMenu(oldContainerBlock.getLocation());
                if (layout != null) {
                    for (Player player : layout.getViewers()) {
                        player.closeInventory();
                    }
                }
            }
            if (newContainerBlock.getState() instanceof InventoryHolder newContainer) {
                newContainer.getInventory().setContents(container.getInventory().getContents());
            }
        }
        oldSignBlock.setType(Material.AIR);
        oldBlock.setType(Material.AIR);
        oldContainerBlock.setType(Material.AIR);
    }

    @EventHandler
    public void onChestPlace(BlockPlaceEvent event) {
        /*
         * Fixes container items.
         */
        if (event.isCancelled()) return;
        Block block = event.getBlock();
        if (!(block.getState() instanceof InventoryHolder container)) return;
        for (int i = 0; i < container.getInventory().getSize(); i++) {
            ItemStack insideItem = container.getInventory().getItem(i);
            if (insideItem == null) continue;
            container.getInventory().setItem(i, ItemUtils.fixItem(insideItem));
        }
    }

    @EventHandler
    public void onPlace(BlockPlaceEvent event) {
        Player player = event.getPlayer();
        Planet planet = OpenCreative.getPlanetsManager().getPlanetByPlayer(player);
        DevPlanet devPlanet = OpenCreative.getPlanetsManager().getDevPlanet(player);

        if (devPlanet != null) {

            Block block = event.getBlock();
            Block supportBlock = block.getRelative(BlockFace.DOWN);

            DevPlatform platform = devPlanet.getPlatformInLocation(event.getBlock().getLocation());
            if (platform == null || block.getY() < 1) {
                event.setCancelled(true);
                Sounds.DEV_NOT_ALLOWED.play(player);
                return;
            }
            if (supportBlock.getType() == platform.getFloorMaterial()) {
                if (block.getType() == Material.PISTON && ((supportBlock.getZ() - devPlanet.getDevPlatformer().getPlatformBeginLocation(platform).getBlockZ()) % 4) == 0 && supportBlock.getRelative(BlockFace.WEST).getType() == platform.getActionMaterial()) {
                    Directional directional = (Directional) block.getBlockData();
                    if (directional.getFacing() != BlockFace.EAST && directional.getFacing() != BlockFace.WEST) {
                        directional.setFacing(player.getFacing().getOppositeFace());
                    }
                    if (directional.getFacing() != BlockFace.EAST && directional.getFacing() != BlockFace.WEST) {
                        directional.setFacing(block.getRelative(BlockFace.WEST).isEmpty() ? BlockFace.WEST : BlockFace.EAST);
                    }
                    block.setBlockData(directional);
                } else if (block.getType() == Material.REDSTONE_WALL_TORCH) {
                    if (block.getRelative(BlockFace.EAST)
                            .getRelative(BlockFace.DOWN).getType() != platform.getEventMaterial()) {
                        Sounds.DEV_NOT_ALLOWED.play(player);
                        event.setCancelled(true);
                    } else {
                        devPlanet.addChangedColumn(block.getRelative(BlockFace.EAST).getLocation());
                        if (block.getBlockData() instanceof Powerable powerable) {
                            powerable.setPowered(true);
                            block.setBlockData(powerable);
                        }
                        Sounds.DEV_SET_DEBUG_TORCH.play(player);
                    }
                } else if ((!(block.getType().name().contains("SIGN") && supportBlock.getX() >= 4 && (supportBlock.getX() % 2) == 0)) && (!devPlanet.getAllowedBlocks().contains(block.getType())) || block.getY() <= 0) {
                    player.sendActionBar(getLocaleComponent("world.dev-mode.cant-place-on-floor"));
                    Sounds.DEV_NOT_ALLOWED.play(player);
                    event.setCancelled(true);
                }
            } else if (supportBlock.getType() == platform.getEventMaterial()) {
                if (devPlanet.getEventsBlocks().contains(block.getType())) {
                    Material additionalBlockMaterial = Material.REDSTONE_ORE;
                    String signText = "unknown";
                    ExecutorCategory executorCategory = ExecutorCategory.getByMaterial(block.getType());
                    ActionCategory actionCategory = ActionCategory.getByMaterial(block.getType());
                    if (executorCategory != null) {
                        signText = executorCategory.name().toLowerCase();
                        additionalBlockMaterial = executorCategory.getAdditionalBlock();
                    } else if (actionCategory != null) {
                        signText = actionCategory.name().toLowerCase();
                        additionalBlockMaterial = actionCategory.getAdditionalBlock();
                    }
                    devPlanet.addChangedColumn(block.getLocation());
                    placeDevBlock(block.getLocation(), block.getType(), additionalBlockMaterial, devPlanet.getSignMaterial(), signText);
                } else {
                    player.sendActionBar(getLocaleComponent("world.dev-mode.cant-place-action-on-event"));
                    Sounds.DEV_NOT_ALLOWED.play(player);
                    event.setCancelled(true);
                }
            } else if (supportBlock.getType() == platform.getActionMaterial()) {
                if (devPlanet.getActionsBlocks().contains(block.getType())) {
                    Material additionalBlockMaterial = Material.REDSTONE_ORE;
                    String signText = "unknown";
                    ExecutorCategory executorCategory = ExecutorCategory.getByMaterial(block.getType());
                    ActionCategory actionCategory = ActionCategory.getByMaterial(block.getType());
                    if (executorCategory != null) {
                        signText = executorCategory.name().toLowerCase();
                        additionalBlockMaterial = executorCategory.getAdditionalBlock();
                    } else if (actionCategory != null) {
                        signText = actionCategory.name().toLowerCase();
                        additionalBlockMaterial = actionCategory.getAdditionalBlock();
                    }
                    if (block.getRelative(BlockFace.EAST).getType() == Material.PISTON) {
                        move(block.getLocation(), BlockFace.EAST);
                    }
                    devPlanet.addInsideCodeColumnChange(block.getLocation());
                    placeDevBlock(block.getLocation(), block.getType(), additionalBlockMaterial, devPlanet.getSignMaterial(), signText);
                } else {
                    player.sendActionBar(getLocaleComponent("world.dev-mode.cant-place-event-on-action"));
                    Sounds.DEV_NOT_ALLOWED.play(player);
                    event.setCancelled(true);
                }
            }  else {
                if (block.getType() != Material.COMPARATOR) {
                    Sounds.DEV_NOT_ALLOWED.play(player);
                }
                event.setCancelled(true);
            }
        } else if (planet != null) {
            if (ChangedWorld.isPlayerWithLocation(player) && !planet.getWorldPlayers().canBuild(player)) {
                player.sendActionBar(getLocaleComponent("not-builder"));
                event.setCancelled(true);
                return;
            }
            new PlaceBlockEvent(event.getPlayer(), event).callEvent();
            if (event.getBlock().getType() == Material.TNT && !event.isCancelled() && !planet.isOwner(player)) {
                OpenCreative.getWander(player).getGriefStats().addTntPlacementsAmount(1);
            }
        } else if (isEntityInLobby(player) && OpenCreative.getSettings().getLobbySettings().isPlacingBlocksDisallowed()
                && !player.hasPermission("opencreative.lobby.placing-blocks.bypass")) {
            event.setCancelled(true);
            player.sendActionBar(getLocaleComponent("not-for-lobby"));
        }
    }
}

