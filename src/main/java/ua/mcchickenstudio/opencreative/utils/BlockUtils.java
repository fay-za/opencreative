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

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.WorldBorder;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.Sign;
import org.bukkit.block.sign.Side;
import org.bukkit.block.sign.SignSide;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import ua.mcchickenstudio.opencreative.OpenCreative;
import ua.mcchickenstudio.opencreative.coding.blocks.actions.ActionCategory;
import ua.mcchickenstudio.opencreative.planets.DevPlatform;

import java.util.ArrayList;
import java.util.List;

/**
 * <h1>BlockUtils</h1>
 * This class represents useful utils for manipulating with blocks.
 */
public final class BlockUtils {

    /**
     * Change text into line on sign.
     *
     * @param location Location of sign block
     * @param line     Number of sign line (1-4)
     * @param text     Text to set in sign
     * @return true - if sign line changed successful, false - if failed
     */
    public static boolean setSignLine(Location location, int line, String text) {
        Block block = location.getBlock();
        if (line < 1 || line > 4) return false;
        if (!(block.getState() instanceof Sign sign)) return false;
        SignSide side = sign.getSide(Side.FRONT);
        side.line(line - 1, Component.text(text));
        sign.update();
        return true;
    }

    public static boolean isSignLineEmpty(Location location, int line) {
        Block block = location.getBlock();
        if (line < 1 || line > 4) return true;
        if (!(block.getState() instanceof Sign sign)) return true;
        SignSide side = sign.getSide(Side.FRONT);
        TextComponent textComponent = (TextComponent) side.line(line - 1);
        return textComponent.content().isEmpty();
    }

    /**
     * Get text from line in sign block
     *
     * @param location Location of sign block
     * @param line     Number of sign line (1-4)
     * @return Text from sign line
     */
    public static String getSignLine(Location location, int line) {
        Block block = location.getBlock();
        if (line < 1 || line > 4) return null;
        if (!(block.getState() instanceof Sign sign)) return null;
        SignSide side = sign.getSide(Side.FRONT);
        TextComponent textComponent = (TextComponent) side.line(line - 1);
        return textComponent.content();
    }

    public static void sendSignChange(Location location, Player player, int lineNumber, String newLine) {
        Block block = location.getBlock();
        if (!(block.getState() instanceof Sign sign)) return;
        List<Component> newLines = sign.getSide(Side.FRONT).lines();
        newLines.set(lineNumber - 1, Component.text(newLine));
        new BukkitRunnable() {
            @Override
            public void run() {
                player.sendSignChange(block.getLocation(), newLines);
            }
        }.runTaskLater(OpenCreative.getPlugin(), 5L);
    }

    public static int getClosingBracketX(DevPlatform platform, Block conditionBlock) {
        Location location = conditionBlock.getLocation();
        World world = location.getWorld();
        List<String> conditions = new ArrayList<>();
        try {
            for (double x = location.getX() + 2; x < platform.getEndCoordinate() - 4; x += 2) {
                Block block = world.getBlockAt(new Location(world, x, location.getBlockY(), location.getBlockZ()));
                ActionCategory category = ActionCategory.getByMaterial(block.getType());
                if (block.getType() == Material.AIR) {
                    if (block.getRelative(BlockFace.EAST).getType() == Material.PISTON) {
                        if (!conditions.isEmpty()) {
                            String last = conditions.getLast();
                            conditions.remove(last);
                        } else {
                            return block.getRelative(BlockFace.EAST).getX();
                        }
                    }
                } else if (category != null && category.isCondition()) {
                    if (block.getRelative(BlockFace.EAST).getType() == Material.PISTON) {
                        conditions.add("cound" + block.getX());
                    }
                }
            }
        } catch (Exception exception) {
            return -1;
        }
        return -1;
    }

    public static boolean isOutOfBorders(Location location) {
        WorldBorder border = location.getWorld().getWorldBorder();
        Location borderCenter = border.getCenter();

        double radius = border.getSize() / 2 + 1;
        double borderCenterX1 = borderCenter.getX() + radius;
        double borderCenterX2 = borderCenter.getX() - radius;
        double borderCenterZ1 = borderCenter.getZ() + radius;
        double borderCenterZ2 = borderCenter.getZ() - radius;

        double playerX = location.getX();
        double playerZ = location.getZ();

        if (!(borderCenterX1 > playerX && playerX > borderCenterX2)) {
            return true;
        } else return !(borderCenterZ1 > playerZ && playerZ > borderCenterZ2);
    }

    public static void copySignData(Sign oldSign, Sign sign) {
        for (byte i = 0; i < oldSign.getSide(Side.FRONT).lines().size(); i++) {
            sign.getSide(Side.FRONT).line(i, oldSign.getSide(Side.FRONT).line(i));
        }
        sign.getSide(Side.FRONT).setGlowingText(oldSign.getSide(Side.FRONT).isGlowingText());
        sign.setBlockData(oldSign.getBlockData());
        sign.update();
    }

    public static int getBeginningBracketX(Block firstBlock) {
        Location location = firstBlock.getLocation();
        World world = location.getWorld();
        List<String> conditions = new ArrayList<>();
        for (int x = location.getBlockX() - 2; x >= 6; x = x - 2) {
            Block block = world.getBlockAt(new Location(world, x, location.getBlockY(), location.getBlockZ()));
            ActionCategory category = ActionCategory.getByMaterial(block.getType());
            if (block.getType() == Material.AIR) {
                if (block.getRelative(BlockFace.WEST).getType() == Material.PISTON) {
                    if (!conditions.isEmpty()) {
                        String last = conditions.getLast();
                        conditions.remove(last);
                    } else {
                        return block.getRelative(BlockFace.WEST).getX();
                    }
                }
            } else if (category != null && category.isCondition()) {
                if (block.getRelative(BlockFace.WEST).getType() == Material.PISTON) {
                    conditions.add("cound" + block.getX());
                }
            }
        }
        return -1;
    }

}
