CREATE TABLE IF NOT EXISTS users
(
    id       INTEGER GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
    email    VARCHAR NOT NULL UNIQUE,
    login    VARCHAR NOT NULL UNIQUE,
    name     VARCHAR NOT NULL,
    birthday DATE    NOT NULL
);

CREATE TABLE IF NOT EXISTS friends
(
    id INTEGER GENERATED BY DEFAULT AS IDENTITY UNIQUE,
    user_id   INTEGER NOT NULL REFERENCES users (id),
    friend_id INTEGER NOT NULL REFERENCES users (id),
    PRIMARY KEY (user_id, friend_id)
);

CREATE TABLE IF NOT EXISTS mpa
(
    id   INTEGER GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
    name VARCHAR NOT NULL
);
MERGE INTO mpa KEY (ID)
    VALUES (1, 'G'),
           (2, 'PG'),
           (3, 'PG-13'),
           (4, 'R'),
           (5, 'NC-17');

CREATE TABLE IF NOT EXISTS genres
(
    id   INTEGER GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
    name VARCHAR NOT NULL
);
MERGE INTO genres KEY (ID)
    VALUES (1, 'Комедия'),
           (2, 'Драма'),
           (3, 'Мультфильм'),
           (4, 'Триллер'),
           (5, 'Документальный'),
           (6, 'Боевик');

CREATE TABLE IF NOT EXISTS films
(
    id           INTEGER GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
    name         VARCHAR NOT NULL,
    description  VARCHAR NOT NULL,
    release_date DATE    NOT NULL,
    duration     INTEGER NOT NULL,
    mpa_id       INTEGER NOT NULL,
    FOREIGN KEY (mpa_id) REFERENCES mpa (id)
);

CREATE TABLE IF NOT EXISTS films_genres
(
    film_id  INTEGER NOT NULL,
    genre_id INTEGER NOT NULL,
    PRIMARY KEY (film_id, genre_id),
    FOREIGN KEY (film_id) REFERENCES films (id),
    FOREIGN KEY (genre_id) REFERENCES genres (id)
);

CREATE TABLE IF NOT EXISTS likes
(
    id INTEGER GENERATED BY DEFAULT AS IDENTITY UNIQUE,
    film_id INTEGER NOT NULL,
    user_id INTEGER NOT NULL,
    PRIMARY KEY (film_id, user_id),
    FOREIGN KEY (film_id) REFERENCES films (id),
    FOREIGN KEY (user_id) REFERENCES users (id)
);

CREATE TABLE IF NOT EXISTS directors (
  id INTEGER GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
  name VARCHAR NOT NULL
);

CREATE TABLE IF NOT EXISTS films_directors (
  film_id INTEGER NOT NULL,
  director_id INTEGER NOT NULL,
  PRIMARY KEY (film_id, director_id),
  FOREIGN KEY (film_id) REFERENCES films(id) ON DELETE CASCADE,
  FOREIGN KEY (director_id) REFERENCES directors(id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS reviews
(
    id          INTEGER GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
    content     VARCHAR NOT NULL CHECK (LENGTH(TRIM(content)) > 0),
    is_positive BOOLEAN NOT NULL,
    user_id     INTEGER NOT NULL REFERENCES users (id),
    film_id     INTEGER NOT NULL REFERENCES films (id),
    UNIQUE (user_id, film_id)
);

CREATE TABLE IF NOT EXISTS films_rating
(
    id      INTEGER GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
    film_id INTEGER NOT NULL UNIQUE REFERENCES films (id),
    rating  INTEGER NOT NULL DEFAULT 0
);

CREATE TABLE IF NOT EXISTS reviews_likes
(
    id        INTEGER GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
    review_id INTEGER NOT NULL REFERENCES reviews (id),
    user_id   INTEGER NOT NULL REFERENCES users (id),
    is_liked  BOOLEAN,
    UNIQUE (review_id, user_id)
);

CREATE TABLE IF NOT EXISTS events
(
    id         INTEGER GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
    timestamp  TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    user_id    INTEGER NOT NULL REFERENCES users (id),
    event_type VARCHAR NOT NULL CHECK (event_type IN ('LIKE', 'REVIEW', 'FRIEND')),
    operation  VARCHAR NOT NULL CHECK (operation IN ('REMOVE', 'ADD', 'UPDATE')),
    entity_id  INTEGER NOT NULL
);