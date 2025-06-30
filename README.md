# Pet Story App

This project provides a small Spring Boot application that lets you upload a picture of your pet, describes the picture using a local model and generates a fun story about it. The example integrates with open-source GitHub models by calling Python scripts that use the HuggingFace `transformers` library.

## Building and running locally

The application requires Java 21, Maven and Python 3 with the `transformers` and `Pillow` packages installed. The Python scripts download open models from HuggingFace on first run.

```bash
cd pet-story-app
./mvnw spring-boot:run
```

Then open <http://localhost:8080> in a browser and upload an image.

## Container

A simple Dockerfile is included to run the application in a local container:

```bash
docker build -t pet-story-app .
docker run -p 8080:8080 pet-story-app
```
