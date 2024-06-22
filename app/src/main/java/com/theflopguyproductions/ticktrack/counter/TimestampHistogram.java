package com.theflopguyproductions.ticktrack.counter;

import java.time.Duration;
import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

public class TimestampHistogram {

    public static Map<Long, Integer> createHistogram(Iterable<Instant> timestamps, Instant initialTime, Duration timeDelta) {
        Map<Long, Integer> histogram = new HashMap<>();
        for (Instant timestamp : timestamps) {
            long binIndex = Duration.between(initialTime, timestamp).toMillis() / timeDelta.toMillis();
            histogram.put(binIndex, histogram.getOrDefault(binIndex, 0) + 1);
        }
        return histogram;
    }
}