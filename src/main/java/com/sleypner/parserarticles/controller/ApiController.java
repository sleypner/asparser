package com.sleypner.parserarticles.controller;

import com.sleypner.parserarticles.exceptions.ArticleNotFoundExceptions;
import com.sleypner.parserarticles.model.services.*;
import com.sleypner.parserarticles.model.source.entityes.*;
import com.sleypner.parserarticles.model.source.other.FortressTable;
import com.sleypner.parserarticles.model.source.other.OnlineChart;
import com.sleypner.parserarticles.model.source.other.OnlineChartData;
import com.sleypner.parserarticles.model.source.other.OnlineChartOptions;
import com.sleypner.parserarticles.parsing.Processing;
import lombok.Data;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api")
public class ApiController {

    Logger logger = LoggerFactory.getLogger(ApiController.class);
    private final ArticleService articleService;
    private final OnlineStatusService onlineStatusService;
    private final Processing processing;
    private final EventsService eventsService;
    private final RaidBossesService raidBossesService;
    private final FortressHistoryService fortressHistoryService;
    private final FortressService fortressService;
    private final ClanService clanService;
    private final UsersService usersService;
    private final RolesService rolesService;

    @Autowired
    public ApiController(ArticleService articleService,
                         OnlineStatusService onlineStatusService,
                         Processing processing,
                         EventsService eventsService,
                         RaidBossesService raidBossesService,
                         FortressHistoryService fortressHistoryService,
                         FortressService fortressService,
                         ClanService clanService, UsersService usersService, RolesService rolesService) {
        this.articleService = articleService;
        this.onlineStatusService = onlineStatusService;
        this.processing = processing;
        this.eventsService = eventsService;
        this.raidBossesService = raidBossesService;
        this.fortressHistoryService = fortressHistoryService;
        this.fortressService = fortressService;
        this.clanService = clanService;
        this.usersService = usersService;
        this.rolesService = rolesService;
    }

    @GetMapping(value = "/articles/{id}", produces = "application/json")
    public Article goArticleByID(@PathVariable("id") int id) {
        if ((id > articleService.getAll().size() || id < 0)) {
            String message = "article id not found - " + id;
            logger.atError()
//                    .setMessage(message)
                    .addKeyValue("exception_class", this.getClass().getSimpleName())
                    .addKeyValue("error_message", message)
                    .log();

            throw new ArticleNotFoundExceptions(message);
        }
        return articleService.getById(id);
    }

    @GetMapping(value = "/articles", produces = "application/json")
    public List<Article> getArticlesByDateAndMore(@RequestParam(name = "title", required = false) String title,
                                                  @RequestParam(name = "subtitle", required = false) String subtitle,
                                                  @RequestParam(name = "description", required = false) String description,
                                                  @RequestParam(name = "dateStart", required = false) LocalDateTime dateStart,
                                                  @RequestParam(name = "dateEnd", required = false) LocalDateTime dateEnd) {

        return articleService.getByDateAndMore(title, subtitle, description, dateStart, dateEnd);
    }

    @DeleteMapping(value = "/articles/{id}", produces = "application/json")
    public void deleteArticleByID(@PathVariable("id") int id) {
        if ((id > articleService.getAll().size() || id < 0)) {

            String message = "article id not found - " + id;

            logger.atError()
//                    .setMessage(message)
                    .addKeyValue("exception_class", this.getClass().getSimpleName())
                    .addKeyValue("error_message", message)
                    .log();
            throw new ArticleNotFoundExceptions(message);
        }
        articleService.deleteById(id);
    }

    @RequestMapping(value = "/articles", produces = "application/json", method = RequestMethod.POST)
    public List<Article> getArticles(@RequestBody FormData data) {
        List<Article> articleList = new ArrayList<>();

        articleList = articleService.getAll();

        if (Objects.equals(data.sort, "asc")) {
            articleList.sort(Comparator.comparing(Article::getCreateOn));
        } else {
            articleList.sort(Comparator.comparing(Article::getCreateOn).reversed());
        }

        return articleList;
    }

    @PostMapping(value = "/articles/parse", produces = "application/json")
    public Map<String, Integer> parseArticles(@RequestParam(name = "force", required = true) boolean force) {
        if (force) {
            return processing.processingArticles();
        }
        return null;
    }

    @GetMapping(value = "/online", produces = "application/json")
    public OnlineChartOptions getOnline(
            @RequestParam(name = "server", required = false) String server,
            @RequestParam(name = "period-start", required = false) LocalDateTime periodStart,
            @RequestParam(name = "period-end", required = false) LocalDateTime periodEnd,
            @RequestParam(name = "interval", required = false) Integer interval
    ) {
        List<OnlineChart> listChart = onlineStatusService.getByTimePeriod(periodStart, periodEnd, interval);

        List<OnlineChart> newListChart = listChart
                .stream()
                .filter(serv -> serv.getServer().endsWith(server))
                .collect(Collectors.toList());

        return new OnlineChartOptions("line", new OnlineChartData().getChartData(newListChart));
    }

    @RequestMapping(value = "/events", produces = "application/json", method = RequestMethod.POST)
    public List<Events> getEvents(@RequestBody FormData data) {
        List<Events> listEvents = new ArrayList<>();
        if (Objects.equals(data.server, "all")) {
            listEvents = eventsService.getAll();
        } else {
            listEvents = eventsService.getByServer(data.server);
        }

        if (Objects.equals(data.sort, "asc")) {
            listEvents.sort(Comparator.comparing(Events::getDate));
        } else {
            listEvents.sort(Comparator.comparing(Events::getDate).reversed());
        }

        if (!data.type.equalsIgnoreCase("all")) {
            listEvents = listEvents.stream().filter(el -> el.getType().equals(data.type)).toList();
        }

        return listEvents;
    }

    @RequestMapping(value = "/bosses", produces = "application/json", method = RequestMethod.POST)
    public List<RaidBosses> getBosses(
            @RequestBody(required = false) FormData data
    ) {
        List<RaidBosses> listBosses;

        if (data.server == null || data.server.isEmpty()) {
            listBosses = raidBossesService.getAll();
            return listBosses;
        }

        if (Objects.equals(data.server, "all")) {
            listBosses = raidBossesService.getAll();
        } else {
            listBosses = raidBossesService.getByServer(data.server);
        }

        if (Objects.equals(data.sort, "asc")) {
            listBosses.sort(Comparator.comparing(RaidBosses::getRespawnStart));
        } else {
            listBosses.sort(Comparator.comparing(RaidBosses::getRespawnStart).reversed());
        }

        if (!data.type.equalsIgnoreCase("all")) {
            listBosses = listBosses.stream().filter(el -> el.getType().equals(data.type)).toList();
        }

        return listBosses;
    }

    @RequestMapping(value = "/fortress", produces = "application/json", method = RequestMethod.POST)
    public List<FortressTable> getFortress(
            @RequestBody(required = false) FormData data
    ) {

        List<FortressHistory> fortressHistoryList;

        fortressHistoryList = fortressHistoryService.getCurrentStatusOfForts();

        List<FortressTable> fortressTableList = new ArrayList<>();
        for (FortressHistory fortressHistory : fortressHistoryList) {
            Fortress fortress = fortressService.getById(fortressHistory.getFortressId());
            if (!Objects.equals(fortress.getServer(), data.server) && !Objects.equals(data.server, "all")) {
                continue;
            }

            Clan clan = clanService.getById(fortressHistory.getClanId());

            FortressTable fortressTable = new FortressTable(
                    fortress.getName(),
                    fortress.getServer(),
                    fortress.getSkills().stream().toList(),
                    fortressHistory.getUpdatedDate(),
                    clan,
                    fortressHistory.getCoffer(),
                    fortressHistory.getHoldTime()
            );
            if (fortressTable.getSkills() == null || fortressTable.getSkills().isEmpty()) {
                fortressTable.setSkills(new ArrayList<>());
            }
            fortressTableList.add(fortressTable);
        }

        return fortressTableList;
    }

    @RequestMapping(value = "/fortress-history", produces = "application/json", method = RequestMethod.POST)
    public List<FortressTable> getFortressHistory(
            @RequestBody(required = false) FormData data
    ) {
        List<FortressHistory> fortressHistoryList;

        if (Objects.equals(data.server, "all")) {
            fortressHistoryList = fortressHistoryService.getAll();
        } else {
            if (data.server == null || data.server.isEmpty()) {
                fortressHistoryList = fortressHistoryService.getAll();
            } else {
                fortressHistoryList = fortressHistoryService.getByServer(data.server);
            }
        }

        if (Objects.equals(data.sort, "asc")) {
            fortressHistoryList.sort(Comparator.comparing(FortressHistory::getUpdatedDate));
        } else {
            fortressHistoryList.sort(Comparator.comparing(FortressHistory::getUpdatedDate).reversed());
        }

        List<FortressTable> fortressTableList = new ArrayList<>();
        for (FortressHistory fortressHistory : fortressHistoryList) {
            Fortress fortress = fortressService.getById(fortressHistory.getFortressId());
            Clan clan = clanService.getById(fortressHistory.getClanId());

            FortressTable fortressTable = new FortressTable(
                    fortress.getName(),
                    fortress.getServer(),
                    fortress.getSkills().stream().toList(),
                    fortressHistory.getUpdatedDate(),
                    clan,
                    fortressHistory.getCoffer(),
                    fortressHistory.getHoldTime()
            );
            if (fortressTable.getSkills() == null || fortressTable.getSkills().isEmpty()) {
                fortressTable.setSkills(new ArrayList<>());
            }
            fortressTableList.add(fortressTable);
        }
        return fortressTableList;
    }

    @Data
    public static class FormData {
        private String server;
        private String type;
        private String sort;

        public FormData(String server, String sort) {
            this.server = server;
            this.sort = sort;
            this.type = "";
        }
    }
}

