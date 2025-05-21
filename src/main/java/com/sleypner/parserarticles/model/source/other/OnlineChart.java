package com.sleypner.parserarticles.model.source.other;

import lombok.Getter;
import lombok.Setter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Field;
import java.sql.Timestamp;
import java.util.StringJoiner;

@Setter
@Getter
public class OnlineChart {

    private Logger logger = LoggerFactory.getLogger(OnlineChart.class);

    Timestamp period;
    Short min;
    Short avg;
    Short max;
    Short minTrade;
    Short avgTrade;
    Short maxTrade;
    String server;

    public OnlineChart(Timestamp period, Short min, Short avg, Short max, Short minTrade, Short avgTrade, Short maxTrade, String server) {
        this.period = period;
        this.min = min;
        this.avg = avg;
        this.max = max;
        this.minTrade = minTrade;
        this.avgTrade = avgTrade;
        this.maxTrade = maxTrade;
        this.server = server;
    }

    @Override
    public String toString() {
        return new StringJoiner(", ", OnlineChart.class.getSimpleName() + "[", "]")
                .add("period=" + period)
                .add("min=" + min)
                .add("avg=" + avg)
                .add("max=" + max)
                .add("minTrade=" + minTrade)
                .add("avgTrade=" + avgTrade)
                .add("maxTrade=" + maxTrade)
                .add("server=" + server)
                .toString();
    }

    public Short getShortValue(String name){
        try {
            Field field = this.getClass().getDeclaredField(name);
            return field.getShort(this.getClass());
        } catch (NoSuchFieldException | IllegalAccessException e) {
            logger.atError()
                    .addKeyValue("exception_class",this.getClass().getSimpleName())
                    .addKeyValue("error_message", e.getMessage())
                    .log();
            return null;
        }
    }
}

