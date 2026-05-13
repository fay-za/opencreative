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

package ua.mcchickenstudio.opencreative.indev.blocks;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

import static ua.mcchickenstudio.opencreative.utils.ErrorUtils.sendWarningErrorMessage;

public class CodingBlocks {

    private final List<CodingBlock> codingBlocks = new ArrayList<>();

    public @Nullable CodingBlock getBlock(@NotNull String id) {
        for (CodingBlock block : codingBlocks) {
            if (block.getId().equalsIgnoreCase(id)) {
                return block;
            }
        }
        return null;
    }

    public void registerBlock(@NotNull CodingBlock block) {
        if (getBlock(block.getId()) != null) {
            sendWarningErrorMessage("[CODING] Tried to register block with same IDs: " + block.getId());
        }
    }

}
