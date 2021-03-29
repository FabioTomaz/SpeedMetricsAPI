package com.factorypal.demo.exceptions;

import org.springframework.http.HttpStatus;


public class InvalidTimestampException extends StatusException {
    private final long currentTimestamp;
    private final long requestTimestamp;
    private final int period;

    public InvalidTimestampException(long currentTimestamp, long requestTimestamp, int period) {
        super(HttpStatus.NO_CONTENT);
        this.currentTimestamp = currentTimestamp;
        this.requestTimestamp = requestTimestamp;
        this.period = period;
    }

    @Override
    public String getMessage() {
        if (requestTimestamp > currentTimestamp) {
            return String.format(
                    "Request timestamp (%d) is older than the current timestamp (%d) minus %d minutes",
                    requestTimestamp,
                    currentTimestamp,
                    period
            );
        } else {
            return String.format(
                    "Timestamps from the future are not allowed. Current timestamp (%d) is smaller than the request timestamp (%d)",
                    currentTimestamp,
                    requestTimestamp
            );
        }
    }
}