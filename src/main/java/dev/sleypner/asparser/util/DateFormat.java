package dev.sleypner.asparser.util;


import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAccessor;

public class DateFormat {
    public static DateTimeFormatter dateFormat = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    public static TemporalAccessor format(String date) {
        return dateFormat.parse(date);
    }

    public static String format(TemporalAccessor date) {
        return dateFormat.format(date);
    }

}
