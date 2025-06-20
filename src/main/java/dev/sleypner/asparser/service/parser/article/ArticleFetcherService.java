package dev.sleypner.asparser.service.parser.article;

import dev.sleypner.asparser.domain.model.Article;
import dev.sleypner.asparser.service.parser.shared.BaseFetcher;
import dev.sleypner.asparser.service.parser.shared.Fetcher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class ArticleFetcherService extends BaseFetcher<Article> implements Fetcher<Article> {

    Logger log = LoggerFactory.getLogger(getClass());

}
