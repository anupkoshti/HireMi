package com.example.HireMi;

import com.example.HireMi.config.DotEnvConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Profile;
import org.springframework.data.mongodb.core.MongoTemplate;

@SpringBootApplication
public class HireMiApplication implements CommandLineRunner {

	@Autowired(required = false)
	private MongoTemplate mongoTemplate;

	public static void main(String[] args) {
		SpringApplication app = new SpringApplication(HireMiApplication.class);
		app.addInitializers(new DotEnvConfig());
		app.run(args);
	}

	@Override
	public void run(String... args) throws Exception {
		if (mongoTemplate != null) {
			try {
				// Test database connection on startup
				mongoTemplate.getDb().runCommand(new org.bson.Document("ping", 1));
				System.out.println("✅ MongoDB connection successful!");
				System.out.println("📊 Database: " + mongoTemplate.getDb().getName());
			} catch (Exception e) {
				System.err.println("❌ MongoDB connection failed: " + e.getMessage());
				System.err.println("🔧 Please check your MongoDB connection settings");
			}
		}
	}
}
