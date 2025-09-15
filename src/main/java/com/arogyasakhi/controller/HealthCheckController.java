package com.arogyasakhi.controller;

import org.bson.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HealthCheckController {
    
    @Autowired
    private MongoTemplate mongoTemplate;
    
    @GetMapping("/health")
    public ResponseEntity<String> health() {
        return ResponseEntity.ok("Arogya-Sakhi Bot is running!");
    }
    
    @GetMapping("/health/db")
    public ResponseEntity<String> checkDatabase() {
        try {
            mongoTemplate.getDb().runCommand(new Document("ping", 1));
            return ResponseEntity.ok("Database connection successful");
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Database connection failed: " + e.getMessage());
        }
    }
}
