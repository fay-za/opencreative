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

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import ua.mcchickenstudio.opencreative.menus.AbstractMenu;
import ua.mcchickenstudio.opencreative.menus.BlockMenu;
import ua.mcchickenstudio.opencreative.menus.buttons.ParameterButton;

import java.util.ArrayList;
import java.util.List;

import static ua.mcchickenstudio.opencreative.utils.ItemUtils.createItem;
import static ua.mcchickenstudio.opencreative.utils.MessageUtils.getLocaleMessage;

public class ExecutorArgumentsMenu extends AbstractMenu implements BlockMenu {

    private final String type;
    private final Block containerBlock;

    private final ItemStack NO_ARGUMENT = createItem(Material.LIGHT_GRAY_STAINED_GLASS, 1,"menus.developer.function.arguments.no-argument");
    private final ItemStack NO_ARGUMENT_PANE = createItem(Material.LIGHT_GRAY_STAINED_GLASS_PANE, 1,"menus.developer.function.arguments.no-argument");
    private final List<ParameterButton> typeChoosers = new ArrayList<>();

    public ExecutorArgumentsMenu(@NotNull String type, @NotNull Block containerBlock) {
        super(3, getLocaleMessage("menus.developer.function-arguments.title"));
        this.type = type;
        this.containerBlock = containerBlock;
    }

    @Override
    public void fillItems(Player player) {

    }

    @Override
    public void onClick(@NotNull InventoryClickEvent event) {
        setItem(DECORATION_PANE_ITEM, 0, 8, 9, 17, 18, 26, 27);
        for (int arg = 1; arg <= 9; arg++) {
            fillArgument(arg);
        }
    }

    private void fillArgument(int argument) {
        ItemStack item = getFromContent(0);
        if (item.isEmpty()) {
            setItem(NO_ARGUMENT_PANE, argument-1, argument+17);
            setItem(NO_ARGUMENT, argument+8);
        }
    }

    private @NotNull ItemStack getFromContent(int slot) {
        if (!(containerBlock.getState() instanceof InventoryHolder container)) return ItemStack.empty();
        if (slot < 0 || slot >= container.getInventory().getContents().length) return ItemStack.empty();
        ItemStack item = container.getInventory().getContents()[slot];
        if (item == null) return ItemStack.empty();
        return item;
    }

    @Override
    public void onOpen(@NotNull InventoryOpenEvent event) {

    }

    @Override
    public @Nullable BlockState getBlockState() {
        return containerBlock.getState();
    }

    @Override
    public @Nullable Location getLocation() {
        return containerBlock.getLocation();
    }
}
