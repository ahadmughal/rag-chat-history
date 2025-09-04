package com.rag.chat.service;

import com.theokanning.openai.OpenAiHttpException;
import com.theokanning.openai.OpenAiService;
import com.theokanning.openai.completion.chat.ChatCompletionRequest;
import com.theokanning.openai.completion.chat.ChatCompletionResult;
import com.theokanning.openai.completion.chat.ChatMessage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.Collections;

@Service
@Slf4j
public class OpenAiRagService {

    private final OpenAiService openAiService;

    public OpenAiRagService(@Value("${openai.api.key}") String apiKey) {
        this.openAiService = new OpenAiService(apiKey, Duration.ofSeconds(30));
    }

    public String generateResponse(String userMessage) {
        try {
            ChatCompletionRequest request = ChatCompletionRequest.builder()
                    .model("gpt-3.5-turbo")
                    .messages(Collections.singletonList(new ChatMessage("user", userMessage)))
                    .maxTokens(100)
                    .build();

            ChatCompletionResult result = openAiService.createChatCompletion(request);
            return result.getChoices().get(0).getMessage().getContent();

        } catch (OpenAiHttpException e) {
            log.error("OpenAI HTTP Error: status={} message={}", e.statusCode, e.getMessage(), e);
            return "OpenAI API error (" + e.statusCode + "): " + e.getMessage();

        } catch (Exception e) {
            log.error("Unexpected OpenAI API error", e);
            return "Unexpected OpenAI service error: " + e.getMessage();
        }
    }
}
