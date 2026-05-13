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

import ua.mcchickenstudio.opencreative.coding.variables.ValueType;

/**
 * This class represents an Argument Slot, that can be added to actions.
 * It's used for creating layouts and saving items into chest.
 *
 * @see ParameterSlot
 */
public class ArgumentSlot {

    private final String path;
    private final ValueType varType;
    private final int listSize;
    private final boolean acceptEmptyItems;

    public ArgumentSlot(String path, ValueType varType) {
        this.varType = varType;
        this.listSize = 1;
        this.path = path;
        this.acceptEmptyItems = false;
    }

    public ArgumentSlot(String path, ValueType varType, int listSize) {
        this.varType = varType;
        this.listSize = listSize;
        this.path = path;
        this.acceptEmptyItems = false;
    }

    public ArgumentSlot(String path, ValueType varType, int listSize, boolean acceptEmptyItems) {
        this.varType = varType;
        this.listSize = listSize;
        this.path = path;
        this.acceptEmptyItems = acceptEmptyItems;
    }


    public ValueType getVarType() {
        return varType;
    }

    public boolean isList() {
        return getListSize() > 1;
    }


    public boolean acceptEmptyItems() {
        return acceptEmptyItems;
    }

    public int getListSize() {
        return listSize;
    }

    public String getPath() {
        return path;
    }

    public boolean isParameter() {
        return varType == ValueType.PARAMETER;
    }
}
