package com.theflopguyproductions.ticktrack.counter;

import java.time.Duration;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Map;

public class CounterData implements Comparable<CounterData>{

    private static final int MAX_TIMESTAMP_COUNT = 100;
    int counterFlag;
    long counterValue, counterSignificantCount;
    boolean counterSignificantExist, counterSwipeMode, counterPersistentNotification, isNegativeAllowed;
    String counterLabel, counterID;
    long counterTimestamp;
    TimestampFifoQueue timestamps = new TimestampFifoQueue(MAX_TIMESTAMP_COUNT);
    int daysToAggregate;
    int resetHour;


    public boolean isNegativeAllowed() {
        return isNegativeAllowed;
    }

    public void setNegativeAllowed(boolean negativeAllowed) {
        isNegativeAllowed = negativeAllowed;
    }

    public String getCounterID() {
        return counterID;
    }

    public void setCounterID(String counterID) {
        this.counterID = counterID;
    }

    public boolean isCounterPersistentNotification() {
        return counterPersistentNotification;
    }

    public void setCounterPersistentNotification(boolean counterPersistentNotification) {
        this.counterPersistentNotification = counterPersistentNotification;
    }

    public boolean isCounterSwipeMode() {
        return counterSwipeMode;
    }

    public void setCounterSwipeMode(boolean counterSwipeMode) {
        this.counterSwipeMode = counterSwipeMode;
    }

    public long getCounterSignificantCount() {
        return counterSignificantCount;
    }

    public void setCounterSignificantCount(long counterSignificantCount) {
        this.counterSignificantCount = counterSignificantCount;
    }

    public boolean isCounterSignificantExist() {
        return counterSignificantExist;
    }

    public void setCounterSignificantExist(boolean counterSignificantExist) {
        this.counterSignificantExist = counterSignificantExist;
    }

    public long getCounterValue() {
        return counterValue;
    }

    public void setCounterValue(long counterValue) {
        this.counterValue = counterValue;
    }

    public int getCounterFlag() {
        return counterFlag;
    }

    public void setCounterFlag(int counterFlag) {
        this.counterFlag = counterFlag;
    }

    public String getCounterLabel() {
        return counterLabel;
    }

    public void setCounterLabel(String counterLabel) {
        this.counterLabel = counterLabel;
    }

    public long getCounterTimestamp() {
        return counterTimestamp;
    }

    public void setCounterTimestamp(long counterTimestamp) {
        this.counterTimestamp = counterTimestamp;
        timestamps.addTimestamp(Instant.ofEpochMilli(counterTimestamp));
    }

    public Integer[] getAggregateCounts(long nowTimestamp) {
        LocalDateTime now = LocalDateTime.ofInstant(Instant.ofEpochMilli(nowTimestamp),ZoneId.systemDefault());
        int current_hour = now.getHour();
        LocalDateTime t0;
        if(current_hour>=resetHour) {
            t0 = LocalDateTime.of(now.toLocalDate(), LocalTime.of(resetHour, 0, 0));
        }
        else {
            t0 = LocalDateTime.of(now.toLocalDate().minusDays(1), LocalTime.of(resetHour, 0, 0));
        }
        ZonedDateTime zt0 = t0.atZone(ZoneId.systemDefault());
        Map<Long, Integer> histogram = TimestampHistogram.createHistogram(
                timestamps.getAsIterable(), zt0.toInstant(), Duration.ofDays(daysToAggregate)
        );

        Integer[] result = new Integer[10];
        for(long i=0; i<10; i++) {
            result[(int) i] = histogram.getOrDefault(-i, 0);
        }
        return result;
    }

    @Override
    public int compareTo(CounterData counterData) {
        int check = 1;
        if(this.getCounterTimestamp()==counterData.getCounterTimestamp()){
            check=0;
        } else if (this.getCounterTimestamp()<counterData.getCounterTimestamp()){
            check=-1;
        }

        if(check<=0){
            if(check==0){
                return 0;
            } else {
                return 1;
            }
        }
        return -1;
    }

}
