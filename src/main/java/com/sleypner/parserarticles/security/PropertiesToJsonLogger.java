package com.sleypner.parserarticles.security;

import org.springframework.boot.context.event.ApplicationEnvironmentPreparedEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.PropertySource;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.LinkedHashMap;
import java.util.Map;

public class PropertiesToJsonLogger implements ApplicationListener<ApplicationEnvironmentPreparedEvent> {

    private static final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public void onApplicationEvent(ApplicationEnvironmentPreparedEvent event) {
        ConfigurableEnvironment env = event.getEnvironment();
        Map<String, Object> allProperties = new LinkedHashMap<>();

        env.getSystemEnvironment().forEach((key, value) -> {
                allProperties.put(key.toString(), value);
        });

        try {
            String jsonProperties = objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(allProperties);
            System.out.println(jsonProperties);
        } catch (Exception e) {
            System.err.println("Failed to convert properties to JSON: " + e.getMessage());
        }
    }
}
