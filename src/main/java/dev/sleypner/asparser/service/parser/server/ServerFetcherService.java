package dev.sleypner.asparser.service.parser.server;

import dev.sleypner.asparser.domain.model.Server;
import dev.sleypner.asparser.service.parser.shared.BaseFetcher;
import dev.sleypner.asparser.service.parser.shared.Fetcher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class ServerFetcherService extends BaseFetcher<Server> implements Fetcher<Server> {

    Logger log = LoggerFactory.getLogger(getClass());

}
