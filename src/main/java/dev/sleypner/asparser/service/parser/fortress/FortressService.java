package dev.sleypner.asparser.service.parser.fortress;

import dev.sleypner.asparser.domain.model.*;
import dev.sleypner.asparser.service.parser.shared.*;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;
import reactor.util.retry.Retry;

import java.net.URI;
import java.time.Duration;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor(onConstructor_ = {@Autowired})
public class FortressService implements OrchestrationService<Fortress> {

    Logger log = LoggerFactory.getLogger(getClass());

    private final FortressParserService fortressParserService;
    private final Fetcher<Fortress> fetcherService;
    private final Parser<FortressData> parserService;
    private final EntityParserConfig<Fortress> parserConfig;

    private final RepositoryManager<Server> serverRepository;
    private final RepositoryManager<Fortress> fortressRepository;
    private final RepositoryManager<Image> imageRepository;
    private final RepositoryManager<FortressSkill> fortressSkillRepository;
    private final RepositoryManager<Clan> clanRepository;
    private final RepositoryManager<FortressHistory> fortressHistoryRepository;

    private Set<Server> serverList;

    @PostConstruct
    public void init() {
        serverList = new HashSet<>(serverRepository.getAll());
    }

    @Override
    public Mono<Void> processList() {
        return Flux.fromIterable(parserConfig.getUris())
                .flatMap(uri -> processSinglePage(uri)
                        .doOnSubscribe(_ -> log.info("Processing URI: {}", uri))
                        .onErrorResume(e -> {
                            log.error("Failed to process URI: {}", uri, e);
                            return Mono.empty();
                        }))
                .then();
    }

    private Mono<Void> processSinglePage(URI uri) {
        return fetcherService.fetch(uri).flatMap(document -> {
                            return Mono.justOrEmpty(parserService.parse(document.setServers(serverList)));
                        }
                )
                .flatMapMany(Flux::fromIterable)
                .flatMap(this::processFortressData, 4)
                .then();
    }

    private Mono<Void> processFortressData(FortressData data) {

        Fortress fortress = data.getFortress();

        Mono<Fortress> fortressMono = processImage(fortress.getImage())
                .map(fortress::setImage);

        Mono<Set<FortressSkill>> skillsMono = Mono.justOrEmpty(data.getFortressSkills())
                .filter(s -> !s.isEmpty())
                .flatMapMany(Flux::fromIterable)
                .flatMap(skill -> {
                    Image image = skill.getImage();
                    skill.setImage(null);

                    return processFortressSkill(skill)
                            .flatMap(savedSkill -> {
                                image.setFortressSkill(savedSkill);
                                return processImage(image)
                                        .map(savedImage -> {
                                            savedSkill.setImage(savedImage);
                                            return savedSkill;
                                        });
                            });
                })
                .collect(Collectors.toCollection(HashSet::new));

        Mono<Fortress> saveFortressMono = fortressMono
                .flatMap(fortressToSave -> skillsMono
                        .flatMap(skills -> {
                            fortressToSave.setSkills(skills);

                            return Mono.fromCallable(() -> fortressRepository.save(fortressToSave))
                                    .subscribeOn(Schedulers.boundedElastic())
                                    .retryWhen(Retry.backoff(3, Duration.ofMillis(200)))
                                    .onErrorResume(e -> {
                                        log.error("Failed to save fortress: {} after retry", fortressToSave, e);
                                        return Mono.empty();
                                    });
                        })
                );
        //--

        return saveFortressMono.flatMap(savedFortress -> {
            data.setFortress(savedFortress);
            // Clan image
            return saveRest(data);
            //--
        }).onErrorResume(e -> {
            log.error("Error processing Fortress", e);
            return Mono.empty();
        });
    }

    private Mono<FortressSkill> processFortressSkill(FortressSkill fortressSkill) {

        Mono<FortressSkill> fortressSkillMono = Mono.justOrEmpty(
                fortressSkillRepository.getByName(fortressSkill.getName())
        );

        return fortressSkillMono
                .switchIfEmpty(Mono.defer(() -> {
                    return Mono.fromCallable(() -> fortressSkillRepository.save(fortressSkill))
                            .subscribeOn(Schedulers.boundedElastic())
                            .onErrorResume(e -> {
                                log.error("Failed to save Skill: {}", fortressSkill, e);
                                return Mono.empty();
                            });
                }))
                .retryWhen(Retry.backoff(3, Duration.ofMillis(200)))
                .onErrorResume(e -> {
                    log.error("Failed to process skills after retry", e);
                    return Mono.empty();
                });
    }

    private Mono<Image> processImage(Image image) {
        return Mono.fromCallable(() -> {
                    return imageRepository.getByName(image.getExternalName())
                            .orElseGet(() -> imageRepository.save(image));
                })
                .flatMap(fetcherService::fetchImages)
                .subscribeOn(Schedulers.boundedElastic())
                .retryWhen(Retry.backoff(3, Duration.ofMillis(200)))
                .onErrorResume(e -> {
                    log.error("Failed to save Image: {} after retry", image, e);
                    return Mono.empty();
                });
    }

    private Mono<Void> saveRest(FortressData data) {
        if (data.getClanUrl() != null && data.getFortressHistory() != null) {
            return processClan(data)
                    .then(processFortressHistory(data));
        } else if (data.getClanUrl() != null) {
            return processClan(data);
        } else if (data.getFortressHistory() != null) {
            return processFortressHistory(data);
        } else {
            return Mono.empty();
        }
    }

    private Mono<Void> processClan(FortressData data) {
        return fetcherService.fetch(URI.create(data.getClanUrl()))
                .flatMap(document -> {
                    Clan clan = fortressParserService.parseClan(document.setServers(serverList));
                    Image image = clan.getImage();
                    clan.setImage(null);
                    Mono<Clan> clanMono = Mono.defer(() -> {
                        return Mono.fromCallable(() -> clanRepository.save(clan))
                                .subscribeOn(Schedulers.boundedElastic())
                                .retryWhen(Retry.backoff(3, Duration.ofMillis(200)))
                                .onErrorResume(e -> {
                                    log.error("Failed to save Clan: {} after retry", clan, e);
                                    return Mono.empty();
                                });
                    });

                    return clanMono.flatMap(savedClan -> {
                        data.setClan(savedClan);
                        if (image == null) {
                            return Mono.just(savedClan);
                        }
                        image.setClan(savedClan);
                        return processImage(image).flatMap(savedImage -> {
                            return Mono.just(savedClan.setImage(savedImage));
                        });
                    });
                })
                .then()
                .onErrorResume(e -> {
                    log.error("Failed to process Clan", e);
                    return Mono.empty();
                });
    }

    private Mono<Void> processFortressHistory(FortressData data) {
        return Mono.fromCallable(() -> {
                    FortressHistory fortressHistory = data.getFortressHistory();
                    fortressHistory.setFortressId(data.getFortress().getId());
                    if (data.getClan() != null) {
                        fortressHistory.setClanId(data.getClan().getId());
                    }
                    fortressHistoryRepository.save(fortressHistory);
                    return null;
                }).subscribeOn(Schedulers.boundedElastic())
                .then();
    }

}
