package com.example.HireMi.config;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.config.AbstractMongoClientConfiguration;

@Configuration
public class MongoConfig extends AbstractMongoClientConfiguration {

    @Value("${spring.data.mongodb.database:clearhire}")
    private String databaseName;

    @Override
    protected String getDatabaseName() {
        return databaseName;
    }

    @Bean
    @Override
    public MongoClient mongoClient() {
        String connectionString = "mongodb+srv://thdarklord12345_db_user:8mcZ329BgAm30p81@cluster0.wcwqpgi.mongodb.net/clearhire?retryWrites=true&w=majority&appName=Cluster0";
        System.out.println("🔗 Connecting to MongoDB Atlas: " + connectionString.replaceAll(":[^@]*@", ":***@"));
        return MongoClients.create(connectionString);
    }
}