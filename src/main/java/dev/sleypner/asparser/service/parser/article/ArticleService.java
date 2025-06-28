package dev.sleypner.asparser.service.parser.article;

import dev.sleypner.asparser.domain.model.Article;
import dev.sleypner.asparser.service.parser.shared.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class ArticleService extends BaseOrchestrationService<Article> implements OrchestrationService<Article> {

    Logger log = LoggerFactory.getLogger(getClass());

    protected ArticleService(RepositoryManager<Article> repositoryManager,
                             Fetcher<Article> fetcher,
                             Parser<Article> parser,
                             EntityParserConfig<Article> parserConfig) {
        super(repositoryManager, fetcher, parser, parserConfig);
    }

}
