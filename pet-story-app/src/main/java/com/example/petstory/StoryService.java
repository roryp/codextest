package com.example.petstory;

import com.azure.ai.inference.ChatCompletionsClient;
import com.azure.ai.inference.ChatCompletionsClientBuilder;
import com.azure.ai.inference.models.ChatCompletions;
import com.azure.ai.inference.models.ChatCompletionsOptions;
import com.azure.ai.inference.models.ChatRequestMessage;
import com.azure.ai.inference.models.ChatRequestSystemMessage;
import com.azure.ai.inference.models.ChatRequestUserMessage;
import com.azure.core.credential.AzureKeyCredential;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;

@Service
public class StoryService {

    private final ChatCompletionsClient client;

    public StoryService() {
        String token = System.getenv("GITHUB_TOKEN");
        if (token == null) {
            throw new IllegalStateException("GITHUB_TOKEN not set");
        }
        this.client = new ChatCompletionsClientBuilder()
                .credential(new AzureKeyCredential(token))
                .endpoint("https://models.github.ai/inference")
                .buildClient();
    }

    public String generateStory(String description) {
        List<ChatRequestMessage> messages = Arrays.asList(
                new ChatRequestSystemMessage("You are a creative storyteller."),
                new ChatRequestUserMessage("Write a fun short story about a pet described as: " + description)
        );

        ChatCompletionsOptions options = new ChatCompletionsOptions(messages);
        options.setModel("openai/gpt-4.1-nano");

        ChatCompletions completions = client.complete(options);
        return completions.getChoice().getMessage().getContent();
    }
}
