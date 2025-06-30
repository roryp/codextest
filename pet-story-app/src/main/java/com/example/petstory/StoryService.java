package com.example.petstory;

import com.azure.ai.inference.ChatCompletionsClient;
import com.azure.ai.inference.ChatCompletionsClientBuilder;
import com.azure.ai.inference.models.ChatCompletions;
import com.azure.ai.inference.models.ChatCompletionsOptions;
import com.azure.ai.inference.models.ChatRequestMessage;
import com.azure.ai.inference.models.ChatRequestSystemMessage;
import com.azure.ai.inference.models.ChatRequestUserMessage;
import com.azure.core.credential.AzureKeyCredential;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;

@Service
public class StoryService {

    private static final Logger logger = LoggerFactory.getLogger(StoryService.class);
    private final ChatCompletionsClient client;

    public StoryService() {
        String token = System.getenv("GITHUB_TOKEN");
        if (token == null) {
            logger.warn("GITHUB_TOKEN environment variable not set - service will not be available");
            this.client = null;
            return;
        }
        
        try {
            this.client = new ChatCompletionsClientBuilder()
                    .credential(new AzureKeyCredential(token))
                    .endpoint("https://models.github.ai/inference")
                    .buildClient();
            logger.info("StoryService initialized successfully");
        } catch (Exception e) {
            logger.error("Failed to initialize Azure AI client", e);
            throw new RuntimeException("Failed to initialize story service", e);
        }
    }

    public String generateStory(String description) {
        if (description == null || description.trim().isEmpty()) {
            logger.warn("Empty or null description provided");
            throw new IllegalArgumentException("Description cannot be empty");
        }
        
        if (description.length() > 1000) {
            logger.warn("Description too long, truncating: {} characters", description.length());
            description = description.substring(0, 1000);
        }
        
        if (client == null) {
            logger.warn("StoryService client not available - falling back to mock service");
            throw new RuntimeException("StoryService not available - GITHUB_TOKEN not configured");
        }
        
        try {
            logger.debug("Generating story for description: {}", description);
            
            List<ChatRequestMessage> messages = Arrays.asList(
                    new ChatRequestSystemMessage("You are a creative storyteller who writes fun, family-friendly short stories about pets. Keep stories under 500 words and appropriate for all ages."),
                    new ChatRequestUserMessage("Write a fun short story about a pet described as: " + description)
            );

            ChatCompletionsOptions options = new ChatCompletionsOptions(messages);
            options.setModel("openai/gpt-4.1-nano");

            logger.debug("Sending request to Azure AI service for story generation");
            ChatCompletions completions = client.complete(options);
            
            if (completions.getChoices() == null || completions.getChoices().isEmpty()) {
                logger.error("No response received from AI service");
                throw new RuntimeException("No response from AI service");
            }
            
            String result = completions.getChoices().get(0).getMessage().getContent();
            logger.debug("Generated story of length: {}", result.length());
            return result;
            
        } catch (Exception e) {
            logger.error("Error generating story for description: {}", description, e);
            // Check if it's a budget limit error or other service error
            if (e.getMessage() != null && e.getMessage().contains("budget limit")) {
                throw new RuntimeException("GitHub Models API budget limit reached. Please check your account or use a different API key.", e);
            }
            throw new RuntimeException("Failed to generate story", e);
        }
    }
}
