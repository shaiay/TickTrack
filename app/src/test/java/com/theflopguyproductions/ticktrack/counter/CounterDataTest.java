package com.theflopguyproductions.ticktrack.counter;

import junit.framework.TestCase;

import java.time.Instant;
import java.time.temporal.ChronoUnit;

public class CounterDataTest extends TestCase {

    public void testGetAggregateCounts() {
        CounterData counterData = new CounterData();
        counterData.resetHour = 5;
        counterData.daysToAggregate = 1;
        Instant now = Instant.parse("2020-12-01T00:00:00Z");
        int days_counted = 12;
        int counters_per_day = 10;

        Instant start_counting = now.minus(days_counted, ChronoUnit.DAYS);
        for(int day = 0; day < days_counted; day++) {
            for(int counter = 0; counter < counters_per_day; counter++) {
                counterData.setCounterTimestamp(
                        start_counting.plus(
                                day, ChronoUnit.DAYS).toEpochMilli() +
                                counter * ChronoUnit.HOURS.getDuration().toMillis()
                );
            }
        }

        Integer[] histogram = counterData.getAggregateCounts(now.toEpochMilli());
        assertEquals(histogram.length, 10);
        for (int num=1; num<9; num++) {
            assertEquals(Integer.valueOf(10), histogram[num]);
        }
        assertEquals(Integer.valueOf(16), histogram[0]);
        assertEquals(Integer.valueOf(4), histogram[9]);
    }
}