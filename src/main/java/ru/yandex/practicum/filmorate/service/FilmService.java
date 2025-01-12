package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.storage.FilmStorage;
import ru.yandex.practicum.filmorate.storage.GenreStorage;
import ru.yandex.practicum.filmorate.storage.MpaStorage;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.util.Collection;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class FilmService {
    private final FilmStorage filmStorage;
    private final MpaStorage mpaStorage;
    private final GenreStorage genreStorage;
    private final UserStorage userStorage;

    public Collection<Film> getAllFilms() {
        log.info("Получение списка всех фильмов");
        return filmStorage.getAllFilms();
    }

    public Film createFilm(Film film) {
        log.info("Добавление нового фильма: {}", film.getName());
        mpaStorage.checkMpa(film.getMpa().getId());
        genreStorage.checkGenres(film.getGenres());
        return filmStorage.createFilm(film);
    }

    public Film updateFilm(Film newFilm) {
        log.info("Обновление фильма с id = {}", newFilm.getId());
        mpaStorage.checkMpa(newFilm.getMpa().getId());
        genreStorage.checkGenres(newFilm.getGenres());
        filmStorage.getFilmById(newFilm.getId());
        return filmStorage.updateFilm(newFilm);
    }

    public Film getFilmById(Long id) {
        log.info("Получение фильма с id = {}", id);
        return filmStorage.getFilmById(id);
    }

    public void addLike(Long id, Long userId) {
        log.info("Добавление лайка фильму с id = {} от пользователя с id = {}", id, userId);
        userStorage.getUserById(userId);
        filmStorage.addLike(id, userId);
    }

    public void deleteLike(Long id, Long userId) {
        log.info("Удаление лайка фильму с id = {} от пользователя с id = {}", id, userId);
        userStorage.getUserById(userId);
        filmStorage.deleteLike(id, userId);
    }

    public List<Film> getPopularFilms(Long count, Integer genreId, Integer year) {
        log.info("Получение списка из {} популярных фильмов", count);
        return filmStorage.getPopularFilms(count, genreId, year);
    }

    public Collection<Mpa> getAllMpa() {
        log.info("Получение списка всех рейтингов");
        return mpaStorage.getAllMpa();
    }

    public Mpa getMpaById(Integer id) {
        log.info("Получение рейтинга с id = {}", id);
        return mpaStorage.getMpaById(id);
    }

    public Collection<Genre> getAllGenres() {
        log.info("Получение списка всех жанров");
        return genreStorage.getAllGenres();
    }

    public Genre getGenreById(Integer id) {
        log.info("Получение жанра с id = {}", id);
        return genreStorage.getGenreById(id);
    }
}