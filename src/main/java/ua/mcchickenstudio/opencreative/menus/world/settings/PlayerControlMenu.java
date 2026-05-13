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

package ua.mcchickenstudio.opencreative.menus.world.settings;

import com.destroystokyo.paper.profile.PlayerProfile;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import ua.mcchickenstudio.opencreative.OpenCreative;
import ua.mcchickenstudio.opencreative.menus.AbstractMenu;
import ua.mcchickenstudio.opencreative.menus.buttons.ParameterButton;
import ua.mcchickenstudio.opencreative.menus.world.WorldMenu;
import ua.mcchickenstudio.opencreative.planets.Planet;
import ua.mcchickenstudio.opencreative.settings.Sounds;
import ua.mcchickenstudio.opencreative.utils.MessageUtils;
import ua.mcchickenstudio.opencreative.utils.PlayerConfirmation;

import java.util.ArrayList;
import java.util.List;

import static ua.mcchickenstudio.opencreative.utils.ItemUtils.*;
import static ua.mcchickenstudio.opencreative.utils.MessageUtils.getLocaleMessage;
import static ua.mcchickenstudio.opencreative.utils.MessageUtils.substring;
import static ua.mcchickenstudio.opencreative.utils.PlayerUtils.isEntityInDevPlanet;

/**
 * This class represents a menu, that allows to control selected player:
 * change dev/build permissions, whitelist, kick or ban.
 */
public final class PlayerControlMenu extends AbstractMenu implements WorldMenu {

    private final String nickname;
    private final Planet planet;
    private final List<ParameterButton> buttons = new ArrayList<>();

    private final ItemStack KICK = createItem(Material.STRUCTURE_VOID, 1, "menus.player-control.items.kick", "kick");
    private final ItemStack BAN = createItem(Material.BARRIER, 1, "menus.player-control.items.ban", "ban");
    private final ItemStack UNBAN = createItem(Material.LIME_STAINED_GLASS, 1, "menus.player-control.items.unban", "unban");
    private final ItemStack WHITELIST = createItem(Material.MAP, 1, "menus.player-control.items.whitelist", "whitelist");
    private final ItemStack UNWHITELIST = createItem(Material.FILLED_MAP, 1, "menus.player-control.items.whitelist-remove", "unwhitelist");

    private final ItemStack TRANSFER = createItem(Material.CAKE, 1, "menus.player-control.items.transfer-ownership", "transfer");

    private final ItemStack BACK = createItem(Material.ARROW, 1, "menus.player-control.items.back", "back");

    public PlayerControlMenu(String nickname, Planet planet) {
        super(4, MessageUtils.getLocaleMessage("menus.player-control.title", false)
                .replace("%name%", substring(nickname, 20)));
        this.nickname = nickname;
        this.planet = planet;
    }

    @Override
    public void fillItems(Player opener) {

        setItem(DECORATION_PANE_ITEM, 28, 34);
        setItem(createItem(getSelectedPlayer() == null ? Material.RED_STAINED_GLASS_PANE :
                Material.LIGHT_BLUE_STAINED_GLASS_PANE, 1), 29, 33);
        setItem(31, getPlayerIcon(nickname));

        Player player = getSelectedPlayer();

        setItem(27, BACK);

        if (isBanned()) {
            setItem(13, UNBAN);
            setItem(35, DECORATION_PANE_ITEM);
            return;
        } else {
            setItem(16, BAN);
        }

        int moveToLeft = 0;
        if (player != null && !isEntityInDevPlanet(player)) {
            ParameterButton fly = new ParameterButton(player.getAllowFlight(),
                    List.of(false, true), "fly", "menus.all-worlds", "menus.player-control.items.flight",
                    List.of(Material.FEATHER, Material.FEATHER));
            buttons.add(fly);
            setItem(10, fly.getItem());
        } else {
            setItem(10, AIR_ITEM);
            moveToLeft = 1;
        }
        ParameterButton build = new ParameterButton(getBuildPermission(),
                List.of("none", "not-trusted", "trusted"), "build", "menus.all-worlds", "menus.player-control.items.build",
                List.of(Material.BRICK_STAIRS, Material.BRICKS, Material.RED_NETHER_BRICKS));
        buttons.add(build);
        setItem(11 - moveToLeft, build.getItem());
        ParameterButton dev = new ParameterButton(getDevPermission(),
                List.of("none", "guest", "not-trusted", "trusted"), "dev", "menus.all-worlds", "menus.player-control.items.dev",
                List.of(Material.AMETHYST_BLOCK, Material.MEDIUM_AMETHYST_BUD, Material.LARGE_AMETHYST_BUD, Material.AMETHYST_CLUSTER));
        buttons.add(dev);

        setItem(12 - moveToLeft, dev.getItem());
        setItem(13 - moveToLeft, isWhitelisted() ? UNWHITELIST : WHITELIST);
        setItem(15, player != null ? KICK : AIR_ITEM);

        if (getBuildPermission().equals("trusted") && getDevPermission().equals("trusted")
                && isWhitelisted() && getSelectedPlayer() != null) {
            setItem(35, TRANSFER);
        } else {
            setItem(35, DECORATION_PANE_ITEM);
        }
    }

    private String getBuildPermission() {
        if (planet.getWorldPlayers().getBuildersTrusted().contains(nickname)) {
            return "trusted";
        } else if (planet.getWorldPlayers().getBuildersNotTrusted().contains(nickname)) {
            return "not-trusted";
        } else return "none";
    }

    private String getDevPermission() {
        if (planet.getWorldPlayers().getBuildersTrusted().contains(nickname)) {
            return "trusted";
        } else if (planet.getWorldPlayers().getBuildersNotTrusted().contains(nickname)) {
            return "not-trusted";
        } else if (planet.getWorldPlayers().getDevelopersGuests().contains(nickname)) {
            return "guest";
        } else return "none";
    }

    private boolean isWhitelisted() {
        return planet.getWorldPlayers().getWhitelistedPlayers().contains(nickname);
    }

    private boolean isBanned() {
        return planet.getWorldPlayers().isBanned(nickname);
    }

    private ItemStack getPlayerIcon(String nickname) {
        ItemStack item = createItem(Material.PLAYER_HEAD, 1, "menus.player-control.items.player");
        ItemMeta meta = item.getItemMeta();
        if (meta == null) {
            return item;
        }
        if (meta instanceof SkullMeta skullMeta) {
            PlayerProfile profile = Bukkit.createProfile(nickname);
            skullMeta.setPlayerProfile(profile);
            item.setItemMeta(skullMeta);
        }

        String statusKey;
        Player player = Bukkit.getPlayerExact(nickname);
        if (planet.getWorldPlayers().isBanned(nickname)) {
            statusKey = "banned";
        } else if (player == null || !planet.getPlayers().contains(player)) {
            statusKey = "offline";
        } else {
            statusKey = "online";
        }
        replacePlaceholderInLore(item, "%name%",
                substring(nickname, 30));
        replacePlaceholderInLore(item, "%status%",
                getLocaleMessage("menus.player-control.items.player." + statusKey));
        return item;
    }

    @Override
    public void onClick(@NotNull InventoryClickEvent event) {
        event.setCancelled(true);
        ItemStack item = event.getCurrentItem();
        if (!isPlayerClicked(event) || !isClickedInMenuSlots(event)) return;
        if (item == null) return;
        Player clicker = (Player) event.getWhoClicked();
        Player selectedPlayer = getSelectedPlayer();
        for (ParameterButton button : buttons) {
            if (!button.getItem().equals(item)) continue;
            if (onParameterChange(clicker, selectedPlayer, event, button)) return;
        }
        switch (getItemType(item)) {
            case "kick" -> {
                clicker.closeInventory();
                if (selectedPlayer != null) {
                    clicker.sendMessage(getLocaleMessage("world.players.kick.kicked")
                            .replace("%player%", nickname));
                    planet.getWorldPlayers().kickPlayer(selectedPlayer);
                }
            }
            case "ban" -> {
                clicker.closeInventory();
                int limit = planet.getLimits().getBlacklistedLimit();
                if (planet.getWorldPlayers().getBannedPlayers().size() > limit) {
                    clicker.sendMessage(getLocaleMessage("world.players.black-list.limit")
                            .replace("%limit%", String.valueOf(limit)));
                    return;
                }
                clicker.sendMessage(getLocaleMessage("world.players.black-list.added")
                        .replace("%player%", nickname));
                planet.getWorldPlayers().banPlayer(nickname);
            }
            case "unban" -> {
                clicker.closeInventory();
                clicker.sendMessage(getLocaleMessage("world.players.black-list.removed")
                        .replace("%player%", nickname));
                planet.getWorldPlayers().unbanPlayer(nickname);
            }
            case "whitelist" -> {
                clicker.closeInventory();
                int limit = planet.getLimits().getWhitelistedLimit();
                if (planet.getWorldPlayers().getWhitelistedPlayers().size() > limit) {
                    clicker.sendMessage(getLocaleMessage("world.players.white-list.limit")
                            .replace("%limit%", String.valueOf(limit)));
                    return;
                }
                clicker.sendMessage(getLocaleMessage("world.players.white-list.added")
                        .replace("%player%", nickname));
                planet.getWorldPlayers().whitelistPlayer(nickname);
            }
            case "unwhitelist" -> {
                clicker.closeInventory();
                clicker.sendMessage(getLocaleMessage("world.players.white-list.removed")
                        .replace("%player%", nickname));
                planet.getWorldPlayers().removeFromWhitelist(nickname);
            }
            case "transfer" -> {
                clicker.closeInventory();
                if (selectedPlayer == null) {
                    clicker.sendMessage(getLocaleMessage("world.players.transfer-ownership.offline")
                            .replace("%player%", nickname));
                    return;
                }
                if (OpenCreative.getPlanetsManager().getPlanetsByOwner(selectedPlayer).size() >= OpenCreative.getSettings().getGroups().getGroup(selectedPlayer).getWorldsLimit()) {
                    clicker.sendMessage(getLocaleMessage("world.players.transfer-ownership.limit")
                            .replace("%player%", nickname));
                    return;
                }
                clicker.sendMessage(getLocaleMessage("world.players.transfer-ownership.confirm-old")
                        .replace("%player%", nickname).replace("%id%", String.valueOf(planet.getId())));
                if (!PlayerConfirmation.hasConfirmation(clicker)) {
                    PlayerConfirmation.setConfirmation(clicker, PlayerConfirmation.TRANSFER_OWNERSHIP, nickname);
                }
            }
            case "back" -> new PlayersBrowserMenu(clicker, planet).open(clicker);
        }
    }

    private boolean onParameterChange(Player clicker, Player selectedPlayer, InventoryClickEvent event, ParameterButton button) {
        switch (button.getName().toLowerCase()) {
            case "fly" -> {
                button.next();
                setItem(event.getRawSlot(), button.getItem());
                boolean flight = (boolean) button.getCurrentValue();
                if (selectedPlayer != null) {
                    if (isEntityInDevPlanet(selectedPlayer)) {
                        Sounds.PLAYER_FAIL.play(clicker);
                        return true;
                    }
                    selectedPlayer.setAllowFlight(flight);
                    selectedPlayer.setFlying(flight);
                }
                return true;
            }
            case "dev" -> {
                button.next();
                setItem(event.getRawSlot(), button.getItem());
                String dev = (String) button.getCurrentValue();
                switch (dev.toLowerCase()) {
                    case "none" -> {
                        clicker.sendMessage(getLocaleMessage("world.players.developers.removed")
                                .replace("%player%", nickname));
                        planet.getWorldPlayers().removeDeveloper(nickname);
                    }
                    case "guest" -> {
                        int limit = planet.getLimits().getDevelopersLimit();
                        if (planet.getWorldPlayers().getAllDevelopers().size() > limit) {
                            clicker.sendMessage(getLocaleMessage("world.players.developers.limit").replace("%limit%", String.valueOf(limit)));
                            return true;
                        }
                        clicker.sendMessage(MessageUtils.getLocaleMessage("world.players.developers.guest")
                                .replace("%player%", nickname));
                        planet.getWorldPlayers().addDeveloperGuest(nickname);
                    }
                    case "not-trusted" -> {
                        clicker.sendMessage(getLocaleMessage("world.players.developers.added")
                                .replace("%player%", nickname));
                        planet.getWorldPlayers().addDeveloper(nickname, false);
                    }
                    case "trusted" -> {
                        clicker.sendMessage(MessageUtils.getLocaleMessage("world.players.developers.trusted")
                                .replace("%player%", nickname));
                        planet.getWorldPlayers().addDeveloper(nickname, true);
                    }
                }
                return true;
            }
            case "build" -> {
                button.next();
                setItem(event.getRawSlot(), button.getItem());
                String build = (String) button.getCurrentValue();
                switch (build.toLowerCase()) {
                    case "none" -> {
                        clicker.sendMessage(getLocaleMessage("world.players.builders.removed")
                                .replace("%player%", nickname));
                        planet.getWorldPlayers().removeBuilder(nickname);
                    }
                    case "not-trusted" -> {
                        int limit = planet.getLimits().getBuildersLimit();
                        if (planet.getWorldPlayers().getAllBuilders().size() > limit) {
                            clicker.sendMessage(getLocaleMessage("world.players.builders.limit")
                                    .replace("%limit%", String.valueOf(limit)));
                            return true;
                        }
                        clicker.sendMessage(getLocaleMessage("world.players.builders.added")
                                .replace("%player%", nickname));
                        planet.getWorldPlayers().addBuilder(nickname, false);
                    }
                    case "trusted" -> {
                        clicker.sendMessage(MessageUtils.getLocaleMessage("world.players.builders.trusted")
                                .replace("%player%", nickname));
                        planet.getWorldPlayers().addBuilder(nickname, true);
                    }
                }
                return true;
            }
        }
        return false;
    }

    private @Nullable Player getSelectedPlayer() {
        Player player = Bukkit.getPlayerExact(nickname);
        if (player == null) return null;
        if (this.planet.getPlayers().contains(player)) {
            return player;
        }
        return null;
    }

    @Override
    public void onOpen(@NotNull InventoryOpenEvent event) {
        Sounds.MENU_OPEN_WORLD_MODERATION.play(event.getPlayer());
    }

    @Override
    public Planet getPlanet() {
        return planet;
    }
}
