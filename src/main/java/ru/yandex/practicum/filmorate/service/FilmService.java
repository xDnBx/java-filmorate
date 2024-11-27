package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.FilmStorage;

import java.util.Collection;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class FilmService {
    private final FilmStorage filmStorage;

    public Collection<Film> getAllFilms() {
        log.info("Получение списка всех фильмов");
        return filmStorage.getAllFilms();
    }

    public Film createFilm(Film film) {
        log.info("Добавление нового фильма: {}", film.getName());
        return filmStorage.createFilm(film);
    }

    public Film updateFilm(Film newFilm) {
        log.info("Обновление фильма с id = {}", newFilm.getId());
        return filmStorage.updateFilm(newFilm);
    }

    public Film getFilmById(Long id) {
        log.info("Получение фильма с id = {}", id);
        return filmStorage.getFilmById(id);
    }

    public void addLike(Long id, Long userId) {
        log.info("Добавление лайка фильму с id = {} от пользователя с id = {}", id, userId);
        filmStorage.addLike(id, userId);
    }

    public void deleteLike(Long id, Long userId) {
        log.info("Удаление лайка фильму с id = {} от пользователя с id = {}", id, userId);
        filmStorage.deleteLike(id, userId);
    }

    public List<Film> getPopularFilms(Long count) {
        log.info("Получение списка из {} популярных фильмов", count);
        return filmStorage.getPopularFilms(count);
    }
}