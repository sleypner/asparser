package dev.sleypner.asparser.util;

import dev.sleypner.asparser.domain.model.Image;
import dev.sleypner.asparser.domain.model.Server;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.experimental.Accessors;
import org.jsoup.nodes.Document;

import java.time.LocalDateTime;
import java.util.Set;

@Data
@RequiredArgsConstructor
@Accessors(chain = true)
public class HtmlDocument {
    private Document document;
    private Set<HtmlElement> htmlElements;
    private Set<Server> servers;
    private LocalDateTime LastEntryDate;
    private Image image;

    public HtmlDocument(Document document, Set<HtmlElement> htmlElements) {
        this.document = document;
        this.htmlElements = htmlElements;
    }

    public HtmlDocument(Document document) {
        this.document = document;
    }
}
