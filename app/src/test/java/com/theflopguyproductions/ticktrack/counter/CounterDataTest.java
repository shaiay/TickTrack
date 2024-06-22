package com.theflopguyproductions.ticktrack.counter;

import static java.lang.System.*;

import junit.framework.TestCase;

import java.time.Instant;
import java.time.temporal.ChronoUnit;

public class CounterDataTest extends TestCase {

    public void testGetAggregateCounts() {
        CounterData counterData = new CounterData();
        counterData.resetHour = 23;
        counterData.daysToAggregate = 1;
        Instant now = Instant.ofEpochMilli(currentTimeMillis());
        int days_counted = 12;
        int counters_per_day = 10;

        Instant start_counting = now.minus(days_counted, ChronoUnit.DAYS);
        for(int day = 0; day < days_counted; day++) {
            for(int counter = 0; counter < counters_per_day; counter++) {
                counterData.setCounterTimestamp(start_counting.plus(day, ChronoUnit.DAYS).toEpochMilli()+ counter * ChronoUnit.HOURS.getDuration().toMillis());
            }
        }

        Integer[] histogram = counterData.getAggregateCounts();
        assertEquals(histogram.length, 10);
    }
}