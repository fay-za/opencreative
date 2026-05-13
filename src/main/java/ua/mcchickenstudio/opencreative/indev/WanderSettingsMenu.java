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

package ua.mcchickenstudio.opencreative.indev;

import com.destroystokyo.paper.profile.PlayerProfile;
import net.kyori.adventure.title.Title;
import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;
import org.jetbrains.annotations.NotNull;
import ua.mcchickenstudio.opencreative.OpenCreative;
import ua.mcchickenstudio.opencreative.menus.AbstractMenu;
import ua.mcchickenstudio.opencreative.menus.buttons.ParameterButton;
import ua.mcchickenstudio.opencreative.settings.Sounds;
import ua.mcchickenstudio.opencreative.utils.MessageUtils;
import ua.mcchickenstudio.opencreative.utils.PlayerConfirmation;
import ua.mcchickenstudio.opencreative.wanders.OfflineWander;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

import static ua.mcchickenstudio.opencreative.utils.ItemUtils.*;
import static ua.mcchickenstudio.opencreative.utils.MessageUtils.*;

public class WanderSettingsMenu extends AbstractMenu {

    private final String nickname;
    private final OfflineWander wander;

    private final ItemStack CHANGE_DESCRIPTION = createItem(Material.BOOK, 1, "menus.profile-settings.items.change-description", "description");
    private final ParameterButton CHANGE_GENDER;

    private final ItemStack CHANGE_SOCIAL_DISCORD = createItem(Material.BLUE_STAINED_GLASS, 1, "menus.profile-settings.items.change-social-discord", "discord");
    private final ItemStack CHANGE_SOCIAL_TWITTER = createItem(Material.CYAN_STAINED_GLASS, 1, "menus.profile-settings.items.change-social-twitter", "twitter");
    private final ItemStack CHANGE_SOCIAL_YOUTUBE = createItem(Material.RED_STAINED_GLASS, 1, "menus.profile-settings.items.change-social-youtube", "youtube");
    private final ItemStack CHANGE_SOCIAL_TELEGRAM = createItem(Material.LIGHT_BLUE_STAINED_GLASS, 1, "menus.profile-settings.items.change-social-telegram", "telegram");

    public WanderSettingsMenu(@NotNull String nickname) {
        super(4, getLocaleMessage("menus.profile-settings.title", false));
        this.nickname = nickname;
        this.wander = OpenCreative.getOfflineWander(Bukkit.getOfflinePlayer(nickname).getUniqueId());
        CHANGE_GENDER = new ParameterButton(
                wander.getGender() == null ? "unknown" : wander.getGender().name().toLowerCase().replace("_", "-"),
                List.of("male", "female", "non-binary", "unknown"),
                "gender",
                "menus.profile-settings",
                "menus.profile-settings.items.gender",
                List.of(Material.LIGHT_BLUE_DYE, Material.PINK_DYE, Material.YELLOW_DYE, Material.TOTEM_OF_UNDYING)
        );
    }

    private ItemStack getHead() {
        ItemStack item = createItem(Material.PLAYER_HEAD, 1, "menus.profile-settings.items.player");
        ItemMeta meta = item.getItemMeta();
        if (meta == null) {
            return item;
        }
        if (meta instanceof SkullMeta skullMeta) {
            PlayerProfile profile = Bukkit.createProfile(nickname);
            skullMeta.setPlayerProfile(profile);
            item.setItemMeta(skullMeta);
        }
        List<String> lore = new ArrayList<>();
        OfflinePlayer offlinePlayer = wander.getOfflinePlayer();
        for (String loreLine : MessageUtils.getLocaleItemDescription("menus.player-profile.items.player.lore")) {
            if (loreLine.contains("%description%")) {
                String description = wander.getDescription() == null ? getLocaleMessage("profiles.default-description") : wander.getDescription();
                String[] newLines = description.split("\\\\n");
                for (String newLine : newLines) {
                    lore.add(loreLine.replace("%description%", ChatColor.translateAlternateColorCodes('&', "&f" + newLine)));
                }
            } else {
                lore.add(ChatColor.translateAlternateColorCodes('&', parsePAPI(offlinePlayer, loreLine)));
            }
        }
        meta.setLore(lore);
        replacePlaceholderInLore(item, "%player%", nickname);
        replacePlaceholderInLore(item, "%gender%", wander.getGender() == null
                ? OfflineWander.Gender.UNKNOWN.getLocaleName() : wander.getGender().getLocaleName());
        return item;
    }

    @Override
    public void fillItems(Player player) {
        setItem(10, CHANGE_DESCRIPTION);
        setItem(11, CHANGE_GENDER.getItem());

        setItem(13, CHANGE_SOCIAL_DISCORD);
        setItem(14, CHANGE_SOCIAL_TELEGRAM);
        setItem(15, CHANGE_SOCIAL_TWITTER);
        setItem(16, CHANGE_SOCIAL_YOUTUBE);
        setItem(30, getHead());
        setItem(32, getSocialLinks());
        setItem(createItem(Material.CYAN_STAINED_GLASS_PANE, 1), 29, 33);
        setItem(DECORATION_PANE_ITEM, 27, 28, 34, 35);
    }

    @Override
    public void onClick(@NotNull InventoryClickEvent event) {
        ItemStack item = event.getCurrentItem();
        event.setCancelled(true);
        Player player = (Player) event.getWhoClicked();
        switch (getItemType(item)) {
            case "description" -> {
                player.showTitle(Title.title(
                        toComponent(getLocaleMessage("settings.profile-description.title")), toComponent(getLocaleMessage("settings.world-description.subtitle")),
                        Title.Times.times(Duration.ofMillis(100), Duration.ofSeconds(30), Duration.ofMillis(130))
                ));
                PlayerConfirmation.setConfirmation(player, PlayerConfirmation.PROFILE_DESCRIPTION);
                player.sendMessage(getLocaleMessage("settings.profile-description.usage"));
                player.closeInventory();
            }
            case "gender" -> {
                Sounds.PROFILE_SETTINGS_GENDER_CHANGE.play(player);
                CHANGE_GENDER.next();
                wander.setGender(OfflineWander.Gender.getGender(
                        CHANGE_GENDER.getCurrentValue().toString().toUpperCase().replaceAll("-", "_")
                ));
                setItem(event.getRawSlot(), CHANGE_GENDER.getItem());
                setItem(30, getHead());
            }
            case "discord", "youtube", "twitter", "telegram" -> {
                String social = getItemType(item);
                PlayerConfirmation.setConfirmation(player, PlayerConfirmation.PROFILE_SOCIAL_CHANGE, social);
                player.showTitle(Title.title(
                        getLocaleComponent("settings.profile-social-" + social + ".title"),
                        getLocaleComponent("settings.profile-social-" + social + ".subtitle"),
                        Title.Times.times(Duration.ofMillis(100), Duration.ofSeconds(30), Duration.ofMillis(130))
                ));
                player.sendMessage(getLocaleMessage("settings.profile-social-" + social + ".usage"));
                player.closeInventory();
            }
        }
    }

    private ItemStack getSocialLinks() {
        ItemStack item = createItem(Material.NAME_TAG, 1, "menus.player-profile.items.social-links");
        List<String> socialSites = List.of("discord", "youtube", "telegram", "twitter");
        for (String site : socialSites) {
            String link = wander.getLink(site);
            if (link == null) {
                link = getLocaleMessage("menus.player-profile.items.social-links.unknown", false);
            }
            replacePlaceholderInLore(item, "%" + site + "%", link);
        }
        return item;
    }

    @Override
    public void onOpen(@NotNull InventoryOpenEvent event) {
        Sounds.MENU_OPEN_PROFILE_SETTINGS.play(event.getPlayer());
    }
}
