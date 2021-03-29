package com.factorypal.demo.controller;

import com.factorypal.demo.SpeedMertricsServiceApplication;
import com.factorypal.demo.exceptions.IDNotRegisteredException;
import com.factorypal.demo.exceptions.InvalidTimestampException;
import com.factorypal.demo.exceptions.StatusException;
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
import org.springframework.web.servlet.NoHandlerFoundException;

import javax.annotation.Resource;
import java.sql.Timestamp;
import java.time.ZoneId;
import java.util.List;

import static org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR;
import static org.springframework.http.HttpStatus.NOT_FOUND;
import static org.springframework.http.HttpStatus.OK;

@RestController
public class SpeedController {

    private final Logger logger = LoggerFactory.getLogger(SpeedMertricsServiceApplication.class);

    @Value("${appconf.periodMinutes}")
    private int periodMinutes;

    @Value("#{'${appconf.ids}'.split(',')}")
    private List<Long> knownIds;

    @Resource(name = "speedService")
    SpeedService speedService;


    @GetMapping("/metrics")
    public ResponseEntity<List<LineMetrics>> getMetrics() throws Exception {
        return new ResponseEntity<>(this.speedService.getLineMetrics(), OK);
    }

    @GetMapping("/metrics/{lineid}")
    public ResponseEntity<?> getMetrics(@PathVariable Long lineid) {
        if (!knownIds.contains(lineid)) {
            throw new IDNotRegisteredException(lineid);
        }

        return new ResponseEntity<>(this.speedService.getLineMetrics(lineid), OK);
    }

    @PostMapping("/linespeed")
    public ResponseEntity<?> newSpeedEntry(@RequestBody SpeedEntry entry) {
        if (!knownIds.contains(entry.getLine_id())) {
            throw new IDNotRegisteredException(entry.getLine_id());
        }

        Timestamp timestamp = new Timestamp(entry.getTimestamp());
        Timestamp currentTimestamp = Operations.getCurrentTimestamp(ZoneId.of("UTC"));

        long minutesDiff = Operations.compareTwoTimeStamps(timestamp, currentTimestamp);
        if (minutesDiff > periodMinutes || minutesDiff < 0) {
            throw new InvalidTimestampException(currentTimestamp.getTime(), timestamp.getTime(), periodMinutes);
        }

        this.speedService.addSpeedEntry(entry);

        return new ResponseEntity<>("Speed entry added", OK);
    }

    @ExceptionHandler({
            InvalidTimestampException.class,
            IDNotRegisteredException.class
    })
    public ResponseEntity<ErrorResponse> handleInvalidTimestampException(
            StatusException exception,
            WebRequest request
    ) {
        logger.error(exception.getMessage(), exception);
        return buildErrorResponse(
                exception.getMessage(),
                exception.getHttpStatus()
        );
    }

    @ExceptionHandler(NoHandlerFoundException.class)
    public ResponseEntity<ErrorResponse> handleNotFoundException(
            Exception exception,
            WebRequest request
    ) {
        logger.error("Page not found");
        return buildErrorResponse(
                exception.getMessage(),
                NOT_FOUND
        );
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleAllUncaughtException(
            Exception exception,
            WebRequest request
    ) {
        logger.error("An error occurred while processing the request", exception);
        return buildErrorResponse(
                "An error occurred while processing the request",
                INTERNAL_SERVER_ERROR
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
