package com.sleypner.parserarticles.model.services;

import jakarta.mail.Session;

import java.util.Properties;

public interface EmailService {

    void sendVerificationCode(String email, int code);

    public Properties getMailProperties();

    public Session getMailSession(Properties props);

    public String getMessage(String username, int code);

    public String getSubject();
}
