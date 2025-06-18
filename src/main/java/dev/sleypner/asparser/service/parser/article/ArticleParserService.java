package dev.sleypner.asparser.service.parser.article;

import dev.sleypner.asparser.domain.model.Article;
import dev.sleypner.asparser.service.parser.shared.Parser;
import dev.sleypner.asparser.util.DateFormat;
import dev.sleypner.asparser.util.HtmlDocument;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Component
public class ArticleParserService implements Parser<Article> {

    @Override
    public Set<Article> parse(HtmlDocument htmlDocument) {
        Document document = htmlDocument.getDocument();
        Elements elements = document.select("#page_contents div.text");
        return elements.stream().map(element -> {

            Element titleElement = element.selectFirst("p.tema");
            String title = titleElement != null ? titleElement.text() : "";
            String link = titleElement != null ? titleElement.selectFirst("a").attr("href") : "";

            Element articleElement = element.selectFirst("div > div");
            String subtitle = articleElement != null
                    ? Optional.ofNullable(articleElement.selectFirst("p"))
                    .map(Element::text)
                    .orElse("")
                    : "";
            String description = articleElement != null ? articleElement.html() : "";

            Element infoElement = element.selectFirst("p.inf");
            LocalDateTime createdDate = LocalDateTime.from(
                    DateFormat.format(
                            infoElement != null
                                    ? Optional.ofNullable(infoElement.selectFirst("span.col")).map(Element::text).orElse("")
                                    : ""
                    )
            );

            return Article.builder()
                    .title(title)
                    .link(link)
                    .subtitle(subtitle)
                    .description(description)
                    .createOn(createdDate)
                    .build();
        }).collect(Collectors.toSet());
    }
}
