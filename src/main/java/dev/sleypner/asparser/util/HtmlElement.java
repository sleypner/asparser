package dev.sleypner.asparser.util;

import lombok.Data;
import lombok.experimental.Accessors;
import org.jsoup.select.Elements;

@Data
@Accessors(chain = true)
public class HtmlElement {

    private final String tagName;
    private final String text;
    private final Elements children;

    public HtmlElement(String tagName, String text, Elements children) {
        this.tagName = tagName;
        this.text = text;
        this.children = children;
    }

}