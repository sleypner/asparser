package com.sleypner.parserarticles;

import com.sleypner.parserarticles.model.services.EmailService;
import com.sleypner.parserarticles.parsing.HttpAction;
import com.sleypner.parserarticles.parsing.Output;
import com.sleypner.parserarticles.parsing.Parser;
import com.sleypner.parserarticles.parsing.Special;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.web.servlet.error.ErrorMvcAutoConfiguration;
import org.springframework.context.annotation.Bean;

import java.io.IOException;
import java.net.URI;
import java.time.LocalDateTime;

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