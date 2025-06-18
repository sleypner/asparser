package dev.sleypner.asparser.service.parser.shared;

import dev.sleypner.asparser.util.HtmlDocument;
import reactor.core.publisher.Mono;

import java.io.IOException;
import java.net.URI;

public interface Fetcher<T> {
    Mono<HtmlDocument> fetch(URI uri);
}
