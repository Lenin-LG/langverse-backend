-- Crear tabla de temas (topics)
CREATE TABLE topics (
    topic_id BIGINT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(255) NOT NULL
);

-- Crear tabla de frases (phrases)
CREATE TABLE phrases (
    phrase_id BIGINT PRIMARY KEY AUTO_INCREMENT,
    text VARCHAR(500) NOT NULL,
    topic_id BIGINT,
    CONSTRAINT fk_phrase_topic FOREIGN KEY (topic_id)
        REFERENCES topics(topic_id)
        ON DELETE SET NULL
        ON UPDATE CASCADE
);

-- Crear tabla de prácticas de speaking
CREATE TABLE speaking_practices (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    user_id VARCHAR(100) NOT NULL,
    phrase_id BIGINT,
    spoken_text TEXT,
    accuracy DOUBLE,
    practice_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_speaking_phrase FOREIGN KEY (phrase_id)
        REFERENCES phrases(phrase_id)
        ON DELETE SET NULL
        ON UPDATE CASCADE
);

-- ================================================
-- Datos iniciales para Topics
-- ================================================
INSERT INTO topics (name) VALUES
('Daily Conversations'),
('Travel and Tourism'),
('Food and Restaurants'),
('Work and Business'),
('Technology and Innovation');

-- ================================================
-- Datos iniciales para Phrases
-- ================================================
INSERT INTO phrases (text, topic_id) VALUES
('Hello, how are you?', 1),
('What are your plans for today?', 1),
('Where is the nearest bus stop?', 2),
('Can I see the menu, please?', 3),
('I have a meeting at 10 AM.', 4),
('Technology makes life easier.', 5);