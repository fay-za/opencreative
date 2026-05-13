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

package ua.mcchickenstudio.opencreative.menus;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;
import org.jetbrains.annotations.NotNull;
import ua.mcchickenstudio.opencreative.settings.Sounds;

import java.util.List;

import static ua.mcchickenstudio.opencreative.utils.ItemUtils.*;

public final class ConfirmationMenu extends AbstractMenu {

    private final Material infoMaterial;
    private final String infoName;
    private final List<String> infoLore;
    private final BukkitRunnable yesRunnable;

    public ConfirmationMenu(String title, Material infoMaterial, String infoName, List<String> infoLore, BukkitRunnable yesRunnable) {
        super(6, title);
        this.infoMaterial = infoMaterial;
        this.infoName = infoName;
        this.infoLore = infoLore;
        this.yesRunnable = yesRunnable;
    }

    @Override
    public void fillItems(Player player) {
        ItemStack info = createItem(infoMaterial, 1);
        setDisplayName(info, infoName);
        setLore(info, infoLore);
        setItem(13, info);
        setItem(37, createItem(Material.RED_STAINED_GLASS, 1, "menus.confirmation.items.cancel"));
        player.setCooldown(Material.LIME_SHULKER_BOX, 60);
        setItem(43, createItem(Material.LIME_SHULKER_BOX, 1, "menus.confirmation.items.confirm"));

    }

    @Override
    public void onClick(@NotNull InventoryClickEvent event) {
        event.setCancelled(true);
        if (!isPlayerClicked(event) || !isClickedInMenuSlots(event)) {
            return;
        }
        Player player = (Player) event.getWhoClicked();
        if (event.getSlot() == 43) {
            event.getClickedInventory().setItem(43, AIR_ITEM);
            player.closeInventory();
            yesRunnable.run();
        } else if (event.getSlot() == 37) {
            player.closeInventory();
        }
    }

    @Override
    public void onOpen(@NotNull InventoryOpenEvent event) {
        if (event.getPlayer() instanceof Player player) {
            Sounds.MENU_OPEN_CONFIRMATION.play(player);
        }
    }
}
