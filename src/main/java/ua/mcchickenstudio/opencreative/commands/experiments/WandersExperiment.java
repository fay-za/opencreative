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

package ua.mcchickenstudio.opencreative.commands.experiments;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import ua.mcchickenstudio.opencreative.OpenCreative;
import ua.mcchickenstudio.opencreative.wanders.OfflineWander;
import ua.mcchickenstudio.opencreative.wanders.Wander;
import ua.mcchickenstudio.opencreative.indev.WanderMenu;
import ua.mcchickenstudio.opencreative.indev.WanderSettingsMenu;
import ua.mcchickenstudio.opencreative.planets.Planet;

import java.util.List;
import java.util.Set;
import java.util.UUID;

import static ua.mcchickenstudio.opencreative.utils.MessageUtils.getLocaleMessage;

public final class WandersExperiment extends Experiment {

    @Override
    public @NotNull String getId() {
        return "wanders";
    }

    @Override
    public @NotNull String getName() {
        return "Wanders Subsystem";
    }

    @Override
    public @NotNull String getDescription() {
        return "Adds favorite, unfavorite worlds, saving players last world";
    }

    @Override
    public void handleCommand(@NotNull CommandSender sender, @NotNull String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage(getLocaleMessage("only-players"));
            return;
        }
        if (args.length == 0) {
            player.sendMessage(getLocaleMessage("too-few-args"));
            return;
        }
        Wander wander = OpenCreative.getWander(player);
        switch (args[0].toLowerCase()) {
            case "favorite" -> {
                Planet planet = OpenCreative.getPlanetsManager().getPlanetByPlayer(player);
                if (planet == null) {
                    player.sendMessage(getLocaleMessage("only-in-world"));
                    return;
                }
                if (wander.addFavoriteWorld(planet.getId())) {
                    wander.getPlayer().sendMessage(getLocaleMessage("world.favorites.added")
                            .replace("%id%", String.valueOf(planet.getId())));
                } else {
                    wander.getPlayer().sendMessage("Already in favorites!");
                }
                Set<Integer> favoriteWorlds = wander.getFavoriteWorlds();
                if (favoriteWorlds.isEmpty()) {
                    wander.getPlayer().sendMessage("No favorite worlds");
                }
                wander.getPlayer().sendMessage("Favorite worlds (" + favoriteWorlds.size() + "): ");
                for (int world : favoriteWorlds) {
                    wander.getPlayer().sendMessage(" - " + world);
                }
            }
            case "unfavorite" -> {
                Planet planet = OpenCreative.getPlanetsManager().getPlanetByPlayer(player);
                if (planet == null) {
                    player.sendMessage(getLocaleMessage("only-in-world"));
                    return;
                }
                if (wander.removeFavoriteWorld(planet.getId())) {
                    wander.getPlayer().sendMessage(getLocaleMessage("world.favorites.removed")
                            .replace("%id%", String.valueOf(planet.getId())));
                } else {
                    wander.getPlayer().sendMessage("Not in favorites!");
                }
                Set<Integer> favoriteWorlds = wander.getFavoriteWorlds();
                if (favoriteWorlds.isEmpty()) {
                    wander.getPlayer().sendMessage("No favorite worlds");
                }
                wander.getPlayer().sendMessage("Favorite worlds (" + favoriteWorlds.size() + "): ");
                for (int world : favoriteWorlds) {
                    wander.getPlayer().sendMessage(" - " + world);
                }
            }
            case "gender" -> {
                if (args.length == 1) {
                    sender.sendMessage(getLocaleMessage("too-few-args"));
                    return;
                }
                OfflineWander.Gender gender = OfflineWander.Gender.getGender(args[1]);
                if (wander.setGender(gender)) {
                    sender.sendMessage("Set gender: " + gender.name());
                } else {
                    sender.sendMessage("Already with gender " + gender.name());
                }
            }
            case "menu" -> {
                new WanderMenu(player.getName()).open(player);
            }
            case "settings" -> {
                new WanderSettingsMenu(player.getName()).open(player);
            }
            case "info" -> {
                if (args.length == 1) {
                    sender.sendMessage(getLocaleMessage("too-few-args"));
                    return;
                }
                Player found = Bukkit.getPlayer(args[1]);
                if (found == null) {
                    sender.sendMessage(getLocaleMessage("not-found-player"));
                    return;
                }
                wander = OpenCreative.getWander(found);
                sender.sendMessage("Wander " + found.getName());
                sender.sendMessage("   Gender: " + (wander.getGender() == null ? "None" : wander.getGender().name()));
                sender.sendMessage("   Description: " + (wander.getDescription() == null ? "None" : wander.getDescription()));
                sender.sendMessage("   Last World: " + (wander.getLastPlayedWorldId() == -1 ? "None" : wander.getLastPlayedWorldId()));

                Set<Integer> favorites = wander.getFavoriteWorlds();
                sender.sendMessage("   Favorite Worlds (" + favorites.size() + "): " +
                        String.join(", ", favorites.stream().map(Object::toString).toList()));

                List<UUID> friends = wander.getFriends();
                sender.sendMessage("   Friends (" + friends.size() + "): " +
                        String.join(", ", friends.stream().map(uuid -> Bukkit.getOfflinePlayer(uuid).getName()).toList()));

                Location lastLocation = wander.getLastLocation();
                sender.sendMessage("   Last Location: " + (lastLocation == null ? "None" :
                        lastLocation.getBlockX() + " " + lastLocation.getBlockX() + " " + lastLocation.getBlockZ()));

            }
        }
    }

    @Override
    public @Nullable List<String> tabCommand(@NotNull CommandSender sender, @NotNull String[] args) {
        if (args.length == 0) {
            return List.of("favorite", "unfavorite", "info", "gender", "menu", "settings");
        }
        if (args.length == 1) {
            if (args[0].equalsIgnoreCase("gender")) {
                return List.of("male", "female", "non_binary", "unknown");
            }
        }
        return null;
    }

}
