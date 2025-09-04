package com.rag.chat.service;

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
        int maxRetries = 3;
        int retryCount = 0;
        long backoffMillis = 1000; // 1s initial wait

        while (retryCount < maxRetries) {
            try {
                ChatCompletionRequest request = ChatCompletionRequest.builder()
                        .model("gpt-3.5-turbo")
                        .messages(Collections.singletonList(new ChatMessage("user", userMessage)))
                        .maxTokens(100)
                        .build();

                ChatCompletionResult result = openAiService.createChatCompletion(request);
                return result.getChoices().get(0).getMessage().getContent();

            } catch (Exception e) {
                retryCount++;
                String errorMsg = e.getMessage() != null ? e.getMessage() : "Unknown error";

                // Handle rate limit
                if (errorMsg.contains("429")) {
                    log.warn("OpenAI rate limit hit. Retrying {}/{} after {} ms", retryCount, maxRetries, backoffMillis);
                    if (retryCount == maxRetries) {
                        return "Rate limit exceeded. Please slow down and try again.";
                    }
                    try {
                        Thread.sleep(backoffMillis);
                    } catch (InterruptedException ie) {
                        Thread.currentThread().interrupt();
                        break;
                    }
                    backoffMillis *= 2; // exponential backoff
                } else {
                    log.error("OpenAI API error: {}", errorMsg, e);
                    return "OpenAI service error: " + errorMsg;
                }
            }
        }

        return "Unexpected error. Please try again later.";
    }
}
