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

import java.util.List;

public final class GetItemLoreLineAction extends VariableAction {
    public GetItemLoreLineAction(Executor executor, Target target, int x, Arguments args) {
        super(executor, target, x, args);
    }

    @Override
    protected void execute() {
        VariableLink link = getArguments().getVariableLink("variable", this);
        ItemStack item = getArguments().getItem("item", getArguments().getItem("variable", new ItemStack(Material.APPLE), this), this);
        int index = getArguments().getInt("index", 1, this);
        ItemMeta meta = item.getItemMeta();
        if (meta == null) {
            setVarValue(link, "");
            return;
        }
        List<String> lore = meta.getLore();
        if (lore == null) {
            setVarValue(link, "");
            return;
        }
        if (lore.size() > 64 || index > 64) {
            setVarValue(link, "");
            return;
        }
        if (index > lore.size()) {
            setVarValue(link, "");
            return;
        }
        setVarValue(link, lore.get(index - 1));
    }

    @Override
    public @NotNull ActionType getActionType() {
        return ActionType.VAR_GET_ITEM_LORE_LINE;
    }
}
