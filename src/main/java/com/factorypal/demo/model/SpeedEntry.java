package com.factorypal.demo.model;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SpeedEntry {

    private Long line_id;
    private Double speed;
    private Long timestamp;

}