# Pet Story App

A Spring Boot web application that generates AI-powered descriptions and stories for uploaded pet images using GitHub Models API.

## Features

- 📸 **Image Upload**: Upload pet photos through a simple web interface
- 🤖 **AI-Powered Descriptions**: Generate detailed captions for pet images using OpenAI's GPT-4o-mini
- 📚 **Story Generation**: Create fun, family-friendly short stories based on pet descriptions
- 🛡️ **Robust Fallback**: Graceful degradation when AI services are unavailable
- 🔒 **Secure**: Built with Spring Security for authentication

## Technology Stack

- **Backend**: Spring Boot 3.5.3, Java 21
- **AI Integration**: Azure AI Inference SDK with GitHub Models API
- **Security**: Spring Security
- **Frontend**: Thymeleaf templates with Bootstrap styling
- **Build Tool**: Maven

## Prerequisites

- Java 21 or higher
- Maven 3.6+
- GitHub account with access to GitHub Models
- GitHub Personal Access Token with `models:read` permissions

## Setup & Installation

### 1. Clone the Repository
```bash
git clone <repository-url>
cd pet-story-app
```

### 2. Configure GitHub Models API

1. Create a GitHub Personal Access Token:
   - Go to GitHub Settings → Developer settings → Personal access tokens
   - Generate a new token with `models:read` permissions
   
2. Set the environment variable:
   ```bash
   # Windows
   set GITHUB_TOKEN=your_github_token_here
   
   # Linux/Mac
   export GITHUB_TOKEN=your_github_token_here
   ```

### 3. Build the Application
```bash
mvn clean compile
```

### 4. Run the Application
```bash
mvn spring-boot:run
```

The application will start on `http://localhost:8080`

## Usage

1. **Access the Application**: Navigate to `http://localhost:8080`
2. **Upload Image**: Click "Choose File" and select a pet image
3. **Generate Content**: Click "Upload and Generate Story"
4. **View Results**: See the AI-generated description and story

## API Configuration

The application uses the following AI model configuration:

- **Endpoint**: `https://models.github.ai/inference`
- **Model**: `openai/gpt-4o-mini` (multimodal, cost-effective)
- **Authentication**: GitHub Personal Access Token

## Rate Limits

GitHub Models free tier has the following limits for `gpt-4o-mini`:
- **15 requests per minute**
- **150 requests per day**
- **8000 tokens input, 4000 tokens output** per request
- **5 concurrent requests**

When limits are exceeded, the application automatically falls back to local generation.

## Fallback Mechanism

When AI services are unavailable (budget limits, network issues, etc.), the application provides:
- **Fallback Captions**: Generic but descriptive pet descriptions
- **Fallback Stories**: Template-based stories with pet characteristics
- **Seamless UX**: Users experience no interruption in service

## Security

- **Spring Security**: Basic authentication enabled
- **File Validation**: Image upload validation and sanitization
- **Error Handling**: Secure error messages without sensitive information

## Project Structure

```
pet-story-app/
├── src/main/java/com/example/petstory/
│   ├── PetStoryApplication.java      # Main Spring Boot application
│   ├── PetController.java            # Web controller with fallback logic
│   ├── ImageService.java             # AI image captioning service
│   ├── StoryService.java             # AI story generation service
│   └── SecurityConfig.java           # Security configuration
├── src/main/resources/
│   ├── templates/
│   │   ├── index.html                # Upload page
│   │   └── result.html               # Results display page
│   └── application.properties        # App configuration
├── src/test/java/                    # Unit tests
├── pom.xml                           # Maven dependencies
└── README.md                         # This file
```

## Testing

Run the test suite:
```bash
mvn test
```

### Manual Testing with Python Script

A Python test script is included for automated testing:
```bash
python test_image_upload.py
```

## Troubleshooting

### Common Issues

1. **"Budget limit reached" error**
   - Wait for daily rate limit reset (24 hours)
   - The app will continue working with fallback responses

2. **"GITHUB_TOKEN not configured" warning**
   - Ensure the environment variable is set correctly
   - Restart the application after setting the token

3. **"Unknown model" error**
   - Verify the model identifier is correct: `openai/gpt-4o-mini`
   - Check GitHub Models availability

### Logs

Enable debug logging by adding to `application.properties`:
```properties
logging.level.com.example.petstory=DEBUG
```

## Development

### Adding New Models

To use a different AI model:

1. Update the model identifier in both services:
   ```java
   options.setModel("new-model-identifier");
   ```

2. Ensure the model supports the required capabilities (vision for ImageService)

### Extending Functionality

- **New File Types**: Modify file upload validation in `PetController`
- **Custom Prompts**: Update system messages in service classes
- **Enhanced UI**: Modify Thymeleaf templates in `src/main/resources/templates/`

## Production Deployment

For production use:

1. **Upgrade to Paid GitHub Models**: Higher rate limits and SLA
2. **Environment Variables**: Use proper secret management
3. **Security**: Configure HTTPS and authentication
4. **Monitoring**: Add application monitoring and logging
5. **Database**: Consider adding data persistence

## Contributing

1. Fork the repository
2. Create a feature branch
3. Make changes with tests
4. Submit a pull request

## License

This project is licensed under the MIT License - see the LICENSE file for details.

## Support

For issues and questions:
- Check the troubleshooting section
- Review GitHub Models documentation
- Open an issue in the repository

---

**Note**: This application is designed for educational and demonstration purposes. For production use, consider additional security hardening and monitoring.
