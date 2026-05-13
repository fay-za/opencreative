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

package ua.mcchickenstudio.opencreative.coding.blocks.actions.structureactions.structureonly;

import org.jetbrains.annotations.NotNull;
import ua.mcchickenstudio.opencreative.coding.arguments.Arguments;
import ua.mcchickenstudio.opencreative.coding.blocks.actions.ActionType;
import ua.mcchickenstudio.opencreative.coding.blocks.actions.Target;
import ua.mcchickenstudio.opencreative.coding.blocks.actions.structureactions.StructureAction;
import ua.mcchickenstudio.opencreative.coding.blocks.executors.Executor;
import ua.mcchickenstudio.opencreative.coding.blocks.executors.other.Structure;

public final class SetFieldAction extends StructureAction {

    public SetFieldAction(Executor executor, Target target, int x, Arguments args) {
        super(executor, target, x, args);
    }

    @Override
    protected void execute() {
        String fieldName = getArguments().getText("field", "", this);
        Object value = getArguments().getValue("value", this);

        if (fieldName.isEmpty()) return;

        Structure structure = null;

        if (getExecutor() instanceof Structure exec) {
            structure = exec;
        } else {
            for (Structure s : getPlanet().getTerritory().getScript().getExecutors().getStructuresList()) {
                if (s.getActions().contains(this)) {
                    structure = s;
                    break;
                }
            }
        }

        if (structure != null) {
            structure.setField(fieldName, value);
        }
    }

    @Override
    public @NotNull ActionType getActionType() {
        return ActionType.STRUCT_SET_FIELD_VALUE;
    }
}