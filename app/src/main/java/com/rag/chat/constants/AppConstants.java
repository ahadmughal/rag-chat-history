package com.rag.chat.constants;

public class AppConstants {

    public static final String SENDING_MESSAGE_FOR_SESSION = "Sending message for session {}";
    public static final String SESSION_NOT_FOUND_LOG = "Session not found: {}";
    public static final String SESSION_NOT_FOUND_EXEC = "Session not found";
    public static final String USER_MESSAGE_SAVED_LOG = "User message saved successfully with ID {}";
    public static final String ERROR_AI_RESPONSE_LOG = "Error while generating AI response: {}";
    public static final String BOT_RESPONSE_EXEC = "Sorry, I couldn't process your message at the moment. Please try again later.";
    public static final String BOT_RESPONSE_UPDATED = "Bot response updated for message ID {}";
    public static final String DELETED_SESSION_MESSAGES = "Deleted session {} and all related messages";
    public static final String DEACTIVATED_OLD_SESSION = "Deactivated old session: sessionId={}";
    public static final String CREATED_NEW_CHAT_SESSION = "Created new chat session: sessionId={}, sessionName={}";
    public static final String SESSION_NOT_EMPTY = "Session name must not be empty";
    public static final String OPEN_AI_HTTP_LOG = "OpenAI HTTP Error: status={} message={}";
    public static final String OPEN_AI_ERROR_RESPONSE_PRE = "OpenAI API error (";
    public static final String OPEN_AI_ERROR_RESPONSE_POST = "): ";
    public static final String UNEXPECTED_OPENAI_ERROR = "Unexpected OpenAI API error: ";
    public static final String SEND_MESSAGE_REQUEST_LOG = "Received send message request for session {}";
    public static final String ERROR_CREATING_SESSION = "Error creating session: {}";
    public static final String CREATE_SESSION_REQUEST = "Received create session request: sessionName={}";
    public static final String SESSION_CREATED_SUCCESSFULLY = "Session created successfully: sessionId={}, userId={}";
    public static final String SENDER_USER = "user";
    public static final String SENDER_BOT = "bot";
    public static final String OPENAI_MODEL = "gpt-3.5-turbo";
    public static final String REQUEST_ID_KEY = "requestId";

}
