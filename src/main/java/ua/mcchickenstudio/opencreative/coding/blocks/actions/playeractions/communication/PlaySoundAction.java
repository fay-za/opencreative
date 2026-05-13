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

package ua.mcchickenstudio.opencreative.coding.blocks.actions.playeractions.communication;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.SoundCategory;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import ua.mcchickenstudio.opencreative.coding.arguments.Arguments;
import ua.mcchickenstudio.opencreative.coding.blocks.actions.ActionType;
import ua.mcchickenstudio.opencreative.coding.blocks.actions.Target;
import ua.mcchickenstudio.opencreative.coding.blocks.actions.playeractions.PlayerAction;
import ua.mcchickenstudio.opencreative.coding.blocks.executors.Executor;

public final class PlaySoundAction extends PlayerAction {

    public PlaySoundAction(Executor executor, Target target, int x, Arguments args) {
        super(executor, target, x, args);
    }

    @Override
    public void executePlayer(@NotNull Player player) {
        String sound = getArguments().getText("sound", "entity.player.levelup", this);
        ItemStack musicDisc = getArguments().getItem("sound", new ItemStack(Material.AIR), this);
        if (musicDisc.getType() != Material.AIR && musicDisc.getType().name().contains("MUSIC_DISC")) {
            sound = musicDisc.getType().name().toLowerCase().replace("music_disc_", "music_disc.");
        }
        float volume = getArguments().getFloat("volume", 100f, this);
        float pitch = getArguments().getFloat("pitch", 1f, this);
        String categoryString = getArguments().getText("category", "ambient", this);
        Location location = getArguments().getLocation("location", player.getLocation(), this);
        long seed = getArguments().getLong("seed", 0L, this);
        SoundCategory category;
        try {
            category = SoundCategory.valueOf(categoryString.toUpperCase());
        } catch (Exception error) {
            category = SoundCategory.AMBIENT;
        }
        if (getArguments().pathExists("seed")) {
            player.playSound(location, sound, category, volume, pitch, seed);
        } else {
            player.playSound(location, sound, category, volume, pitch);
        }
    }

    @Override
    public @NotNull ActionType getActionType() {
        return ActionType.PLAYER_PLAY_SOUND;
    }
}
