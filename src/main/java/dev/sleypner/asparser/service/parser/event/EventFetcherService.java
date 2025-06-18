package dev.sleypner.asparser.service.parser.event;

import dev.sleypner.asparser.domain.model.Event;
import dev.sleypner.asparser.service.parser.shared.BaseFetcher;
import dev.sleypner.asparser.service.parser.shared.Fetcher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class EventFetcherService extends BaseFetcher<Event> implements Fetcher<Event> {

    Logger log = LoggerFactory.getLogger(getClass());

}
