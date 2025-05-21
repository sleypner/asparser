package com.sleypner.parserarticles.parsing;

import manifold.ext.rt.api.Extension;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAccessor;
@Extension
public class DateFormat {
    static Logger logger = LoggerFactory.getLogger(DateFormat.class);
    public static DateTimeFormatter dateFormat = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    public static TemporalAccessor format(String date){
        return dateFormat.parse(date);
    }
    public static String format(TemporalAccessor date){
        return dateFormat.format(date);
    }

}
