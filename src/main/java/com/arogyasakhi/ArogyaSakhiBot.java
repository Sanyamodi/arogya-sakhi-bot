package com.arogyasakhi;

import com.arogyasakhi.model.HealthConsultation;
import com.arogyasakhi.model.UserProfile;
import com.arogyasakhi.repository.HealthConsultationRepository;
import com.arogyasakhi.service.GeminiService;
import com.arogyasakhi.service.LanguageService;
import com.arogyasakhi.service.UserProfileService;
import com.arogyasakhi.service.UserSessionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.util.*;

@Component
public class ArogyaSakhiBot extends TelegramLongPollingBot {
    
    @Value("${telegram.bot.username}")
    private String botUsername;
    
    @Value("${telegram.bot.token}")
    private String botToken;
    
    @Autowired
    private UserProfileService userProfileService;
    
    @Autowired
    private GeminiService geminiService;
    
    @Autowired
    private HealthConsultationRepository consultationRepository;
    
    @Autowired
    private LanguageService languageService;
    
    @Autowired
    private UserSessionService userSessionService;
    
    private final Map<Long, UserProfile> tempProfiles = new HashMap<>();
    
    @Override
    public String getBotUsername() {
        return botUsername;
    }
    
    @Override
    public String getBotToken() {
        return botToken;
    }
    
    @Override
    public void onUpdateReceived(Update update) {
        if (update.hasMessage() && update.getMessage().hasText()) {
            Long chatId = update.getMessage().getChatId();
            String messageText = update.getMessage().getText();
            String firstName = update.getMessage().getFrom().getFirstName();
            
            System.out.println("📨 Received message from " + firstName + " (ID: " + chatId + "): " + messageText);
            
            handleMessage(chatId, messageText, firstName);
        }
    }
    
    private void handleMessage(Long chatId, String messageText, String firstName) {
        String currentState = userSessionService.getUserState(chatId);
        String userLanguage = userSessionService.getUserLanguage(chatId);
        
        System.out.println("🔄 Processing message - State: " + currentState + ", Language: " + userLanguage);
        
        switch (messageText) {
            case "/start":
                handleStartCommand(chatId, firstName);
                break;
            case "🏥 Health Consultation":
            case "🏥 स्वास्थ्य परामर्श":
                startHealthConsultation(chatId);
                break;
            case "👤 My Profile":
            case "👤 मेरी प्रोफाइल":
                showUserProfile(chatId);
                break;
            case "📝 Update Profile":
            case "📝 प्रोफाइल अपडेट करें":
                startProfileUpdate(chatId);
                break;
            case "📊 Health History":
            case "📊 स्वास्थ्य इतिहास":
                showHealthHistory(chatId);
                break;
            case "ℹ️ Help":
            case "ℹ️ सहायता":
                showHelp(chatId);
                break;
            case "🌐 Language":
            case "🌐 भाषा":
                showLanguageSelection(chatId);
                break;
            case "🇺🇸 English":
                setUserLanguage(chatId, "en");
                break;
            case "🇮🇳 हिंदी":
                setUserLanguage(chatId, "hi");
                break;
            default:
                handleStateBasedMessage(chatId, messageText, currentState);
                break;
        }
    }
    
    private void handleStartCommand(Long chatId, String firstName) {
        // Initialize user session
        userSessionService.getUserSession(chatId);
        
        String welcomeMessage = String.format(
            "🙏 Namaste %s! Welcome to Arogya-Sakhi! 🏥\n\n" +
            "I'm your personal health assistant powered by AI. I can help you with:\n\n" +
            "🔸 Health consultations and symptom analysis\n" +
            "🔸 Home remedies and medication suggestions\n" +
            "🔸 First aid guidance\n" +
            "🔸 Doctor recommendations when needed\n" +
            "🔸 Health profile management\n\n" +
            "⚠️ IMPORTANT: I provide general health guidance only. Always consult healthcare professionals for serious conditions.\n\n" +
            "Choose your language / भाषा चुनें:",
            firstName
        );
        
        sendMessageWithKeyboard(chatId, welcomeMessage, getLanguageKeyboard());
    }
    
    private void showLanguageSelection(Long chatId) {
        String userLanguage = userSessionService.getUserLanguage(chatId);
        String message = languageService.getMessage("welcome", userLanguage, "");
        sendMessageWithKeyboard(chatId, "Choose your language / भाषा चुनें:", getLanguageKeyboard());
    }
    
    private void setUserLanguage(Long chatId, String language) {
        userSessionService.updateUserLanguage(chatId, language);
        String message = languageService.getMessage("language_selected", language);
        sendMessageWithKeyboard(chatId, message, getMainKeyboard(language));
        
        // Check if user profile exists
        UserProfile profile = userProfileService.getUserProfile(chatId);
        if (!userProfileService.isProfileComplete(profile)) {
            String incompleteMessage = languageService.getMessage("profile_incomplete", language);
            sendMessage(chatId, incompleteMessage);
        }
    }
    
    private void startHealthConsultation(Long chatId) {
        String userLanguage = userSessionService.getUserLanguage(chatId);
        UserProfile profile = userProfileService.getUserProfile(chatId);
        
        System.out.println("🩺 Starting health consultation for user: " + chatId);
        
        if (!userProfileService.isProfileComplete(profile)) {
            String message = languageService.getMessage("profile_incomplete", userLanguage);
            sendMessage(chatId, message);
            return;
        }
        
        userSessionService.updateUserState(chatId, "AWAITING_SYMPTOMS");
        String message = languageService.getMessage("consultation_start", userLanguage);
        sendMessage(chatId, message);
    }
    
    private void startProfileUpdate(Long chatId) {
        String userLanguage = userSessionService.getUserLanguage(chatId);
        UserProfile profile = userProfileService.getUserProfile(chatId);
        tempProfiles.put(chatId, profile);
        userSessionService.updateUserState(chatId, "PROFILE_NAME");
        
        String message = languageService.getMessage("profile_setup", userLanguage);
        sendMessage(chatId, message);
    }
    
    private void handleStateBasedMessage(Long chatId, String messageText, String currentState) {
        System.out.println("🔄 Handling state-based message - State: " + currentState);
        
        if (currentState == null) {
            String userLanguage = userSessionService.getUserLanguage(chatId);
            sendMessage(chatId, "I didn't understand that. Please use the menu options or type /start to begin.");
            return;
        }
        
        switch (currentState) {
            case "AWAITING_SYMPTOMS":
                handleSymptomsInput(chatId, messageText);
                break;
            case "PROFILE_NAME":
                handleNameInput(chatId, messageText);
                break;
            case "PROFILE_AGE":
                handleAgeInput(chatId, messageText);
                break;
            case "PROFILE_GENDER":
                handleGenderInput(chatId, messageText);
                break;
            case "PROFILE_WEIGHT":
                handleWeightInput(chatId, messageText);
                break;
            case "PROFILE_HEIGHT":
                handleHeightInput(chatId, messageText);
                break;
            case "PROFILE_BLOOD_GROUP":
                handleBloodGroupInput(chatId, messageText);
                break;
            case "PROFILE_ALLERGIES":
                handleAllergiesInput(chatId, messageText);
                break;
            case "PROFILE_DISEASES":
                handleDiseasesInput(chatId, messageText);
                break;
            case "PROFILE_MEDICATIONS":
                handleMedicationsInput(chatId, messageText);
                break;
            case "PROFILE_EMERGENCY":
                handleEmergencyContactInput(chatId, messageText);
                break;
            default:
                String userLanguage = userSessionService.getUserLanguage(chatId);
                sendMessage(chatId, "I didn't understand that. Please use the menu options or type /start to begin.");
                break;
        }
    }
    
    private void handleSymptomsInput(Long chatId, String symptoms) {
        String userLanguage = userSessionService.getUserLanguage(chatId);
        String analyzingMessage = languageService.getMessage("analyzing_symptoms", userLanguage);
        sendMessage(chatId, analyzingMessage);
        
        System.out.println("🔍 Analyzing symptoms for user: " + chatId);
        
        UserProfile profile = userProfileService.getUserProfile(chatId);
        String recommendation = geminiService.getHealthRecommendation(symptoms, profile, userLanguage);
        
        // Save consultation
        HealthConsultation consultation = new HealthConsultation(chatId, symptoms);
        consultation.setAiRecommendation(recommendation);
        
        // Determine if doctor is recommended based on keywords
        String lowerRecommendation = recommendation.toLowerCase();
        boolean doctorRecommended = lowerRecommendation.contains("doctor") || 
                                   lowerRecommendation.contains("emergency") ||
                                   lowerRecommendation.contains("hospital") ||
                                   lowerRecommendation.contains("urgent") ||
                                   lowerRecommendation.contains("डॉक्टर") ||
                                   lowerRecommendation.contains("आपातकाल") ||
                                   lowerRecommendation.contains("अस्पताल");
        
        consultation.setDoctorRecommended(doctorRecommended);
        
        try {
            consultationRepository.save(consultation);
            System.out.println("✅ Consultation saved successfully for user: " + chatId);
        } catch (Exception e) {
            System.err.println("❌ Failed to save consultation: " + e.getMessage());
            e.printStackTrace();
        }
        
        // Send recommendation
        sendMessage(chatId, "🩺 HEALTH RECOMMENDATION\n\n" + recommendation);
        
        if (doctorRecommended) {
            String doctorMessage = languageService.getMessage("doctor_recommendation", userLanguage);
            sendMessage(chatId, doctorMessage);
        }
        
        userSessionService.updateUserState(chatId, null);
        String anythingElseMessage = languageService.getMessage("anything_else", userLanguage);
        sendMessageWithKeyboard(chatId, anythingElseMessage, getMainKeyboard(userLanguage));
    }
    
    private void handleNameInput(Long chatId, String name) {
        String userLanguage = userSessionService.getUserLanguage(chatId);
        UserProfile profile = tempProfiles.get(chatId);
        String[] nameParts = name.trim().split(" ", 2);
        profile.setFirstName(nameParts[0]);
        if (nameParts.length > 1) {
            profile.setLastName(nameParts[1]);
        }
        
        userSessionService.updateUserState(chatId, "PROFILE_AGE");
        String message = languageService.getMessage("enter_age", userLanguage);
        sendMessage(chatId, message);
    }
    
    private void handleAgeInput(Long chatId, String ageText) {
        String userLanguage = userSessionService.getUserLanguage(chatId);
        try {
            int age = Integer.parseInt(ageText.trim());
            if (age < 1 || age > 120) {
                sendMessage(chatId, "Please enter a valid age between 1 and 120:");
                return;
            }
            
            UserProfile profile = tempProfiles.get(chatId);
            profile.setAge(age);
            
            userSessionService.updateUserState(chatId, "PROFILE_GENDER");
            String message = languageService.getMessage("select_gender", userLanguage);
            sendMessage(chatId, message);
        } catch (NumberFormatException e) {
            sendMessage(chatId, "Please enter a valid number for age:");
        }
    }
    
    private void handleGenderInput(Long chatId, String genderInput) {
        String userLanguage = userSessionService.getUserLanguage(chatId);
        String gender;
        switch (genderInput.trim()) {
            case "1":
                gender = "hi".equals(userLanguage) ? "पुरुष" : "Male";
                break;
            case "2":
                gender = "hi".equals(userLanguage) ? "महिला" : "Female";
                break;
            case "3":
                gender = "hi".equals(userLanguage) ? "अन्य" : "Other";
                break;
            default:
                String selectMessage = languageService.getMessage("select_gender", userLanguage);
                sendMessage(chatId, selectMessage);
                return;
        }
        
        UserProfile profile = tempProfiles.get(chatId);
        profile.setGender(gender);
        
        userSessionService.updateUserState(chatId, "PROFILE_WEIGHT");
        String message = languageService.getMessage("enter_weight", userLanguage);
        sendMessage(chatId, message);
    }
    
    private void handleWeightInput(Long chatId, String weightText) {
        String userLanguage = userSessionService.getUserLanguage(chatId);
        try {
            double weight = Double.parseDouble(weightText.trim());
            if (weight < 10 || weight > 300) {
                sendMessage(chatId, "Please enter a valid weight between 10 and 300 kg:");
                return;
            }
            
            UserProfile profile = tempProfiles.get(chatId);
            profile.setWeight(weight);
            
            userSessionService.updateUserState(chatId, "PROFILE_HEIGHT");
            String message = languageService.getMessage("enter_height", userLanguage);
            sendMessage(chatId, message);
        } catch (NumberFormatException e) {
            sendMessage(chatId, "Please enter a valid number for weight:");
        }
    }
    
    private void handleHeightInput(Long chatId, String heightText) {
        String userLanguage = userSessionService.getUserLanguage(chatId);
        try {
            double height = Double.parseDouble(heightText.trim());
            if (height < 50 || height > 250) {
                sendMessage(chatId, "Please enter a valid height between 50 and 250 cm:");
                return;
            }
            
            UserProfile profile = tempProfiles.get(chatId);
            profile.setHeight(height);
            
            userSessionService.updateUserState(chatId, "PROFILE_BLOOD_GROUP");
            String message = languageService.getMessage("enter_blood_group", userLanguage);
            sendMessage(chatId, message);
        } catch (NumberFormatException e) {
            sendMessage(chatId, "Please enter a valid number for height:");
        }
    }
    
    private void handleBloodGroupInput(Long chatId, String bloodGroup) {
        String userLanguage = userSessionService.getUserLanguage(chatId);
        UserProfile profile = tempProfiles.get(chatId);
        if (!bloodGroup.trim().equalsIgnoreCase("skip")) {
            profile.setBloodGroup(bloodGroup.trim().toUpperCase());
        }
        
        userSessionService.updateUserState(chatId, "PROFILE_ALLERGIES");
        String message = languageService.getMessage("enter_allergies", userLanguage);
        sendMessage(chatId, message);
    }
    
    private void handleAllergiesInput(Long chatId, String allergiesText) {
        String userLanguage = userSessionService.getUserLanguage(chatId);
        UserProfile profile = tempProfiles.get(chatId);
        if (!allergiesText.trim().equalsIgnoreCase("none")) {
            List<String> allergies = Arrays.asList(allergiesText.split(","));
            allergies.replaceAll(String::trim);
            profile.setAllergies(allergies);
        }
        
        userSessionService.updateUserState(chatId, "PROFILE_DISEASES");
        String message = languageService.getMessage("enter_diseases", userLanguage);
        sendMessage(chatId, message);
    }
    
    private void handleDiseasesInput(Long chatId, String diseasesText) {
        String userLanguage = userSessionService.getUserLanguage(chatId);
        UserProfile profile = tempProfiles.get(chatId);
        if (!diseasesText.trim().equalsIgnoreCase("none")) {
            List<String> diseases = Arrays.asList(diseasesText.split(","));
            diseases.replaceAll(String::trim);
            profile.setPreviousDiseases(diseases);
        }
        
        userSessionService.updateUserState(chatId, "PROFILE_MEDICATIONS");
        String message = languageService.getMessage("enter_medications", userLanguage);
        sendMessage(chatId, message);
    }
    
    private void handleMedicationsInput(Long chatId, String medicationsText) {
        String userLanguage = userSessionService.getUserLanguage(chatId);
        UserProfile profile = tempProfiles.get(chatId);
        if (!medicationsText.trim().equalsIgnoreCase("none")) {
            List<String> medications = Arrays.asList(medicationsText.split(","));
            medications.replaceAll(String::trim);
            profile.setCurrentMedications(medications);
        }
        
        userSessionService.updateUserState(chatId, "PROFILE_EMERGENCY");
        String message = languageService.getMessage("enter_emergency_contact", userLanguage);
        sendMessage(chatId, message);
    }
    
    private void handleEmergencyContactInput(Long chatId, String emergencyContact) {
        String userLanguage = userSessionService.getUserLanguage(chatId);
        UserProfile profile = tempProfiles.get(chatId);
        if (!emergencyContact.trim().equalsIgnoreCase("skip")) {
            profile.setEmergencyContact(emergencyContact.trim());
        }
        
        // Save profile
        userProfileService.updateProfileStatus(profile);
        
        try {
            userProfileService.saveUserProfile(profile);
            System.out.println("✅ User profile saved successfully for user: " + chatId);
        } catch (Exception e) {
            System.err.println("❌ Failed to save user profile: " + e.getMessage());
            e.printStackTrace();
            sendMessage(chatId, "Sorry, there was an error saving your profile. Please try again.");
            return;
        }
        
        // Clean up
        tempProfiles.remove(chatId);
        userSessionService.updateUserState(chatId, null);
        
        double bmi = profile.getBMI();
        String bmiCategory = getBMICategory(bmi);
        
        String completionMessage = String.format(
            "✅ Profile completed successfully!\n\n" +
            "📊 Your Health Summary:\n" +
            "• Name: %s %s\n" +
            "• Age: %d years\n" +
            "• Gender: %s\n" +
            "• BMI: %.1f (%s)\n" +
            "• Blood Group: %s\n\n" +
            "You can now use the Health Consultation feature for personalized recommendations!",
            profile.getFirstName(),
            profile.getLastName() != null ? profile.getLastName() : "",
            profile.getAge(),
            profile.getGender(),
            bmi,
            bmiCategory,
            profile.getBloodGroup() != null ? profile.getBloodGroup() : "Not provided"
        );
        
        sendMessageWithKeyboard(chatId, completionMessage, getMainKeyboard(userLanguage));
    }
    
    private void showUserProfile(Long chatId) {
        String userLanguage = userSessionService.getUserLanguage(chatId);
        UserProfile profile = userProfileService.getUserProfile(chatId);
        
        if (!userProfileService.isProfileComplete(profile)) {
            String message = languageService.getMessage("profile_incomplete", userLanguage);
            sendMessage(chatId, message);
            return;
        }
        
        double bmi = profile.getBMI();
        String bmiCategory = getBMICategory(bmi);
        
        StringBuilder profileText = new StringBuilder();
        profileText.append("👤 YOUR HEALTH PROFILE\n\n");
        profileText.append("📋 Basic Information:\n");
        profileText.append("• Name: ").append(profile.getFirstName());
        if (profile.getLastName() != null) {
            profileText.append(" ").append(profile.getLastName());
        }
        profileText.append("\n");
        profileText.append("• Age: ").append(profile.getAge()).append(" years\n");
        profileText.append("• Gender: ").append(profile.getGender()).append("\n");
        profileText.append("• Weight: ").append(profile.getWeight()).append(" kg\n");
        profileText.append("• Height: ").append(profile.getHeight()).append(" cm\n");
        profileText.append("• BMI: ").append(String.format("%.1f", bmi)).append(" (").append(bmiCategory).append(")\n");
        
        if (profile.getBloodGroup() != null) {
            profileText.append("• Blood Group: ").append(profile.getBloodGroup()).append("\n");
        }
        
        if (profile.getAllergies() != null && !profile.getAllergies().isEmpty()) {
            profileText.append("\n🚫 Allergies:\n");
            profile.getAllergies().forEach(allergy -> profileText.append("• ").append(allergy).append("\n"));
        }
        
        if (profile.getPreviousDiseases() != null && !profile.getPreviousDiseases().isEmpty()) {
            profileText.append("\n🏥 Previous Diseases:\n");
            profile.getPreviousDiseases().forEach(disease -> profileText.append("• ").append(disease).append("\n"));
        }
        
        if (profile.getCurrentMedications() != null && !profile.getCurrentMedications().isEmpty()) {
            profileText.append("\n💊 Current Medications:\n");
            profile.getCurrentMedications().forEach(medication -> profileText.append("• ").append(medication).append("\n"));
        }
        
        if (profile.getEmergencyContact() != null) {
            profileText.append("\n📞 Emergency Contact: ").append(profile.getEmergencyContact());
        }
        
        sendMessage(chatId, profileText.toString());
    }
    
    private void showHealthHistory(Long chatId) {
        String userLanguage = userSessionService.getUserLanguage(chatId);
        List<HealthConsultation> consultations = consultationRepository.findByChatIdOrderByConsultationTimeDesc(chatId);
        
        if (consultations.isEmpty()) {
            String message = "hi".equals(userLanguage) ? 
                "📊 कोई स्वास्थ्य परामर्श नहीं मिला। '🏥 स्वास्थ्य परामर्श' का उपयोग करके अपना पहला परामर्श शुरू करें।" :
                "📊 No health consultations found. Start your first consultation using '🏥 Health Consultation'.";
            sendMessage(chatId, message);
            return;
        }
        
        StringBuilder historyText = new StringBuilder();
        historyText.append("hi".equals(userLanguage) ? "📊 आपका स्वास्थ्य इतिहास\n\n" : "📊 YOUR HEALTH HISTORY\n\n");
        
        int count = 0;
        for (HealthConsultation consultation : consultations) {
            if (count >= 5) break; // Show last 5 consultations
            
            historyText.append("📅 ").append(consultation.getConsultationTime().toLocalDate()).append("\n");
            historyText.append("hi".equals(userLanguage) ? "🔸 लक्षण: " : "🔸 Symptoms: ").append(consultation.getSymptoms()).append("\n");
            if (consultation.isDoctorRecommended()) {
                historyText.append("hi".equals(userLanguage) ? "⚠️ डॉक्टर परामर्श की सिफारिश की गई थी\n" : "⚠️ Doctor consultation was recommended\n");
            }
            historyText.append("\n");
            count++;
        }
        
        sendMessage(chatId, historyText.toString());
    }
    
    private void showHelp(Long chatId) {
        String userLanguage = userSessionService.getUserLanguage(chatId);
        String helpText = "hi".equals(userLanguage) ?
            "ℹ️ आरोग्य-सखी सहायता\n\n" +
            "🏥 स्वास्थ्य परामर्श:\n" +
            "आपके लक्षणों और प्रोफाइल के आधार पर AI-संचालित स्वास्थ्य सिफारिशें प्राप्त करें।\n\n" +
            "👤 मेरी प्रोफाइल:\n" +
            "अपनी पूरी स्वास्थ्य प्रोफाइल देखें।\n\n" +
            "📝 प्रोफाइल अपडेट करें:\n" +
            "बेहतर सिफारिशों के लिए अपनी स्वास्थ्य जानकारी जोड़ें या अपडेट करें।\n\n" +
            "📊 स्वास्थ्य इतिहास:\n" +
            "अपने पिछले परामर्श देखें।\n\n" +
            "⚠️ महत्वपूर्ण अस्वीकरण:\n" +
            "• यह बॉट केवल सामान्य स्वास्थ्य मार्गदर्शन प्रदान करता है\n" +
            "• गंभीर स्थितियों के लिए हमेशा स्वास्थ्य पेशेवरों से सलाह लें\n" +
            "• आपातकाल में तुरंत 108 पर कॉल करें\n" +
            "• यह पेशेवर चिकित्सा सलाह का विकल्प नहीं है\n\n" +
            "📞 आपातकालीन नंबर:\n" +
            "• आपातकाल: 108\n" +
            "• एम्बुलेंस: 102\n" +
            "• चिकित्सा हेल्पलाइन: 104" :
            
            "ℹ️ AROGYA-SAKHI HELP\n\n" +
            "🏥 Health Consultation:\n" +
            "Get AI-powered health recommendations based on your symptoms and profile.\n\n" +
            "👤 My Profile:\n" +
            "View your complete health profile.\n\n" +
            "📝 Update Profile:\n" +
            "Add or update your health information for better recommendations.\n\n" +
            "📊 Health History:\n" +
            "View your previous consultations.\n\n" +
            "⚠️ IMPORTANT DISCLAIMERS:\n" +
            "• This bot provides general health guidance only\n" +
            "• Always consult healthcare professionals for serious conditions\n" +
            "• In emergencies, call 108 immediately\n" +
            "• This is not a substitute for professional medical advice\n\n" +
            "📞 Emergency Numbers:\n" +
            "• Emergency: 108\n" +
            "• Ambulance: 102\n" +
            "• Medical Helpline: 104";
        
        sendMessage(chatId, helpText);
    }
    
    private String getBMICategory(double bmi) {
        if (bmi < 18.5) return "Underweight";
        else if (bmi < 25) return "Normal";
        else if (bmi < 30) return "Overweight";
        else return "Obese";
    }
    
    private ReplyKeyboardMarkup getLanguageKeyboard() {
        ReplyKeyboardMarkup keyboardMarkup = new ReplyKeyboardMarkup();
        keyboardMarkup.setResizeKeyboard(true);
        keyboardMarkup.setOneTimeKeyboard(false);
        
        List<KeyboardRow> keyboard = new ArrayList<>();
        
        KeyboardRow row1 = new KeyboardRow();
        row1.add(new KeyboardButton("🇺🇸 English"));
        row1.add(new KeyboardButton("🇮🇳 हिंदी"));
        
        keyboard.add(row1);
        keyboardMarkup.setKeyboard(keyboard);
        return keyboardMarkup;
    }
    
    private ReplyKeyboardMarkup getMainKeyboard(String language) {
        ReplyKeyboardMarkup keyboardMarkup = new ReplyKeyboardMarkup();
        keyboardMarkup.setResizeKeyboard(true);
        keyboardMarkup.setOneTimeKeyboard(false);
        
        List<KeyboardRow> keyboard = new ArrayList<>();
        
        if ("hi".equals(language)) {
            KeyboardRow row1 = new KeyboardRow();
            row1.add(new KeyboardButton("🏥 स्वास्थ्य परामर्श"));
            row1.add(new KeyboardButton("👤 मेरी प्रोफाइल"));
            
            KeyboardRow row2 = new KeyboardRow();
            row2.add(new KeyboardButton("📝 प्रोफाइल अपडेट करें"));
            row2.add(new KeyboardButton("📊 स्वास्थ्य इतिहास"));
            
            KeyboardRow row3 = new KeyboardRow();
            row3.add(new KeyboardButton("ℹ️ सहायता"));
            row3.add(new KeyboardButton("🌐 भाषा"));
            
            keyboard.add(row1);
            keyboard.add(row2);
            keyboard.add(row3);
        } else {
            KeyboardRow row1 = new KeyboardRow();
            row1.add(new KeyboardButton("🏥 Health Consultation"));
            row1.add(new KeyboardButton("👤 My Profile"));
            
            KeyboardRow row2 = new KeyboardRow();
            row2.add(new KeyboardButton("📝 Update Profile"));
            row2.add(new KeyboardButton("📊 Health History"));
            
            KeyboardRow row3 = new KeyboardRow();
            row3.add(new KeyboardButton("ℹ️ Help"));
            row3.add(new KeyboardButton("🌐 Language"));
            
            keyboard.add(row1);
            keyboard.add(row2);
            keyboard.add(row3);
        }
        
        keyboardMarkup.setKeyboard(keyboard);
        return keyboardMarkup;
    }
    
    private void sendMessage(Long chatId, String text) {
        SendMessage message = new SendMessage();
        message.setChatId(chatId.toString());
        message.setText(text);
        
        try {
            execute(message);
            System.out.println("✅ Message sent successfully to user: " + chatId);
        } catch (TelegramApiException e) {
            System.err.println("❌ Failed to send message to user " + chatId + ": " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    private void sendMessageWithKeyboard(Long chatId, String text, ReplyKeyboardMarkup keyboard) {
        SendMessage message = new SendMessage();
        message.setChatId(chatId.toString());
        message.setText(text);
        message.setReplyMarkup(keyboard);
        
        try {
            execute(message);
            System.out.println("✅ Message with keyboard sent successfully to user: " + chatId);
        } catch (TelegramApiException e) {
            System.err.println("❌ Failed to send message with keyboard to user " + chatId + ": " + e.getMessage());
            e.printStackTrace();
        }
    }
}
