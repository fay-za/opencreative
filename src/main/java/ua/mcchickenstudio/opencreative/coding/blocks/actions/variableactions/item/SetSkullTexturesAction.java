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

import com.destroystokyo.paper.profile.PlayerProfile;
import com.destroystokyo.paper.profile.ProfileProperty;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;
import org.jetbrains.annotations.NotNull;
import ua.mcchickenstudio.opencreative.coding.arguments.Arguments;
import ua.mcchickenstudio.opencreative.coding.blocks.actions.ActionType;
import ua.mcchickenstudio.opencreative.coding.blocks.actions.Target;
import ua.mcchickenstudio.opencreative.coding.blocks.actions.variableactions.VariableAction;
import ua.mcchickenstudio.opencreative.coding.blocks.executors.Executor;
import ua.mcchickenstudio.opencreative.coding.variables.VariableLink;

import java.util.UUID;

public final class SetSkullTexturesAction extends VariableAction {
    public SetSkullTexturesAction(Executor executor, Target target, int x, Arguments args) {
        super(executor, target, x, args);
    }

    @Override
    protected void execute() {
        VariableLink link = getArguments().getVariableLink("variable", this);
        String textures = getArguments().getText("textures", "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNjllZjA4MjRkMzFiNDU3NTEzZGI1Y2ZkZjk1ZWNlMjAxODBjZWYzYzhmODFhZmRjM2FlMmM5MzE0YmMyMGRiNSJ9fX0=", this);
        ItemStack item = getArguments().getItem("item", getArguments().getItem("variable", new ItemStack(Material.PLAYER_HEAD), this), this);
        ItemMeta meta = item.getItemMeta();
        if (meta == null) {
            return;
        }
        if (meta instanceof SkullMeta skullMeta) {
            PlayerProfile profile = Bukkit.createProfile(UUID.randomUUID());
            profile.setProperty(new ProfileProperty("textures", textures));
            skullMeta.setPlayerProfile(profile);
            item.setItemMeta(skullMeta);
        }
        setVarValue(link, item);
    }

    @Override
    public @NotNull ActionType getActionType() {
        return ActionType.VAR_SET_ITEM_SKULL_TEXTURES;
    }
}
