package dev.sleypner.asparser.service.parser.server;

import dev.sleypner.asparser.domain.model.Server;
import dev.sleypner.asparser.service.parser.shared.Parser;
import dev.sleypner.asparser.util.HtmlDocument;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Service;

import java.util.Set;
import java.util.stream.Collectors;

@Service
public class ServerParserService implements Parser<Server> {

    @Override
    public Set<Server> parse(HtmlDocument htmlDocument) {
        Document document = htmlDocument.getDocument();
        Elements serverIds = document.select("select#serv");
        Elements elements = document.select("#OnlineStatus>div>p.txb img");
        return elements.stream().map(element -> {

            String fullName = element.attr("title").isEmpty() ? element.attr("alt") : element.attr("title");
            int externalId = Integer.parseInt(serverIds.select("*:contains(" + fullName + ")").attr("value"));
            String[] arrName = fullName.split(" ");
            String serverName = arrName[0];
            String rates = arrName[1];

            return Server.builder()
                    .status("up")
                    .externalId(externalId)
                    .name(serverName)
                    .rates(rates)
                    .build();

        }).collect(Collectors.toSet());
    }
}
