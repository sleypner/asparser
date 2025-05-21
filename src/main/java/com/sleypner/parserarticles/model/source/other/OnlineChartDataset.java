package com.sleypner.parserarticles.model.source.other;

import java.util.List;

public class OnlineChartDataset {
    public String label;
    public List<Short> data;

    public OnlineChartDataset(String label, List<Short> data) {
        this.label = label;
        this.data = data;
    }
}
