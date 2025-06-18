package dev.sleypner.asparser.service.parser.shared;

import reactor.core.publisher.Mono;

public interface OrchestrationService<T> {
    Mono<Void> processList();
}
