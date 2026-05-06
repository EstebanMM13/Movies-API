-- ==================== DATOS DE PRUEBA ====================
-- Este script se ejecuta automáticamente al arrancar la app con perfil 'dev'
-- Los datos solo se insertan si las tablas están vacías

-- Insertar géneros (si no existen)
INSERT IGNORE INTO genres (id, name) VALUES (1, 'Acción');
INSERT IGNORE INTO genres (id, name) VALUES (2, 'Aventura');
INSERT IGNORE INTO genres (id, name) VALUES (3, 'Comedia');
INSERT IGNORE INTO genres (id, name) VALUES (4, 'Drama');
INSERT IGNORE INTO genres (id, name) VALUES (5, 'Ciencia Ficción');
INSERT IGNORE INTO genres (id, name) VALUES (6, 'Terror');
INSERT IGNORE INTO genres (id, name) VALUES (7, 'Romance');
INSERT IGNORE INTO genres (id, name) VALUES (8, 'Animación');

-- Insertar películas de ejemplo
INSERT IGNORE INTO movies (id, title, description, movie_year, rating, votes, image_url) VALUES
(1, 'Inception', 'Un ladrón que roba secretos corporativos a través del uso de la tecnología de los sueños', 2010, 0, 0, 'https://image.tmdb.org/t/p/w500/9gk7adHYeDvHkCSEqAvQNLV5Uge.jpg'),
(2, 'The Matrix', 'Un programador descubre que la realidad no es más que una simulación', 1999, 0, 0, 'https://image.tmdb.org/t/p/w500/f89U3ADr1oiB1s9GkdPOEpXUk5H.jpg'),
(3, 'Interstellar', 'Un grupo de exploradores viajan a través de un agujero de gusano', 2014, 0, 0, 'https://image.tmdb.org/t/p/w500/gEU2ibni4qzHp6gKjZHtB6nPE1F.jpg'),
(4, 'The Dark Knight', 'Batman lucha contra el Joker, un criminal que siembra el caos en Gotham', 2008, 0, 0, 'https://image.tmdb.org/t/p/w500/qJ2tW6WMUDux911r6m7haRef0WH.jpg'),
(5, 'Pulp Fiction', 'Las vidas de dos asesinos a sueldo, un boxeador y unos gánsteres se entrelazan', 1994, 0, 0, 'https://image.tmdb.org/t/p/w500/d5iIlFn5s0ImszYzBPb8JPIfbXD.jpg');

-- Relacionar películas con géneros (movie_genres)
INSERT IGNORE INTO movie_genres (movie_id, genre_id) VALUES
(1, 1), (1, 5),  -- Inception: Acción, Ciencia Ficción
(2, 1), (2, 5),  -- Matrix: Acción, Ciencia Ficción
(3, 2), (3, 5),  -- Interstellar: Aventura, Ciencia Ficción
(4, 1), (4, 4),  -- Dark Knight: Acción, Drama
(5, 4), (5, 3);  -- Pulp Fiction: Drama, Comedia

-- Insertar usuario de prueba (password = "123456" encriptado con BCrypt)
-- La contraseña encriptada es: $2a$10$NkM5JkqZkM5JkqZkM5JkquU8JkqZkM5JkqZkM5JkqZkM5JkqZkM5Jkq
INSERT IGNORE INTO users (id, username, email, password, role) VALUES
(1, 'admin', 'admin@movies.com', '$2a$10$NkM5JkqZkM5JkqZkM5JkquU8JkqZkM5JkqZkM5JkqZkM5JkqZkM5Jkq', 1),
(2, 'user', 'user@movies.com', '$2a$10$NkM5JkqZkM5JkqZkM5JkquU8JkqZkM5JkqZkM5JkqZkM5JkqZkM5Jkq', 0);

-- Reiniciar las secuencias de AUTO_INCREMENT (para MySQL)
ALTER TABLE genres AUTO_INCREMENT = 9;
ALTER TABLE movies AUTO_INCREMENT = 6;
ALTER TABLE users AUTO_INCREMENT = 3;