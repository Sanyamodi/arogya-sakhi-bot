package com.arogyasakhi.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;
import java.time.LocalDateTime;
import java.util.List;

@Document(collection = "user_profiles")
public class UserProfile {
    
    @Id
    private String id;
    
    @Field("chat_id")
    private Long chatId;
    
    @Field("first_name")
    private String firstName;
    
    @Field("last_name")
    private String lastName;
    
    private Integer age;
    private String gender;
    private Double weight; // in kg
    private Double height; // in cm
    
    @Field("blood_group")
    private String bloodGroup;
    
    private List<String> allergies;
    
    @Field("previous_diseases")
    private List<String> previousDiseases;
    
    @Field("current_medications")
    private List<String> currentMedications;
    
    @Field("emergency_contact")
    private String emergencyContact;
    
    @Field("created_at")
    private LocalDateTime createdAt;
    
    @Field("updated_at")
    private LocalDateTime updatedAt;
    
    private ProfileStatus status = ProfileStatus.INCOMPLETE;
    
    // Constructors
    public UserProfile() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }
    
    public UserProfile(Long chatId) {
        this();
        this.chatId = chatId;
    }
    
    // Getters and Setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    
    public Long getChatId() { return chatId; }
    public void setChatId(Long chatId) { this.chatId = chatId; }
    
    public String getFirstName() { return firstName; }
    public void setFirstName(String firstName) { this.firstName = firstName; }
    
    public String getLastName() { return lastName; }
    public void setLastName(String lastName) { this.lastName = lastName; }
    
    public Integer getAge() { return age; }
    public void setAge(Integer age) { this.age = age; }
    
    public String getGender() { return gender; }
    public void setGender(String gender) { this.gender = gender; }
    
    public Double getWeight() { return weight; }
    public void setWeight(Double weight) { this.weight = weight; }
    
    public Double getHeight() { return height; }
    public void setHeight(Double height) { this.height = height; }
    
    public String getBloodGroup() { return bloodGroup; }
    public void setBloodGroup(String bloodGroup) { this.bloodGroup = bloodGroup; }
    
    public List<String> getAllergies() { return allergies; }
    public void setAllergies(List<String> allergies) { this.allergies = allergies; }
    
    public List<String> getPreviousDiseases() { return previousDiseases; }
    public void setPreviousDiseases(List<String> previousDiseases) { this.previousDiseases = previousDiseases; }
    
    public List<String> getCurrentMedications() { return currentMedications; }
    public void setCurrentMedications(List<String> currentMedications) { this.currentMedications = currentMedications; }
    
    public String getEmergencyContact() { return emergencyContact; }
    public void setEmergencyContact(String emergencyContact) { this.emergencyContact = emergencyContact; }
    
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    
    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
    
    public ProfileStatus getStatus() { return status; }
    public void setStatus(ProfileStatus status) { this.status = status; }
    
    public double getBMI() {
        if (weight != null && height != null && height > 0) {
            double heightInMeters = height / 100.0;
            return weight / (heightInMeters * heightInMeters);
        }
        return 0.0;
    }
    
    public enum ProfileStatus {
        INCOMPLETE, COMPLETE, UPDATING
    }
}
