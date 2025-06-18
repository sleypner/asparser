package dev.sleypner.asparser.controllers.pages;

import dev.sleypner.asparser.service.parser.online.persistence.OnlinePersistence;
import dev.sleypner.asparser.util.DateFormat;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Controller
@RequiredArgsConstructor(onConstructor_ = {@Autowired})
public class OnlinePageController {

    private final OnlinePersistence onlinePersistence;

    @GetMapping("/online")
    String online(
            Model model,
            @RequestParam(name = "server", required = false) String server,
            @RequestParam(name = "period-start", required = false) LocalDateTime periodStart,
            @RequestParam(name = "period-end", required = false) LocalDateTime periodEnd,
            @RequestParam(name = "interval", required = false) Integer interval) {

        if (server == null) {
            server = "x5";
        }
        if (periodStart == null) {
            periodStart = LocalDateTime.now().minusYears(5);
        }
        if (periodEnd == null) {
            periodEnd = LocalDateTime.now();
        }
        if (interval == null) {
            interval = 1800;
        }

//        List<OnlineChart> listChart = onlineStatusService.getByTimePeriod(periodStart, periodEnd, interval);
//        String finalServer = server;
//        List<OnlineChart> newListChart = listChart.stream().filter(serv -> serv.getServer().endsWith(finalServer)).collect(Collectors.toList());

        List<String> optionsServerName = List.of("x5", "x3", "x1", "x55", "x7");
        Map<String, Integer> optionsInterval = Map.ofEntries(
                Map.entry("30 min", 1800),
                Map.entry("1 hour", 3600),
                Map.entry("3 hour", 3600 * 3),
                Map.entry("6 hour", 3600 * 6),
                Map.entry("12 hour", 3600 * 12),
                Map.entry("1 day", 864000)
        );


        model.addAttribute("interval", interval)
                .addAttribute("optionsInterval", optionsInterval)
                .addAttribute("optionsServerName", optionsServerName)
//                .addAttribute("online", newListChart)
                .addAttribute("server", server)
                .addAttribute("periodStart", periodStart.format(DateFormat.dateFormat))
                .addAttribute("periodEnd", periodEnd.format(DateFormat.dateFormat))
                .addAttribute("loc", "online");

        return "layouts/online";
    }

}
