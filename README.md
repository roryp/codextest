# Pet Story App

This project provides a Spring Boot application that lets you upload a picture of your pet and generates a fun story about it. The application integrates with GitHub Models over HTTPS using the `azure-ai-inference` library to call AI models for image description and story generation.

## Features

- **Image Upload & Validation**: Secure file upload with size and type validation
- **AI-Powered Image Description**: Uses Microsoft Phi-4 multimodal model to describe pet images
- **Story Generation**: Creates fun, family-friendly stories using OpenAI GPT-4.1-nano
- **Security Features**: File upload protection, input sanitization, and security headers
- **Error Handling**: Comprehensive error handling with user-friendly messages
- **Responsive UI**: Clean, modern web interface with CSS styling

## Prerequisites

- Java 17 or higher
- Maven 3.6+ 
- GitHub Personal Access Token with access to GitHub Models API

## Setup Instructions

### 1. Clone the Repository

```bash
git clone <repository-url>
cd pet-story-app
```

### 2. Set Environment Variables

You need a GitHub Personal Access Token with access to the GitHub Models API:

```bash
export GITHUB_TOKEN=your_github_token_here
```

On Windows:
```cmd
set GITHUB_TOKEN=your_github_token_here
```

### 3. Build the Application

```bash
./mvnw clean compile
```

### 4. Run Tests

```bash
./mvnw test
```

Note: Some tests require the GITHUB_TOKEN environment variable to be set.

### 5. Run the Application

```bash
./mvnw spring-boot:run
```

The application will start on `http://localhost:8080`

## Usage

1. Open your web browser and navigate to `http://localhost:8080`
2. Click "Choose File" and select an image of your pet
3. Supported formats: JPEG, PNG, GIF, WebP (max 10MB)
4. Click "Generate Story" 
5. Wait for the AI to analyze your image and generate a story
6. View the pet description and generated story
7. Click "Upload Another Image" to try with a different photo

## Configuration

The application can be configured via `src/main/resources/application.properties`:

```properties
# File upload limits
spring.servlet.multipart.max-file-size=10MB
spring.servlet.multipart.max-request-size=10MB

# Logging levels
logging.level.com.example.petstory=INFO

# Server configuration
server.error.include-stacktrace=never
server.error.include-message=always
```

## Security Features

- **File Validation**: Only image files up to 10MB are accepted
- **Input Sanitization**: All user inputs are sanitized to prevent injection attacks
- **Secure Headers**: Security headers configured to prevent common attacks
- **Temporary File Cleanup**: Uploaded files are securely deleted after processing
- **Error Handling**: Detailed error messages without exposing sensitive information

## Container Deployment

A Dockerfile is included for containerized deployment:

```bash
docker build -t pet-story-app .
docker run -e GITHUB_TOKEN=your_token -p 8080:8080 pet-story-app
```

## API Dependencies

This application uses the following AI models via GitHub Models:

- **Image Description**: `microsoft/Phi-4-multimodal-instruct`
- **Story Generation**: `openai/gpt-4.1-nano`

## Project Structure

```
src/
├── main/
│   ├── java/com/example/petstory/
│   │   ├── PetStoryApplication.java     # Main Spring Boot application
│   │   ├── PetController.java           # Web controller with file upload handling
│   │   ├── ImageService.java            # AI service for image description
│   │   ├── StoryService.java            # AI service for story generation
│   │   └── SecurityConfig.java          # Security configuration
│   └── resources/
│       ├── application.properties       # Application configuration
│       └── templates/                   # Thymeleaf HTML templates
│           ├── index.html              # Upload form
│           └── result.html             # Results display
└── test/
    └── java/com/example/petstory/      # Unit tests
```

## Troubleshooting

### Common Issues

1. **GITHUB_TOKEN not set error**
   - Ensure your GitHub token is properly set as an environment variable
   - Verify the token has access to GitHub Models API

2. **File upload fails**
   - Check file size (max 10MB) and format (JPEG, PNG, GIF, WebP only)
   - Ensure sufficient disk space for temporary files

3. **Build failures**
   - Ensure Java 17+ is installed and JAVA_HOME is set correctly
   - Run `./mvnw clean` to clear any build artifacts

4. **AI service errors**
   - Verify internet connectivity
   - Check GitHub Models API status
   - Ensure your token has not expired

### Logs

Application logs are available at INFO level for the `com.example.petstory` package. Check logs for detailed error information:

```bash
./mvnw spring-boot:run --debug
```

## Contributing

1. Follow existing code style and patterns
2. Add unit tests for new functionality
3. Update documentation for any configuration changes
4. Test with various image types and sizes

## License

This project is provided as-is for demonstration purposes.
