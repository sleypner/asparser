package dev.sleypner.asparser.service.parser.online;

import dev.sleypner.asparser.domain.model.OnlineStatus;
import dev.sleypner.asparser.service.parser.shared.EntityParserConfig;
import lombok.Data;
import org.springframework.stereotype.Component;

import java.net.URI;
import java.util.List;

@Component
@Data
public class OnlineParserConfig implements EntityParserConfig<OnlineStatus> {
    private String uri = "https://asterios.tm/index.php?js=1";
    private String name = "online";

    @Override
    public List<URI> getUris() {
        return List.of(URI.create(uri));
    }
}