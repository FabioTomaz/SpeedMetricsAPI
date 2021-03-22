package com.factorypal.demo.controller;

import com.factorypal.demo.SpeedMertricsServiceApplication;
import com.factorypal.demo.model.Metrics;
import com.factorypal.demo.model.SpeedEntry;
import com.factorypal.demo.services.LineMetrics;
import com.factorypal.demo.services.SpeedService;
import com.factorypal.demo.util.Operations;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import java.sql.Timestamp;
import java.time.ZoneId;
import java.util.Map;

import static com.factorypal.demo.util.Constants.KNOWN_IDS;
import static org.springframework.http.HttpStatus.NOT_FOUND;
import static org.springframework.http.HttpStatus.NO_CONTENT;

@RestController
public class SpeedController {

    private final Logger logger = LoggerFactory.getLogger(SpeedMertricsServiceApplication.class);

    private final SpeedService speedService;

    public SpeedController() {
        this.speedService = new SpeedService(60);
    }

    @GetMapping("/metrics")
    public Map<Long, LineMetrics> getMetrics() {
        return this.speedService.getMetrics();
    }

    @GetMapping("/metrics/{lineid}")
    public Metrics getMetrics(@PathVariable Long lineid) {
        if (!KNOWN_IDS.contains(lineid)) {
            throw new ResponseStatusException(NOT_FOUND, "Unable to find resource");
        }

        return this.speedService.getMetrics(lineid);
    }

    @PostMapping("/linespeed")
    public void newSpeedEntry(@RequestBody SpeedEntry entry) {
        if (!KNOWN_IDS.contains(entry.getLine_id())) {
            throw new ResponseStatusException(NOT_FOUND, "Unable to find resource");
        }

        Timestamp timestamp = new Timestamp(entry.getTimestamp());
        Timestamp currentTimestamp = Operations.getCurrentTimestamp(ZoneId.of("UTC"));

        logger.info("CURRENT TIMESTAMP: " + currentTimestamp.toString());

        long minutesDiff = Operations.compareTwoTimeStamps(currentTimestamp, timestamp);

        if (minutesDiff > 60 || minutesDiff < 0) {
            throw new ResponseStatusException(NO_CONTENT, "Unable to find resource");
        }

        logger.info(entry.toString());

        this.speedService.addEntry(entry);
    }

}
