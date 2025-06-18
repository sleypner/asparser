package dev.sleypner.asparser.service.parser.fortress;

import dev.sleypner.asparser.domain.model.Clan;
import dev.sleypner.asparser.domain.model.Fortress;
import dev.sleypner.asparser.domain.model.FortressHistory;
import dev.sleypner.asparser.domain.model.FortressSkill;
import dev.sleypner.asparser.service.parser.shared.*;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.net.URI;

@Service
@RequiredArgsConstructor(onConstructor_ = {@Autowired})
public class FortressService implements OrchestrationService<Fortress> {

    Logger log = LoggerFactory.getLogger(getClass());

    private final PersistenceManager<Fortress> pmFortress;
    private final PersistenceManager<FortressSkill> pmFortressSkills;
    private final PersistenceManager<FortressHistory> pmFortressHistory;
    private final PersistenceManager<Clan> pmClan;
    private final Fetcher<Fortress> fetcherService;
    private final Parser<FortressData> parserService;
    private final EntityParserConfig<Fortress> parserConfig;

    @Override
    public Mono<Void> processList() {
        return Flux.fromIterable(parserConfig.getUris())
                .flatMap(uri -> processSinglePage(uri)
                        .doOnSubscribe(s -> log.info("Processing URI: {}", uri))
                        .onErrorResume(e -> {
                            log.error("Failed to process URI: {}", uri, e);
                            return Mono.empty();
                        }))
                .then();
    }

    private Mono<Void> processSinglePage(URI uri) {
        return fetcherService.fetch(uri).flatMap(data -> Mono.justOrEmpty(parserService.parse(data)))
                .flatMapMany(Flux::fromIterable)
                .flatMap(this::processFortressData, 4)
                .onErrorResume(e -> {
                    log.error("Failed to process URI: {}", uri, e);
                    return Mono.error(e);
                })
                .then();
    }

    private Mono<Void> processFortressData(FortressData data) {
        return Mono.fromCallable(() -> {
                    return pmFortress.save(data.getFortress());

                })
                .flatMap(savedFortress -> {
                    data.setFortress(savedFortress);

                    return Mono.when(
                            data.getClanUrl() != null ? fetchAndSaveClan(data) : Mono.empty(),
                            data.getFortressHistory() != null ? saveHistory(data) : Mono.empty()
                    );
                })
                .onErrorResume(e -> {
                    log.error("Error processing Fortress", e);
                    return Mono.empty();
                });
    }

    private Mono<Void> fetchAndSaveClan(FortressData data) {
        return fetcherService.fetch(URI.create(data.getClanUrl()))
                .flatMap(clanHtml -> {
                    Clan clan = FortressParserService.parseClan(clanHtml);
                    Clan SavedClan = pmClan.save(clan);
                    data.setClan(SavedClan);
                    return Mono.fromCallable(() -> SavedClan);
                })
                .then()
                .onErrorResume(e -> {
                    log.error("Failed to process Clan", e);
                    return Mono.empty();
                });
    }

    private Mono<Void> saveHistory(FortressData data) {
        return Mono.fromCallable(() -> {
            data.getFortressHistory().setFortressId(data.getFortress().getId());
            if (data.getClan() != null) {
                data.getFortressHistory().setClanId(data.getClan().getId());
            }
            return pmFortressHistory.save(data.getFortressHistory());
        }).then();
    }

}
