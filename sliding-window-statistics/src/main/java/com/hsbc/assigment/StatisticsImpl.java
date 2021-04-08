package com.hsbc.assigment;

import java.util.*;

public class StatisticsImpl implements SlidingWindowStatistics.Statistics {

    double mean;
    private int count;
    private final Map<Long, Integer> possibleModeValues = new HashMap<>();


    public void collect(long value) {
        count++;
        double delta = value - mean;
        mean += delta / count;

        possibleModeValues.merge(value, 1, Integer::sum);

        // possibleModeValues.put(value, possibleModeValues.get(value) == null ? 1 : possibleModeValues.get(value) + 1);
    }


    public double getMean() {
        return mean;
    }

    public List<Long> getMode() {
        List<Long> modeValues = new ArrayList<>();
        try {
            long maxValueInMap = (Collections.max(possibleModeValues.values()));
            for (Map.Entry<Long, Integer> entry : possibleModeValues.entrySet()) {
                if (entry.getValue() == maxValueInMap) {
                    modeValues.add(entry.getKey());
                }
            }
        } catch (NoSuchElementException e) {
            System.out.println("No mode can be found for this series");
        }
        return modeValues;
    }

    public int getPercentile(List<Integer> latencies, double percentile) {
        Collections.sort(latencies);
        int index = (int) Math.ceil(percentile / 100.0 * latencies.size());
        return latencies.get(index - 1);

    }
}
