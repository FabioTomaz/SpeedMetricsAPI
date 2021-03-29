package com.factorypal.demo.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AggregatedMetrics {
    private double min;
    private double max;
    private double sum;
    private int total;
}
