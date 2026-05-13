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

package ua.mcchickenstudio.opencreative.coding.menus.layouts;

import org.bukkit.block.Block;
import org.jetbrains.annotations.NotNull;
import ua.mcchickenstudio.opencreative.coding.blocks.actions.ActionType;

/**
 * <h1>LayoutMaker</h1>
 * This class represents a maker for coding container layout.
 * Sets glasses and argument slots.
 */
public final class LayoutMaker extends Layout {

    public LayoutMaker(@NotNull ActionType action, @NotNull Block chestBlock) {
        super(3, action, chestBlock);
    }

    @Override
    protected void fillArgumentItems() {
        if (actionType == ActionType.WORLD_ADD_CRAFTING_RECIPE) {
            fillCraftingRecipeLayout();
            return;
        }
        ArgumentSlot[] args = requiredSlots;
        if (args.length > 0 && args[0].isList()) {
            fillWithFirstList(args);
            return;
        }
        fillWithArguments(args);
    }

    private void fillCraftingRecipeLayout() {
        setRows(5);
        setItem(DECORATION_PANE_ITEM, 0, 4, 5, 6, 7, 8, 15, 33, 36, 40, 41, 42);
        setGlass(1, 1, 2, 3, 9, 13, 18, 22, 27, 31, 37, 38, 39);
        setArgSlot(1, 10, 11, 12, 19, 20, 21, 28, 29, 30);

        setArgSlot(2, 23);
        setGlass(2, 14, 24, 32);

        setGlass(3, 16);
        setArgSlot(3, 17);

        setGlass(4, 25);
        setArgSlot(4, 26);

        setGlass(5, 34);
        setArgSlot(5, 35);

        setGlass(6, 43);
        setArgSlot(6, 44);
    }

    private void fillWithArguments(@NotNull ArgumentSlot[] args) {
        for (int slot = 0; slot < 3 * 9; slot++) {
            setItem(slot, DECORATION_PANE_ITEM);
        }
        switch (args.length) {
            case 1:
                setArgSlotCross(1, 13);
                break;
            case 2:
                setArgSlotCross(1, 11);
                setArgSlotCross(2, 15);
                break;
            case 3:
                setArgSlotCross(1, 10);
                setArgSlotCross(2, 13);
                setArgSlotCross(3, 16);
                break;
            case 4:
                setArgSlotVertical(1, 10);
                setArgSlotVertical(2, 12);
                setArgSlotVertical(3, 14);
                setArgSlotVertical(4, 16);
                break;
            case 5:
                setArgSlotVertical(1, 9);
                setArgSlotVertical(2, 11);
                setArgSlotVertical(3, 13);
                setArgSlotVertical(4, 15);
                setArgSlotVertical(5, 17);
                break;
            case 6:
                setArgSlotVertical(1, 10);
                setArgSlotVertical(2, 11);
                setArgSlotVertical(3, 12);
                setArgSlotVertical(4, 14);
                setArgSlotVertical(5, 15);
                setArgSlotVertical(6, 16);
                break;
            case 7:
                setArgSlotVertical(1, 10);
                setArgSlotVertical(2, 11);
                setArgSlotVertical(3, 12);
                setArgSlotVertical(4, 13);
                setArgSlotVertical(5, 14);
                setArgSlotVertical(6, 15);
                setArgSlotVertical(7, 16);
                break;
            case 8:
                setArgSlotVertical(1, 9);
                setArgSlotVertical(2, 10);
                setArgSlotVertical(3, 11);
                setArgSlotVertical(4, 12);
                setArgSlotVertical(5, 14);
                setArgSlotVertical(6, 15);
                setArgSlotVertical(7, 16);
                setArgSlotVertical(8, 17);
                break;
            case 9:
                setArgSlotVertical(1, 9);
                setArgSlotVertical(2, 10);
                setArgSlotVertical(3, 11);
                setArgSlotVertical(4, 12);
                setArgSlotVertical(5, 13);
                setArgSlotVertical(6, 14);
                setArgSlotVertical(7, 15);
                setArgSlotVertical(8, 16);
                setArgSlotVertical(9, 17);
                break;
        }
    }

    private void fillWithFirstList(@NotNull ArgumentSlot[] args) {
        switch (args[0].getListSize()) {
            case 9: {
                setRows(3);
                if (actionType == ActionType.WORLD_SPAWN_ENTITY) {
                    setRows(6);
                }
                setGlass(1, 0, 1, 2, 3, 4, 5, 6, 7, 8,
                        18, 19, 20, 21, 22, 23, 24, 25, 26);
                for (int slot = 9; slot < 18; slot++) {
                    setArgSlot(1, slot);
                }
                if (actionType == ActionType.WORLD_SPAWN_ENTITY) {
                    int slot = 36;
                    for (int argNumber = 2; argNumber <= args.length; argNumber++) {
                        if (slot <= 44) {
                            setArgSlotVertical(argNumber, slot);
                            slot++;
                        }
                    }
                    return;
                }
                if (args.length > 1) {
                    if (args[1].isList()) {
                        setRows(6);
                        setGlass(2, 27, 28, 29, 30, 31, 32, 33, 34, 35,
                                45, 46, 47, 48, 49, 50, 51, 52, 53);
                        for (int slot = 36; slot < 45; slot++) {
                            setArgSlot(2, slot);
                        }
                        if (args.length > 2) {
                            setArgSlotHorizontal(3, 49);
                        }
                    } else {
                        setRows(4);
                        for (int slot = 27; slot < 36; slot++) {
                            setItem(slot, DECORATION_PANE_ITEM);
                        }
                        int remainingSlots = args.length - 1;
                        int i = 2;
                        for (int slot : getCentredSlots(remainingSlots, 4)) {
                            if (remainingSlots > 3) {
                                setGlass(i, (slot - 9));
                                setArgSlot(i, slot);
                            } else {
                                setArgSlotHorizontal(i, slot);
                            }
                            i++;
                        }
                    }
                }
                break;
            }
            case 18: {
                setRows(4);
                setGlass(1, 0, 1, 2, 3, 4, 5, 6, 7, 8,
                        27, 28, 29, 30, 31, 32, 33, 34, 35);
                for (int slot = 9; slot < 27; slot++) {
                    setArgSlot(1, slot);
                }
                if (args.length > 1 && !args[1].isList()) {
                    setRows(6);
                    for (int slot = 36; slot < 54; slot++) {
                        setItem(slot, DECORATION_PANE_ITEM);
                    }
                    int remainingSlots = args.length - 1;
                    int i = 2;
                    for (int slot : getCentredSlots(remainingSlots, 6)) {
                        if (remainingSlots > 3) {
                            setGlass(i, (slot - 9));
                            setArgSlot(i, slot);
                        } else {
                            setArgSlotHorizontal(i, slot);
                        }
                        i++;
                    }
                }
                break;
            }
            case 27: {
                setRows(5);
                setGlass(1, 0, 1, 2, 3, 4, 5, 6, 7, 8,
                        36, 37, 38, 39, 40, 41, 42, 43, 44);
                for (int slot = 9; slot < 36; slot++) {
                    setArgSlot(1, slot);
                }
                if (args.length > 1 && !args[1].isList()) {
                    setArgSlotHorizontal(2, 40);
                }
                break;
            }
            case 45: {
                setRows(6);
                int number = 1;
                for (int slot = 9; slot < 45; slot++) {
                    setArgSlot(number++, slot);
                }
                setGlass(1, 45, 46, 47, 48, 49, 50, 51, 52, 53);
            }
        }
    }

}
