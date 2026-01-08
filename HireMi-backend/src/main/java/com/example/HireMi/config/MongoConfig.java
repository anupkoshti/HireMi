package com.example.HireMi.config;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.data.mongodb.core.MongoTemplate;

@Configuration
public class MongoConfig {

    @Value("${MONGODB_URI}")
    private String mongoUri;

    @Value("${spring.data.mongodb.database:clearhire}")
    private String databaseName;

    @Bean
    @Primary
    public MongoClient mongoClient() {
        System.out.println("🔗 Connecting to MongoDB Atlas: " + mongoUri.replaceAll(":[^@]*@", ":***@"));
        return MongoClients.create(mongoUri);
    }

    @Bean
    @Primary
    public MongoTemplate mongoTemplate() {
        return new MongoTemplate(mongoClient(), databaseName);
    }
}