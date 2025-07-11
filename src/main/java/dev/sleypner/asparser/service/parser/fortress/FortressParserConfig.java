package dev.sleypner.asparser.service.parser.fortress;

import dev.sleypner.asparser.domain.model.Fortress;
import dev.sleypner.asparser.domain.model.Server;
import dev.sleypner.asparser.service.parser.shared.EntityParserConfig;
import dev.sleypner.asparser.service.parser.shared.RepositoryManager;
import lombok.Data;
import org.springframework.stereotype.Component;

import java.net.URI;
import java.util.List;
import java.util.stream.Collectors;

@Component
@Data
public class FortressParserConfig implements EntityParserConfig<Fortress> {
    private final String name = "Fortress";
    private static final String host = "https://asterios.tm";
    private final RepositoryManager<Server> serverRepository;

    @Override
    public List<URI> getUris() {
        return serverRepository.getAll().stream().map(server -> {
            String uri = host + "/static/ratings/fortress/*.en.html";
            if (server.getExternalId() != null) {
                char ch = server.getExternalId().toString().charAt(0);
                uri = uri.replace('*', ch);
                return URI.create(uri);
            }
            return null;
        }).collect(Collectors.toList());
    }

    public static String pullHost() {
        return host;
    }
}
