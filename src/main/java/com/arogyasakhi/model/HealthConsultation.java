package com.arogyasakhi.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;
import java.time.LocalDateTime;

@Document(collection = "health_consultations")
public class HealthConsultation {
    
    @Id
    private String id;
    
    @Field("chat_id")
    private Long chatId;
    
    private String symptoms;
    
    @Field("ai_recommendation")
    private String aiRecommendation;
    
    private String severity;
    
    @Field("doctor_recommended")
    private boolean doctorRecommended;
    
    @Field("first_aid_advice")
    private String firstAidAdvice;
    
    @Field("consultation_time")
    private LocalDateTime consultationTime;
    
    // Constructors
    public HealthConsultation() {
        this.consultationTime = LocalDateTime.now();
    }
    
    public HealthConsultation(Long chatId, String symptoms) {
        this();
        this.chatId = chatId;
        this.symptoms = symptoms;
    }
    
    // Getters and Setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    
    public Long getChatId() { return chatId; }
    public void setChatId(Long chatId) { this.chatId = chatId; }
    
    public String getSymptoms() { return symptoms; }
    public void setSymptoms(String symptoms) { this.symptoms = symptoms; }
    
    public String getAiRecommendation() { return aiRecommendation; }
    public void setAiRecommendation(String aiRecommendation) { this.aiRecommendation = aiRecommendation; }
    
    public String getSeverity() { return severity; }
    public void setSeverity(String severity) { this.severity = severity; }
    
    public boolean isDoctorRecommended() { return doctorRecommended; }
    public void setDoctorRecommended(boolean doctorRecommended) { this.doctorRecommended = doctorRecommended; }
    
    public String getFirstAidAdvice() { return firstAidAdvice; }
    public void setFirstAidAdvice(String firstAidAdvice) { this.firstAidAdvice = firstAidAdvice; }
    
    public LocalDateTime getConsultationTime() { return consultationTime; }
    public void setConsultationTime(LocalDateTime consultationTime) { this.consultationTime = consultationTime; }
}
