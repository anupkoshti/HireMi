package com.example.HireMi.config;

import io.github.cdimascio.dotenv.Dotenv;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.MapPropertySource;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
public class DotEnvConfig implements ApplicationContextInitializer<ConfigurableApplicationContext> {
    @Override
    public void initialize(ConfigurableApplicationContext applicationContext) {
        ConfigurableEnvironment environment = applicationContext.getEnvironment();

        try {
            Dotenv dotenv = Dotenv.configure()
                    .directory(".")
                    .ignoreIfMalformed()
                    .ignoreIfMissing()
                    .load();

            Map<String, Object> dotenvProperties = new HashMap<>();
            dotenv.entries().forEach(entry -> {
                dotenvProperties.put(entry.getKey(), entry.getValue());
                System.out.println("Loaded env var: " + entry.getKey() + " = " + 
                    (entry.getKey().contains("PASSWORD") || entry.getKey().contains("SECRET") ? "***" : entry.getValue()));
            });

            environment.getPropertySources().addFirst(new MapPropertySource("dotenv", dotenvProperties));
            System.out.println("✅ Successfully loaded .env file with " + dotenvProperties.size() + " properties");
        } catch (Exception e) {
            System.err.println("❌ Warning: Could not load .env file: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
