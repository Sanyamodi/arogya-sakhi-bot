package com.arogyasakhi.service;

import org.springframework.stereotype.Service;
import java.util.HashMap;
import java.util.Map;

@Service
public class LanguageService {
    
    private final Map<String, Map<String, String>> messages = new HashMap<>();
    
    public LanguageService() {
        initializeMessages();
    }
    
    private void initializeMessages() {
        // English messages
        Map<String, String> englishMessages = new HashMap<>();
        englishMessages.put("welcome", "🙏 Welcome to Arogya-Sakhi! Your personal health assistant.");
        englishMessages.put("language_selected", "✅ Language set to English. Let's get started with your health journey!");
        englishMessages.put("profile_incomplete", "⚠️ Please complete your profile first for accurate health recommendations. Use '📝 Update Profile' option.");
        englishMessages.put("consultation_start", "🩺 Health Consultation Started\n\nPlease describe your current symptoms in detail. Include:\n• What symptoms are you experiencing?\n• When did they start?\n• How severe are they (1-10)?\n• Any triggers you noticed?\n\nType your symptoms below:");
        englishMessages.put("profile_setup", "📝 Profile Setup\n\nLet's set up your health profile for personalized recommendations.\n\nFirst, please enter your full name:");
        englishMessages.put("analyzing_symptoms", "🔄 Analyzing your symptoms... Please wait.");
        englishMessages.put("doctor_recommendation", "🚨 DOCTOR RECOMMENDATION\n\nBased on your symptoms, I recommend consulting a healthcare professional.\n\n📞 Emergency Numbers:\n• Emergency: 108\n• Ambulance: 102\n• Medical Helpline: 104\n\n🏥 Find nearby doctors and hospitals using Google Maps or Practo app.");
        englishMessages.put("anything_else", "Is there anything else I can help you with?");
        englishMessages.put("enter_age", "Great! Now please enter your age:");
        englishMessages.put("select_gender", "Please select your gender:\n1. Male\n2. Female\n3. Other\n\nType 1, 2, or 3:");
        englishMessages.put("enter_weight", "Please enter your weight in kg (e.g., 65.5):");
        englishMessages.put("enter_height", "Please enter your height in cm (e.g., 170):");
        englishMessages.put("enter_blood_group", "Please enter your blood group (e.g., A+, B-, O+, AB-) or type 'skip':");
        englishMessages.put("enter_allergies", "Do you have any allergies? Please list them separated by commas, or type 'none':");
        englishMessages.put("enter_diseases", "Do you have any previous diseases or medical conditions? Please list them separated by commas, or type 'none':");
        englishMessages.put("enter_medications", "Are you currently taking any medications? Please list them separated by commas, or type 'none':");
        englishMessages.put("enter_emergency_contact", "Please provide an emergency contact number, or type 'skip':");
        
        // Hindi messages
        Map<String, String> hindiMessages = new HashMap<>();
        hindiMessages.put("welcome", "🙏 आरोग्य-सखी में आपका स्वागत है! आपका व्यक्तिगत स्वास्थ्य सहायक।");
        hindiMessages.put("language_selected", "✅ भाषा हिंदी में सेट की गई। आइए अपनी स्वास्थ्य यात्रा शुरू करते हैं!");
        hindiMessages.put("profile_incomplete", "⚠️ सटीक स्वास्थ्य सिफारिशों के लिए कृपया पहले अपनी प्रोफाइल पूरी करें। '📝 प्रोफाइल अपडेट करें' विकल्प का उपयोग करें।");
        hindiMessages.put("consultation_start", "🩺 स्वास्थ्य परामर्श शुरू\n\nकृपया अपने वर्तमान लक्षणों का विस्तार से वर्णन करें। शामिल करें:\n• आप कौन से लक्षण महसूस कर रहे हैं?\n• ये कब शुरू हुए?\n• ये कितने गंभीर हैं (1-10)?\n• कोई ट्रिगर जो आपने देखे?\n\nनीचे अपने लक्षण लिखें:");
        hindiMessages.put("profile_setup", "📝 प्रोफाइल सेटअप\n\nव्यक्तिगत सिफारिशों के लिए आइए आपकी स्वास्थ्य प्रोफाइल सेट करते हैं।\n\nपहले, कृपया अपना पूरा नाम दर्ज करें:");
        hindiMessages.put("analyzing_symptoms", "🔄 आपके लक्षणों का विश्लेषण कर रहे हैं... कृपया प्रतीक्षा करें।");
        hindiMessages.put("doctor_recommendation", "🚨 डॉक्टर की सिफारिश\n\nआपके लक्षणों के आधार पर, मैं किसी स्वास्थ्य पेशेवर से सलाह लेने की सिफारिश करता हूं।\n\n📞 आपातकालीन नंबर:\n• आपातकाल: 108\n• एम्बुलेंस: 102\n• चिकित्सा हेल्पलाइन: 104\n\n🏥 Google Maps या Practo ऐप का उपयोग करके नजदीकी डॉक्टर और अस्पताल खोजें।");
        hindiMessages.put("anything_else", "क्या कोई और चीज़ है जिसमें मैं आपकी मदद कर सकूं?");
        hindiMessages.put("enter_age", "बहुत बढ़िया! अब कृपया अपनी उम्र दर्ज करें:");
        hindiMessages.put("select_gender", "कृपया अपना लिंग चुनें:\n1. पुरुष\n2. महिला\n3. अन्य\n\n1, 2, या 3 टाइप करें:");
        hindiMessages.put("enter_weight", "कृपया अपना वजन किलो में दर्ज करें (जैसे, 65.5):");
        hindiMessages.put("enter_height", "कृपया अपनी ऊंचाई सेमी में दर्ज करें (जैसे, 170):");
        hindiMessages.put("enter_blood_group", "कृपया अपना ब्लड ग्रुप दर्ज करें (जैसे, A+, B-, O+, AB-) या 'skip' टाइप करें:");
        hindiMessages.put("enter_allergies", "क्या आपको कोई एलर्जी है? कृपया उन्हें कॉमा से अलग करके लिस्ट करें, या 'none' टाइप करें:");
        hindiMessages.put("enter_diseases", "क्या आपको कोई पुरानी बीमारियां या चिकित्सा स्थितियां हैं? कृपया उन्हें कॉमा से अलग करके लिस्ट करें, या 'none' टाइप करें:");
        hindiMessages.put("enter_medications", "क्या आप वर्तमान में कोई दवाएं ले रहे हैं? कृपया उन्हें कॉमा से अलग करके लिस्ट करें, या 'none' टाइप करें:");
        hindiMessages.put("enter_emergency_contact", "कृपया एक आपातकालीन संपर्क नंबर प्रदान करें, या 'skip' टाइप करें:");
        
        messages.put("en", englishMessages);
        messages.put("hi", hindiMessages);
    }
    
    public String getMessage(String key, String language) {
        return getMessage(key, language, "");
    }
    
    public String getMessage(String key, String language, String defaultValue) {
        Map<String, String> languageMessages = messages.get(language);
        if (languageMessages != null && languageMessages.containsKey(key)) {
            return languageMessages.get(key);
        }
        
        // Fallback to English if key not found in selected language
        Map<String, String> englishMessages = messages.get("en");
        if (englishMessages != null && englishMessages.containsKey(key)) {
            return englishMessages.get(key);
        }
        
        return defaultValue.isEmpty() ? key : defaultValue;
    }
}
