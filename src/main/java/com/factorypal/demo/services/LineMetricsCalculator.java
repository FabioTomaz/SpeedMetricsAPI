package com.factorypal.demo.services;

import com.factorypal.demo.model.AggregatedMetrics;
import com.factorypal.demo.model.Metrics;
import com.factorypal.demo.util.Operations;
import lombok.Data;
import org.springframework.beans.factory.annotation.Value;

import java.sql.Timestamp;
import java.util.HashMap;
import java.util.Map;

@Data
public class LineMetricsCalculator {

    private long periodMinutes;

    private Map<Timestamp, AggregatedMetrics> speedHistory;

    public LineMetricsCalculator(long periodMinutes) {
        this.periodMinutes=periodMinutes;
        this.speedHistory = new HashMap<>();
    }

    // Takes O(log(n))
    public void addSpeedEntry(Timestamp timestamp, double speed) {
        if (!this.speedHistory.containsKey(timestamp)) {
            this.speedHistory.put(timestamp, new AggregatedMetrics(speed, speed, speed, 1));
        } else {
            if (speed < this.speedHistory.get(timestamp).getMin()) {
                this.speedHistory.get(timestamp).setMin(speed);
            }

            if (speed > this.speedHistory.get(timestamp).getMax()) {
                this.speedHistory.get(timestamp).setMax(speed);
            }

            this.speedHistory.get(timestamp).setSum(this.speedHistory.get(timestamp).getSum() + speed);
            this.speedHistory.get(timestamp).setTotal(this.speedHistory.get(timestamp).getTotal() + 1);
        }
    }

    public Metrics getMetrics(Timestamp currentTimestamp) {
        if (speedHistory.isEmpty()) {
            return new Metrics(0.0, 0.0, 0.0);
        }

        double min = Integer.MAX_VALUE;
        double max = 0;
        double sum = 0;
        double total = 0;
        for (Map.Entry<Timestamp, AggregatedMetrics> entry : speedHistory.entrySet()) {
            long diff = Operations.compareTwoTimeStamps(entry.getKey(), currentTimestamp);
            if (diff > periodMinutes || diff < 0) {
                continue;
            }

            if (entry.getValue().getMin() < min) {
                min = entry.getValue().getMin();
            }
            if (entry.getValue().getMax() > max) {
                max = entry.getValue().getMax();
            }
            sum += entry.getValue().getSum();
            total += entry.getValue().getTotal();
        }

        return new Metrics(min, max, sum / total);
    }

    public void filter(Timestamp currentTimestamp) {
        for (Map.Entry<Timestamp, AggregatedMetrics> entry : this.speedHistory.entrySet()) {
            long diff = Operations.compareTwoTimeStamps(entry.getKey(), currentTimestamp);
            if (diff > periodMinutes || diff < 0) {
                this.speedHistory.remove(entry.getKey());
            }
        }
    }
}