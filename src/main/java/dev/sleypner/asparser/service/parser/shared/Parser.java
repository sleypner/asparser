package dev.sleypner.asparser.service.parser.shared;

import dev.sleypner.asparser.util.HtmlDocument;

import java.util.Set;

public interface Parser<T> {
    Set<T> parse(HtmlDocument document);
}
