package dev.sleypner.asparser.service.parser.online;

import dev.sleypner.asparser.domain.model.OnlineStatus;
import dev.sleypner.asparser.service.parser.shared.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class OnlineService extends BaseOrchestrationService<OnlineStatus> implements OrchestrationService<OnlineStatus> {

    Logger log = LoggerFactory.getLogger(getClass());

    protected OnlineService(RepositoryManager<OnlineStatus> repositoryManager,
                            Fetcher<OnlineStatus> fetcher,
                            Parser<OnlineStatus> parser,
                            EntityParserConfig<OnlineStatus> parserConfig) {
        super(repositoryManager, fetcher, parser, parserConfig, "OnlineStatus");
    }
}
