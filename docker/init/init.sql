-- ==================== SCHEMA ====================
CREATE TABLE genres (
    id BIGINT PRIMARY KEY,
    name VARCHAR(255) NOT NULL UNIQUE
);

CREATE TABLE movies (
    id BIGINT PRIMARY KEY,
    title VARCHAR(255) NOT NULL,
    description TEXT,
    movie_year INT,
    rating DOUBLE,
    votes INT,
    image_url VARCHAR(500)
);

CREATE TABLE users (
    id BIGINT PRIMARY KEY,
    username VARCHAR(255) NOT NULL UNIQUE,
    email VARCHAR(255) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    role INT NOT NULL
);

CREATE TABLE movie_genres (
    movie_id BIGINT,
    genre_id BIGINT,
    PRIMARY KEY (movie_id, genre_id),
    FOREIGN KEY (movie_id) REFERENCES movies(id),
    FOREIGN KEY (genre_id) REFERENCES genres(id)
);

-- ==================== DATA ====================

INSERT INTO genres (id, name) VALUES
(1, 'Acción'),
(2, 'Aventura'),
(3, 'Comedia'),
(4, 'Drama'),
(5, 'Ciencia Ficción'),
(6, 'Terror'),
(7, 'Romance'),
(8, 'Animación');

INSERT INTO movies (id, title, description, movie_year, rating, votes, image_url) VALUES
(1, 'Inception', 'Un ladrón que roba secretos corporativos...', 2010, 0, 0, 'https://image.tmdb.org/t/p/w500/9gk7adHYeDvHkCSEqAvQNLV5Uge.jpg'),
(2, 'The Matrix', 'Un programador descubre que la realidad...', 1999, 0, 0, 'https://image.tmdb.org/t/p/w500/f89U3ADr1oiB1s9GkdPOEpXUk5H.jpg'),
(3, 'Interstellar', 'Exploradores viajan por un agujero...', 2014, 0, 0, 'https://image.tmdb.org/t/p/w500/gEU2ibni4qzHp6gKjZHtB6nPE1F.jpg'),
(4, 'The Dark Knight', 'Batman lucha contra el Joker...', 2008, 0, 0, 'https://image.tmdb.org/t/p/w500/qJ2tW6WMUDux911r6m7haRef0WH.jpg'),
(5, 'Pulp Fiction', 'Historias entrelazadas...', 1994, 0, 0, 'https://image.tmdb.org/t/p/w500/d5iIlFn5s0ImszYzBPb8JPIfbXD.jpg');

INSERT INTO movie_genres (movie_id, genre_id) VALUES
(1, 1), (1, 5),
(2, 1), (2, 5),
(3, 2), (3, 5),
(4, 1), (4, 4),
(5, 4), (5, 3);

-- ==================== USERS ====================

INSERT INTO users (id, username, email, password, role) VALUES
(1, 'admin', 'admin@movies.com', '$2a$10$u2TpvdAYxjM0xLrOOqFRfeEvu3Vg/NfBIwtYJFayCGyIvtPyGBCVS', 1),
(2, 'user', 'user@movies.com', '$2a$10$u2TpvdAYxjM0xLrOOqFRfeEvu3Vg/NfBIwtYJFayCGyIvtPyGBCVS', 0);