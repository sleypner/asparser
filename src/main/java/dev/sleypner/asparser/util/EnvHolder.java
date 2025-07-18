package dev.sleypner.asparser.util;

import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

@Component
public class EnvHolder implements ApplicationContextAware {

    private static Environment environment;

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) {
        environment = applicationContext.getEnvironment();
    }

    public static Environment getEnv() {
        return environment;
    }
}