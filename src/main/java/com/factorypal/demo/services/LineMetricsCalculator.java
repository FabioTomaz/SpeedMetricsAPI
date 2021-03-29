package com.factorypal.demo.services;

import com.factorypal.demo.model.AggregatedMetrics;
import com.factorypal.demo.model.Metrics;
import lombok.Data;

import java.sql.Timestamp;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;

@Data
public class LineMetricsCalculator {

    private SortedMap<Timestamp, AggregatedMetrics> speedHistory;

    public LineMetricsCalculator() {
        this.speedHistory= new TreeMap<>();
    }

    // Takes O(log(n))
    public void addSpeedEntry(Timestamp timestamp, double speed) {
        if (!this.speedHistory.containsKey(timestamp)) {
            this.speedHistory.put(timestamp, new AggregatedMetrics(speed, speed, speed, 1));
        } else {
            if(speed<this.speedHistory.get(timestamp).getMin()) {
                this.speedHistory.get(timestamp).setMin(speed);
            }

            if(speed>this.speedHistory.get(timestamp).getMax()) {
                this.speedHistory.get(timestamp).setMax(speed);
            }

            this.speedHistory.get(timestamp).setSum(this.speedHistory.get(timestamp).getSum()+speed);
            this.speedHistory.get(timestamp).setTotal(this.speedHistory.get(timestamp).getTotal()+1);
        }
    }

    public Metrics getMetrics(Timestamp from, Timestamp to) {
        SortedMap<Timestamp, AggregatedMetrics> subMap= this.speedHistory.subMap(from, to);
        if (subMap.isEmpty()) {
            return new Metrics(0.0, 0.0, 0.0);
        }

        double min = Integer.MAX_VALUE;
        double max = 0;
        double sum = 0;
        double total = 0;
        for(Map.Entry<Timestamp, AggregatedMetrics> entry: subMap.entrySet()) {
            if (entry.getValue().getMin()<min) {
                min=entry.getValue().getMin();
            }
            if (entry.getValue().getMax()>max) {
                max=entry.getValue().getMax();
            }
            sum+=entry.getValue().getSum();
            total+=entry.getValue().getTotal();
        }

        return new Metrics(min, max, sum/total);
    }

}
