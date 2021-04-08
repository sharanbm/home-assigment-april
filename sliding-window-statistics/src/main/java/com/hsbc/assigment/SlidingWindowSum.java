package com.hsbc.assigment;

import java.math.BigDecimal;
import java.util.PriorityQueue;

public class SlidingWindowSum {
    final long windowSize;
    final PriorityQueue<Item> window = new PriorityQueue<>(Item.timestampComparator);
    private BigDecimal sum = BigDecimal.ZERO;

    public SlidingWindowSum(long windowSize) {
        this.windowSize = windowSize;
    }

    public void add(long now, Item item) {
        window.add(item);
        sum = sum.add(BigDecimal.valueOf(item.amount));
        sweepOldItems(now);
    }

    public double getSum(long now) {
        sweepOldItems(now);
        return sum.doubleValue();
    }

    public int getCount(long now) {
        sweepOldItems(now);
        return window.size();
    }

    private void sweepOldItems(long now) {
        while (!window.isEmpty() && window.peek().timestamp + windowSize < now) {
            Item item = window.poll();
            sum = sum.subtract(BigDecimal.valueOf(item.amount));
        }
    }
}
