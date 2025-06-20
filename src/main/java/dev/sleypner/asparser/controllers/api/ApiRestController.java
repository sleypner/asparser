package dev.sleypner.asparser.controllers.api;

import dev.sleypner.asparser.domain.model.*;
import dev.sleypner.asparser.dto.FortressTable;
import dev.sleypner.asparser.dto.OnlineChart;
import dev.sleypner.asparser.dto.OnlineChartData;
import dev.sleypner.asparser.dto.OnlineChartOptions;
import dev.sleypner.asparser.service.parser.article.persistence.ArticlePersistence;
import dev.sleypner.asparser.service.parser.bosses.persistence.RaidBossesPersistence;
import dev.sleypner.asparser.service.parser.event.persistence.EventPersistence;
import dev.sleypner.asparser.service.parser.fortress.persistence.ClanPersistence;
import dev.sleypner.asparser.service.parser.fortress.persistence.FortressHistoryPersistence;
import dev.sleypner.asparser.service.parser.fortress.persistence.FortressPersistence;
import dev.sleypner.asparser.service.parser.online.persistence.OnlinePersistence;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor(onConstructor_ = {@Autowired})
public class ApiRestController {

    private final Logger logger = LoggerFactory.getLogger(ApiRestController.class);
    private final ArticlePersistence articlePersistence;
    private final OnlinePersistence onlinePersistence;
    private final EventPersistence eventPersistence;
    private final RaidBossesPersistence raidBossesPersistence;
    private final FortressHistoryPersistence fortressHistoryPersistence;
    private final FortressPersistence fortressPersistence;
    private final ClanPersistence clanPersistence;

    @RequestMapping(value = "/articles", produces = MediaType.APPLICATION_JSON_VALUE, method = RequestMethod.POST)
    public List<Article> getArticles(@RequestBody FormData data) {
        List<Article> articleList = articlePersistence.getAll();

        if (Objects.equals(data.sort, "asc")) {
            articleList.sort(Comparator.comparing(Article::getCreateOn));
        } else {
            articleList.sort(Comparator.comparing(Article::getCreateOn).reversed());
        }

        return articleList;
    }

    @GetMapping(value = "/online", produces = MediaType.APPLICATION_JSON_VALUE)
    public OnlineChartOptions getOnline(
            @RequestParam(name = "server", required = false) String server,
            @RequestParam(name = "period-start", required = false) LocalDateTime periodStart,
            @RequestParam(name = "period-end", required = false) LocalDateTime periodEnd,
            @RequestParam(name = "interval", required = false) Integer interval
    ) {
        List<OnlineChart> listChart = onlinePersistence.getByTimePeriod(periodStart, periodEnd, interval);

        List<OnlineChart> newListChart = listChart
                .stream()
                .filter(serv -> serv.getServer().endsWith(server))
                .collect(Collectors.toList());

        return new OnlineChartOptions("line", new OnlineChartData().getChartData(newListChart));
    }

    @RequestMapping(value = "/events", produces = MediaType.APPLICATION_JSON_VALUE, method = RequestMethod.POST)
    public List<Event> getEvents(@RequestBody FormData data) {
        List<Event> listEvents = new ArrayList<>();
        if (Objects.equals(data.server, "all")) {
            listEvents = eventPersistence.getAll();
        } else {
            listEvents = eventPersistence.getByServer(data.server);
        }

        if (Objects.equals(data.sort, "asc")) {
            listEvents.sort(Comparator.comparing(Event::getDate));
        } else {
            listEvents.sort(Comparator.comparing(Event::getDate).reversed());
        }

        if (!data.type.equalsIgnoreCase("all")) {
            listEvents = listEvents.stream().filter(el -> el.getType().equals(data.type)).toList();
        }

        return listEvents;
    }

    @RequestMapping(value = "/bosses", produces = MediaType.APPLICATION_JSON_VALUE, method = RequestMethod.POST)
    public List<RaidBoss> getBosses(
            @RequestBody(required = false) FormData data
    ) {
        List<RaidBoss> listBosses;

        if (data.server == null || data.server.isEmpty()) {
            listBosses = raidBossesPersistence.getAll();
            return listBosses;
        }

        if (Objects.equals(data.server, "all")) {
            listBosses = raidBossesPersistence.getAll();
        } else {
            listBosses = raidBossesPersistence.getByServer(data.server);
        }

        if (Objects.equals(data.sort, "asc")) {
            listBosses.sort(Comparator.comparing(RaidBoss::getRespawnStart));
        } else {
            listBosses.sort(Comparator.comparing(RaidBoss::getRespawnStart).reversed());
        }

        if (!data.type.equalsIgnoreCase("all")) {
            listBosses = listBosses.stream().filter(el -> el.getType().equals(data.type)).toList();
        }

        return listBosses;
    }

    @RequestMapping(value = "/fortress", produces = MediaType.APPLICATION_JSON_VALUE, method = RequestMethod.POST)
    public List<FortressTable> getFortress(
            @RequestBody(required = false) FormData data
    ) {

        List<FortressHistory> fortressHistoryList;

        fortressHistoryList = fortressHistoryPersistence.getCurrentStatusOfForts();

        List<FortressTable> fortressTableList = new ArrayList<>();
        for (FortressHistory fortressHistory : fortressHistoryList) {
            Fortress fortress = fortressPersistence.getById(fortressHistory.getFortressId());
            if (!Objects.equals(fortress.getServer(), data.server) && !Objects.equals(data.server, "all")) {
                continue;
            }

            Clan clan = clanPersistence.getById(fortressHistory.getClanId());

            FortressTable fortressTable = FortressTable.builder()
                    .name(fortress.getName())
                    .server(fortress.getServer())
                    .skills(fortress.getSkills().stream().toList())
                    .updatedDate(fortress.getUpdatedDate())
                    .clan(clan)
                    .coffer(fortressHistory.getCoffer())
                    .holdTime(fortressHistory.getHoldTime())
                    .build();
            if (fortressTable.getSkills() == null || fortressTable.getSkills().isEmpty()) {
                fortressTable.setSkills(new ArrayList<>());
            }
            fortressTableList.add(fortressTable);
        }

        return fortressTableList;
    }

    @RequestMapping(value = "/fortress-history", produces = MediaType.APPLICATION_JSON_VALUE, method = RequestMethod.POST)
    public List<FortressTable> getFortressHistory(@RequestBody(required = false) FormData data) {
        List<FortressHistory> fortressHistoryList;

        if (Objects.equals(data.server, "all")) {
            fortressHistoryList = fortressHistoryPersistence.getAll();
        } else {
            if (data.server == null || data.server.isEmpty()) {
                fortressHistoryList = fortressHistoryPersistence.getAll();
            } else {
                fortressHistoryList = fortressHistoryPersistence.getByServer(data.server);
            }
        }

        if (Objects.equals(data.sort, "asc")) {
            fortressHistoryList.sort(Comparator.comparing(FortressHistory::getUpdatedDate));
        } else {
            fortressHistoryList.sort(Comparator.comparing(FortressHistory::getUpdatedDate).reversed());
        }

        List<FortressTable> fortressTableList = new ArrayList<>();
        for (FortressHistory fortressHistory : fortressHistoryList) {
            Fortress fortress = fortressPersistence.getById(fortressHistory.getFortressId());
            Clan clan = clanPersistence.getById(fortressHistory.getClanId());

            FortressTable fortressTable = FortressTable.builder()
                    .name(fortress.getName())
                    .server(fortress.getServer())
                    .skills(fortress.getSkills().stream().toList())
                    .updatedDate(fortress.getUpdatedDate())
                    .clan(clan)
                    .coffer(fortressHistory.getCoffer())
                    .holdTime(fortressHistory.getHoldTime())
                    .build();

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

