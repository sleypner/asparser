package dev.sleypner.asparser.service.parser.shared;

import dev.sleypner.asparser.domain.model.Image;
import dev.sleypner.asparser.util.HtmlDocument;
import reactor.core.publisher.Mono;

import java.net.URI;

public interface Fetcher<T> {
    Mono<HtmlDocument> fetch(URI uri);

    Mono<Image> fetchImages(Image image);
}
