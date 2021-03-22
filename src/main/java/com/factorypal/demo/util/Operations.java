package com.factorypal.demo.util;

import java.sql.Time;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;

public class Operations {
    public static long compareTwoTimeStamps(java.sql.Timestamp currentTime, java.sql.Timestamp oldTime) {
        long milliseconds1 = oldTime.getTime();
        long milliseconds2 = currentTime.getTime();

        long diff = milliseconds2 - milliseconds1;

        return diff / (60 * 1000);
    }

    public static Timestamp getCurrentTimestamp(ZoneId zoneId) {
        LocalDateTime ldt = LocalDateTime.now();
        ZonedDateTime zdt = ZonedDateTime.of(ldt, ZoneId.systemDefault());
        ZonedDateTime gmt = zdt.withZoneSameInstant(zoneId);
        return Timestamp.valueOf(gmt.toLocalDateTime());
    }

    public static Timestamp[] getTimestampInterval(Timestamp toTimestamp, int minutes) {
        return new Timestamp[]{new Timestamp(toTimestamp.getTime() - (minutes * 60 * 1000)), toTimestamp};
    }

    public static Timestamp[] getTimestampInterval(int minutes) {
        Timestamp timestamp = getCurrentTimestamp(ZoneId.of("UTC"));

        return getTimestampInterval(timestamp, minutes);
    }
}
