package com.arogyasakhi.controller;

import com.arogyasakhi.service.GeminiService;
import com.arogyasakhi.model.UserProfile;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.reactive.function.client.WebClient;

@RestController
public class TestController {
    
    @Autowired
    private GeminiService geminiService;
    
    @Value("${gemini.api.key}")
    private String apiKey;
    
    @Value("${gemini.api.url}")
    private String apiUrl;
    
    @GetMapping("/test-gemini")
    public String testGemini(@RequestParam(defaultValue = "headache and fever") String symptoms,
                           @RequestParam(defaultValue = "en") String language) {
        try {
            System.out.println("🧪 Testing Gemini API...");
            
            // Create a test user profile
            UserProfile testProfile = new UserProfile(12345L);
            testProfile.setAge(30);
            testProfile.setGender("Male");
            testProfile.setWeight(70.0);
            testProfile.setHeight(175.0);
            
            String result = geminiService.getHealthRecommendation(symptoms, testProfile, language);
            return "✅ Test Result:\n\n" + result;
        } catch (Exception e) {
            return "❌ Test Error: " + e.getMessage();
        }
    }
    
    @GetMapping("/test-api-direct")
    public String testApiDirect() {
        try {
            System.out.println("🧪 Testing Gemini API directly...");
            
            // Test with minimal request using correct model
            String testPrompt = "What are home remedies for headache?";
            
            WebClient webClient = WebClient.builder().build();
            
            String requestBody = """
                {
                    "contents": [
                        {
                            "parts": [
                                {
                                    "text": "%s"
                                }
                            ]
                        }
                    ],
                    "generationConfig": {
                        "temperature": 0.3,
                        "maxOutputTokens": 1000
                    }
                }
                """.formatted(testPrompt);
            
            String fullUrl = apiUrl + "?key=" + apiKey;
            
            String response = webClient.post()
                    .uri(fullUrl)
                    .header("Content-Type", "application/json")
                    .bodyValue(requestBody)
                    .retrieve()
                    .bodyToMono(String.class)
                    .block();
            
            return "✅ Direct API Test Result:\n\n" + response;
            
        } catch (Exception e) {
            return "❌ Direct API Test Error: " + e.getMessage();
        }
    }
    
    @GetMapping("/list-models")
    public String listModels() {
        try {
            System.out.println("🧪 Listing available Gemini models...");
            
            WebClient webClient = WebClient.builder().build();
            
            String listUrl = "https://generativelanguage.googleapis.com/v1beta/models?key=" + apiKey;
            
            String response = webClient.get()
                    .uri(listUrl)
                    .retrieve()
                    .bodyToMono(String.class)
                    .block();
            
            return "✅ Available Models:\n\n" + response;
            
        } catch (Exception e) {
            return "❌ List Models Error: " + e.getMessage();
        }
    }
    
    @GetMapping("/check-config")
    public String checkConfig() {
        StringBuilder config = new StringBuilder();
        config.append("🔧 Configuration Check:\n\n");
        config.append("API Key: ").append(apiKey != null && !apiKey.isEmpty() ? "✅ Present (" + apiKey.length() + " chars)" : "❌ Missing").append("\n");
        config.append("API URL: ").append(apiUrl != null ? "✅ " + apiUrl : "❌ Missing").append("\n");
        
        // Test API key format
        if (apiKey != null) {
            if (apiKey.startsWith("AIza")) {
                config.append("API Key Format: ✅ Valid Google API key format\n");
            } else {
                config.append("API Key Format: ❌ Invalid format (should start with 'AIza')\n");
            }
        }
        
        return config.toString();
    }
}
