package com.factorypal.demo.services;

import com.factorypal.demo.model.LineMetrics;
import com.factorypal.demo.model.Metrics;
import com.factorypal.demo.model.SpeedEntry;
import com.factorypal.demo.util.Operations;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.time.ZoneId;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


@Service
public class SpeedService {

    private final Logger logger = LoggerFactory.getLogger(SpeedService.class);

    private final Map<Long, LineMetricsCalculator> lineMetrics;

    @Value("${appconf.periodMinutes}")
    private int periodMinutes;

    public SpeedService() {
        this.lineMetrics = new HashMap<>();
    }

    public synchronized void addSpeedEntry(SpeedEntry speedEntry) {
        if (!lineMetrics.containsKey(speedEntry.getLine_id())) {
            lineMetrics.put(speedEntry.getLine_id(), new LineMetricsCalculator(periodMinutes));
        }

        lineMetrics.get(speedEntry.getLine_id()).addSpeedEntry(
                Operations.truncateTimestamp(new Timestamp(speedEntry.getTimestamp())),
                speedEntry.getSpeed()
        );
    }

    public synchronized List<LineMetrics> getLineMetrics() {
        return this.lineMetrics.keySet().stream()
                .map(lineMetricsCalculator -> new LineMetrics(
                        lineMetricsCalculator,
                        this.getLineMetrics(lineMetricsCalculator)
                )).collect(Collectors.toList());
    }

    public synchronized Metrics getLineMetrics(long lineId) {
        Timestamp currentTimestamp = Operations.getCurrentTimestamp(ZoneId.of("UTC"));
        return lineMetrics.getOrDefault(lineId, new LineMetricsCalculator(periodMinutes)).getMetrics(currentTimestamp);
    }

    public synchronized void cleanEntries() {
        logger.info("Removing older entries...");

        Timestamp currentTimestamp = Operations.getCurrentTimestamp(ZoneId.of("UTC"));
        for (Map.Entry<Long, LineMetricsCalculator> entry : lineMetrics.entrySet()) {
            entry.getValue().filter(currentTimestamp);
        }
    }
}
