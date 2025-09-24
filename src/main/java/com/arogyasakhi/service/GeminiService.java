package com.arogyasakhi.service;

import com.arogyasakhi.model.UserProfile;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class GeminiService {
    
    @Value("${gemini.api.key}")
    private String apiKey;
    
    @Value("${gemini.api.url}")
    private String apiUrl;
    
    private final WebClient webClient;
    private final ObjectMapper objectMapper;
    
    public GeminiService() {
        this.webClient = WebClient.builder()
                .codecs(configurer -> configurer.defaultCodecs().maxInMemorySize(2 * 1024 * 1024))
                .build();
        this.objectMapper = new ObjectMapper();
    }
    
    public String getHealthRecommendation(String symptoms, UserProfile userProfile, String language) {
        try {
            System.out.println("🔍 Starting health recommendation...");
            System.out.println("🔑 API Key present: " + (apiKey != null && !apiKey.isEmpty()));
            System.out.println("🌐 API URL: " + apiUrl);
            System.out.println("📝 Symptoms: " + symptoms);
            System.out.println("🗣️ Language: " + language);
            
            // Validate inputs
            if (apiKey == null || apiKey.trim().isEmpty()) {
                System.err.println("❌ Gemini API key is missing!");
                return getErrorMessage(language, "API key not configured");
            }
            
            if (symptoms == null || symptoms.trim().isEmpty()) {
                return getErrorMessage(language, "No symptoms provided");
            }
            
            String prompt = buildDetailedHealthPrompt(symptoms, userProfile, language);
            System.out.println("📝 Prompt length: " + prompt.length());
            
            Map<String, Object> requestBody = buildRequestBody(prompt);
            
            // Log the request body for debugging
            String requestJson = objectMapper.writeValueAsString(requestBody);
            System.out.println("📤 Request JSON length: " + requestJson.length());
            
            String fullUrl = apiUrl + "?key=" + apiKey;
            
            Mono<String> responseMono = webClient.post()
                    .uri(fullUrl)
                    .header("Content-Type", "application/json")
                    .header("User-Agent", "Arogya-Sakhi-Bot/1.0")
                    .bodyValue(requestBody)
                    .retrieve()
                    .bodyToMono(String.class)
                    .timeout(Duration.ofSeconds(30));
            
            String responseBody = responseMono.block();
            System.out.println("📥 Response received, length: " + (responseBody != null ? responseBody.length() : 0));
            
            String recommendation = parseGeminiResponse(responseBody);
            System.out.println("✅ Health recommendation generated successfully");
            
            return recommendation;
            
        } catch (WebClientResponseException e) {
            System.err.println("❌ Gemini API Error:");
            System.err.println("   Status: " + e.getStatusCode());
            System.err.println("   Response: " + e.getResponseBodyAsString());
            
            // Try to parse error response
            try {
                JsonNode errorResponse = objectMapper.readTree(e.getResponseBodyAsString());
                if (errorResponse.has("error")) {
                    JsonNode error = errorResponse.get("error");
                    String errorMessage = error.has("message") ? error.get("message").asText() : "Unknown error";
                    System.err.println("   Error message: " + errorMessage);
                    return getErrorMessage(language, "API Error: " + errorMessage);
                }
            } catch (Exception parseError) {
                System.err.println("   Could not parse error response: " + parseError.getMessage());
            }
            
            return getErrorMessage(language, "API Error: " + e.getStatusCode());
            
        } catch (Exception e) {
            System.err.println("❌ Unexpected error in health recommendation: " + e.getMessage());
            e.printStackTrace();
            return getErrorMessage(language, "Unexpected error: " + e.getMessage());
        }
    }
    
    private Map<String, Object> buildRequestBody(String prompt) {
        Map<String, Object> requestBody = new HashMap<>();
        
        // Contents array
        Map<String, Object> content = new HashMap<>();
        Map<String, Object> part = new HashMap<>();
        part.put("text", prompt);
        content.put("parts", List.of(part));
        requestBody.put("contents", List.of(content));
        
        // Generation config for better medical responses
        Map<String, Object> generationConfig = new HashMap<>();
        generationConfig.put("temperature", 0.3); // Lower temperature for more consistent medical advice
        generationConfig.put("topK", 40);
        generationConfig.put("topP", 0.95);
        generationConfig.put("maxOutputTokens", 500);
        requestBody.put("generationConfig", generationConfig);
        
        return requestBody;
    }
    
    private String buildDetailedHealthPrompt(String symptoms, UserProfile userProfile, String language) {
        StringBuilder prompt = new StringBuilder();
        
        if ("hi".equals(language)) {
            prompt.append("आप एक अनुभवी डॉक्टर हैं। मरीज के लक्षणों के लिए विस्तृत सलाह दें।\n\n");
            
            // Add patient information
            if (userProfile != null && userProfile.getAge() != null) {
                prompt.append("मरीज की जानकारी:\n");
                prompt.append("- उम्र: ").append(userProfile.getAge()).append(" साल\n");
                if (userProfile.getGender() != null) {
                    prompt.append("- लिंग: ").append(userProfile.getGender()).append("\n");
                }
                if (userProfile.getWeight() != null && userProfile.getHeight() != null) {
                    prompt.append("- BMI: ").append(String.format("%.1f", userProfile.getBMI())).append("\n");
                }
                if (userProfile.getAllergies() != null && !userProfile.getAllergies().isEmpty()) {
                    prompt.append("- एलर्जी: ").append(String.join(", ", userProfile.getAllergies())).append("\n");
                }
                if (userProfile.getCurrentMedications() != null && !userProfile.getCurrentMedications().isEmpty()) {
                    prompt.append("- वर्तमान दवाएं: ").append(String.join(", ", userProfile.getCurrentMedications())).append("\n");
                }
                prompt.append("\n");
            }
            
            prompt.append("लक्षण: ").append(symptoms).append("\n\n");
            prompt.append("कृपया निम्नलिखित प्रारूप में विस्तृत सलाह दें:\n\n");
            prompt.append("🔍 गंभीरता का स्तर: (कम/मध्यम/उच्च/आपातकाल)\n\n");
            prompt.append("🏠 घरेलू उपचार:\n");
            prompt.append("- तुरंत राहत के लिए क्या करें\n");
            prompt.append("- प्राकृतिक उपचार\n");
            prompt.append("- आहार संबंधी सुझाव\n");
            prompt.append("- जीवनशैली में बदलाव\n\n");
            prompt.append("💊 दवा सुझाव (बिना पर्चे वाली):\n");
            prompt.append("- दर्द निवारक दवाएं\n");
            prompt.append("- खुराक और समय\n");
            prompt.append("- सावधानियां\n\n");
            prompt.append("⚠️ चेतावनी संकेत:\n");
            prompt.append("- तुरंत डॉक्टर से मिलें यदि\n");
            prompt.append("- आपातकालीन स्थितियां\n\n");
            prompt.append("📞 कब डॉक्टर से संपर्क करें:\n");
            prompt.append("- लक्षण बिगड़ने पर\n");
            prompt.append("- कितने दिन बाद\n\n");
            prompt.append("महत्वपूर्ण: यह केवल सामान्य सलाह है। गंभीर स्थिति में तुरंत चिकित्सक से संपर्क करें।");
            
        } else {
            prompt.append("You are an experienced doctor. Provide detailed medical advice for the patient's symptoms.\n\n");
            
            // Add patient information
            if (userProfile != null && userProfile.getAge() != null) {
                prompt.append("Patient Information:\n");
                prompt.append("- Age: ").append(userProfile.getAge()).append(" years\n");
                if (userProfile.getGender() != null) {
                    prompt.append("- Gender: ").append(userProfile.getGender()).append("\n");
                }
                if (userProfile.getWeight() != null && userProfile.getHeight() != null) {
                    prompt.append("- BMI: ").append(String.format("%.1f", userProfile.getBMI())).append("\n");
                }
                if (userProfile.getAllergies() != null && !userProfile.getAllergies().isEmpty()) {
                    prompt.append("- Allergies: ").append(String.join(", ", userProfile.getAllergies())).append("\n");
                }
                if (userProfile.getCurrentMedications() != null && !userProfile.getCurrentMedications().isEmpty()) {
                    prompt.append("- Current Medications: ").append(String.join(", ", userProfile.getCurrentMedications())).append("\n");
                }
                prompt.append("\n");
            }
            
            prompt.append("Symptoms: ").append(symptoms).append("\n\n");
            prompt.append("Please provide detailed advice in the following format:\n\n");
            prompt.append("🔍 SEVERITY LEVEL: (Low/Moderate/High/Emergency)\n\n");
            prompt.append("🏠 HOME REMEDIES:\n");
            prompt.append("- Immediate relief measures\n");
            prompt.append("- Natural treatments\n");
            prompt.append("- Dietary recommendations\n");
            prompt.append("- Lifestyle modifications\n\n");
            prompt.append("💊 MEDICATION SUGGESTIONS (Over-the-counter):\n");
            prompt.append("- Pain relievers/fever reducers\n");
            prompt.append("- Dosage and timing\n");
            prompt.append("- Precautions and contraindications\n\n");
            prompt.append("⚠️ WARNING SIGNS:\n");
            prompt.append("- When to seek immediate medical attention\n");
            prompt.append("- Emergency symptoms to watch for\n\n");
            prompt.append("📞 WHEN TO CONSULT A DOCTOR:\n");
            prompt.append("- If symptoms worsen\n");
            prompt.append("- Timeline for medical consultation\n\n");
            prompt.append("IMPORTANT: This is general medical advice only. Seek immediate professional medical care for serious conditions.");
        }
        
        return prompt.toString();
    }
    
    private String parseGeminiResponse(String responseBody) {
        try {
            if (responseBody == null || responseBody.trim().isEmpty()) {
                System.err.println("❌ Empty response from Gemini API");
                return "No response received from AI service.";
            }
            
            JsonNode root = objectMapper.readTree(responseBody);
            
            // Check for error in response
            if (root.has("error")) {
                JsonNode error = root.get("error");
                String errorMessage = error.has("message") ? error.get("message").asText() : "Unknown API error";
                System.err.println("❌ Gemini API returned error: " + errorMessage);
                return "AI service error: " + errorMessage;
            }
            
            // Parse successful response
            JsonNode candidates = root.get("candidates");
            if (candidates != null && candidates.isArray() && candidates.size() > 0) {
                JsonNode firstCandidate = candidates.get(0);
                
                // Check finish reason
                if (firstCandidate.has("finishReason")) {
                    String finishReason = firstCandidate.get("finishReason").asText();
                    System.out.println("📋 Finish reason: " + finishReason);
                    
                    if ("SAFETY".equals(finishReason)) {
                        return "Response was blocked due to safety filters. Please rephrase your symptoms or consult a healthcare professional directly.";
                    }
                }
                
                JsonNode content = firstCandidate.get("content");
                if (content != null && content.has("parts")) {
                    JsonNode parts = content.get("parts");
                    if (parts.isArray() && parts.size() > 0) {
                        JsonNode firstPart = parts.get(0);
                        if (firstPart.has("text")) {
                            String text = firstPart.get("text").asText();
                            if (text != null && !text.trim().isEmpty()) {
                                System.out.println("✅ Successfully parsed response");
                                // Format the response for better readability
                                String formattedText = formatResponseText(text.trim());
                                return formattedText;
                            }
                        }
                    }
                }
            }
            
            System.err.println("❌ Could not find valid content in response");
            System.err.println("Response structure: " + root.toString());
            return "Unable to process the AI response. Please try again.";
            
        } catch (Exception e) {
            System.err.println("❌ Error parsing Gemini response: " + e.getMessage());
            e.printStackTrace();
            return "Error processing AI response: " + e.getMessage();
        }
    }
    
    private String getErrorMessage(String language, String error) {
        if ("hi".equals(language)) {
            return "मुझे तकनीकी समस्या हो रही है। कृपया बाद में पुनः प्रयास करें या गंभीर लक्षणों के लिए तुरंत डॉक्टर से संपर्क करें।\n\n" +
                   "आपातकालीन नंबर: 108\n" +
                   "त्रुटि: " + error;
        } else {
            return "I'm experiencing technical difficulties. Please try again later or consult a healthcare professional for serious symptoms.\n\n" +
                   "Emergency number: 108\n" +
                   "Error: " + error;
        }
    }

    private String formatResponseText(String text) {
        // Remove excessive asterisks and format properly
        String formatted = text
                // Replace **text** with bold formatting for Telegram
                .replaceAll("\\*\\*([^*]+)\\*\\*", "*$1*")
                // Replace single asterisks with bullet points
                .replaceAll("(?m)^\\s*\\*\\s*", "• ")
                // Clean up multiple asterisks
                .replaceAll("\\*{3,}", "")
                // Format section headers
                .replaceAll("(?i)🔍\\s*SEVERITY LEVEL:", "\n🔍 *SEVERITY LEVEL:*")
                .replaceAll("(?i)🏠\\s*HOME REMEDIES:", "\n🏠 *HOME REMEDIES:*")
                .replaceAll("(?i)💊\\s*MEDICATION SUGGESTIONS:", "\n💊 *MEDICATION SUGGESTIONS:*")
                .replaceAll("(?i)⚠️\\s*WARNING SIGNS:", "\n⚠️ *WARNING SIGNS:*")
                .replaceAll("(?i)📞\\s*WHEN TO CONSULT:", "\n📞 *WHEN TO CONSULT A DOCTOR:*")
                .replaceAll("(?i)IMPORTANT:", "\n⚠️ *IMPORTANT:*")
                // Format Hindi headers
                .replaceAll("(?i)🔍\\s*गंभीरता का स्तर:", "\n🔍 *गंभीरता का स्तर:*")
                .replaceAll("(?i)🏠\\s*घरेलू उपचार:", "\n🏠 *घरेलू उपचार:*")
                .replaceAll("(?i)💊\\s*दवा सुझाव:", "\n💊 *दवा सुझाव:*")
                .replaceAll("(?i)⚠️\\s*चेतावनी संकेत:", "\n⚠️ *चेतावनी संकेत:*")
                .replaceAll("(?i)📞\\s*कब डॉक्टर से संपर्क करें:", "\n📞 *कब डॉक्टर से संपर्क करें:*")
                .replaceAll("(?i)महत्वपूर्ण:", "\n⚠️ *महत्वपूर्ण:*")
                // Clean up extra newlines
                .replaceAll("\n{3,}", "\n\n")
                // Ensure proper spacing after emojis
                .replaceAll("([🔍🏠💊⚠️📞])([^\\s])", "$1 $2");
        
        return formatted.trim();
    }
}
