package com.factorypal.demo.services;

import com.factorypal.demo.model.Metrics;
import lombok.Data;

import java.sql.Timestamp;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;

// 1 2 2 3 4 5 6
@Data
public class LineMetrics {

    private SortedMap<Timestamp, Double> speedHistory;

    private double minSpeed=0;

    private double maxSpeed=0;

    private double speedSum=0;
    private int speedTotal=0;

    public LineMetrics() {
        this.speedHistory= new TreeMap<>();
    }

    public void addSpeedEntry(Timestamp timestamp, double speed) {
        if(speed<minSpeed) {
            this.minSpeed=speed;
        }

        if(speed>maxSpeed) {
            this.maxSpeed=speed;
        }

        this.speedSum+=speed;
        this.speedTotal+=1;

        // O(log(n))
        this.speedHistory.put(timestamp, speed);
    }

    public void removeSpeedEntries(Timestamp until) {
        for(Map.Entry<Timestamp, Double> speedEntry: speedHistory.entrySet()) {
            if (speedEntry.getKey().compareTo(until)<0) {
                speedHistory.remove(speedEntry.getKey());
            } else {
                break;
            }
        }
    }

    public Metrics getMetrics(Timestamp from, Timestamp to) {
        SortedMap<Timestamp, Double> subMap= this.speedHistory.subMap(from, to);
        double min = Integer.MAX_VALUE;
        double max = 0;
        double sum = 0;
        double total = 0;
        for(Map.Entry<Timestamp, Double> entry: subMap.entrySet()) {
            if (entry.getValue()<min) {
                min=entry.getValue();
            }
            if (entry.getValue()>max) {
                max=entry.getValue();
            }
            sum+=entry.getValue();
            total+=1;
        }

        return new Metrics(min, max, sum/total);
    }

}
