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

package ua.mcchickenstudio.opencreative.coding.blocks.actions.variableactions.list;

import org.jetbrains.annotations.NotNull;
import ua.mcchickenstudio.opencreative.coding.arguments.Arguments;
import ua.mcchickenstudio.opencreative.coding.blocks.actions.ActionType;
import ua.mcchickenstudio.opencreative.coding.blocks.actions.Target;
import ua.mcchickenstudio.opencreative.coding.blocks.actions.variableactions.VariableAction;
import ua.mcchickenstudio.opencreative.coding.blocks.executors.Executor;
import ua.mcchickenstudio.opencreative.coding.variables.VariableLink;
import ua.mcchickenstudio.opencreative.utils.millennium.math.Statistics;

import java.util.List;

public final class CalculateFromListAction extends VariableAction {

    // Made by pawsashatoy :)
    public CalculateFromListAction(Executor executor, Target target, int x, Arguments args) {
        super(executor, target, x, args);
    }

    @Override
    protected void execute() {
        VariableLink variable = getArguments().getVariableLink("variable", this);
        List<Object> elements = getArguments().getList("list", this);
        final String type = getArguments().getText("calculation", "get-min", this);

        if (elements != null && !elements.isEmpty() && elements.getFirst() instanceof Number) {
            List<Number> numbers = elements.stream()
                    .filter(o -> o instanceof Number)
                    .map(o -> (Number) o)
                    .toList();
            setVarValue(variable, switch (type) {
                case ("get-min") -> Statistics.getMin(numbers);
                case ("get-max") -> Statistics.getMax(numbers);
                case ("average") -> Statistics.getAverage(numbers);
                case ("variance") -> Statistics.getVariance(numbers);
                case ("deviation") -> Statistics.getStandardDeviation(numbers);
                case ("median") -> Statistics.getMedian(numbers);
                case ("distinct") -> Statistics.getDistinct(numbers);
                case ("kurtosis") -> Statistics.getKurtosis(numbers);
                case ("skewness") -> Statistics.getSkewness(numbers);
                case ("mode") -> Statistics.getMode(numbers);
                case ("quantile") -> Statistics.getQuantile(numbers, 1.0);
                case ("iqr") -> Statistics.getIQR(numbers);
                case ("shannon-entropy") -> Statistics.getShannonEntropy(numbers);
                case ("gini-index") -> Statistics.getGiniIndex(numbers);
                case ("linear-trend") -> Statistics.getLinearTrend(numbers);
                case ("jolt-delta-list") -> Statistics.getJiffDelta(numbers, 1);
                case ("outliers-list") -> Statistics.getOutliersSimply(numbers);
                default -> 0;
            });
        }

    }

    @Override
    public @NotNull ActionType getActionType() {
        return ActionType.VAR_CALCULATE_FROM_LIST;
    }
}
