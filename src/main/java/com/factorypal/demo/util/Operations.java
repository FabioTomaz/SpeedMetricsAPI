package com.factorypal.demo.util;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Calendar;

public class Operations {
    public static long compareTwoTimeStamps(java.sql.Timestamp currentTime, java.sql.Timestamp oldTime) {
        long milliseconds1 = currentTime.getTime();
        long milliseconds2 = oldTime.getTime();

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

    public static Timestamp truncateTimestamp(Timestamp timestamp) {
        Calendar cal = Calendar.getInstance(); // locale-specific
        cal.setTime(timestamp);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);
        return new Timestamp(cal.getTimeInMillis());
    }
}
