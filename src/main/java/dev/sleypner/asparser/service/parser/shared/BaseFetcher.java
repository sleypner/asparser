package dev.sleypner.asparser.service.parser.shared;

import dev.sleypner.asparser.exceptions.FetchException;
import dev.sleypner.asparser.http.CustomWebClient;
import dev.sleypner.asparser.util.HtmlDocument;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import reactor.core.publisher.Mono;
import reactor.core.publisher.SignalType;

import java.net.URI;

@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public abstract class BaseFetcher<T> implements Fetcher<T> {
    protected final Logger log = LoggerFactory.getLogger(getClass());

    @Override
    public Mono<HtmlDocument> fetch(URI uri) {

        log.info("Starting fetch for URI: {}", uri);

        return Mono.defer(() -> {

                    String baseUri = createBaseUri(uri);
                    String path = createPath(uri);
                    log.info("Creating WebClient for Host: {}", baseUri);

                    return createWebClient(baseUri)
                            .parsePage(path)
                            .doOnSubscribe(subscription -> log.info("Subscribed to page parsing: {}", uri))
                            .doOnSuccess(document -> handleSuccess(document, uri))
                            .doOnError(e -> handleError(e, uri))
                            .doOnCancel(() -> handleCansel(uri))
                            .doFinally(signal -> handleFinally(uri, signal));

                })
                .onErrorResume(e -> handleCriticalError(e, uri));
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
        }else {
            log.error("Path is empty in URI: {}", uri);
            return null;
        }
        if (uri.getQuery() != null) {
            path.append("?").append(uri.getQuery());
        }
        return path.toString();
    }
    protected void handleSuccess(HtmlDocument doc, URI uri) {
        log.info("Successfully fetched: {}", uri);
    }

    protected void handleError(Throwable e, URI uri) {
        log.error("Fetch failed for {}: {}", uri.getPath(), e.getMessage());
    }

    protected void handleFinally(URI uri, SignalType signalType) {
        switch (signalType) {
            case ON_SUBSCRIBE -> log.info("Subscription started for URI: {}", uri);
            case ON_NEXT      -> log.info("Data received for URI: {}", uri);
            case ON_COMPLETE  -> log.info("Successfully completed for URI: {}", uri);
            case ON_ERROR     -> log.warn("Completed with error for URI: {}", uri);
            case CANCEL       -> log.warn("Operation was cancelled for URI: {}", uri);
            default           -> log.debug("Finished with unknown signal [{}] for URI: {}", signalType, uri);
        }
    }

    protected void handleCansel(URI uri) {
        log.warn("Fetch operation was cancelled for {}", uri.getPath());
    }

    protected Mono<HtmlDocument> handleCriticalError(Throwable e, URI uri) {
        log.error("Critical error for {}: {}", uri, e.getMessage());
        return Mono.error(new FetchException("Fetch failed", e));
    }
}
