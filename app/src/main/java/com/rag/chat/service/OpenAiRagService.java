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

import static com.rag.chat.constants.AppConstants.*;

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
                    .model(OPENAI_MODEL)
                    .messages(Collections.singletonList(new ChatMessage(SENDER_USER, userMessage)))
                    .maxTokens(100)
                    .build();

            ChatCompletionResult result = openAiService.createChatCompletion(request);
            return result.getChoices().get(0).getMessage().getContent();

        } catch (OpenAiHttpException e) {
            log.error(OPEN_AI_HTTP_LOG, e.statusCode, e.getMessage(), e);
            return OPEN_AI_ERROR_RESPONSE_PRE + e.statusCode + OPEN_AI_ERROR_RESPONSE_POST + e.getMessage();

        } catch (Exception e) {
            log.error(UNEXPECTED_OPENAI_ERROR, e);
            return UNEXPECTED_OPENAI_ERROR + e.getMessage();
        }
    }
}
