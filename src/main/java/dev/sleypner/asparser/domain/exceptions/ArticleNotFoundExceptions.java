package dev.sleypner.asparser.domain.exceptions;

public class ArticleNotFoundExceptions extends RuntimeException {
    public ArticleNotFoundExceptions(String message) {
        super(message);
    }
}
