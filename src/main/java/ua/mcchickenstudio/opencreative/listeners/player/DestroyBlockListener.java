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

import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockDamageEvent;
import org.bukkit.inventory.InventoryHolder;
import ua.mcchickenstudio.opencreative.OpenCreative;
import ua.mcchickenstudio.opencreative.coding.blocks.actions.ActionCategory;
import ua.mcchickenstudio.opencreative.coding.blocks.events.player.interaction.DamageBlockEvent;
import ua.mcchickenstudio.opencreative.coding.blocks.events.player.interaction.DestroyBlockEvent;
import ua.mcchickenstudio.opencreative.coding.blocks.executors.ExecutorCategory;
import ua.mcchickenstudio.opencreative.menus.Menus;
import ua.mcchickenstudio.opencreative.planets.DevPlanet;
import ua.mcchickenstudio.opencreative.planets.DevPlatform;
import ua.mcchickenstudio.opencreative.planets.Planet;
import ua.mcchickenstudio.opencreative.settings.Sounds;
import ua.mcchickenstudio.opencreative.settings.items.Items;

import java.util.List;
import java.util.Random;

import static ua.mcchickenstudio.opencreative.listeners.player.PlaceBlockListener.move;
import static ua.mcchickenstudio.opencreative.utils.BlockUtils.getClosingBracketX;
import static ua.mcchickenstudio.opencreative.utils.MessageUtils.getPlayerLocaleComponent;
import static ua.mcchickenstudio.opencreative.utils.PlayerUtils.isEntityInLobby;
import static ua.mcchickenstudio.opencreative.utils.PlayerUtils.translateBlockSign;

/**
 * <h1>DestroyBlockListener</h1>
 * This class represents a listener for when a player
 * destroys block or damages block in world.
 */
public final class DestroyBlockListener implements Listener {

    private static final int SHIFT_COMPACT_MAX_PASSES = 20;
    private final Random random = new Random();

    @EventHandler
    public void onBreak(BlockBreakEvent event) {
        Player player = event.getPlayer();
        Planet planet = OpenCreative.getPlanetsManager().getPlanetByPlayer(player);
        DevPlanet devPlanet = OpenCreative.getPlanetsManager().getDevPlanet(player);
        if (devPlanet != null) {
            Block block = event.getBlock();
            boolean chainBreak = OpenCreative.getSettings().getCodingSettings().isShiftBreakChainEnabled() && player.isSneaking();

            if (player.getInventory().getItemInMainHand().getType() == Material.COMPARATOR) {
                event.setCancelled(true);
                return;
            }

            DevPlatform platform = devPlanet.getPlatformInLocation(block.getLocation());
            if (platform == null) {
                event.setCancelled(true);
                return;
            }

            if (devPlanet.getIndestructibleBlocks().contains(block.getType())
                    || block.getType() == platform.getFloorMaterial()
                    || block.getType() == platform.getEventMaterial()
                    || block.getType() == platform.getActionMaterial()
                    || (block.getType() == Material.PISTON && block.getRelative(BlockFace.WEST).isSolid())
            ) {
                Sounds.DEV_NOT_ALLOWED.play(player);
                event.setCancelled(true);
            }

            if (block.getType() == Material.REDSTONE_WALL_TORCH) {
                devPlanet.addInsideCodeColumnChange(block.getRelative(BlockFace.EAST).getLocation());
                Sounds.DEV_UNSET_DEBUG_TORCH.play(player);
                return;
            }

            if (devPlanet.getAllCodingBlocksForPlacing().contains(block.getType())) {
                ActionCategory category = ActionCategory.getByMaterial(block.getType());
                if (category != null) {
                    devPlanet.addInsideCodeColumnChange(block.getLocation());
                    boolean usedShiftChainOnMulti = false;
                    if (chainBreak && category.isMultiAction()) {
                        usedShiftChainOnMulti = true;
                        if (!destroyBracketChain(platform, devPlanet, block)) {
                            platform.destroyCodingBlock(block.getLocation(), devPlanet.isDropItems());
                        }
                        if (!devPlanet.getPlanet().isOwner(player)) {
                            OpenCreative.getWander(player).getGriefStats().addDestroyedCodingBlocksAmount(1);
                        }
                    } else {
                        boolean destroyedBlock = platform.destroyCodingBlock(block.getLocation(), devPlanet.isDropItems());
                        if (!devPlanet.getPlanet().isOwner(player) && destroyedBlock) {
                            OpenCreative.getWander(player).getGriefStats().addDestroyedCodingBlocksAmount(1);
                        }
                    }
                    if (usedShiftChainOnMulti && OpenCreative.getSettings().getCodingSettings().isShiftBreakChainCompactFull()) {
                        compactCodingLineLeftByVanillaMove(devPlanet, platform, block);
                    } else {
                        move(block.getLocation(), BlockFace.WEST);
                    }
                } else {
                    if (ExecutorCategory.getByMaterial(block.getType()) != null
                            && chainBreak) {
                        devPlanet.addChangedColumn(block.getLocation());
                        int destroyedBlocks = platform.destroyCodingLine(block.getLocation(), devPlanet.isDropItems());
                        if (!devPlanet.getPlanet().isOwner(player)) {
                            OpenCreative.getWander(player).getGriefStats().addDestroyedCodingBlocksAmount(destroyedBlocks);
                        }
                    } else {
                        devPlanet.addChangedColumn(block.getLocation());
                        boolean destroyedBlock = platform.destroyCodingBlock(block.getLocation(), devPlanet.isDropItems());
                        devPlanet.clearMarkedExecutors(block.getLocation());
                        if (!devPlanet.getPlanet().isOwner(player) && destroyedBlock) {
                            OpenCreative.getWander(player).getGriefStats().addDestroyedCodingBlocksAmount(1);
                        }
                    }

                }
                event.setCancelled(true);
            }

            if (block.getType().name().contains("WALL_SIGN")) {
                event.setCancelled(true);
                translateBlockSign(block, player);
            }

            if (block.getState() instanceof InventoryHolder) {
                Block blockAtDown = block.getRelative(BlockFace.DOWN).getRelative(BlockFace.DOWN);
                if (blockAtDown.getType() != platform.getEventMaterial() && blockAtDown.getType() != platform.getActionMaterial()) {
                    return;
                }
                event.setCancelled(true);
            }
        } else if (planet != null) {
            if (ChangedWorld.isPlayerWithLocation(player)) {
                if (!planet.getWorldPlayers().canBuild(player)) {
                    player.sendActionBar(getPlayerLocaleComponent("not-builder", player));
                    event.setCancelled(true);
                    return;
                }
                if (player.getInventory().getItemInMainHand().getType() == Material.PAPER) {
                    event.setCancelled(true);
                }
                return;
            }
            new DestroyBlockEvent(event.getPlayer(), event).callEvent();
            if (!event.isCancelled()) {
                Menus.onBlockDestroy(event.getBlock().getLocation());
                if (!planet.isOwner(event.getPlayer())) {
                    OpenCreative.getWander(event.getPlayer()).getGriefStats().addDestroyedBlocksAmount(1);
                }
            }
        } else if (isEntityInLobby(player) && OpenCreative.getSettings().getLobbySettings().isDestroyingBlocksDisallowed()
                && !player.hasPermission("opencreative.lobby.destroying-blocks.bypass")) {
            event.setCancelled(true);
            player.sendActionBar(getPlayerLocaleComponent("not-for-lobby", player));
        }
    }

    @EventHandler
    public void onStartDamaging(BlockDamageEvent event) {
        Planet planet = OpenCreative.getPlanetsManager().getPlanetByPlayer(event.getPlayer());
        if (planet != null) new DamageBlockEvent(event.getPlayer(), event).callEvent();
    }

    private boolean destroyBracketChain(DevPlatform platform, DevPlanet devPlanet, Block block) {
        int closingBracketX = getClosingBracketX(platform, block);
        if (closingBracketX < 0) {
            return false;
        }
        int y = block.getY();
        int z = block.getZ();
        int startX = Math.min(block.getX(), closingBracketX);
        int endX = Math.max(block.getX(), closingBracketX);

        // Remove from right to left on coding-slot grid to avoid nested branch corruption.
        // Coding blocks are placed on every second X; x+1 is auxiliary bracket slot.
        for (int x = endX; x >= startX; x -= 2) {
            Block inLine = block.getWorld().getBlockAt(x, y, z);
            if (devPlanet.getAllCodingBlocksForPlacing().contains(inLine.getType())) {
                platform.destroyCodingBlock(inLine.getLocation(), devPlanet.isDropItems());
            }
        }
        return true;
    }

    private void compactCodingLineLeftByVanillaMove(DevPlanet devPlanet, DevPlatform platform, Block startBlock) {
        int y = startBlock.getY();
        int z = startBlock.getZ();
        int beginX = startBlock.getX();
        int endX = devPlanet.getDevPlatformer().getPlatformEndLocation(platform).getBlockX() - 1;
        for (int pass = 0; pass < SHIFT_COMPACT_MAX_PASSES; pass++) {
            int gapX = findGapToCompact(startBlock, y, z, beginX, endX);
            if (gapX < 0) {
                break;
            }
            Block gapBlock = startBlock.getWorld().getBlockAt(gapX, y, z);
            if (!move(gapBlock.getLocation(), BlockFace.WEST)) {
                break;
            }
        }
    }

    private int findGapToCompact(Block startBlock, int y, int z, int beginX, int endX) {
        for (int x = beginX; x <= endX; x++) {
            if (startBlock.getWorld().getBlockAt(x, y, z).getType() != Material.AIR) {
                continue;
            }
            if (hasMovableBlockToRight(startBlock, y, z, x, endX)) {
                return x;
            }
        }
        return -1;
    }

    private boolean hasMovableBlockToRight(Block startBlock, int y, int z, int gapX, int endX) {
        for (int x = gapX + 1; x <= endX; x++) {
            if (startBlock.getWorld().getBlockAt(x, y, z).getType() != Material.AIR) {
                return true;
            }
        }
        return false;
    }

}
