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

package ua.mcchickenstudio.opencreative.coding.blocks.actions.variableactions.item;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;
import ua.mcchickenstudio.opencreative.coding.arguments.Arguments;
import ua.mcchickenstudio.opencreative.coding.blocks.actions.ActionType;
import ua.mcchickenstudio.opencreative.coding.blocks.actions.Target;
import ua.mcchickenstudio.opencreative.coding.blocks.actions.variableactions.VariableAction;
import ua.mcchickenstudio.opencreative.coding.blocks.executors.Executor;
import ua.mcchickenstudio.opencreative.coding.variables.VariableLink;

import java.util.ArrayList;
import java.util.List;

public final class SetItemLoreLineAction extends VariableAction {
    public SetItemLoreLineAction(Executor executor, Target target, int x, Arguments args) {
        super(executor, target, x, args);
    }

    @Override
    protected void execute() {
        VariableLink link = getArguments().getVariableLink("variable", this);
        ItemStack item = getArguments().getItem("item", getArguments().getItem("variable", new ItemStack(Material.APPLE), this), this);
        int index = getArguments().getInt("index", 1, this);
        ItemMeta meta = item.getItemMeta();
        if (meta == null) {
            return;
        }
        List<Component> newLore = meta.lore();
        if (newLore == null) {
            newLore = new ArrayList<>();
        }
        Component text = getArguments().getComponent("line", Component.text(""), this);
        if (newLore.size() > 64 || index > 64) {
            return;
        }
        if (index > newLore.size()) {
            int size = newLore.size();
            while (size < index) {
                newLore.add(Component.text(""));
                size++;
            }
        }
        if (!text.hasDecoration(TextDecoration.ITALIC)) {
            text = text.decoration(TextDecoration.ITALIC, false);
        }
        newLore.set(index - 1, text);
        meta.lore(newLore);
        item.setItemMeta(meta);
        if (link == null) return;
        setVarValue(link, item);
    }

    @Override
    public @NotNull ActionType getActionType() {
        return ActionType.VAR_SET_ITEM_LORE_LINE;
    }
}
