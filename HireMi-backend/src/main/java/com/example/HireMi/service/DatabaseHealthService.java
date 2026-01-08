package com.example.HireMi.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Service;

@Service
public class DatabaseHealthService {

    @Autowired
    private MongoTemplate mongoTemplate;

    public boolean isDatabaseConnected() {
        try {
            mongoTemplate.getDb().runCommand(new org.bson.Document("ping", 1));
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public String getDatabaseStatus() {
        try {
            mongoTemplate.getDb().runCommand(new org.bson.Document("ping", 1));
            return "Connected to: " + mongoTemplate.getDb().getName();
        } catch (Exception e) {
            return "Connection failed: " + e.getMessage();
        }
    }

    public long getCollectionCount(String collectionName) {
        try {
            return mongoTemplate.getCollection(collectionName).countDocuments();
        } catch (Exception e) {
            throw new RuntimeException("Failed to count documents in collection: " + collectionName, e);
        }
    }
}