package dev.sleypner.asparser.service.parser.online;

import dev.sleypner.asparser.domain.model.OnlineStatus;
import dev.sleypner.asparser.domain.model.Server;
import dev.sleypner.asparser.service.parser.shared.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.net.URI;
import java.util.HashSet;
import java.util.Set;

@Service
public class OnlineService extends BaseOrchestrationService<OnlineStatus> implements OrchestrationService<OnlineStatus> {

    private final Logger log = LoggerFactory.getLogger(getClass());
    private final RepositoryManager<Server> serverPersistence;

    protected OnlineService(RepositoryManager<OnlineStatus> repositoryManager,
                            Fetcher<OnlineStatus> fetcher,
                            Parser<OnlineStatus> parser,
                            EntityParserConfig<OnlineStatus> parserConfig,
                            RepositoryManager<Server> serverPersistence) {
        super(repositoryManager, fetcher, parser, parserConfig);
        this.serverPersistence = serverPersistence;
    }

    @Override
    public Mono<Void> processSinglePage(URI url) {
        return fetcher.fetch(url)
                .flatMap(data -> {
                    data.setServers(new HashSet<>(serverPersistence.getAll()));
                    return Mono.just(parser.parse(data));
                })
                .doOnNext(set -> {
                    Set<OnlineStatus> saved = repositoryManager.save(set);
                    log.info("Saved {}: {}", parserConfig.getName(), saved.size());
                })
                .then();
    }
}
