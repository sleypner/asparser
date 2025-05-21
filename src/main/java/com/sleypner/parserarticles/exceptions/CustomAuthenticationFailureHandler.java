package com.sleypner.parserarticles.exceptions;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;

import java.io.IOException;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

public class CustomAuthenticationFailureHandler implements AuthenticationFailureHandler {
    Logger logger = LoggerFactory.getLogger(CustomAuthenticationFailureHandler.class);

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public void onAuthenticationFailure(
            HttpServletRequest request,
            HttpServletResponse response,
            AuthenticationException exception) {

        response.setStatus(HttpStatus.UNAUTHORIZED.value());
        Map<String, Object> data = new HashMap<>();
        data.put(
                "timestamp",
                Calendar.getInstance().getTime());
        data.put(
                "exception",
                exception.getMessage());

        try {
            response.getOutputStream()
                    .println(objectMapper.writeValueAsString(data));
        } catch (IOException e) {
            logger.atError()
                    .addKeyValue("exception_class", e.getClass().getSimpleName())
                    .addKeyValue("error_message", e.getMessage())
                    .log();
        }
    }
}
