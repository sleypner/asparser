package com.sleypner.parserarticles.controller;

import com.sleypner.parserarticles.exceptions.ArticleNotFoundExceptions;
import com.sleypner.parserarticles.model.services.*;
import com.sleypner.parserarticles.model.source.entityes.*;
import com.sleypner.parserarticles.model.source.other.FortressTable;
import com.sleypner.parserarticles.model.source.other.OnlineChart;
import com.sleypner.parserarticles.model.source.other.OnlineChartData;
import com.sleypner.parserarticles.model.source.other.OnlineChartOptions;
import com.sleypner.parserarticles.parsing.Processing;
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

    @PostMapping(value = "/articles", produces = "application/json")
    public Article addArticle(@RequestBody Article article) {
        article.setId(articleService.getAll().size() + 1);
        return articleService.save(article);
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

    @GetMapping(value = "/events", produces = "application/json")
    public List<Events> getEvents(
            @RequestParam(name = "server") String server,
            @RequestParam(name = "type") String type,
            @RequestParam(name = "sort") String sort
    ) {
        List<Events> listEvents = new ArrayList<>();
        if (Objects.equals(server, "all")) {
            listEvents = eventsService.getAll();
        } else {
            listEvents = eventsService.getByServer(server);
        }

        if (Objects.equals(sort, "asc")) {
            listEvents.sort(Comparator.comparing(Events::getDate));
        } else if (Objects.equals(sort, "desc")) {
            listEvents.sort(Comparator.comparing(Events::getDate).reversed());
        }

        if (!type.equalsIgnoreCase("all")) {
            listEvents = listEvents.stream().filter(el -> el.getType().equals(type)).toList();
        }

        return listEvents;
    }

    @GetMapping(value = "/bosses", produces = "application/json")
    public List<RaidBosses> getBosses(
            @RequestParam(name = "server") String server,
            @RequestParam(name = "type") String type,
            @RequestParam(name = "sort") String sort
    ) {
        List<RaidBosses> listBosses = new ArrayList<>();
        if (Objects.equals(server, "all")) {
            listBosses = raidBossesService.getAll();
        } else {
            listBosses = raidBossesService.getByServer(server);
        }

        if (Objects.equals(sort, "asc")) {
            listBosses.sort(Comparator.comparing(RaidBosses::getDate));
        } else if (Objects.equals(sort, "desc")) {
            listBosses.sort(Comparator.comparing(RaidBosses::getDate).reversed());
        }

        if (!type.equalsIgnoreCase("all")) {
            listBosses = listBosses.stream().filter(el -> el.getType().equals(type)).toList();
        }

        return listBosses;
    }

    @GetMapping(value = "/fortress", produces = "application/json")
    public List<FortressTable> getFortress(
            @RequestParam(name = "server") String server
    ) {

        List<FortressTable> fortressTableList = new ArrayList<>();
        List<FortressHistory> fortressHistoryList = fortressHistoryService.getCurrentStatusOfForts();
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
        return fortressTableList;
    }
}

