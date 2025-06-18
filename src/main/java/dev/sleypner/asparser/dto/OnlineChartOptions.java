package dev.sleypner.asparser.dto;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class OnlineChartOptions {
    public String type = "line";

    public OnlineChartData data;

    public OnlineChartOptions(String type, OnlineChartData data) {
        this.type = type;
        this.data = data;
    }

    public OnlineChartOptions() {
    }

}
