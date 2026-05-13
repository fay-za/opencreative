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

package ua.mcchickenstudio.opencreative.indev.values;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.event.HoverEvent;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import net.kyori.adventure.title.Title;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import ua.mcchickenstudio.opencreative.settings.Sounds;

import java.time.Duration;

import static ua.mcchickenstudio.opencreative.utils.ItemUtils.*;
import static ua.mcchickenstudio.opencreative.utils.MessageUtils.*;

/**
 * <h1>TextCodingValue</h1>
 * This class represents a coding value, that
 * stores text (string).
 */
public class TextCodingValue extends CodingValue<String> implements ChatEditableValue, InteractableValue{

    public TextCodingValue() {
        super("text", Material.BOOK, Material.BROWN_STAINED_GLASS_PANE);
    }

    @Override
    public void onPlayerChat(@NotNull Player player, @NotNull ItemStack item, @NotNull String message) {
        ItemMeta meta = item.getItemMeta();
        Component newName = LegacyComponentSerializer.legacyAmpersand()
                .deserialize(message.replace("%space%", " "));
        meta.displayName(newName);
        item.setItemMeta(meta);
        Sounds.DEV_TEXT_SET.play(player);
        setPersistentData(item,getCodingValueKey(),"TEXT");
        player.getInventory().setItemInMainHand(item);
        Component subtitle = meta.displayName();
        if (subtitle == null) subtitle = newName;
        player.showTitle(Title.title(
                getLocaleComponent("world.dev-mode.set-variable"), subtitle,
                Title.Times.times(Duration.ofMillis(250), Duration.ofSeconds(2), Duration.ofMillis(750))
        ));
        player.swingMainHand();
    }

    @Override
    public @Nullable String deserialize(@NotNull Object value, @NotNull Saver type) {
        switch (type) {
            case ITEM -> {
                if (!(value instanceof ItemStack item)) {
                    return null;
                }
                ItemMeta meta = item.getItemMeta();
                if (meta == null) return null;
                Component displayName = meta.displayName();
                if (displayName == null) return "";
                return LegacyComponentSerializer.legacySection().serialize(displayName);
            }
            case JSON, YAML -> {
                return String.valueOf(value);
            }
        }
        return "";
    }

    @Override
    public @NotNull Object serialize(@NotNull String value, @NotNull Saver type) {
        if (type == Saver.ITEM) {
            ItemStack item = createItem(Material.BOOK, 1, "menus.developer.variables.items.text");
            setDisplayName(item, value);
            return item;
        }
        return value;
    }

    @Override
    public boolean onPlayerInteract(@NotNull Player player, @NotNull ItemStack item, @NotNull PlayerInteractEvent event) {
        if (event.getAction() == Action.RIGHT_CLICK_AIR) {
            ItemMeta meta = item.getItemMeta();
            if (meta == null || !meta.hasDisplayName()) {
                return true;
            }
            Component displayName = meta.displayName();
            if (displayName != null) {
                player.sendMessage(displayName.hoverEvent(HoverEvent.showText(toComponent(getLocaleMessage("world.dev-mode.click-to-copy")))).clickEvent(ClickEvent.suggestCommand(meta.getDisplayName().replace("§","&"))));
                setPersistentData(item, getCodingValueKey(),"TEXT");
                player.swingMainHand();
            }
            return true;
        }
        return false;
    }
}
