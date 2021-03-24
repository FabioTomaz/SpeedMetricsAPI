package com.factorypal.demo.model;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class LineMetrics {
    private long line_id;

    private Metrics metrics;
}
