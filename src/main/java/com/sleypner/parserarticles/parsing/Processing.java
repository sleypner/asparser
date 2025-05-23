package com.sleypner.parserarticles.parsing;

import com.sleypner.parserarticles.model.services.EventsService;
import com.sleypner.parserarticles.model.source.entityes.Article;
import com.sleypner.parserarticles.model.source.entityes.Clan;
import com.sleypner.parserarticles.model.source.entityes.Events;
import com.sleypner.parserarticles.model.source.entityes.OnlineStatus;
import com.sleypner.parserarticles.parsing.raw.ArticlesParser;
import com.sleypner.parserarticles.parsing.raw.EventsParser;
import com.sleypner.parserarticles.parsing.raw.FortressParser;
import com.sleypner.parserarticles.parsing.raw.OnlineParser;
import com.sleypner.parserarticles.special.HttpAction;
import org.jsoup.nodes.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.net.URI;
import java.net.http.HttpResponse;
import java.time.LocalDateTime;
import java.util.*;

@Component
public class Processing {

    Parser parser;
    Output output;
    EventsService eventsService;
    HttpAction httpAction;
    FortressParser fortressParser;
    ArticlesParser articlesParser;
    EventsParser eventsParser;
    OnlineParser onlineParser;

    @Autowired
    public Processing(Parser parser, Output output,
                      EventsService eventsService, HttpAction httpAction) {
        this.parser = parser;
        this.output = output;
        this.eventsService = eventsService;
        this.httpAction = httpAction;
    }

    public Processing() {
    }

    public Map<String, Integer> processingArticles() {
        Map<String, Integer> result = new HashMap<>();
        articlesParser = new ArticlesParser();
        List<URI> uris = articlesParser.getUris();

        Map<String, Integer> savedArticles = new HashMap<>();
        List<HttpResponse<String>> response = httpAction.getHttpResponses(uris);
        List<Document> documents = httpAction.getDocuments(response);

        for (Document doc : documents) {
            List<Article> parseList = parser.parseArticles(doc).reversed();
            savedArticles = output.saveArticles(parseList);
        }

        // "articles" is not always available
        result.put("articles", savedArticles.get("articles"));

        return result;
    }

    public Map<String, Integer> processingOnlineArticles() {
        Map<String, Integer> result = new HashMap<>();
        articlesParser = new ArticlesParser();
        onlineParser = new OnlineParser();
        List<URI> articlesUris = articlesParser.getUris();
        List<URI> onlineUris = onlineParser.getUris();

        Map<String, Integer> savedArticles = new HashMap<>();
        for (URI uri : articlesUris) {
            HttpResponse<String> articlesResponse = httpAction.getHttpResponse(uri);
            Document articlesDocument = httpAction.getDocument(articlesResponse);
            List<Article> articlesList = parser.parseArticles(articlesDocument).reversed();
            if (!articlesList.isEmpty() && articlesDocument.html().length() > 100) {
                savedArticles = output.saveArticles(articlesList);
            }
        }

        Map<String, Integer> savedOnline = new HashMap<>();
        for (URI uri : onlineUris) {
            HttpResponse<String> statusResponse = httpAction.getHttpResponse(uri);
            Document statusDocument = httpAction.getDocument(statusResponse);
            List<OnlineStatus> statusList = parser.parseStatus(statusDocument);
            if (!statusList.isEmpty() && statusDocument.html().length() > 100) {
                savedOnline = output.saveOnline(statusList);
            }
        }

        result.put("online", savedOnline.get("online"));
        result.put("articles", savedArticles.get("articles"));

        return result;
    }

    public Map<String, Integer> processingFortress() {
        Map<String, Integer> result = new HashMap<>();
        fortressParser = new FortressParser();
        List<URI> uris = fortressParser.getUris();

        List<HttpResponse<String>> responses = httpAction.getHttpResponses(uris);
        List<Document> documents = httpAction.getDocuments(responses);

        List<FortressParser> fortressList = new ArrayList<>();
        for (Document doc : documents) {
            List<FortressParser> thisFortressList = parser.parseFortress(doc);
            for (FortressParser fp : thisFortressList) {
                String urlClan = fp.getClanUrl();
                if (urlClan.equals("NPC")) {
                    fp.setClan(new Clan("NPC", fp.getFortress().getServer()));
                    continue;
                }
                HttpResponse<String> clanResponse = httpAction.getHttpResponse(URI.create(urlClan));
                Document clanDocument = httpAction.getDocument(clanResponse);

                Clan clan = parser.parseClan(clanDocument);
                fp.setClan(clan);
            }
            fortressList.addAll(thisFortressList);
        }
        return output.saveFortress(fortressList);
    }

    public Map<String, Integer> processingEventsAndBosses() {
        Map<String, Integer> result = new HashMap<>();
        eventsParser = new EventsParser();
        List<URI> uris = eventsParser.getUris();
        LocalDateTime lastDate = eventsService.getLastEntryDate();
        List<HttpResponse<String>> response = httpAction.getHttpResponses(uris);
        List<Document> documents = httpAction.getDocuments(response);

        int eventsAdded = 0, bossesAdded = 0, bossesUpdated = 0;
        for (Document doc : documents) {

            List<Events> eventList = parser.parseEvents(doc, lastDate);
            eventList.sort(Comparator.comparing(events -> events.getDate()));
            if (!eventList.isEmpty()) {
                Map<String, Integer> savedEvents = output.saveEvents(eventList);
                Map<String, Integer> savedBosses = output.saveRaidBosses(eventList);

                eventsAdded += savedEvents.get("added");
                bossesAdded += savedBosses.get("added");
                bossesUpdated += savedBosses.get("updated");
            }
        }
        result.put("events_added", eventsAdded);
        result.put("bosses_added", bossesAdded);
        result.put("bosses_updated", bossesUpdated);
        return result;
    }
}
