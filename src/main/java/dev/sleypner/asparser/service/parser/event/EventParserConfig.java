package dev.sleypner.asparser.service.parser.event;

import dev.sleypner.asparser.domain.model.Event;
import dev.sleypner.asparser.domain.model.Server;
import dev.sleypner.asparser.service.parser.shared.EntityParserConfig;
import lombok.Data;
import org.springframework.stereotype.Component;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Component
@Data
public class EventParserConfig implements EntityParserConfig<Event> {
    private final String url = "https://asterios.tm/index.php?cmd=rss";
    private final String baseUrl = "https://asterios.tm/index.php?cmd=rss&serv={x}&filter={y}";
    private final String name = "Events";

    @Override
    public List<URI> getUris(List<Server> servers) {
        List<Integer> serversIds = servers.stream().map(Server::getExternalId).toList();
        return createUris(serversIds);
    }
    @Override
    public List<URI> getUris() {

        List<Integer> serverIds = new ArrayList<>(List.of(
                3, 8, 0, 2, 6
        ));
        return createUris(serverIds);
    }

    private List<URI> createUris(List<Integer> serversIds) {
        List<String> filterList = new ArrayList<>(List.of(
                "epic", "keyboss", "siege", "tw"
        ));
        List<URI> listUri = new ArrayList<>();
        for (Integer serv : serversIds) {
            String url = baseUrl.replace("{x}", serv.toString());
            for (String filter : filterList) {
                String urlWithFilter = url.replace("{y}", filter);
                URI uri = URI.create(urlWithFilter);
                listUri.add(uri);
            }
        }
        return listUri;
    }
}
