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

package ua.mcchickenstudio.opencreative.coding.blocks.executors.player;

import org.jetbrains.annotations.NotNull;
import ua.mcchickenstudio.opencreative.coding.blocks.DisplayableIcon;
import ua.mcchickenstudio.opencreative.coding.blocks.executors.EventAwaiter;
import ua.mcchickenstudio.opencreative.coding.blocks.executors.Executor;
import ua.mcchickenstudio.opencreative.coding.blocks.executors.ExecutorCategory;

public abstract class PlayerExecutor extends Executor implements DisplayableIcon, EventAwaiter {

    public PlayerExecutor(@NotNull String id) {
        super("player_" + id, ExecutorCategory.EVENT_PLAYER);
    }

    public PlayerExecutor(@NotNull String id, boolean ignored) {
        super(id, ExecutorCategory.EVENT_PLAYER);
    }

}
