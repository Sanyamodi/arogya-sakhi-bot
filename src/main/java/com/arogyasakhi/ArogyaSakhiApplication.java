package com.arogyasakhi;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.data.mongodb.core.MongoTemplate;

@SpringBootApplication
public class ArogyaSakhiApplication implements CommandLineRunner {

    @Autowired
    private ArogyaSakhiBot arogyaSakhiBot;
    
    @Autowired
    private MongoTemplate mongoTemplate;
    
    @Value("${telegram.bot.username}")
    private String botUsername;
    
    @Value("${telegram.bot.token}")
    private String botToken;
    
    @Value("${gemini.api.key}")
    private String geminiApiKey;

    public static void main(String[] args) {
        // Load environment variables from .env file
        System.setProperty("spring.config.import", "optional:file:.env[.properties]");
        SpringApplication.run(ArogyaSakhiApplication.class, args);
    }

    @Override
    public void run(String... args) throws Exception {
        try {
            // Print configuration info
            System.out.println("🔗 Starting Arogya-Sakhi Bot...");
            System.out.println("🔗 Bot Username: " + (botUsername != null ? botUsername : "NOT SET"));
            System.out.println("🔗 MongoDB URI: " + (System.getenv("MONGO_URI") != null ? "✅ Present" : "❌ Missing"));
            System.out.println("🔗 Gemini API Key: " + (geminiApiKey != null ? "✅ Present" : "❌ Missing"));
            System.out.println("🔗 Bot Token: " + (botToken != null ? "✅ Present" : "❌ Missing"));
            
            // Test MongoDB connection with timeout
            System.out.println("🔗 Testing MongoDB Atlas connection...");
            mongoTemplate.getDb().runCommand(new org.bson.Document("ping", 1));
            System.out.println("✅ MongoDB Atlas connected successfully!");
            
            // Register Telegram bot
            System.out.println("🤖 Registering Telegram bot...");
            TelegramBotsApi botsApi = new TelegramBotsApi(DefaultBotSession.class);
            botsApi.registerBot(arogyaSakhiBot);
            System.out.println("🏥 Arogya-Sakhi Bot started successfully!");
            System.out.println("🌐 Multi-language support: Hindi & English");
            System.out.println("🚀 Bot is ready to receive messages!");
            
        } catch (TelegramApiException e) {
            System.err.println("❌ Failed to start Telegram bot: " + e.getMessage());
            e.printStackTrace();
        } catch (Exception e) {
            System.err.println("❌ Failed to connect to MongoDB Atlas: " + e.getMessage());
            System.err.println("💡 Please check your MongoDB Atlas connection string and network connectivity");
            e.printStackTrace();
        }
    }
}
