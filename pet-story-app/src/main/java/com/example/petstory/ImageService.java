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

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Arrays;
import java.util.Base64;
import java.util.List;

@Service
public class ImageService {

    private static final Logger logger = LoggerFactory.getLogger(ImageService.class);
    private final ChatCompletionsClient client;

    public ImageService() {
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
            logger.info("ImageService initialized successfully");
        } catch (Exception e) {
            logger.error("Failed to initialize Azure AI client", e);
            throw new RuntimeException("Failed to initialize image service", e);
        }
    }

    public String caption(File image) throws Exception {
        if (image == null || !image.exists()) {
            logger.error("Image file is null or does not exist");
            throw new IllegalArgumentException("Image file is required");
        }
        
        if (image.length() == 0) {
            logger.error("Image file is empty");
            throw new IllegalArgumentException("Image file cannot be empty");
        }
        
        if (client == null) {
            logger.warn("ImageService client not available - falling back to mock service");
            throw new RuntimeException("ImageService not available - GITHUB_TOKEN not configured");
        }
        
        try {
            logger.debug("Reading image file: {}", image.getAbsolutePath());
            String base64 = Base64.getEncoder().encodeToString(Files.readAllBytes(image.toPath()));

            List<ChatRequestMessage> messages = Arrays.asList(
                    new ChatRequestSystemMessage("You are a helpful assistant that describes images of pets. Keep descriptions family-friendly and focus on the pet's appearance and characteristics."),
                    new ChatRequestUserMessage("Describe this pet image: data:image/png;base64," + base64)
            );

            ChatCompletionsOptions options = new ChatCompletionsOptions(messages);
            options.setModel("github:azure-openai/gpt-4o-mini");

            logger.debug("Sending request to Azure AI service");
            ChatCompletions completions = client.complete(options);
            
            if (completions.getChoices() == null || completions.getChoices().isEmpty()) {
                logger.error("No response received from AI service");
                throw new RuntimeException("No response from AI service");
            }
            
            String result = completions.getChoices().get(0).getMessage().getContent();
            logger.debug("Received caption: {}", result);
            return result;
            
        } catch (IOException e) {
            logger.error("Error reading image file: {}", image.getAbsolutePath(), e);
            throw new RuntimeException("Failed to read image file", e);
        } catch (Exception e) {
            logger.error("Error generating image caption", e);
            // Check if it's a budget limit error or other service error
            if (e.getMessage() != null && e.getMessage().contains("budget limit")) {
                throw new RuntimeException("GitHub Models API budget limit reached. Please check your account or use a different API key.", e);
            }
            throw new RuntimeException("Failed to generate image caption", e);
        }
    }
}
