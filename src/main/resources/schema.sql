DROP TABLE IF EXISTS users CASCADE;
DROP TABLE IF EXISTS friends CASCADE;
DROP TABLE IF EXISTS mpa CASCADE;
DROP TABLE IF EXISTS genres CASCADE;
DROP TABLE IF EXISTS films CASCADE;
DROP TABLE IF EXISTS films_genres CASCADE;
DROP TABLE IF EXISTS likes CASCADE;

CREATE TABLE IF NOT EXISTS users (
  id INTEGER GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
  email VARCHAR NOT NULL UNIQUE,
  login VARCHAR NOT NULL UNIQUE,
  name VARCHAR NOT NULL,
  birthday DATE NOT NULL
);

CREATE TABLE IF NOT EXISTS friends (
  user_id INTEGER NOT NULL REFERENCES users(id),
  friend_id INTEGER NOT NULL REFERENCES users(id),
  PRIMARY KEY (user_id, friend_id)
);

CREATE TABLE IF NOT EXISTS mpa (
  id INTEGER GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
  name VARCHAR NOT NULL
);

CREATE TABLE IF NOT EXISTS genres (
  id INTEGER GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
  name VARCHAR NOT NULL
);

CREATE TABLE IF NOT EXISTS films (
  id INTEGER GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
  name VARCHAR NOT NULL,
  description VARCHAR NOT NULL,
  release_date DATE NOT NULL,
  duration INTEGER NOT NULL,
  mpa_id INTEGER NOT NULL,
  FOREIGN KEY (mpa_id) REFERENCES mpa(id)
);

CREATE TABLE IF NOT EXISTS films_genres (
  film_id INTEGER NOT NULL,
  genre_id INTEGER NOT NULL,
  PRIMARY KEY (film_id, genre_id),
  FOREIGN KEY (film_id) REFERENCES films(id),
  FOREIGN KEY (genre_id) REFERENCES genres(id)
);

CREATE TABLE IF NOT EXISTS likes (
  film_id INTEGER NOT NULL,
  user_id INTEGER NOT NULL,
  PRIMARY KEY (film_id, user_id),
  FOREIGN KEY (film_id) REFERENCES films(id),
  FOREIGN KEY (user_id) REFERENCES users(id)
);