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

package ua.mcchickenstudio.opencreative.utils.millennium.math;

import com.google.common.collect.Lists;
import ua.mcchickenstudio.opencreative.utils.async.Pair;

import java.util.*;
import java.util.stream.Collectors;

public final class Statistics {

    public static double getVariance(final Collection<? extends Number> data) {
        int count = 0;

        double sum = 0.0;
        double variance = 0.0;

        final double average;

        for (final Number number : data) {
            sum += number.doubleValue();
            ++count;
        }

        average = sum / count;

        for (final Number number : data) {
            variance += Math.pow(number.doubleValue() - average, 2.0);
        }

        return variance / count;
    }

    public static double getMin(final Collection<? extends Number> collection) {
        double min = Double.MAX_VALUE;

        for (final Number number : collection) {
            min = Math.min(min, number.doubleValue());
        }

        return min;
    }

    public static double getMax(final Collection<? extends Number> collection) {
        double max = Double.MIN_VALUE;

        for (final Number number : collection) {
            max = Math.max(max, number.doubleValue());
        }

        return max;
    }

    public static double getStandardDeviation(final Collection<? extends Number> data) {
        final double variance = getVariance(data);

        return Math.sqrt(variance);
    }

    public static double getSkewness(final Collection<? extends Number> data) {
        double sum = 0;
        int count = 0;

        final List<Double> numbers = Lists.newArrayList();

        for (final Number number : data) {
            sum += number.doubleValue();
            ++count;

            numbers.add(number.doubleValue());
        }

        Collections.sort(numbers);

        final double mean = sum / count;
        final double median = (count % 2 != 0) ? numbers.get(count / 2) : (numbers.get((count - 1) / 2) + numbers.get(count / 2)) / 2;
        final double variance = getVariance(data);

        return 3 * (mean - median) / variance;
    }

    public static double getAverage(final Collection<? extends Number> data) {
        double sum = 0.0;

        for (final Number number : data) {
            sum += number.doubleValue();
        }
        final double result = sum / data.size();
        return (Double.isNaN(result)) ? 0 : result;
    }

    public static double getKurtosis(final Collection<? extends Number> data) {
        double sum = 0.0;
        int count = 0;

        for (final Number number : data) {
            sum += number.doubleValue();
            ++count;
        }

        if (count < 3.0) {
            return 0.0;
        }

        final double efficiencyFirst = count * (count + 1.0) / ((count - 1.0) * (count - 2.0) * (count - 3.0));
        final double efficiencySecond = 3.0 * Math.pow(count - 1.0, 2.0) / ((count - 2.0) * (count - 3.0));
        final double average = sum / count;

        double variance = 0.0;
        double varianceSquared = 0.0;

        for (final Number number : data) {
            variance += Math.pow(average - number.doubleValue(), 2.0);
            varianceSquared += Math.pow(average - number.doubleValue(), 4.0);
        }

        return efficiencyFirst * (varianceSquared / Math.pow(variance / sum, 2.0)) - efficiencySecond;
    }

    public static long getMode(final Collection<? extends Number> array) {
        long mode = (long) array.toArray()[0];
        long maxCount = 0;

        for (final Number value : array) {
            int count = 1;

            for (final Number i : array) {
                if (i.equals(value))
                    count++;

                if (count > maxCount) {
                    mode = (long) value;
                    maxCount = count;
                }
            }
        }

        return mode;
    }


    public static double getMedian(final List<Number> data) {
        if (data.size() % 2 == 0) {
            return (data.get(data.size() / 2).doubleValue() + data.get(data.size() / 2 - 1).doubleValue()) / 2;
        } else {
            return data.get(data.size() / 2).doubleValue();
        }
    }

    public static double getMedianDouble(final List<Double> data) {
        if (data.size() % 2 == 0) {
            return (data.get(data.size() / 2).doubleValue() + data.get(data.size() / 2 - 1).doubleValue()) / 2;
        } else {
            return data.get(data.size() / 2).doubleValue();
        }
    }

    public static int getIntQuotient(final float dividend, final float divisor) {
        final float ans = dividend / divisor;
        final float error = Math.max(dividend, divisor) * 1.0E-3F;

        return (int) (ans + error);
    }

    /**
     * @param collection The collection of numbers you want to analyze.
     * @return A pair of the high and low <a href="https://en.wikipedia.org/wiki/Outlier">outliers</a>.
     */
    public static Pair<List<Double>, List<Double>> getOutliers(final Collection<? extends Number> collection) {
        final List<Double> values = new ArrayList<>();

        for (final Number number : collection) {
            values.add(number.doubleValue());
        }

        final double q1 = getMedianDouble(values.subList(0, values.size() / 2));
        final double q3 = getMedianDouble(values.subList(values.size() / 2, values.size()));

        final double iqr = Math.abs(q1 - q3);
        final double lowThreshold = q1 - 1.5 * iqr, highThreshold = q3 + 1.5 * iqr;

        final Pair<List<Double>, List<Double>> tuple = new Pair<>(new ArrayList<>(), new ArrayList<>());

        for (final Double value : values) {
            if (value < lowThreshold) {
                tuple.getX().add(value);
            } else if (value > highThreshold) {
                tuple.getY().add(value);
            }
        }

        return tuple;
    }

    public static List<List<Double>> getOutliersSimply(final Collection<? extends Number> collection) {
        Pair<List<Double>, List<Double>> result = getOutliers(collection);
        return Arrays.asList(result.getX(), result.getY());
    }

    public static double calculatePercentile(final Collection<? extends Number> data, double percentile) {
        if (data.isEmpty()) {
            throw new IllegalArgumentException("Collection cannot be empty");
        }
        List<Double> sortedValues = data.stream()
                .map(Number::doubleValue)
                .sorted()
                .toList();

        int index = (int) Math.ceil(percentile / 100.0 * sortedValues.size()) - 1;
        if (index < 0) index = 0;
        if (index >= sortedValues.size()) index = sortedValues.size() - 1;

        return sortedValues.get(index);
    }

    public static double getShannonEntropy(final Collection<? extends Number> data) {
        Map<Double, Long> freqMap = data.stream()
                .collect(Collectors.groupingBy(Number::doubleValue, Collectors.counting()));

        double total = data.size();
        return -freqMap.values().stream()
                .mapToDouble(count -> (count / total) * (Math.log(count / total) / Math.log(2)))
                .sum();
    }

    public static double getLinearTrend(final List<? extends Number> data) {
        int n = data.size();
        if (n < 2) return 0;

        double sumX = 0, sumY = 0, sumXY = 0, sumX2 = 0;
        for (int i = 0; i < n; i++) {
            double x = i + 1;
            double y = data.get(i).doubleValue();
            sumX += x;
            sumY += y;
            sumXY += x * y;
            sumX2 += x * x;
        }

        return (n * sumXY - sumX * sumY) / (n * sumX2 - sumX * sumX);
    }

    public static double getQuantile(final Collection<? extends Number> data, double quantile) {
        List<Double> sorted = data.stream().map(Number::doubleValue).sorted().toList();
        int index = (int) Math.ceil(quantile * sorted.size()) - 1;
        return sorted.get(Math.max(0, Math.min(index, sorted.size() - 1)));
    }

    public static double getGiniIndex(final Collection<? extends Number> data) {
        List<Double> sorted = data.stream().map(Number::doubleValue).sorted().toList();
        int n = sorted.size();
        double sum = 0;
        for (Double aDouble : sorted) {
            for (int j = 0; j < n; j++) {
                sum += Math.abs(aDouble - sorted.get(j));
            }
        }

        double mean = getAverage(data);
        return mean == 0 ? 0 : sum / (2 * n * n * mean);
    }

    public static List<Float> getJiffDelta(List<? extends Number> data, int depth) {
        List<Float> result = new ArrayList<>();
        for (Number n : data) result.add(n.floatValue());
        for (int i = 0; i < depth; i++) {
            List<Float> calculate = new ArrayList<>();
            float old = Float.MIN_VALUE;
            for (float n : result) {
                if (old == Float.MIN_VALUE) {
                    old = n;
                    continue;
                }
                calculate.add(Math.abs(Math.abs(n) - Math.abs(old)));
                old = n;
            }
            result = new ArrayList<>(calculate);
        }
        return result;
    }

    public static int getDistinct(final Collection<? extends Number> data) {
        return (int) data.stream().distinct().count();
    }

    public static double getIQR(final Collection<? extends Number> data) {
        List<Double> sorted = data.stream().map(Number::doubleValue).sorted().collect(Collectors.toList());
        return calculatePercentile(sorted, 75) - calculatePercentile(sorted, 25);
    }
}
