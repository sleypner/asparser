package dev.sleypner.asparser.service.parser.shared;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.io.IOException;
import java.net.URI;
import java.util.Set;
import java.util.concurrent.TimeoutException;

public abstract class BaseOrchestrationService<T> implements OrchestrationService<T> {

    private final Logger log = LoggerFactory.getLogger(getClass());

    protected final RepositoryManager<T> repositoryManager;
    protected final Fetcher<T> fetcher;
    protected final Parser<T> parser;
    protected final EntityParserConfig<T> parserConfig;

    protected BaseOrchestrationService(
            RepositoryManager<T> repositoryManager,
            Fetcher<T> fetcher,
            Parser<T> parser,
            EntityParserConfig<T> parserConfig) {
        this.repositoryManager = repositoryManager;
        this.fetcher = fetcher;
        this.parser = parser;
        this.parserConfig = parserConfig;
    }

    public Mono<Void> processList() {
        log.info("Starting processing of {} URIs", parserConfig.getUris().size());
        return Flux.fromIterable(parserConfig.getUris())
                .flatMap(uri -> {
                    return processSinglePage(uri)
                            .doOnSubscribe(sub -> handleSubscribe(uri))
                            .onErrorResume(e -> handleError(uri, e));
                })
                .doOnComplete(this::handleComplete)
                .then();
    }

    public Mono<Void> processSinglePage(URI url) {
        return fetcher.fetch(url)
                .flatMap(data -> Mono.just(parser.parse(data)))
                .doOnNext(set -> {
                    Set<T> saved = repositoryManager.save(set);
                    log.info("Saved {}: {}", parserConfig.getName(), saved.size());
                })
                .then();
    }

    protected void handleComplete() {
        log.info("All URIs processed successfully");
    }

    protected Mono<Void> handleError(URI uri, Throwable e) {
        if (e instanceof TimeoutException) {
            log.warn("Timeout for URI {}, retrying...", uri);
            return processSinglePage(uri).retry(2);
        } else if (e instanceof IOException) {
            log.error("Network error for URI {}, skipping", uri);
            return Mono.empty();
        } else {
            log.error("Critical error for URI {}, aborting", uri, e);
            return Mono.error(e);
        }
    }

    protected void handleSubscribe(URI uri) {
        log.trace("Subscription started for URI: {}", uri);
    }
}
