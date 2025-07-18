package dev.sleypner.asparser.service.parser.shared;

import dev.sleypner.asparser.domain.model.Image;
import dev.sleypner.asparser.exceptions.FetchException;
import dev.sleypner.asparser.http.CustomWebClient;
import dev.sleypner.asparser.util.HtmlDocument;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;
import reactor.core.publisher.SignalType;

import java.net.URI;

@Component
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class BaseFetcher<T> implements Fetcher<T> {
    protected final Logger log = LoggerFactory.getLogger(getClass());

    @Override
    public Mono<HtmlDocument> fetch(URI uri) {

        return Mono.defer(() -> {

                    String baseUri = createBaseUri(uri);
                    String path = createPath(uri);

                    return createWebClient(baseUri)
                            .parsePage(path)
                            .doOnError(e -> handleError(e, uri))
                            .doOnCancel(() -> handleCansel(uri))
                            .doFinally(signal -> handleFinally(uri, signal));

                })
                .onErrorResume(e -> handleCriticalError(e, uri));
    }

    @Override
    public Mono<Image> fetchImages(Image image) {
        URI uri = URI.create(image.getExternalUri());
        log.info("Starting fetch image: {}", uri);

        String baseUri = createBaseUri(uri);

        CustomWebClient webClient = createWebClient(baseUri);

        Mono<Image> fetchMono = webClient.fetchImages(image)
                .doOnError(e -> handleError(e, uri))
                .doOnCancel(() -> handleCansel(uri));

        return Mono.defer(() -> fetchMono)
                .onErrorResume(e -> {
                    return handleCriticalError(e, uri)
                            .thenReturn(image);
                });
    }

    protected CustomWebClient createWebClient(String baseUri) {
        return new CustomWebClient(baseUri);
    }

    protected String createBaseUri(URI uri) {
        StringBuilder sb = new StringBuilder();
        if (uri.getScheme() == null || uri.getHost() == null) {
            log.error("Invalid URI: {}", uri);
            return null;
        }
        sb.append(uri.getScheme()).append("://").append(uri.getHost());
        return sb.toString();
    }

    protected String createPath(URI uri) {
        StringBuilder path = new StringBuilder();
        if (uri.getPath() != null) {
            path.append(uri.getPath());
        } else {
            log.error("Path is empty: {}", uri);
            return null;
        }
        if (uri.getQuery() != null) {
            path.append("?").append(uri.getQuery());
        }
        return path.toString();
    }

    protected void handleError(Throwable e, URI uri) {
        log.error("Fetch failed for {}: {}", uri.getPath(), e.getMessage());
    }

    protected void handleFinally(URI uri, SignalType signalType) {
        switch (signalType) {
            case ON_SUBSCRIBE -> log.info("Subscription started: {}", uri);
            case ON_NEXT -> log.info("Data received: {}", uri);
            case ON_COMPLETE -> log.info("Completed: {}", uri);
            case ON_ERROR -> log.warn("Completed with error: {}", uri);
            case CANCEL -> log.warn("Operation was cancelled: {}", uri);
            default -> log.debug("Finished with unknown signal [{}]: {}", signalType, uri);
        }
    }

    protected void handleCansel(URI uri) {
        log.warn("Fetch operation was cancelled {}", uri.getPath());
    }

    protected Mono<HtmlDocument> handleCriticalError(Throwable e, URI uri) {
        log.error("Critical error for {}: {}", uri, e.getMessage());
        return Mono.error(new FetchException("Fetch failed", e));
    }
}
