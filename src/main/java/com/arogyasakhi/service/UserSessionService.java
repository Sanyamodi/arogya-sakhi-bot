package com.arogyasakhi.service;

import com.arogyasakhi.model.UserSession;
import com.arogyasakhi.repository.UserSessionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UserSessionService {
    
    @Autowired
    private UserSessionRepository userSessionRepository;
    
    public UserSession getUserSession(Long chatId) {
        try {
            Optional<UserSession> session = userSessionRepository.findByChatId(chatId);
            if (session.isPresent()) {
                return session.get();
            } else {
                UserSession newSession = new UserSession(chatId);
                return userSessionRepository.save(newSession);
            }
        } catch (Exception e) {
            System.err.println("❌ Error getting user session for chatId " + chatId + ": " + e.getMessage());
            // Return a default session if database fails
            return new UserSession(chatId);
        }
    }
    
    public String getUserLanguage(Long chatId) {
        try {
            UserSession session = getUserSession(chatId);
            return session.getLanguage();
        } catch (Exception e) {
            System.err.println("❌ Error getting user language: " + e.getMessage());
            return "en"; // Default to English
        }
    }
    
    public void updateUserLanguage(Long chatId, String language) {
        try {
            UserSession session = getUserSession(chatId);
            session.setLanguage(language);
            userSessionRepository.save(session);
            System.out.println("✅ User language updated to " + language + " for chatId: " + chatId);
        } catch (Exception e) {
            System.err.println("❌ Error updating user language: " + e.getMessage());
        }
    }
    
    public String getUserState(Long chatId) {
        try {
            UserSession session = getUserSession(chatId);
            return session.getCurrentState();
        } catch (Exception e) {
            System.err.println("❌ Error getting user state: " + e.getMessage());
            return null;
        }
    }
    
    public void updateUserState(Long chatId, String state) {
        try {
            UserSession session = getUserSession(chatId);
            session.setCurrentState(state);
            userSessionRepository.save(session);
            System.out.println("✅ User state updated to " + state + " for chatId: " + chatId);
        } catch (Exception e) {
            System.err.println("❌ Error updating user state: " + e.getMessage());
        }
    }
    
    public void deleteUserSession(Long chatId) {
        try {
            userSessionRepository.deleteByChatId(chatId);
            System.out.println("✅ User session deleted for chatId: " + chatId);
        } catch (Exception e) {
            System.err.println("❌ Error deleting user session: " + e.getMessage());
        }
    }
}
