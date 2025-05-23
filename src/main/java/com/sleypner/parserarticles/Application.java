package com.sleypner.parserarticles;

import com.sleypner.parserarticles.model.services.EmailService;
import com.sleypner.parserarticles.special.HttpAction;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.io.IOException;

@SpringBootApplication(
        scanBasePackages = {"com.sleypner.parserarticles"}

)
public class Application implements CommandLineRunner {

    EmailService emailService;
    HttpAction httpAction;

    public Application(HttpAction httpAction, EmailService emailService) {
        this.httpAction = httpAction;
        this.emailService = emailService;
    }

    public static void main(String[] args) throws IOException {
        SpringApplication.run(Application.class, args);

    }

    @Override
    public void run(String... args) throws Exception {
    }
}