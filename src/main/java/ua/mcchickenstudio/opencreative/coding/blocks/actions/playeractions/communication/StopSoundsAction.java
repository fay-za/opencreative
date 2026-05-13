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

import net.kyori.adventure.key.Key;
import net.kyori.adventure.sound.SoundStop;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import ua.mcchickenstudio.opencreative.coding.arguments.Arguments;
import ua.mcchickenstudio.opencreative.coding.blocks.actions.ActionType;
import ua.mcchickenstudio.opencreative.coding.blocks.actions.Target;
import ua.mcchickenstudio.opencreative.coding.blocks.actions.playeractions.PlayerAction;
import ua.mcchickenstudio.opencreative.coding.blocks.executors.Executor;

import java.util.List;

public final class StopSoundsAction extends PlayerAction {
    public StopSoundsAction(Executor executor, Target target, int x, Arguments args) {
        super(executor, target, x, args);
    }

    @Override
    public void executePlayer(@NotNull Player player) {
        List<String> sounds = getArguments().getTextList("sounds", this);
        if (sounds.isEmpty()) {
            player.stopAllSounds();
        } else {
            for (String sound : sounds) {
                String nameSpace = "minecraft";
                if (sound.contains(":")) {
                    nameSpace = sound.split(":")[0];
                    sound = sound.split(":")[1];
                }
                player.stopSound(SoundStop.named(Key.key(nameSpace, sound)));
            }
        }
    }

    @Override
    public @NotNull ActionType getActionType() {
        return ActionType.PLAYER_STOP_SOUNDS;
    }
}
