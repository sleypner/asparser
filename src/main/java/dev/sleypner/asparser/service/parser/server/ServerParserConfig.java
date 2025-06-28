package dev.sleypner.asparser.service.parser.server;

import dev.sleypner.asparser.domain.model.Server;
import dev.sleypner.asparser.service.parser.shared.EntityParserConfig;
import lombok.Data;
import org.springframework.stereotype.Component;

import java.net.URI;
import java.util.List;

@Component
@Data
public class ServerParserConfig implements EntityParserConfig<Server> {
    private String uri = "https://asterios.tm/index.php?cmd=rss";
    private String name = "Server";

    @Override
    public List<URI> getUris() {
        return List.of(URI.create(uri));
    }
}