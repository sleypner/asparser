package dev.sleypner.asparser.service.parser.shared;

import dev.sleypner.asparser.domain.model.Server;

import java.net.URI;
import java.util.List;

public interface EntityParserConfig<T> {
    List<URI> getUris();

    /**
     *  Use in events {@code EventParserConfig} for generate {@code List<URI>}
     *
     *  @return in default {@code null} */
    default List<URI> getUris(List<Server> servers) {
        return null;
    }

    String getName();
}
