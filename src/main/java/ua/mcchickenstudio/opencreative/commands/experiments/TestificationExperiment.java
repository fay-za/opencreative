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

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickEvent;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerSwapHandItemsEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.MapMeta;
import org.bukkit.map.MapCanvas;
import org.bukkit.map.MapRenderer;
import org.bukkit.map.MapView;
import org.bukkit.map.MinecraftFont;
import org.bukkit.scheduler.BukkitRunnable;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import ua.mcchickenstudio.opencreative.OpenCreative;
import ua.mcchickenstudio.opencreative.coding.blocks.actions.ActionType;
import ua.mcchickenstudio.opencreative.coding.blocks.executors.Executor;
import ua.mcchickenstudio.opencreative.coding.blocks.executors.Executors;
import ua.mcchickenstudio.opencreative.coding.values.EventValue;
import ua.mcchickenstudio.opencreative.coding.values.EventValues;
import ua.mcchickenstudio.opencreative.settings.Sounds;
import ua.mcchickenstudio.opencreative.utils.ItemUtils;
import ua.mcchickenstudio.opencreative.utils.MessageUtils;

import java.util.*;

import static ua.mcchickenstudio.opencreative.utils.ErrorUtils.sendPlayerErrorMessage;
import static ua.mcchickenstudio.opencreative.utils.MessageUtils.getLocaleMessage;

public final class TestificationExperiment extends Experiment {

    private final Map<UUID, Integer> testerSounds = new HashMap<>();
    private TestificationListener listener;
    private BukkitRunnable actionBarTask;

    @Override
    public @NotNull String getId() {
        return "testification";
    }

    @Override
    public @NotNull String getName() {
        return "Release Testification";
    }

    @Override
    public @NotNull String getDescription() {
        return "Checks before releasing";
    }

    @Override
    public void handleCommand(@NotNull CommandSender sender, @NotNull String[] args) {
        if (args.length == 0) {
            sender.sendMessage(getLocaleMessage("too-few-args"));
            return;
        }
        if (args[0].equalsIgnoreCase("sounds")) {
            if (sender instanceof Player player) {
                if (testerSounds.containsKey(player.getUniqueId())) {
                    testerSounds.remove(player.getUniqueId());
                    player.sendActionBar(Component.text("Stopped playing"));
                    if (listener != null) {
                        PlayerSwapHandItemsEvent.getHandlerList().unregister(listener);
                        listener = null;
                    }
                    if (actionBarTask != null) {
                        actionBarTask.cancel();
                        actionBarTask = null;
                    }
                } else {
                    testerSounds.put(player.getUniqueId(), -1);
                    listener = new TestificationListener();
                    actionBarTask = new BukkitRunnable() {
                        @Override
                        public void run() {
                            for (UUID uuid : testerSounds.keySet()) {
                                Player player = Bukkit.getPlayer(uuid);
                                if (player == null) {
                                    testerSounds.remove(uuid);
                                    continue;
                                }
                                int index = testerSounds.get(uuid);
                                if (index >= 0) {
                                    Sounds[] soundsList = Sounds.values();
                                    Sounds sound = soundsList[index];
                                    if (sound == Sounds.LOBBY_MUSIC) {
                                        sound = soundsList[index+1];
                                    }
                                    player.sendActionBar(Component.text(sound.name().toLowerCase()));
                                } else {
                                    player.sendActionBar(Component.text("Press F to start playing " + Sounds.values().length + " sounds"));
                                }
                            }
                        }
                    };
                    actionBarTask.runTaskTimer(OpenCreative.getPlugin(), 0L, 20L);
                    Bukkit.getPluginManager().registerEvents(listener, OpenCreative.getPlugin());
                }
            }
        } else if (args[0].equalsIgnoreCase("map")) {
            if (sender instanceof Player player) {
                MapView mapView = Bukkit.createMap(player.getWorld());
                mapView.getRenderers().clear();
                mapView.addRenderer(new TestificationMapRender());
                ItemStack mapItem = new ItemStack(Material.FILLED_MAP);
                MapMeta meta = (MapMeta) mapItem.getItemMeta();
                meta.setMapView(mapView);
                mapItem.setItemMeta(meta);
                player.getInventory().addItem(mapItem);
            }
        } else if (args[0].equalsIgnoreCase("translation")) {
            List<String> untranslatedBlocks = new ArrayList<>();
            for (Executor executor : Executors.getInstance().getExecutors()) {
                String path = "items.developer.events." + executor.getID().replace("_", "-") + ".name";
                if (!MessageUtils.messageExists(path)) {
                    untranslatedBlocks.add(path);
                }
                path = "blocks." + executor.getID();
                if (!MessageUtils.messageExists(path)) {
                    untranslatedBlocks.add(path);
                }
            }
            for (ActionType action : ActionType.values()) {
                String path = "items.developer." + (action.isCondition() ? "conditions" : "actions") + "." + action.name().toLowerCase().replace("_", "-") + ".name";
                if (!MessageUtils.messageExists(path)) {
                    untranslatedBlocks.add(path);
                }
                if (!MessageUtils.messageExists(path)) {
                    untranslatedBlocks.add(path);
                }
                path = "blocks." + action.name().toLowerCase();
                if (!MessageUtils.messageExists(path)) {
                    untranslatedBlocks.add(path);
                }
            }
            for (EventValue value : EventValues.getInstance().getEventValues()) {
                String path = "items.developer.event-values.items." + value.getID().toLowerCase().replace("_", "-") + ".name";
                if (!MessageUtils.messageExists(path)) {
                    untranslatedBlocks.add(path);
                }
            }
            for (String string : untranslatedBlocks) {
                sender.sendMessage(Component.text(string).clickEvent(ClickEvent.suggestCommand(string)));
            }
            if (untranslatedBlocks.isEmpty()) {
                sender.sendMessage("Everything is translated :)");
                return;
            }
            sender.sendMessage("--- Untranslated: " + untranslatedBlocks.size());
        } else if (args[0].equalsIgnoreCase("item")) {
            if (!(sender instanceof Player player)) {
                return;
            }
            ItemStack item = player.getInventory().getItemInMainHand();
            if (args.length == 1) return;
            switch (args[1].toLowerCase()) {
                case "1" -> {
                    sender.sendMessage("map");
                    Map<String, Object> serialized = item.serialize();
                    ItemStack newItem = ItemStack.deserialize(serialized);
                    player.getInventory().addItem(newItem);
                    sender.sendMessage(serialized.toString());
                }
                case "2" -> {
                    sender.sendMessage("byte");
                    byte[] serialized = item.serializeAsBytes();
                    ItemStack newItem = ItemStack.deserializeBytes(serialized);
                    player.getInventory().addItem(newItem);
                    sender.sendMessage(Arrays.toString(serialized));
                }
                case "3" -> {
                    sender.sendMessage("byte string");
                    try {
                        String object = ItemUtils.saveItemAsByteArray(item);
                        sender.sendMessage(object);
                        player.getInventory().addItem(ItemUtils.loadItemFromByteArray(object));
                    } catch (Exception error) {
                        sendPlayerErrorMessage(player, "Failed to test items", error);
                    }

                }
            }

        }
    }

    @Override
    public @Nullable List<String> tabCommand(@NotNull CommandSender sender, @NotNull String[] args) {
        if (args.length == 0) {
            return List.of("translation", "item");
        }
        if (args.length == 1) {
            return List.of("1", "2", "3", "4");
        }
        return null;
    }

    @Override
    public void onDisable() {
        if (listener != null) {
            PlayerSwapHandItemsEvent.getHandlerList().unregister(listener);
            listener = null;
        }
        if (actionBarTask != null) {
            actionBarTask.cancel();
            actionBarTask = null;
        }
    }

    public class TestificationListener implements Listener {

        @EventHandler
        public void onClick(PlayerSwapHandItemsEvent event) {
            if (testerSounds.containsKey(event.getPlayer().getUniqueId())) {
                int index = testerSounds.get(event.getPlayer().getUniqueId()) + 1;
                Sounds[] soundsList = Sounds.values();
                if (index >= soundsList.length) {
                    testerSounds.remove(event.getPlayer().getUniqueId());
                    return;
                }
                Sounds sound = soundsList[index];
                if (sound == Sounds.LOBBY_MUSIC) {
                    index++;
                    sound = soundsList[index];
                }
                testerSounds.put(event.getPlayer().getUniqueId(), index);
                event.getPlayer().sendActionBar(Component.text(sound.name().toLowerCase()));
                sound.play(event.getPlayer());
            }
        }

    }

    public static class TestificationMapRender extends MapRenderer {
        @Override
        public void render(@NotNull MapView map, MapCanvas canvas, @NotNull Player player) {
            canvas.drawText(0, 0, MinecraftFont.Font, "67 67 67 67 67 67 67 67\n67 67 67 67 67 67 67 67\n67 67 67 67 67 67 67 67\n67 67 67 67 67 67 67 67\n67 67 67 67 67 67 67 67\n67 67 67 67 67 67 67 67\n67 67 67 67 67 67 67 67\n67 67 67 67 67 67 67 67\n");
        }
    }

}
