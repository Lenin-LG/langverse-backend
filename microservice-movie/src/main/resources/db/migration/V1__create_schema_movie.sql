-- Crear tabla CATEGORY
CREATE TABLE category (
    id_category BIGINT AUTO_INCREMENT PRIMARY KEY,
    description VARCHAR(255) NOT NULL
);

-- Insertar categorías iniciales
INSERT INTO category (description) VALUES ('Movie');
INSERT INTO category (description) VALUES ('Series');

-- Crear tabla MOVIE
CREATE TABLE movie (
    id_movie BIGINT AUTO_INCREMENT PRIMARY KEY,
    title VARCHAR(255) NOT NULL,
    description TEXT,
    id_category BIGINT,
    video_url_480p VARCHAR(500),
    video_url_720p VARCHAR(500),
    video_url_1080p VARCHAR(500),
    audio_url_en VARCHAR(500),
    audio_url_es VARCHAR(500),
    sub_titles_english VARCHAR(500),
    sub_titles_spanish VARCHAR(500),
    duration_in_minutes INT,
    release_date DATE,
    season_number INT,
    episode_number INT,
    image_banner VARCHAR(500),
    estate BOOLEAN,

    CONSTRAINT fk_movie_category FOREIGN KEY (id_category)
        REFERENCES category(id_category)
        ON DELETE SET NULL
        ON UPDATE CASCADE
);