package com.sleypner.parserarticles.model.services;

import jakarta.mail.Session;

import java.util.Properties;

public interface EmailService {

    boolean sendCode(String email, int code);

    Properties getMailProperties();

    Session getMailSession(Properties props);

    String getMessage(String username, int code);

    String getSubject();
}
