package com.sleypner.parserarticles.controller;

import com.sleypner.parserarticles.model.services.*;
import com.sleypner.parserarticles.model.source.entityes.*;
import com.sleypner.parserarticles.model.source.other.FortressTable;
import com.sleypner.parserarticles.model.source.other.OnlineChart;
import com.sleypner.parserarticles.parsing.DateFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Controller
public class PagesController {

    private final ArticleService articleService;
    private final OnlineStatusService onlineStatusService;
    private final FortressService fortressService;
    private final RaidBossesService raidBossesService;
    private final EventsService eventsService;
    private final FortressHistoryService fortressHistoryService;
    private final ClanService clanService;

    String tagNow;

    public PagesController(ArticleService articleService,
                           OnlineStatusService onlineStatusService,
                           FortressService fortressService,
                           RaidBossesService raidBossesService,
                           EventsService eventsService,
                           FortressHistoryService fortressHistoryService,
                           ClanService clanService) {
        this.articleService = articleService;
        this.onlineStatusService = onlineStatusService;
        this.fortressService = fortressService;
        this.raidBossesService = raidBossesService;
        this.eventsService = eventsService;
        this.fortressHistoryService = fortressHistoryService;
        this.clanService = clanService;
    }

    @GetMapping("/")
    String index(Model model) {
        List<Article> listArticles = articleService.getAll();
        model.addAttribute("articles", listArticles);
        model.addAttribute("loc", "index");
        return "home/index";
    }

    @GetMapping("/online")
    String online(Model model,
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

        List<OnlineChart> listChart = new ArrayList<>(onlineStatusService.getByTimePeriod(periodStart, periodEnd, interval));
        String finalServer = server;
        List<OnlineChart> newListChart = listChart.stream().filter(serv -> serv.getServer().endsWith(finalServer)).collect(Collectors.toList());

        List<String> optionsServerName = onlineStatusService.getServers();
        Map<String, Integer> optionsInterval = new HashMap<>();
        optionsInterval.put("30 min", 1800);
        optionsInterval.put("1 hour", 3600);
        optionsInterval.put("3 hour", 3600 * 3);
        optionsInterval.put("6 hour", 3600 * 6);
        optionsInterval.put("12 hour", 3600 * 12);
        optionsInterval.put("1 day", 864000);

        model.addAttribute("interval", interval);
        model.addAttribute("optionsInterval", optionsInterval);
        model.addAttribute("optionsServerName", optionsServerName);
        model.addAttribute("online", newListChart);
        model.addAttribute("server", server);
        model.addAttribute("periodStart", periodStart.format(DateFormat.dateFormat));
        model.addAttribute("periodEnd", periodEnd.format(DateFormat.dateFormat));

        model.addAttribute("loc", "online");

        return "layouts/online";
    }

    @GetMapping("/roulette")
    String roulette(Model model) {

        model.addAttribute("loc", "roulette");

        return "layouts/roulette";
    }

    @GetMapping("/fortress")
    String fortress(Model model) {
        List<FortressTable> fortressTableList = new ArrayList<>();
        List<FortressHistory> fortressHistoryList = fortressHistoryService.getCurrentStatusOfForts().stream().sorted().toList();
        for (FortressHistory fortressHistory : fortressHistoryList) {
            Fortress fortress = fortressService.getById(fortressHistory.getFortressId());
            Clan clan = clanService.getById(fortressHistory.getClanId());

            FortressTable fortressTable = new FortressTable(
                    fortress.getName(),
                    fortress.getServer(),
                    fortress.getSkills().stream().toList(),
                    fortressHistory.getUpdatedDate(),
                    clan.getId(),
                    clan.getName(),
                    clan.getLevel(),
                    clan.getLeader(),
                    clan.getPlayersCount(),
                    clan.getCastle(),
                    clan.getReputation(),
                    clan.getAlliance(),
                    fortressHistory.getCoffer(),
                    fortressHistory.getHoldTime()
            );
            fortressTableList.add(fortressTable);
        }

        model.addAttribute("fortressTable", fortressTableList);

        model.addAttribute("loc", "fortress");

        return "layouts/fortress";
    }

    @GetMapping("/fortress-history")
    String fortressHistory(Model model) {
        List<FortressTable> fortressTableList = new ArrayList<>();
        List<FortressHistory> fortressHistoryList = fortressHistoryService.getAll().stream().sorted().toList();
        for (FortressHistory fortressHistory : fortressHistoryList) {
            Fortress fortress = fortressService.getById(fortressHistory.getFortressId());
            Clan clan = clanService.getById(fortressHistory.getClanId());

            FortressTable fortressTable = new FortressTable(
                    fortress.getName(),
                    fortress.getServer(),
                    fortress.getSkills().stream().toList(),
                    fortressHistory.getUpdatedDate(),
                    clan.getId(),
                    clan.getName(),
                    clan.getLevel(),
                    clan.getLeader(),
                    clan.getPlayersCount(),
                    clan.getCastle(),
                    clan.getReputation(),
                    clan.getAlliance(),
                    fortressHistory.getCoffer(),
                    fortressHistory.getHoldTime()
            );
            if (fortressTable.getSkills() == null || fortressTable.getSkills().isEmpty()) {
                fortressTable.setSkills(new ArrayList<>());
            }
            fortressTableList.add(fortressTable);
        }
        model.addAttribute("fortressTable", fortressTableList);

        model.addAttribute("loc", "fortress-history");

        return "layouts/fortress-history";
    }

    @GetMapping("/bosses")
    String bosses(Model model) {
        List<RaidBosses> bossesList = raidBossesService.getAll().stream().sorted().toList().reversed();
        model.addAttribute("bosses", bossesList);

        model.addAttribute("loc", "bosses");

        return "layouts/bosses";
    }

    @GetMapping("/events")
    String events(Model model) {
        List<Events> eventsList = eventsService.getAll().stream().sorted().toList().reversed();
        model.addAttribute("events", eventsList);

        model.addAttribute("loc", "events");

        return "layouts/events";
    }

}
