package com.example.petstory;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Service;

import java.io.File;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.file.Files;
import java.util.Base64;

@Service
public class ImageService {

    private static final ObjectMapper MAPPER = new ObjectMapper();

    public String caption(File image) throws Exception {
        String token = System.getenv("GITHUB_TOKEN");
        if (token == null) {
            throw new IllegalStateException("GITHUB_TOKEN not set");
        }

        String base64 = Base64.getEncoder().encodeToString(Files.readAllBytes(image.toPath()));
        String requestBody = MAPPER.createObjectNode()
                .put("image", base64)
                .toString();

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("https://api.github.com/models/blip-image-captioning-base:predict"))
                .header("Authorization", "Bearer " + token)
                .header("Accept", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(requestBody))
                .build();

        HttpClient client = HttpClient.newHttpClient();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        JsonNode node = MAPPER.readTree(response.body());
        return node.get("caption").asText();
    }
}
