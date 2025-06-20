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

                    String baseUri = buildBaseUrl(uri);
                    log.debug("Creating WebClient for Host: {}", baseUri);

                    return createWebClient(baseUri)
                            .parsePage(uri.getPath())
                            .doOnSubscribe(subscription -> log.debug("Subscribed to page parsing: {}", uri.getPath()))
                            .doOnSuccess(document -> handleSuccess(document, uri))
                            .doOnError(e -> handleError(e, uri))
                            .doOnCancel(() -> handleCansel(uri))
                            .doFinally(signal -> handleFinally(uri, null));

                })
                .onErrorResume(e -> handleCriticalError(e, uri));
    }

    protected String buildBaseUrl(URI uri) {
        return uri.getScheme() + "://" + uri.getHost();
    }

    protected CustomWebClient createWebClient(String baseUri) {
        return new CustomWebClient(baseUri);
    }

    protected void handleSuccess(HtmlDocument doc, URI uri) {
        log.info("Successfully fetched: {}", uri.getPath());
    }

    protected void handleError(Throwable e, URI uri) {
        log.error("Fetch failed for {}: {}", uri.getPath(), e.getMessage());
    }

    protected void handleFinally(URI uri, SignalType signal) {
        log.debug("Completed fetch operation for {} with signal: {}", uri.getPath(), signal);
    }

    protected void handleCansel(URI uri) {
        log.warn("Fetch operation was cancelled for {}", uri.getPath());
    }

    protected Mono<HtmlDocument> handleCriticalError(Throwable e, URI uri) {
        log.error("Critical error for {}: {}", uri, e.getMessage());
        return Mono.error(new FetchException("Fetch failed", e));
    }
}
