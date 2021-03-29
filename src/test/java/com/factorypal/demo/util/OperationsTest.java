package com.factorypal.demo.util;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Calendar;

import static junit.framework.TestCase.assertEquals;

class OperationsTest {

    private LocalDateTime localDate;

    @BeforeEach
    public void setup() {
        LocalDateTime ldt = LocalDateTime.now();
        ZonedDateTime zdt = ZonedDateTime.of(ldt, ZoneId.systemDefault());
        ZonedDateTime gmt = zdt.withZoneSameInstant(ZoneId.of("UTC"));
        this.localDate = gmt.toLocalDateTime();
    }

    @Test
    void compareTwoEqualTimeStampsTest() {
        assertEquals(0, Operations.compareTwoTimeStamps(
                Timestamp.valueOf(this.localDate),
                Timestamp.valueOf(this.localDate)
        ));
    }

    @Test
    void compareTwoTimeStampsTest1() {
        assertEquals(60, Operations.compareTwoTimeStamps(
                Timestamp.valueOf(this.localDate),
                Timestamp.valueOf(this.localDate.plus(60, ChronoUnit.MINUTES))
        ));
    }

    @Test
    void compareTwoTimeStampsTest2() {
        assertEquals(4320, Operations.compareTwoTimeStamps(
                Timestamp.valueOf(this.localDate),
                Timestamp.valueOf(this.localDate.plus(3, ChronoUnit.DAYS))
        ));
    }

    @Test
    void truncateTimestampTest() {
        Calendar cal1 = Calendar.getInstance(); // locale-specific
        cal1.set(Calendar.YEAR, 2021);
        cal1.set(Calendar.MONTH, 1);
        cal1.set(Calendar.DATE, 20);
        cal1.set(Calendar.HOUR, 10);
        cal1.set(Calendar.MINUTE, 35);
        cal1.set(Calendar.SECOND, 55);
        cal1.set(Calendar.MILLISECOND, 100);

        Calendar cal2 = Calendar.getInstance(); // locale-specific
        cal2.set(Calendar.YEAR, 2021);
        cal2.set(Calendar.MONTH, 1);
        cal2.set(Calendar.DATE, 20);
        cal2.set(Calendar.HOUR, 10);
        cal2.set(Calendar.MINUTE, 35);
        cal2.set(Calendar.SECOND, 0);
        cal2.set(Calendar.MILLISECOND, 0);

        assertEquals(
                new Timestamp(cal2.getTimeInMillis()),
                Operations.truncateTimestamp(new Timestamp(cal1.getTimeInMillis()))
        );
    }

}
