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

package ua.mcchickenstudio.opencreative.coding.modules;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import ua.mcchickenstudio.opencreative.OpenCreative;
import ua.mcchickenstudio.opencreative.coding.CodeConfiguration;
import ua.mcchickenstudio.opencreative.coding.CodingBlockParser;
import ua.mcchickenstudio.opencreative.coding.CodingBlockPlacer;
import ua.mcchickenstudio.opencreative.menus.AbstractMenu;
import ua.mcchickenstudio.opencreative.planets.DevPlanet;
import ua.mcchickenstudio.opencreative.settings.Sounds;
import ua.mcchickenstudio.opencreative.settings.groups.LimitType;
import ua.mcchickenstudio.opencreative.utils.CooldownUtils;

import java.util.LinkedHashSet;
import java.util.Set;

import static ua.mcchickenstudio.opencreative.utils.CooldownUtils.getCooldown;
import static ua.mcchickenstudio.opencreative.utils.CooldownUtils.setCooldown;
import static ua.mcchickenstudio.opencreative.utils.ItemUtils.*;
import static ua.mcchickenstudio.opencreative.utils.MessageUtils.getLocaleMessage;

public final class BlocksManipulatorMenu extends AbstractMenu {

    private final Player player;
    private final DevPlanet devPlanet;

    private final ItemStack duplicate = createItem(Material.BOOKSHELF, 1, "menus.developer.manipulator.items.duplicate", "duplicate");
    private final ItemStack createModule = createItem(Material.CHERRY_CHEST_BOAT, 1, "menus.developer.manipulator.items.create-module", "module");
    private final ItemStack createModuleLimit = createItem(Material.RED_STAINED_GLASS, 1, "menus.developer.manipulator.items.create-module-limit", "module");

    public BlocksManipulatorMenu(@NotNull Player player, @NotNull DevPlanet devPlanet, int selectedAmount) {
        super(3, getLocaleMessage("menus.developer.manipulator.title", false)
                .replace("%amount%", String.valueOf(selectedAmount))
                .replace("%limit%", String.valueOf(OpenCreative.getSettings().getGroups().getGroup(player)
                        .getLimit(LimitType.SELECTED_LINES_AMOUNT).calculateLimit(1)))
        );
        this.player = player;
        this.devPlanet = devPlanet;
    }

    @Override
    public void fillItems(Player player) {
        setItem(12, duplicate);
        int amount = OpenCreative.getModuleManager().getPlayerModules(player.getUniqueId()).size();
        int limit = OpenCreative.getSettings().getGroups().getGroup(player).getModulesLimit();
        int left = limit - amount;
        if (left >= 1) {
            replacePlaceholderInLore(createModule, "%amount%", amount);
            replacePlaceholderInLore(createModule, "%limit%", limit);
            replacePlaceholderInLore(createModule, "%left%", left);
            setItem(14, createModule);
        } else {
            replacePlaceholderInLore(createModuleLimit, "%amount%", amount);
            replacePlaceholderInLore(createModuleLimit, "%limit%", limit);
            replacePlaceholderInLore(createModuleLimit, "%left%", left);
            setItem(14, createModuleLimit);
        }

    }

    @Override
    public void onClick(@NotNull InventoryClickEvent event) {
        if (!isClickedInMenuSlots(event) || !isPlayerClicked(event)) {
            return;
        }
        event.setCancelled(true);
        ItemStack currentItem = event.getCurrentItem();
        if (currentItem == null) {
            return;
        }
        if (!devPlanet.isLoaded()) {
            player.closeInventory();
            return;
        }
        if (itemEquals(currentItem, duplicate)) {
            if (getCooldown(player, CooldownUtils.CooldownType.BLOCKS_DUPLICATION) > 0) {
                player.sendMessage(getLocaleMessage("cooldown").replace("%cooldown%", String.valueOf(getCooldown(player, CooldownUtils.CooldownType.BLOCKS_DUPLICATION))));
                return;
            }
            setCooldown(player, OpenCreative.getSettings().getGroups().getGroup(player)
                    .getBlocksDuplicationCooldown(), CooldownUtils.CooldownType.BLOCKS_DUPLICATION);
            player.closeInventory();
            CodeConfiguration temporary = new CodeConfiguration();
            Set<Location> markedExecutors = devPlanet.getMarkedExecutors(player);
            new CodingBlockParser(devPlanet, true).parseExecutors(devPlanet, temporary, new LinkedHashSet<>(markedExecutors));
            devPlanet.clearMarkedExecutors(player);
            ConfigurationSection section = temporary.getConfigurationSection("code.blocks");
            if (section == null) return;
            CodingBlockPlacer.CodePlacementResult result = new CodingBlockPlacer(devPlanet).placeCodingLines(devPlanet, section);

            if (result.getType() == CodingBlockPlacer.CodePlacementResult.Type.NOT_ENOUGH_SPACE) {
                player.sendMessage(getLocaleMessage("environment.duplication.few-space")
                        .replace("%required%", String.valueOf(markedExecutors.size())));
                Sounds.DEV_NOT_ALLOWED.play(player);
            } else if (result.getType() == CodingBlockPlacer.CodePlacementResult.Type.ERROR) {
                player.sendMessage(getLocaleMessage("environment.duplication.error"));
                Sounds.PLAYER_ERROR.play(player);
                for (Location placedExecutor : result.getPlacedColumns()) {
                    devPlanet.addChangedColumn(placedExecutor);
                }
            } else {
                player.sendMessage(getLocaleMessage("environment.duplication.success"));
                Sounds.DEV_BLOCKS_DUPLICATED.play(player);
                for (Location placedExecutor : result.getPlacedColumns()) {
                    devPlanet.addChangedColumn(placedExecutor);
                }
            }
        } else if (itemEquals(currentItem, createModule)) {
            if (getCooldown(player, CooldownUtils.CooldownType.MODULE_MANIPULATION) > 0) {
                player.sendMessage(getLocaleMessage("cooldown").replace("%cooldown%", String.valueOf(getCooldown(player, CooldownUtils.CooldownType.MODULE_MANIPULATION))));
                return;
            }
            setCooldown(player, OpenCreative.getSettings().getGroups().getGroup(player).getModuleManipulationCooldown(),
                    CooldownUtils.CooldownType.MODULE_MANIPULATION);
            int limit = OpenCreative.getSettings().getGroups().getGroup(player).getModulesLimit();
            if (OpenCreative.getModuleManager().getPlayerModules(player.getUniqueId()).size() > limit) {
                player.closeInventory();
                return;
            }
            player.closeInventory();
            OpenCreative.getModuleManager().createModule(player, devPlanet, devPlanet.getMarkedExecutors(player));
            devPlanet.clearMarkedExecutors(player);
        }

    }

    @Override
    public void onOpen(@NotNull InventoryOpenEvent event) {
        Sounds.MENU_OPEN_BLOCKS_MANIPULATOR.play(player);
    }
}
