package com.factorypal.demo.services;

import com.factorypal.demo.model.LineMetrics;
import com.factorypal.demo.model.Metrics;
import com.factorypal.demo.model.SpeedEntry;
import com.factorypal.demo.util.Operations;

import java.sql.Timestamp;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class SpeedService {

    private final int period;

    private final Map<Long, LineMetricsCalculator> lineMetrics;

    public SpeedService(int period) {
        this.lineMetrics = new HashMap<>();
        this.period = period;
    }

    public synchronized void addEntry(SpeedEntry speedEntry) {
        if (!lineMetrics.containsKey(speedEntry.getLine_id())) {
            lineMetrics.put(speedEntry.getLine_id(), new LineMetricsCalculator());
        }

        lineMetrics.get(speedEntry.getLine_id()).addSpeedEntry(
                Operations.truncateTimestamp(new Timestamp(speedEntry.getTimestamp())),
                speedEntry.getSpeed()
        );
    }

    public synchronized List<LineMetrics> getMetrics() {
        return this.lineMetrics.keySet().stream()
                .map(lineMetricsCalculator -> new LineMetrics(
                        lineMetricsCalculator,
                        this.getMetrics(lineMetricsCalculator)
                )).collect(Collectors.toList());
    }

    public synchronized Metrics getMetrics(long lineId) {
        if(!lineMetrics.containsKey(lineId)) {
            return null;
        }

        Timestamp[] interval = Operations.getTimestampInterval(period);
        return lineMetrics.get(lineId).getMetrics(interval[0], interval[1]);
    }
}
