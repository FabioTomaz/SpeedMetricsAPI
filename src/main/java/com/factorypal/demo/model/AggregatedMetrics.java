package com.factorypal.demo.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AggregatedMetrics {
    private Double max;
    private Double sum;
    private Double min;
    private int total;
}
