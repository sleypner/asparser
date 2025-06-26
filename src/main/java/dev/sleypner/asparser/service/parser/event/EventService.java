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
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
public class EventService extends BaseOrchestrationService<Event> implements OrchestrationService<Event> {

    private final Logger log = LoggerFactory.getLogger(getClass());
    private final RepositoryManager<Event> eventRepository;
    private final DateRepository<Event> dateRepository;
    private final ServerPersistenceImpl sp;
    private final Fetcher<Event> fetcher;
    private final Parser<Event> parserService;
    private final EntityParserConfig<Event> parserConfig;
    private final RaidBossesConverterServices converter;
    private final RepositoryManager<RaidBoss> rRaidBosses;

    protected EventService(RepositoryManager<Event> eventRepository,
                           DateRepository<Event> dateRepository,
                           Fetcher<Event> fetcher,
                           Parser<Event> parser,
                           EntityParserConfig<Event> parserConfig,
                           ServerPersistenceImpl sp,
                           RaidBossesConverterServices converter,
                           RepositoryManager<RaidBoss> rRaidBosses) {
        super(eventRepository, fetcher, parser, parserConfig, "Event");
        this.eventRepository = eventRepository;
        this.dateRepository = dateRepository;
        this.sp = sp;
        this.fetcher = fetcher;
        this.parserService = parser;
        this.parserConfig = parserConfig;
        this.converter = converter;
        this.rRaidBosses = rRaidBosses;
    }

    @Override
    public Mono<Void> processSinglePage(URI uri) {
        return fetcher.fetch(uri)
                .publishOn(Schedulers.boundedElastic())
                .doOnNext(doc -> {
                    List<Server> servers = sp.getAll();
                    LocalDateTime LastEntryDate = dateRepository.getLastDate("date");
                    doc.setServers(new HashSet<>(servers))
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
                    Set<RaidBoss> savedBosses = rRaidBosses.save(bosses);
                    log.debug("Saved bosses: {}", savedBosses.size());


                    Set<Event> saved = eventRepository.save(events);
                    log.debug("Saved events: {}", saved.size());
                })
                .then();
    }

}
