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

import java.io.File;
import java.nio.file.Files;
import java.util.Arrays;
import java.util.Base64;
import java.util.List;

@Service
public class ImageService {

    private final ChatCompletionsClient client;

    public ImageService() {
        String token = System.getenv("GITHUB_TOKEN");
        if (token == null) {
            throw new IllegalStateException("GITHUB_TOKEN not set");
        }
        this.client = new ChatCompletionsClientBuilder()
                .credential(new AzureKeyCredential(token))
                .endpoint("https://models.github.ai/inference")
                .buildClient();
    }

    public String caption(File image) throws Exception {
        String base64 = Base64.getEncoder().encodeToString(Files.readAllBytes(image.toPath()));

        List<ChatRequestMessage> messages = Arrays.asList(
                new ChatRequestSystemMessage("You are a helpful assistant that describes images."),
                new ChatRequestUserMessage("Describe this image: data:image/png;base64," + base64)
        );

        ChatCompletionsOptions options = new ChatCompletionsOptions(messages);
        options.setModel("microsoft/Phi-4-multimodal-instruct");

        ChatCompletions completions = client.complete(options);
        return completions.getChoices().get(0).getMessage().getContent();
    }
}
