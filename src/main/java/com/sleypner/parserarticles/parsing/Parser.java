package com.sleypner.parserarticles.parsing;

import com.sleypner.parserarticles.model.source.entityes.*;
import com.sleypner.parserarticles.parsing.raw.FortressParser;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.net.http.HttpResponse;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

public interface Parser {
    List<Article> parseArticles(Document document);

    private HttpResponse<String> getHttpResponse(String url) {
        return null;
    }

    List<OnlineStatus> parseStatus(Document document);

    List<Events> parseEvents(Document document, LocalDateTime lastDate);

    List<FortressParser> parseFortress(Document document);

    String stringAfter(String string);

    Clan parseClan(Document document);

    Set<FortressSkills> parseSkills(Elements elements);

}
