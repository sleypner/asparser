package dev.sleypner.asparser.service.parser.server;

import dev.sleypner.asparser.domain.model.Server;
import dev.sleypner.asparser.service.parser.shared.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class ServerService extends BaseOrchestrationService<Server> implements OrchestrationService<Server> {

    Logger log = LoggerFactory.getLogger(getClass());

    protected ServerService(RepositoryManager<Server> repositoryManager,
                            Fetcher<Server> fetcher,
                            Parser<Server> parser,
                            EntityParserConfig<Server> parserConfig) {
        super(repositoryManager, fetcher, parser, parserConfig, "Server");
    }
}
