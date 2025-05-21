package com.sleypner.parserarticles.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class ArticleRestExceptionHandler {
    @ExceptionHandler
    public ResponseEntity<ArticleErrorResponse> handleException(ArticleNotFoundExceptions exc) {
        ArticleErrorResponse errorResponse = new ArticleErrorResponse();
        errorResponse.setStatus(HttpStatus.NOT_FOUND.value());
        errorResponse.setMessage(exc.getMessage());
        errorResponse.setTimeStamp(System.currentTimeMillis());

        return new ResponseEntity<>(errorResponse, HttpStatus.NOT_FOUND);
    }
}
