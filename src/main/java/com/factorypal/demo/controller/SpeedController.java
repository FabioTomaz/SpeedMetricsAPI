package com.factorypal.demo.controller;

import com.factorypal.demo.SpeedMertricsServiceApplication;
import com.factorypal.demo.model.ErrorResponse;
import com.factorypal.demo.model.LineMetrics;
import com.factorypal.demo.model.SpeedEntry;
import com.factorypal.demo.services.SpeedService;
import com.factorypal.demo.util.Operations;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.NoHandlerFoundException;

import java.sql.Timestamp;
import java.time.ZoneId;
import java.util.List;

import static com.factorypal.demo.util.Constants.KNOWN_IDS;
import static org.springframework.http.HttpStatus.NOT_FOUND;
import static org.springframework.http.HttpStatus.NO_CONTENT;
import static org.springframework.http.HttpStatus.OK;

@RestController
public class SpeedController {

    public static final String TRACE = "trace";

    private final Logger logger = LoggerFactory.getLogger(SpeedMertricsServiceApplication.class);

    private final SpeedService speedService;

    @Value("${appconf.periodMinutes}")
    private int periodMinutes;

    public SpeedController() {
        this.speedService = new SpeedService(periodMinutes);
    }

    @GetMapping("/metrics")
    public ResponseEntity<List<LineMetrics>> getMetrics() {
        return new ResponseEntity<>(this.speedService.getMetrics(), OK);
    }

    @GetMapping("/metrics/{lineid}")
    public ResponseEntity<?> getMetrics(@PathVariable Long lineid) {
        if (!KNOWN_IDS.contains(lineid)) {
            return new ResponseEntity<>(NOT_FOUND);
        }

        return new ResponseEntity<>(this.speedService.getMetrics(lineid), OK);
    }

    @PostMapping("/linespeed")
    public ResponseEntity<?> newSpeedEntry(@RequestBody SpeedEntry entry) {
        if (!KNOWN_IDS.contains(entry.getLine_id())) {
            return new ResponseEntity<>("Unable to find resource", NOT_FOUND);
        }

        Timestamp timestamp = new Timestamp(entry.getTimestamp());
        Timestamp currentTimestamp = Operations.getCurrentTimestamp(ZoneId.of("UTC"));

        logger.info("CURRENT TIMESTAMP: " + currentTimestamp.toString());

        long minutesDiff = Operations.compareTwoTimeStamps(timestamp, currentTimestamp);

        if (minutesDiff > periodMinutes || minutesDiff < 0) {
            throw new ResponseStatusException(NO_CONTENT, "Timestamp must be no more than 60 minutes from now");
        }

        logger.info(entry.toString());

        this.speedService.addEntry(entry);

        return new ResponseEntity<>("Speed entry added", NOT_FOUND);
    }

    @ExceptionHandler(NoHandlerFoundException.class)
    public ResponseEntity<ErrorResponse> handleAllUncaughtException(
            Exception exception,
            WebRequest request
    ) {
        logger.error("Unknown error occurred", exception);
        return buildErrorResponse(
                exception.getMessage(),
                NOT_FOUND
        );
    }

    private ResponseEntity<ErrorResponse> buildErrorResponse(
            String message,
            HttpStatus httpStatus
    ) {
        ErrorResponse errorResponse = new ErrorResponse(httpStatus.value(), message);

        return ResponseEntity.status(httpStatus).body(errorResponse);
    }
}
