package com.hsbc.assigment;


import java.util.List;

public interface SlidingWindowStatistics {

    void add(long windowSize, Item item);


    // subscriber will have a callback that'll deliver a Stat istics instance (push)
    void subscribeForStatistics();

    // get latest statistics (poll)
    Statistics getLatestStatistics();


    public interface Statistics {
        double getMean();
        List<Long> getMode();
        int getPercentile(List<Integer> latencies, double percentile);
        void collect(long millis);
    }

    
}
