package com.factorypal.demo.services;

import com.factorypal.demo.model.Metrics;
import com.factorypal.demo.model.SpeedEntry;
import com.factorypal.demo.util.Operations;

import java.sql.Timestamp;
import java.util.HashMap;
import java.util.Map;

public class SpeedService {

    private final int period;

    private final Map<Long, LineMetrics> lineMetrics;

    public SpeedService(int period) {
        this.lineMetrics = new HashMap<>();
        this.period = period;
    }

    public synchronized void addEntry(SpeedEntry speedEntry) {
        if (!lineMetrics.containsKey(speedEntry.getLine_id())) {
            lineMetrics.put(speedEntry.getLine_id(), new LineMetrics());
        }

        lineMetrics.get(speedEntry.getLine_id()).addSpeedEntry(
                new Timestamp(speedEntry.getTimestamp()),
                speedEntry.getSpeed()
        );
    }

    public synchronized Map<Long, LineMetrics> getMetrics() {
        return this.lineMetrics;
    }

    public synchronized Metrics getMetrics(long lineId) {
        Timestamp[] interval = Operations.getTimestampInterval(period);
        return lineMetrics.get(lineId).getMetrics(interval[0], interval[1]);
    }
}
