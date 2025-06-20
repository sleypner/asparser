package dev.sleypner.asparser.exceptions;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class ArticleErrorResponse {
    private int status;
    private String message;
    private long timeStamp;

    public ArticleErrorResponse() {
    }

}
