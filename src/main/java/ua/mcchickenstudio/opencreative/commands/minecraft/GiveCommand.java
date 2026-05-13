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

package ua.mcchickenstudio.opencreative.commands.minecraft;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import ua.mcchickenstudio.opencreative.OpenCreative;
import ua.mcchickenstudio.opencreative.commands.CommandHandler;
import ua.mcchickenstudio.opencreative.planets.Planet;
import ua.mcchickenstudio.opencreative.utils.CooldownUtils;
import ua.mcchickenstudio.opencreative.utils.ItemUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static ua.mcchickenstudio.opencreative.utils.CooldownUtils.checkAndSetCooldownWithMessage;
import static ua.mcchickenstudio.opencreative.utils.MessageUtils.getLocaleMessage;

/**
 * <h1>GiveCommand</h1>
 * This command is responsible for giving specified items to player.
 * <p>
 * Using this command from console will redirect to Minecraft command.
 * <p>
 * Available: For world builders or developers.
 */
public class GiveCommand extends CommandHandler {

    @Override
    public void onExecute(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, String[] args) {
        if (!(sender instanceof Player player)) {
            Bukkit.getServer().dispatchCommand(sender, "minecraft:give " + String.join(" ", args));
        } else {
            if (!checkAndSetCooldownWithMessage(player, CooldownUtils.CooldownType.GENERIC_COMMAND)) return;

            if (!player.hasPermission("opencreative.give.bypass")) {
                Planet planet = OpenCreative.getPlanetsManager().getPlanetByPlayer(player);
                if (planet == null) {
                    player.sendMessage(getLocaleMessage("only-in-world"));
                    return;
                }
                if (!(planet.isOwner(player) || planet.getWorldPlayers().canDevelop(player) || planet.getWorldPlayers().canBuild(player))) {
                    player.sendMessage(getLocaleMessage("not-owner"));
                    return;
                }
            }
            if (args.length == 0) {
                sender.sendMessage(getLocaleMessage("commands.give.help"));
                return;
            }
            // give apple
            if (args.length == 1) {
                try {
                    ItemStack item = getItem(args[0], 1);
                    player.getInventory().addItem(item);
                    player.sendMessage(getLocaleMessage("commands.give.given")
                            .replace("%material%", item.getType().name().toLowerCase())
                            .replace("%amount%", "1"));
                } catch (IllegalArgumentException error) {
                    player.sendMessage(getLocaleMessage("commands.give.wrong"));
                }
                // give player apple
            } else if (args.length == 2) {
                Player givePlayer = Bukkit.getPlayer(args[0]);
                if (givePlayer == null) {
                    player.sendMessage(getLocaleMessage("no-player-found"));
                    return;
                }
                Planet givePlanet = OpenCreative.getPlanetsManager().getPlanetByPlayer(givePlayer);
                if (!player.hasPermission("opencreative.give.bypass")) {
                    Planet planet = OpenCreative.getPlanetsManager().getPlanetByPlayer(player);
                    if (planet == null || !planet.equals(givePlanet)) {
                        player.sendMessage(getLocaleMessage("no-player-found"));
                        return;
                    }
                }
                try {
                    ItemStack item = getItem(args[1], 1);
                    givePlayer.getInventory().addItem(item);
                    player.sendMessage(getLocaleMessage("commands.give.given-player")
                            .replace("%player%", givePlayer.getName())
                            .replace("%material%", item.getType().name().toLowerCase())
                            .replace("%amount%", "1"));
                } catch (IllegalArgumentException e) {
                    player.sendMessage(getLocaleMessage("commands.give.wrong"));
                }
                // give player item amount
            } else if (args.length == 3) {
                try {
                    Player givePlayer = Bukkit.getPlayer(args[0]);
                    if (givePlayer == null) {
                        player.sendMessage(getLocaleMessage("no-player-found"));
                        return;
                    }
                    Planet givePlanet = OpenCreative.getPlanetsManager().getPlanetByPlayer(givePlayer);
                    if (!player.hasPermission("opencreative.give.bypass")) {
                        Planet planet = OpenCreative.getPlanetsManager().getPlanetByPlayer(player);
                        if (planet == null || !planet.equals(givePlanet)) {
                            player.sendMessage(getLocaleMessage("no-player-found"));
                            return;
                        }
                    }
                    int amount = Integer.parseInt(args[2]);
                    ItemStack item = getItem(args[1], amount);
                    givePlayer.getInventory().addItem(item);
                    player.sendMessage(getLocaleMessage("commands.give.given-player")
                            .replace("%player%", givePlayer.getName())
                            .replace("%material%", item.getType().name().toLowerCase())
                            .replace("%amount%", String.valueOf(amount)));
                } catch (NumberFormatException error) {
                    player.sendMessage(getLocaleMessage("commands.give.wrong-amount"));
                } catch (IllegalArgumentException error) {
                    player.sendMessage(getLocaleMessage("commands.give.wrong"));
                }
            }
        }
    }

    public @NotNull ItemStack getItem(@NotNull String text, int amount) {
        text = text.toUpperCase().replace("MINECRAFT:", "");
        ItemStack item;
        if (List.of("invisible_item_frame", "item_frame_invisible").contains(text.toLowerCase())) {
            item = new ItemStack(Material.ITEM_FRAME);
            ItemUtils.setPersistentData(item, ItemUtils.getItemEntityInvisible(), 1);
            item.setAmount(amount);
            return item;
        }
        Material material = Material.valueOf(text);
        return new ItemStack(material, amount);
    }

    @Override
    public List<String> onTab(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, String[] args) {
        List<String> tabCompleter = new ArrayList<>();
        if (sender instanceof Player player) {
            if (args.length == 1) {
                tabCompleter.addAll(player.getWorld().getPlayers().stream().map(Player::getName).toList());
            } else if (args.length == 2) {
                tabCompleter.addAll(Arrays.stream(Material.values()).filter(Material::isItem).map(material -> material.name().toLowerCase()).toList());
                tabCompleter.add("invisible_item_frame");
                tabCompleter.add("item_frame_invisible");
            } else if (args.length == 3) {
                tabCompleter.add("1");
                tabCompleter.add("16");
                tabCompleter.add("32");
                tabCompleter.add("64");
            } else {
                return null;
            }
        }
        return tabCompleter;
    }

}
