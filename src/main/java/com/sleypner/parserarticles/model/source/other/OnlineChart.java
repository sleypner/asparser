package com.sleypner.parserarticles.model.source.other;

import jakarta.persistence.ColumnResult;
import jakarta.persistence.ConstructorResult;
import jakarta.persistence.SqlResultSetMapping;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.sql.Timestamp;


@Data
@AllArgsConstructor
@SqlResultSetMapping(
        name = "OnlineChartDtoMapping",
        classes = @ConstructorResult(
                targetClass = OnlineChart.class,
                columns = {
                        @ColumnResult(name = "period", type = Timestamp.class),
                        @ColumnResult(name = "min", type = Short.class),
                        @ColumnResult(name = "avg", type = Short.class),
                        @ColumnResult(name = "max", type = Short.class),
                        @ColumnResult(name = "min_trade", type = Short.class),
                        @ColumnResult(name = "avg_trade", type = Short.class),
                        @ColumnResult(name = "max_trade", type = Short.class),
                        @ColumnResult(name = "server", type = String.class)
                }
        )
)
public class OnlineChart {

    private Timestamp period;
    private Short min;
    private Short avg;
    private Short max;
    private Short min_trade;
    private Short avg_trade;
    private Short max_trade;
    private String server;

}

