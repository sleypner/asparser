package dev.sleypner.asparser.service.parser.online;

import dev.sleypner.asparser.domain.model.OnlineStatus;
import dev.sleypner.asparser.service.parser.shared.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class OnlineService extends BaseOrchestrationService<OnlineStatus> implements OrchestrationService<OnlineStatus> {

    Logger log = LoggerFactory.getLogger(getClass());

    protected OnlineService(PersistenceManager<OnlineStatus> persistenceManager,
                            Fetcher<OnlineStatus> fetcher,
                            Parser<OnlineStatus> parser,
                            EntityParserConfig<OnlineStatus> parserConfig) {
        super(persistenceManager, fetcher, parser, parserConfig, "OnlineStatus");
    }
}
