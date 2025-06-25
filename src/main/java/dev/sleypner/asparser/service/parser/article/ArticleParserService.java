package dev.sleypner.asparser.service.parser.article;

import dev.sleypner.asparser.domain.model.Article;
import dev.sleypner.asparser.service.parser.shared.Parser;
import dev.sleypner.asparser.util.DateFormat;
import dev.sleypner.asparser.util.HtmlDocument;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Component;

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
            String title = Optional.ofNullable(titleElement).map(Element::text).orElse("");

            String link = Optional.ofNullable(titleElement)
                    .map(el -> el.selectFirst("a"))
                    .map(a -> a.attr("href"))
                    .orElse("");

            Element articleElement = element.selectFirst("div > div");
            String subtitle = Optional.ofNullable(articleElement)
                    .map(el -> el.selectFirst("p"))
                    .map(Element::text)
                    .orElse("");

            String description = Optional.ofNullable(articleElement)
                    .map(Element::html)
                    .orElse("");

            LocalDateTime createdDate = Optional.ofNullable(element.selectFirst("p.inf"))
                    .map(el -> el.selectFirst("span.col"))
                    .map(Element::text)
                    .map(DateFormat::format)
                    .map(LocalDateTime::from)
                    .orElse(null);

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
