package com.factorypal.demo.services;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

@Component("metricScheduler")
public class MetricScheduler {

    @Resource(name = "speedService")
    SpeedService speedService;

    @Scheduled(cron = "0 * * * * *")
    public void clearEntries() {
        this.speedService.cleanEntries();
    }
}
