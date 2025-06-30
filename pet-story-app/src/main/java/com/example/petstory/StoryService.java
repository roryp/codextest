package com.example.petstory;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Service;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

@Service
public class StoryService {

    private static final ObjectMapper MAPPER = new ObjectMapper();

    public String generateStory(String description) throws Exception {
        String token = System.getenv("GITHUB_TOKEN");
        if (token == null) {
            throw new IllegalStateException("GITHUB_TOKEN not set");
        }

        String prompt = "Write a fun short story about a pet described as: " + description;
        String requestBody = MAPPER.createObjectNode()
                .put("prompt", prompt)
                .toString();

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("https://api.github.com/models/gpt2:generate"))
                .header("Authorization", "Bearer " + token)
                .header("Accept", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(requestBody))
                .build();

        HttpClient client = HttpClient.newHttpClient();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        JsonNode node = MAPPER.readTree(response.body());
        return node.get("text").asText();
    }
}
