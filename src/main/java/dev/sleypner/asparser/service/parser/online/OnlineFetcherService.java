package dev.sleypner.asparser.service.parser.online;

import dev.sleypner.asparser.domain.model.OnlineStatus;
import dev.sleypner.asparser.service.parser.shared.BaseFetcher;
import dev.sleypner.asparser.service.parser.shared.Fetcher;
import org.springframework.stereotype.Service;

@Service
public class OnlineFetcherService extends BaseFetcher<OnlineStatus> implements Fetcher<OnlineStatus> {

}
