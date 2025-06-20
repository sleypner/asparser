package dev.sleypner.asparser.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
public class OnlineChartData {


    public List<String> labels;
    public List<OnlineChartDataset> datasets;
    @Builder.Default
    Logger logger = LoggerFactory.getLogger(OnlineChartData.class);

    public OnlineChartData getChartData(List<OnlineChart> listChart) {
        OnlineChartData chartDate = new OnlineChartData();
        List<String> lablesList = new ArrayList<>();
        List<OnlineChartDataset> listDataset = new ArrayList<>();

        List<Short> listMin = new ArrayList<>();
        List<Short> listAvg = new ArrayList<>();
        List<Short> listMax = new ArrayList<>();
        List<Short> listMinTrade = new ArrayList<>();
        List<Short> listAvgTrade = new ArrayList<>();
        List<Short> listMaxTrade = new ArrayList<>();

        int i = 0;
        for (OnlineChart s : listChart) {
            LocalDateTime date = s.getPeriod().toLocalDateTime();

            int day = date.getDayOfMonth();
            int month = date.getMonthValue();
            int year = date.getYear();
            int hour = date.getHour();
            int minutes = date.getMinute();
            int second = date.getSecond();

            String datePeriod = "";

            DateTimeFormatter formatterWithoutSeconds = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm");
            DateTimeFormatter formatterWithoutYears = DateTimeFormatter.ofPattern("MM/dd HH:mm");
            DateTimeFormatter formatterWithoutMonth = DateTimeFormatter.ofPattern("dd HH:mm");

            if (i > 0) {
                int prevYear = listChart.get(i - 1).getPeriod().toLocalDateTime().getYear();
                int prevMonth = listChart.get(i - 1).getPeriod().toLocalDateTime().getMonthValue();
                if (year == prevYear) {
                    if (month == prevMonth) {
                        datePeriod = date.format(formatterWithoutMonth);
                    } else {
                        datePeriod = date.format(formatterWithoutYears);

                    }
                } else {
                    datePeriod = date.format(formatterWithoutSeconds);
                }
            } else {
                datePeriod = date.format(formatterWithoutSeconds);
            }


            lablesList.add(datePeriod);
            listMin.add(s.getMin());
            listAvg.add(s.getAvg());
            listMax.add(s.getMax());
            listMinTrade.add(s.getMin_trade());
            listAvgTrade.add(s.getAvg_trade());
            listMaxTrade.add(s.getMax_trade());

            i++;
        }
        List<String> listNames = new ArrayList<>(List.of("min", "avg", "max", "minTrade", "avgTrade", "maxTrade"));
        List<Short> data = new ArrayList<>();

        for (String name : listNames) {
            switch (name) {
                case "min":
                    data = listMin;
                    break;
                case "avg":
                    data = listAvg;
                    break;
                case "max":
                    data = listMax;
                    break;
                case "minTrade":
                    data = listMinTrade;
                    break;
                case "avgTrade":
                    data = listAvgTrade;
                    break;
                case "maxTrade":
                    data = listMaxTrade;
                    break;
            }

            listDataset.add(new OnlineChartDataset(name + " " + listChart.getLast().getServer(), data));
        }

        chartDate.setLabels(lablesList);
        chartDate.setDatasets(listDataset);
        return chartDate;
    }
}
