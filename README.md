![Arogya-Sakhi logo](https://github.com/Sanyamodi/arogya-sakhi-bot/blob/main/arogya_sakhi.png?raw=true)

# Arogya-Sakhi Telegram Bot 🏥

A comprehensive AI-powered health assistant Telegram bot that provides personalized health recommendations, home remedies, medication suggestions, and emergency guidance using Google Gemini AI.

## 🌟 Features

- 🩺 **AI Health Consultations**: Symptom analysis with detailed recommendations
- 👤 **User Profile Management**: Complete health profile with BMI calculation
- 🏠 **Home Remedies**: Natural treatment suggestions based on symptoms
- 💊 **Medication Guidance**: Over-the-counter medication recommendations with dosages
- 🚨 **Emergency Detection**: Identifies serious conditions requiring immediate medical attention
- 🩹 **First Aid Advice**: Temporary treatment guidance for common issues
- 👨‍⚕️ **Doctor Recommendations**: Smart suggestions for when to consult healthcare professionals
- 📊 **Health History**: Tracks and stores previous consultations
- 🌐 **Multi-language Support**: Available in Hindi and English
- 📱 **User-friendly Interface**: Intuitive keyboard navigation

## 🛠️ Technologies Used

- **Java 17** with Spring Boot framework
- **Telegram Bot API** for bot functionality and user interaction
- **Google Gemini AI** (gemini-1.5-flash model) for intelligent health recommendations
- **MongoDB Atlas** for cloud-based data storage
- **Spring Data MongoDB** for database operations
- **WebFlux** for reactive HTTP client operations
- **Maven** for dependency management and build automation

## 🏗️ Architecture Overview

### Core Components

1. **ArogyaSakhiBot.java** - Main bot class handling Telegram interactions
2. **GeminiService.java** - AI service for health recommendations
3. **UserProfileService.java** - User profile management
4. **UserSessionService.java** - Session and state management
5. **LanguageService.java** - Multi-language support

### Data Models

1. **UserProfile** - Stores user health information
2. **HealthConsultation** - Records consultation history
3. **UserSession** - Manages user state and language preferences

### Database Collections

- `user_profiles` - User health profiles and medical history
- `health_consultations` - Consultation records and AI recommendations
- `user_sessions` - User session state and language preferences

## 🚀 Setup Instructions

### Prerequisites

1. **Java 17** or higher
2. **Maven 3.6+**
3. **Telegram Bot Token** (from @BotFather)
4. **Google Gemini API Key**
5. **MongoDB Atlas Account** (or local MongoDB)

### Step 1: Create Telegram Bot

1. Open Telegram and search for `@BotFather`
2. Send `/newbot` command
3. Choose a name: `Arogya-Sakhi`
4. Choose a username: `ArogyaSakhiBot` (or any available name)
5. Copy the bot token provided

### Step 2: Get Google Gemini API Key

1. Go to [Google AI Studio](https://makersuite.google.com/app/apikey)
2. Create a new API key
3. Copy the API key for configuration

### Step 3: Configure Environment Variables

Create a `.env` file in the project root:

\`\`\`env
BOT_USERNAME=YourBotUsername
BOT_TOKEN=YOUR_TELEGRAM_BOT_TOKEN
GEMINI_API_KEY=YOUR_GEMINI_API_KEY
MONGO_URI=YOUR_MONGODB_CONNECTION_STRING
\`\`\`

Or set environment variables:

\`\`\`bash
export BOT_USERNAME="YourBotUsername"
export BOT_TOKEN="YOUR_TELEGRAM_BOT_TOKEN"
export GEMINI_API_KEY="YOUR_GEMINI_API_KEY"
export MONGO_URI="YOUR_MONGODB_CONNECTION_STRING"
\`\`\`

### Step 4: Build and Run

1. **Clone/Download the project**

2. **Build the project:**
   \`\`\`bash
   mvn clean compile
   \`\`\`

3. **Run the application:**
   \`\`\`bash
   mvn spring-boot:run
   \`\`\`

4. **Alternative - Create JAR and run:**
   \`\`\`bash
   mvn clean package
   java -jar target/telegram-health-bot-1.0.0.jar
   \`\`\`

## 📱 Usage Guide

### Bot Commands and Features

- `/start` - Initialize the bot and show language selection
- `🏥 Health Consultation` - Start a new AI-powered health consultation
- `👤 My Profile` - View complete health profile with BMI
- `📝 Update Profile` - Update health information for better recommendations
- `📊 Health History` - View previous consultations and recommendations
- `🌐 Language` - Switch between Hindi and English
- `ℹ️ Help` - Show detailed help information

### Profile Setup Process

The bot guides users through comprehensive profile setup:

1. **Personal Information**: Name, age, gender
2. **Physical Metrics**: Weight, height (auto-calculates BMI)
3. **Medical History**: Allergies, previous diseases
4. **Current Medications**: For drug interaction awareness
5. **Emergency Contact**: For emergency situations

### Health Consultation Process

1. **Symptom Description**: User describes symptoms in detail
2. **AI Analysis**: Gemini AI processes symptoms with user profile context
3. **Structured Recommendations**:
   - Severity assessment (Low/Moderate/High/Emergency)
   - Home remedies with natural treatments
   - Over-the-counter medication suggestions with dosages
   - Warning signs to watch for
   - When to consult a doctor
4. **Follow-up**: Recommendations saved to health history

## 🔧 Code Structure Explanation

### Main Bot Class (ArogyaSakhiBot.java)

\`\`\`java
@Component
public class ArogyaSakhiBot extends TelegramLongPollingBot {
    // Handles all Telegram interactions
    // Manages user states and conversation flow
    // Integrates with all services
}
\`\`\`

**Key Methods:**
- `onUpdateReceived()` - Processes incoming messages
- `handleMessage()` - Routes messages based on content and state
- `handleStateBasedMessage()` - Manages conversation states
- `sendMessageWithKeyboard()` - Sends formatted responses with custom keyboards

### AI Service (GeminiService.java)

\`\`\`java
@Service
public class GeminiService {
    // Integrates with Google Gemini AI
    // Formats medical prompts
    // Parses and formats AI responses
}
\`\`\`

**Key Methods:**
- `getHealthRecommendation()` - Main method for AI consultation
- `buildDetailedHealthPrompt()` - Creates structured medical prompts
- `parseGeminiResponse()` - Extracts and formats AI responses
- `formatResponseText()` - Cleans up formatting for better readability

### User Profile Service (UserProfileService.java)

\`\`\`java
@Service
public class UserProfileService {
    // Manages user health profiles
    // Calculates BMI and health metrics
    // Validates profile completeness
}
\`\`\`

**Key Methods:**
- `getUserProfile()` - Retrieves or creates user profile
- `saveUserProfile()` - Persists profile data
- `isProfileComplete()` - Validates required fields
- `updateProfileStatus()` - Updates profile completion status

### Session Management (UserSessionService.java)

\`\`\`java
@Service
public class UserSessionService {
    // Manages user conversation states
    // Handles language preferences
    // Maintains session persistence
}
\`\`\`

**Key Methods:**
- `getUserSession()` - Gets or creates user session
- `updateUserState()` - Updates conversation state
- `updateUserLanguage()` - Changes user language preference

## 🗄️ Database Schema

### UserProfile Collection
\`\`\`javascript
{
  "_id": "ObjectId",
  "chat_id": 123456789,
  "first_name": "John",
  "last_name": "Doe",
  "age": 30,
  "gender": "Male",
  "weight": 70.5,
  "height": 175.0,
  "blood_group": "O+",
  "allergies": ["Peanuts", "Dust"],
  "previous_diseases": ["Diabetes"],
  "current_medications": ["Metformin"],
  "emergency_contact": "+91-9876543210",
  "status": "COMPLETE",
  "created_at": "2024-01-01T10:00:00",
  "updated_at": "2024-01-01T10:00:00"
}
\`\`\`

### HealthConsultation Collection
\`\`\`javascript
{
  "_id": "ObjectId",
  "chat_id": 123456789,
  "symptoms": "Fever and headache for 2 days",
  "ai_recommendation": "Detailed AI response...",
  "severity": "MODERATE",
  "doctor_recommended": true,
  "consultation_time": "2024-01-01T10:00:00"
}
\`\`\`

## 🚀 Deployment Options

### Local Development
\`\`\`bash
mvn spring-boot:run
\`\`\`

### Docker Deployment
\`\`\`bash
docker build -t arogya-sakhi-bot .
docker run -p 8080:8080 --env-file .env arogya-sakhi-bot
\`\`\`

### Cloud Deployment
- **Heroku**: Use provided `Procfile`
- **AWS**: Deploy using Elastic Beanstalk
- **Google Cloud**: Use App Engine or Cloud Run
- **Railway**: Direct GitHub integration

## 🔍 Testing and Debugging

### Health Check Endpoints
- `GET /health` - Application health status
- `GET /health/db` - Database connectivity check
- `GET /check-config` - Configuration validation
- `GET /test-gemini` - Test AI service functionality
- `GET /list-models` - List available Gemini models

### Testing Commands
\`\`\`bash
# Test configuration
curl http://localhost:8080/check-config

# Test AI service
curl "http://localhost:8080/test-gemini?symptoms=headache&language=en"

# Test database
curl http://localhost:8080/health/db
\`\`\`

## 🛡️ Security and Privacy

- **Data Encryption**: All data stored in encrypted MongoDB Atlas
- **API Security**: Secure API key management
- **Privacy**: No sensitive medical data shared with third parties
- **Compliance**: Follows medical data handling best practices

## ⚠️ Important Disclaimers

- This bot provides **general health guidance only**
- **Always consult healthcare professionals** for serious conditions
- In emergencies, **call 108 immediately**
- This is **not a substitute** for professional medical advice
- AI recommendations are based on general medical knowledge

## 📞 Emergency Numbers (India)

- **Emergency**: 108
- **Ambulance**: 102
- **Medical Helpline**: 104
- **Women Helpline**: 1091
- **Child Helpline**: 1098

## 🤝 Contributing

1. Fork the repository
2. Create a feature branch
3. Make your changes
4. Add tests if applicable
5. Submit a pull request

## 📄 License

This project is licensed under the Apache License - see the LICENSE file for details.

## 🙏 Acknowledgments

- Google Gemini AI for intelligent health recommendations
- Telegram Bot API for seamless user interaction
- MongoDB Atlas for reliable cloud database
- Spring Boot community for excellent framework support

---

**Made with ❤️ for better healthcare accessibility**
\`\`\`
