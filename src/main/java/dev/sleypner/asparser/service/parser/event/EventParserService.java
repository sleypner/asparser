package dev.sleypner.asparser.service.parser.event;

import dev.sleypner.asparser.domain.model.Event;
import dev.sleypner.asparser.domain.model.Server;
import dev.sleypner.asparser.service.parser.shared.Parser;
import dev.sleypner.asparser.util.DateFormat;
import dev.sleypner.asparser.util.HtmlDocument;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class EventParserService implements Parser<Event> {

    @Override
    public Set<Event> parse(HtmlDocument htmlDocument) {
        Document document = htmlDocument.getDocument();
        LocalDateTime lastEntryDate = htmlDocument.getLastEntryDate();

        String fullServer = document.select("select#serv option[selected]").text();
        String[] arrServer = fullServer.split(" ");

        Set<Server> servers = htmlDocument.getServers();
        Server server = servers.stream()
                .filter(serv -> serv.getName().equals(arrServer[0]) && serv.getRates().equals(arrServer[1]))
                .findFirst().orElse(null);

        String type = document.select("select#filter option[selected]").text();
        Elements elements = document.select("#page_contents center>table");
        return elements.stream().map(element -> {

            Element titleElement = element.selectFirst("tr:first-child");
            String fullTitle = titleElement != null ? titleElement.text() : "";

            Element descriptionElement = element.selectFirst("tr:last-child");
            String description = descriptionElement != null ? descriptionElement.text() : "";

            String[] splitFullTitle = fullTitle.split(": ");
            LocalDateTime date = LocalDateTime.from(DateFormat.format(splitFullTitle[0]));
            String title = splitFullTitle[1];
            if (lastEntryDate == null || date.isAfter(lastEntryDate)) {
                return Event.builder()
                        .title(title)
                        .description(description)
                        .date(date)
                        .server(server)
                        .type(type)
                        .build();
            }
            return null;
        }).filter(Objects::nonNull).collect(Collectors.toSet());
    }
}
