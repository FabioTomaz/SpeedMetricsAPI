package com.factorypal.demo.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Metrics {
    private Double min;
    private Double max;
    private Double avg;
}
