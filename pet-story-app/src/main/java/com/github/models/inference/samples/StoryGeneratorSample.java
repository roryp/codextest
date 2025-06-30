package com.github.models.inference.samples;

import com.azure.ai.inference.ChatCompletionsClient;
import com.azure.ai.inference.ChatCompletionsClientBuilder;
import com.azure.ai.inference.models.ChatCompletions;
import com.azure.ai.inference.models.ChatCompletionsOptions;
import com.azure.ai.inference.models.ChatRequestMessage;
import com.azure.ai.inference.models.ChatRequestSystemMessage;
import com.azure.ai.inference.models.ChatRequestUserMessage;
import com.azure.core.credential.AzureKeyCredential;
import com.azure.core.util.Configuration;

import java.util.Arrays;
import java.util.List;

/**
 * Sample program that generates a short story for a pet description
 * using the GitHub Models service.
 */
public final class StoryGeneratorSample {
    private StoryGeneratorSample() {
    }

    public static void main(String[] args) {
        String key = Configuration.getGlobalConfiguration().get("GITHUB_TOKEN");
        String endpoint = "https://models.github.ai/inference";
        String model = "openai/gpt-4.1-nano";

        ChatCompletionsClient client = new ChatCompletionsClientBuilder()
            .credential(new AzureKeyCredential(key))
            .endpoint(endpoint)
            .buildClient();

        String description = "a playful puppy chasing butterflies";
        List<ChatRequestMessage> messages = Arrays.asList(
            new ChatRequestSystemMessage("You are a creative storyteller."),
            new ChatRequestUserMessage(
                "Write a fun short story about a pet described as: " + description)
        );

        ChatCompletionsOptions options = new ChatCompletionsOptions(messages);
        options.setModel(model);

        ChatCompletions completions = client.complete(options);
        System.out.println(completions.getChoices().get(0).getMessage().getContent());
    }
}
