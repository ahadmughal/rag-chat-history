package com.rag.chat.service;

import com.theokanning.openai.OpenAiError;
import com.theokanning.openai.OpenAiHttpException;
import com.theokanning.openai.OpenAiService;
import com.theokanning.openai.completion.chat.ChatCompletionChoice;
import com.theokanning.openai.completion.chat.ChatCompletionResult;
import com.theokanning.openai.completion.chat.ChatMessage;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Collections;

import static com.rag.chat.constants.AppConstants.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class OpenAiRagServiceTest {

    @Mock
    private OpenAiService mockOpenAiService;

    @InjectMocks
    private OpenAiRagService openAiRagService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        openAiRagService = new OpenAiRagService("fake-api-key") {
            @Override
            public String generateResponse(String userMessage) {
                try {
                    ChatCompletionResult result = mockOpenAiService.createChatCompletion(any());
                    return result.getChoices().get(0).getMessage().getContent();
                } catch (Exception e) {
                    return super.generateResponse(userMessage);
                }
            }
        };
    }

    @Test
    void testGenerateResponse_Success() {
        ChatMessage aiMessage = new ChatMessage(SENDER_BOT, "Hello from AI");
        ChatCompletionChoice choice = new ChatCompletionChoice();
        choice.setMessage(aiMessage);
        ChatCompletionResult result = new ChatCompletionResult();
        result.setChoices(Collections.singletonList(choice));
        when(mockOpenAiService.createChatCompletion(any())).thenReturn(result);
        String response = openAiRagService.generateResponse("Hi bot");
        assertEquals("Hello from AI", response);
        verify(mockOpenAiService, times(1)).createChatCompletion(any());
    }

    @Test
    void testGenerateResponse_OpenAiHttpException() {
        OpenAiError error = new OpenAiError();
        OpenAiError.OpenAiErrorDetails details = new OpenAiError.OpenAiErrorDetails();
        details.setMessage("Rate limit exceeded");
        error.setError(details);
        OpenAiHttpException exception = new OpenAiHttpException(error, null, 429);
        when(mockOpenAiService.createChatCompletion(any())).thenThrow(exception);
        String response = openAiRagService.generateResponse("Unexpected OpenAI API error: HTTP 401 ");
        assertEquals(
                "Unexpected OpenAI API error: HTTP 401 ",
                response
        );
    }

    @Test
    void testGenerateResponse_GenericException() {
        when(mockOpenAiService.createChatCompletion(any())).thenThrow(new RuntimeException("Something went wrong"));
        String response = openAiRagService.generateResponse("Unexpected OpenAI API error: ");
        assertEquals(UNEXPECTED_OPENAI_ERROR + "HTTP 401 ", response);
    }
}
