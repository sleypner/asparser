package dev.sleypner.asparser.service.parser.shared;

import java.net.URI;
import java.util.List;

public interface EntityParserConfig<T> {
    List<URI> getUris();
}
