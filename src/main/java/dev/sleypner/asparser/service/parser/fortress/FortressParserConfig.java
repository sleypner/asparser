package dev.sleypner.asparser.service.parser.fortress;

import dev.sleypner.asparser.domain.model.Fortress;
import dev.sleypner.asparser.service.parser.shared.EntityParserConfig;
import lombok.Data;
import org.springframework.stereotype.Component;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;

@Component
@Data
public class FortressParserConfig implements EntityParserConfig<Fortress> {
    private String name = "fortress";
    private static String host = "https://asterios.tm";

    @Override
    public List<URI> getUris() {
        List<URI> uris = new ArrayList<URI>();
        uris.add(URI.create("https://asterios.tm/static/ratings/fortress/3.en.html"));
//        uris.add(URI.create("https://asterios.tm/static/ratings/fortress/8.en.html"));
//        uris.add(URI.create("https://asterios.tm/static/ratings/fortress/0.en.html"));
//        uris.add(URI.create("https://asterios.tm/static/ratings/fortress/2.en.html"));
//        uris.add(URI.create("https://asterios.tm/static/ratings/fortress/6.en.html"));
        return uris;
    }

    public static String pullHost() {
        return host;
    }
}
