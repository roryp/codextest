FROM eclipse-temurin:21-jdk

WORKDIR /app

COPY pet-story-app /app
RUN ./mvnw -q package

EXPOSE 8080
CMD ["java", "-jar", "target/pet-story-app-0.0.1-SNAPSHOT.jar"]
