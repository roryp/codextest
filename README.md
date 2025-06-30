# Pet Story App

This project provides a small Spring Boot application that lets you upload a picture of your pet and generates a fun story about it. The example now integrates directly with GitHub Models over HTTPS instead of running local Python scripts.

## Building and running locally

The application requires Java&nbsp;21 and Maven. It expects a `GITHUB_TOKEN` environment variable with access to the GitHub Models API.

```bash
cd pet-story-app
./mvnw spring-boot:run
```

Then open <http://localhost:8080> in a browser and upload an image.

## Container

A simple Dockerfile is included to run the application in a local container:

```bash
docker build -t pet-story-app .
docker run -e GITHUB_TOKEN=ghp_yourToken -p 8080:8080 pet-story-app
```
