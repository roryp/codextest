# Pet Story App

A Spring Boot web application that generates AI-powered descriptions and stories for uploaded pet images using GitHub Models API.

## Features

- 📸 **Image Upload**: Upload pet photos through a simple web interface
- 🤖 **AI-Powered Descriptions**: Generate detailed captions for pet images using GitHub Models API
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

## License

This project is licensed under the MIT License - see the LICENSE file for details.

## Support

For issues and questions:
- Check the troubleshooting section
- Review GitHub Models documentation
- Open an issue in the repository

---

**Note**: This application is designed for educational and demonstration purposes. For production use, consider additional security hardening and monitoring.
