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
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.jetbrains.annotations.NotNull;
import ua.mcchickenstudio.opencreative.OpenCreative;
import ua.mcchickenstudio.opencreative.coding.CodeConfiguration;
import ua.mcchickenstudio.opencreative.coding.CodingBlockPlacer;
import ua.mcchickenstudio.opencreative.planets.DevPlanet;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.List;

import static ua.mcchickenstudio.opencreative.utils.MessageUtils.getLocaleMessage;

public final class CodeDownloaderExperiment extends Experiment {

    @Override
    public @NotNull String getId() {
        return "code_downloader";
    }

    @Override
    public @NotNull String getName() {
        return "Code Downloader";
    }

    @Override
    public @NotNull String getDescription() {
        return "Downloads code by links";
    }

    @Override
    public void handleCommand(@NotNull CommandSender sender, @NotNull String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage(getLocaleMessage("only-players"));
            return;
        }
        if (args.length == 0) {
            sender.sendMessage(getLocaleMessage("too-few-args"));
            return;
        }
        DevPlanet devPlanet = OpenCreative.getPlanetsManager().getDevPlanet(player);
        if (devPlanet == null) {
            sender.sendMessage(getLocaleMessage("only-in-dev-world"));
            return;
        }
        if (!devPlanet.getPlanet().getWorldPlayers().canDevelop(player)) {
            sender.sendMessage(getLocaleMessage("not-developer"));
            return;
        }
        String link = args[0];
        if (!link.startsWith("https://") || link.length() == 8) {
            sender.sendMessage("Not safe URL! Needs to start with https://");
            return;
        }
        new BukkitRunnable() {
            @Override
            public void run() {
                try {
                    URL requestUrl = new URI(link).toURL();
                    HttpURLConnection connection = (HttpURLConnection) requestUrl.openConnection();
                    connection.setConnectTimeout(5000);
                    connection.setReadTimeout(5000);
                    connection.setRequestMethod("GET");
                    connection.setRequestProperty("User-Agent", "OpenCreative+ Code Downloader");
                    connection.setDoOutput(false);
                    int code = connection.getResponseCode();
                    StringBuilder responseBuilder = new StringBuilder();
                    try (BufferedReader reader = new BufferedReader(new InputStreamReader(
                            code >= 400 ? connection.getErrorStream() : connection.getInputStream(),
                            StandardCharsets.UTF_8))) {
                        char[] buffer = new char[4096];
                        int read;
                        while ((read = reader.read(buffer)) != -1) {
                            responseBuilder.append(buffer, 0, read);
                            if (responseBuilder.length() > 1024 * 1024) {
                                sender.sendMessage("Too large file");
                                return;
                            }
                        }
                    }
                    String response = responseBuilder.toString();
                    CodeConfiguration config = new CodeConfiguration();
                    config.loadFromString(response);
                    ConfigurationSection section = config.getConfigurationSection("code.blocks");
                    if (section == null) {
                        sender.sendMessage("Section does not exists.");
                        return;
                    }
                    Bukkit.getScheduler().runTask(OpenCreative.getPlugin(),
                            () -> {
                                if ((new CodingBlockPlacer(devPlanet).placeCodingLines(devPlanet, section)).getType().isSuccess()) {
                                    sender.sendMessage("Placed");
                                } else {
                                    sender.sendMessage("Failed to place");
                                }
                            });
                } catch (Exception e) {
                    sender.sendMessage("Failed to download code");
                }
            }
        }.runTaskAsynchronously(OpenCreative.getPlugin());
    }

    @Override
    public @NotNull List<String> tabCommand(@NotNull CommandSender sender, @NotNull String[] args) {
        return List.of("https://");
    }

}
