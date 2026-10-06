package com.hospital.util;

import java.sql.Date;
import java.sql.Time;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

/**
 * Date, Time, and Duration formatting utilities.
 */
public final class DateTimeUtil {

    private static final SimpleDateFormat TIME_FMT = new SimpleDateFormat("hh:mm a");
    private static final SimpleDateFormat DATE_FMT = new SimpleDateFormat("yyyy-MM-dd");
    private static final SimpleDateFormat DATE_DISPLAY_FMT = new SimpleDateFormat("MMM dd, yyyy");
    private static final SimpleDateFormat DATETIME_FMT = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    private DateTimeUtil() {
        // Prevent instantiation
    }

    public static Date today() {
        return Date.valueOf(LocalDate.now());
    }

    public static Time nowTime() {
        return Time.valueOf(LocalTime.now());
    }

    public static Timestamp currentTimestamp() {
        return new Timestamp(System.currentTimeMillis());
    }

    public static String formatTime(Time time) {
        if (time == null) return "";
        synchronized (TIME_FMT) {
            return TIME_FMT.format(time);
        }
    }

    public static String formatDate(Date date) {
        if (date == null) return "";
        synchronized (DATE_DISPLAY_FMT) {
            return DATE_DISPLAY_FMT.format(date);
        }
    }

    public static String formatTimestamp(Timestamp ts) {
        if (ts == null) return "";
        synchronized (DATETIME_FMT) {
            return DATETIME_FMT.format(ts);
        }
    }

    public static int minutesBetween(Timestamp start, Timestamp end) {
        if (start == null || end == null) return 0;
        long diffMillis = end.getTime() - start.getTime();
        if (diffMillis <= 0) return 0;
        return (int) (diffMillis / (1000 * 60));
    }
}
