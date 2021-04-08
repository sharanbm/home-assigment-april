package com.hsbc.assigment;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.assertEquals;

/**
 * Unit test for simple App.
 */

public class StatisticsTest {

    private static final List<Integer> TEST_SET = Arrays.asList(7373, 7137, 7137, 7137, 7125, 7588, 7809, 2406);
    private static final List<Integer> EMPTY_SET = new ArrayList<>();

    private SlidingWindowStatistics.Statistics createAndLoad(List<Integer> values) {
        SlidingWindowStatistics.Statistics stats = new StatisticsImpl();
        for (long value : values) {
            stats.collect(value);
        }
        return stats;
    }

    SlidingWindowStatistics.Statistics emptyStatistics;
    SlidingWindowStatistics.Statistics loadedStatistics;
    SlidingWindowStatisticsImpl slidingStatistics;

    @Before
    public void setup() {
        emptyStatistics = createAndLoad(EMPTY_SET);
        loadedStatistics = createAndLoad(TEST_SET);
        slidingStatistics = new SlidingWindowStatisticsImpl(1000);
    }

    @Test
    public void shouldReturnZeroIfEmpty() throws Exception {
        assertEquals(0, slidingStatistics.getSum(0), 0);
        assertEquals(0, slidingStatistics.getCount(0));
    }

    @Test
    public void shouldReturnSum() throws Exception {
        slidingStatistics.add(0, new Item(0, 100));
        slidingStatistics.add(0, new Item(0, 100));
        assertEquals(200, slidingStatistics.getSum(500), 0);
        assertEquals(2, slidingStatistics.getCount(500));
    }

    @Test
    public void shouldSweepOldItems() throws Exception {
        slidingStatistics.add(0, new Item(0, 100));
        slidingStatistics.add(0, new Item(0, 100));
        slidingStatistics.add(0, new Item(1000, 100));
        slidingStatistics.add(0, new Item(1000, 100));
        // First two added items should be dropped
        assertEquals(200, slidingStatistics.getSum(1500), 0);
        assertEquals(2, slidingStatistics.getCount(1500));
    }

    @Test
    public void testMeanForEmptyList() {
        assertEquals(0.0, emptyStatistics.getMean(), 0.0);
    }

    @Test
    public void testModeForEmptyList() {
        Assert.assertTrue(emptyStatistics.getMode().isEmpty());
    }

    @Test
    public void testMeanForValidList() {
        assertEquals(6714.0, loadedStatistics.getMean(), 0.0);
    }

    @Test
    public void testModeForValidList() {
        Assert.assertTrue(loadedStatistics.getMode().size() > 0);
    }

    @Test
    public void testPercentile() {
        assertEquals(7125, loadedStatistics.getPercentile(TEST_SET, 25));
        assertEquals(7137, loadedStatistics.getPercentile(TEST_SET, 50));
        assertEquals(7373, loadedStatistics.getPercentile(TEST_SET, 75));
        assertEquals(7809, loadedStatistics.getPercentile(TEST_SET, 100));
    }


}
