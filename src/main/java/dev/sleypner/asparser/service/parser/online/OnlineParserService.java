package dev.sleypner.asparser.service.parser.online;

import dev.sleypner.asparser.domain.model.OnlineStatus;
import dev.sleypner.asparser.service.parser.shared.Parser;
import dev.sleypner.asparser.util.HtmlDocument;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Service;

import java.util.Set;
import java.util.stream.Collectors;

@Service
public class OnlineParserService implements Parser<OnlineStatus> {

    @Override
    public Set<OnlineStatus> parse(HtmlDocument htmlDocument) {
        Document document = htmlDocument.getDocument();
        Elements elements = document.select("#OnlineStatus table:matches((?i)status:)");
        return elements.stream().map(element -> {

            System.out.println(element.text());
            Elements trElements = element.select("tr");
            short online = Short.parseShort(trElements.getFirst().select("td:last-child").text());
            short onTrade = Short.parseShort(trElements.get(1).select("td:last-child").text());
            String server = trElements.get(2).select("td:last-child").text();

            return OnlineStatus.builder()
                    .online(online)
                    .server(null)
                    .onTrade(onTrade)
                    .build();

        }).collect(Collectors.toSet());
    }
}
