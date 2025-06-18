package dev.sleypner.asparser.service.parser.fortress;

import dev.sleypner.asparser.domain.model.Fortress;
import dev.sleypner.asparser.service.parser.shared.BaseFetcher;
import dev.sleypner.asparser.service.parser.shared.Fetcher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class FortressFetcherService extends BaseFetcher<Fortress> implements Fetcher<Fortress> {

    Logger log = LoggerFactory.getLogger(getClass());

}
