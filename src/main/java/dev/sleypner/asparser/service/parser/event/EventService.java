package dev.sleypner.asparser.service.parser.event;

import dev.sleypner.asparser.domain.model.Event;
import dev.sleypner.asparser.domain.model.RaidBoss;
import dev.sleypner.asparser.service.parser.bosses.RaidBossesConverterServices;
import dev.sleypner.asparser.service.parser.server.persistence.ServerPersistenceImpl;
import dev.sleypner.asparser.service.parser.shared.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.net.URI;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
public class EventService extends BaseOrchestrationService<Event> implements OrchestrationService<Event> {

    private final Logger log = LoggerFactory.getLogger(getClass());
    private final RepositoryManager<Event> pmEvent;
    private final DateRepository<Event> dateRepository;
    private final ServerPersistenceImpl sp;
    private final Fetcher<Event> fetcherService;
    private final Parser<Event> parserService;
    private final EntityParserConfig<Event> parserConfig;
    private final RaidBossesConverterServices converter;
    private final RepositoryManager<RaidBoss> pmRaidBosses;

    protected EventService(RepositoryManager<Event> RepositoryManager,
                           DateRepository<Event> dateRepository,
                           Fetcher<Event> fetcher,
                           Parser<Event> parser,
                           EntityParserConfig<Event> parserConfig,
                           ServerPersistenceImpl sp,
                           RaidBossesConverterServices converter,
                           RepositoryManager<RaidBoss> pmRaidBosses) {
        super(RepositoryManager, fetcher, parser, parserConfig);
        this.pmEvent = RepositoryManager;
        this.dateRepository = dateRepository;
        this.sp = sp;
        this.fetcherService = fetcher;
        this.parserService = parser;
        this.parserConfig = parserConfig;
        this.converter = converter;
        this.pmRaidBosses = pmRaidBosses;
    }

    @Override
    public Mono<Void> processList() {
        List<URI> uris = parserConfig.getUris(sp.getAll());
        log.info("Starting processing of {} URIs", uris.size());
        return Flux.fromIterable(uris)
                .flatMap(uri -> {
                    log.info("Processing URI: {}", uri);
                    return processSinglePage(uri)
                            .doOnSubscribe(sub -> handleSubscribe(uri))
                            .onErrorResume(e -> handleError(uri, e));
                })
                .doOnComplete(this::handleComplete)
                .then();
    }

    @Override
    public Mono<Void> processSinglePage(URI uri) {
        return fetcherService.fetch(uri)
                .publishOn(Schedulers.boundedElastic())
                .doOnNext(doc -> {
                    LocalDateTime lastEntryDate = dateRepository.getLastDate("date");
                    doc.setServers(new HashSet<>(sp.getAll()))
                            .setLastEntryDate(lastEntryDate);
                })
                .flatMap(data -> {
                    try {
                        return Mono.just(parserService.parse(data));
                    } catch (Exception e) {
                        return Mono.error(e);
                    }
                })
                .doOnNext(events -> {

                    Set<RaidBoss> bosses = converter.convert(events);
                    Set<RaidBoss> savedBosses = pmRaidBosses.save(bosses);
                    log.info("Saved bosses: {}", savedBosses.size());


                    Set<Event> saved = pmEvent.save(events);
                    log.info("Saved events: {}", saved.size());
                })
                .then();
    }

}
