CREATE TABLE chat_session (
    id VARCHAR(36) PRIMARY KEY,
    session_name VARCHAR(255) NOT NULL,
    active BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE chat_message (
    id BIGSERIAL PRIMARY KEY,
    session_id VARCHAR(36) NOT NULL,
    sender VARCHAR(50) NOT NULL,
    content TEXT NOT NULL,
    context TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_session
        FOREIGN KEY (session_id)
        REFERENCES chat_session(id)
        ON DELETE CASCADE
);

CREATE TABLE api_key (
    id BIGSERIAL PRIMARY KEY,
    api_key_value VARCHAR(255) UNIQUE NOT NULL,
    owner VARCHAR(255),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

ALTER TABLE chat_session
ADD COLUMN favorite BOOLEAN DEFAULT FALSE;
