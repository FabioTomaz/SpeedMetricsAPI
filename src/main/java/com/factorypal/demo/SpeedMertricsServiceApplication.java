package com.factorypal.demo;

import com.factorypal.demo.conf.ConfigProperties;
import com.factorypal.demo.services.SpeedService;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.event.EventListener;

import javax.annotation.Resource;

@SpringBootApplication
@EnableConfigurationProperties(ConfigProperties.class)
public class SpeedMertricsServiceApplication {

    @Resource(name = "speedService")
    SpeedService speedService;

    public static void main(String[] args) {
        SpringApplication.run(SpeedMertricsServiceApplication.class, args);
    }

    @EventListener(ApplicationReadyEvent.class)
    public void doSomethingAfterStartup() {
        speedService.cleanEntries();
    }
}
