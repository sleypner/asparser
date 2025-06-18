package dev.sleypner.asparser.service.parser.event;

import dev.sleypner.asparser.domain.model.Event;
import dev.sleypner.asparser.domain.model.RaidBoss;
import dev.sleypner.asparser.domain.model.Server;
import dev.sleypner.asparser.service.parser.bosses.RaidBossesConverterServices;
import dev.sleypner.asparser.service.parser.server.persistence.ServerPersistenceImpl;
import dev.sleypner.asparser.service.parser.shared.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.net.URI;
import java.time.LocalDateTime;
import java.util.Set;

@Service
public class EventService extends BaseOrchestrationService<Event> implements OrchestrationService<Event> {

    private final Logger log = LoggerFactory.getLogger(getClass());
    private final PersistenceManager<Event> pmEvent;
    private final ServerPersistenceImpl sp;
    private final Fetcher<Event> fetcherService;
    private final Parser<Event> parserService;
    private final EntityParserConfig<Event> parserConfig;
    private final RaidBossesConverterServices converter;
    private final PersistenceManager<RaidBoss> pmRaidBosses;

    protected EventService(PersistenceManager<Event> persistenceManager,
                           Fetcher<Event> fetcher,
                           Parser<Event> parser,
                           EntityParserConfig<Event> parserConfig,
                           ServerPersistenceImpl sp,
                           RaidBossesConverterServices converter,
                           PersistenceManager<RaidBoss> pmRaidBosses) {
        super(persistenceManager, fetcher, parser, parserConfig, "Event");
        this.pmEvent = persistenceManager;
        this.sp = sp;
        this.fetcherService = fetcher;
        this.parserService = parser;
        this.parserConfig = parserConfig;
        this.converter = converter;
        this.pmRaidBosses = pmRaidBosses;
    }

    @Override
    public Mono<Void> processSinglePage(URI uri) {
        return fetcherService.fetch(uri)
                .publishOn(Schedulers.boundedElastic())
                .doOnNext(doc -> {
                    Set<Server> servers = sp.getAll();
                    LocalDateTime LastEntryDate = pmEvent.getLastDate("date");
                    doc.setServers(servers)
                            .setLastEntryDate(LastEntryDate);
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
                    log.debug("Saved bosses: {}", savedBosses.size());


                    Set<Event> saved = pmEvent.save(events);
                    log.debug("Saved events: {}", saved.size());
                })
                .then();
    }

}
