package com.arogyasakhi.service;

import com.arogyasakhi.model.UserProfile;
import com.arogyasakhi.repository.UserProfileRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
public class UserProfileService {
    
    @Autowired
    private UserProfileRepository userProfileRepository;
    
    public UserProfile getUserProfile(Long chatId) {
        try {
            Optional<UserProfile> profile = userProfileRepository.findByChatId(chatId);
            return profile.orElse(new UserProfile(chatId));
        } catch (Exception e) {
            System.err.println("❌ Error getting user profile for chatId " + chatId + ": " + e.getMessage());
            return new UserProfile(chatId);
        }
    }
    
    public UserProfile saveUserProfile(UserProfile userProfile) {
        try {
            userProfile.setUpdatedAt(LocalDateTime.now());
            UserProfile saved = userProfileRepository.save(userProfile);
            System.out.println("✅ User profile saved successfully for chatId: " + userProfile.getChatId());
            return saved;
        } catch (Exception e) {
            System.err.println("❌ Error saving user profile: " + e.getMessage());
            e.printStackTrace();
            throw e;
        }
    }
    
    public boolean isProfileComplete(UserProfile userProfile) {
        return userProfile != null &&
               userProfile.getAge() != null &&
               userProfile.getGender() != null &&
               userProfile.getWeight() != null &&
               userProfile.getHeight() != null &&
               userProfile.getFirstName() != null;
    }
    
    public void updateProfileStatus(UserProfile userProfile) {
        if (isProfileComplete(userProfile)) {
            userProfile.setStatus(UserProfile.ProfileStatus.COMPLETE);
        } else {
            userProfile.setStatus(UserProfile.ProfileStatus.INCOMPLETE);
        }
    }
    
    public void deleteUserProfile(Long chatId) {
        try {
            userProfileRepository.deleteByChatId(chatId);
            System.out.println("✅ User profile deleted for chatId: " + chatId);
        } catch (Exception e) {
            System.err.println("❌ Error deleting user profile: " + e.getMessage());
        }
    }
    
    public long getTotalUsers() {
        try {
            return userProfileRepository.count();
        } catch (Exception e) {
            System.err.println("❌ Error getting total users count: " + e.getMessage());
            return 0;
        }
    }
    
    public java.util.List<UserProfile> getCompleteProfiles() {
        try {
            return userProfileRepository.findByStatus(UserProfile.ProfileStatus.COMPLETE);
        } catch (Exception e) {
            System.err.println("❌ Error getting complete profiles: " + e.getMessage());
            return new java.util.ArrayList<>();
        }
    }
}
