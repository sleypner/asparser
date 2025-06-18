package dev.sleypner.asparser.service.parser.article;

import dev.sleypner.asparser.domain.model.Article;
import dev.sleypner.asparser.service.parser.shared.EntityParserConfig;
import lombok.Data;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;

import java.net.URI;
import java.util.List;

@Configuration
@Data
public class ArticleParserConfig implements EntityParserConfig<Article> {
    private String uri = "https://asterios.tm/index.php?js=1";
    private String name = "articles";

    @Override
    public List<URI> getUris() {
        return List.of(URI.create(uri));
    }
}
