package com.arogyasakhi.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;
import java.time.LocalDateTime;

@Document(collection = "user_sessions")
public class UserSession {
    
    @Id
    private String id;
    
    @Field("chat_id")
    private Long chatId;
    
    private String language = "en"; // Default to English
    
    @Field("current_state")
    private String currentState;
    
    @Field("created_at")
    private LocalDateTime createdAt;
    
    @Field("updated_at")
    private LocalDateTime updatedAt;
    
    // Constructors
    public UserSession() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }
    
    public UserSession(Long chatId) {
        this();
        this.chatId = chatId;
    }
    
    // Getters and Setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    
    public Long getChatId() { return chatId; }
    public void setChatId(Long chatId) { this.chatId = chatId; }
    
    public String getLanguage() { return language; }
    public void setLanguage(String language) { 
        this.language = language;
        this.updatedAt = LocalDateTime.now();
    }
    
    public String getCurrentState() { return currentState; }
    public void setCurrentState(String currentState) { 
        this.currentState = currentState;
        this.updatedAt = LocalDateTime.now();
    }
    
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    
    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
}
