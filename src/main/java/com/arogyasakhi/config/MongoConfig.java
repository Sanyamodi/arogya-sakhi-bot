package com.arogyasakhi.config;

import com.mongodb.ConnectionString;
import com.mongodb.MongoClientSettings;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.config.AbstractMongoClientConfiguration;
import org.springframework.data.mongodb.core.MongoTemplate;

import java.util.concurrent.TimeUnit;

@Configuration
public class MongoConfig extends AbstractMongoClientConfiguration {
    
    @Value("${spring.data.mongodb.uri}")
    private String mongoUri;
    
    @Override
    protected String getDatabaseName() {
        return "arogya-sakhi";
    }
    
    @Override
    @Bean
    public MongoClient mongoClient() {
        try {
            ConnectionString connectionString = new ConnectionString(mongoUri);
            MongoClientSettings mongoClientSettings = MongoClientSettings.builder()
                    .applyConnectionString(connectionString)
                    .applyToSocketSettings(builder -> 
                        builder.connectTimeout(10, TimeUnit.SECONDS)
                               .readTimeout(10, TimeUnit.SECONDS))
                    .applyToClusterSettings(builder -> 
                        builder.serverSelectionTimeout(10, TimeUnit.SECONDS))
                    .build();
            
            return MongoClients.create(mongoClientSettings);
        } catch (Exception e) {
            System.err.println("❌ Error creating MongoDB client: " + e.getMessage());
            throw e;
        }
    }
    
    @Bean
    public MongoTemplate mongoTemplate() {
        return new MongoTemplate(mongoClient(), getDatabaseName());
    }
    
    @Override
    protected boolean autoIndexCreation() {
        return true;
    }
}
