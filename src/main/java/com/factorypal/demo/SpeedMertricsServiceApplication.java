package com.factorypal.demo;

import com.factorypal.demo.conf.ConfigProperties;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

@SpringBootApplication
@EnableConfigurationProperties(ConfigProperties.class)
public class SpeedMertricsServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(SpeedMertricsServiceApplication.class, args);
    }

}
