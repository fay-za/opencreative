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

import org.bukkit.Material;
import ua.mcchickenstudio.opencreative.coding.variables.ValueType;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * This class represents a Parameter slot, that can be added in actions.
 * It's used in layouts as button, that player can click, and it will change value.
 * With this players can just select parameter value instead of putting tems in chest.
 *
 * @see ArgumentSlot
 */
public final class ParameterSlot extends ArgumentSlot {

    private final List<Object> values = new ArrayList<>();
    private final List<Material> icons = new ArrayList<>();

    public ParameterSlot(String path, List<Object> values, Material... materials) {
        super(path, ValueType.PARAMETER);
        this.values.addAll(values);
        this.icons.addAll(Arrays.asList(materials));
    }

    public ParameterSlot(String path, Material... materials) {
        super(path, ValueType.PARAMETER);
        this.values.add(false);
        this.values.add(true);
        if (Arrays.asList(materials).isEmpty()) {
            this.icons.add(Material.GRAY_DYE);
            this.icons.add(Material.LIME_DYE);
        } else {
            this.icons.addAll(Arrays.asList(materials));
        }
    }

    public ParameterSlot(String path, boolean firstValue, Material trueMaterial, Material falseMaterial) {
        super(path, ValueType.PARAMETER);
        this.values.add(firstValue);
        this.values.add(!firstValue);
        this.icons.add(firstValue ? trueMaterial : falseMaterial);
        this.icons.add(firstValue ? falseMaterial : trueMaterial);
    }

    public ParameterSlot(String path, int maxNumber, Material... materials) {
        super(path, ValueType.PARAMETER);
        for (int i = 0; i < maxNumber; i++) {
            this.values.add(i);
        }
        this.icons.addAll(Arrays.asList(materials));
    }

    public List<Object> getValues() {
        return values;
    }

    public List<Material> getIcons() {
        return icons;
    }
}
