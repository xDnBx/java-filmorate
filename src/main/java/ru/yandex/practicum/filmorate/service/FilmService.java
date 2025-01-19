package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.dao.DirectorRepository;
import ru.yandex.practicum.filmorate.dao.EventRepository;
import ru.yandex.practicum.filmorate.dao.LikeRepository;
import ru.yandex.practicum.filmorate.model.*;
import ru.yandex.practicum.filmorate.storage.FilmStorage;
import ru.yandex.practicum.filmorate.storage.GenreStorage;
import ru.yandex.practicum.filmorate.storage.MpaStorage;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class FilmService {
    private final FilmStorage filmStorage;
    private final MpaStorage mpaStorage;
    private final GenreStorage genreStorage;
    private final UserStorage userStorage;
    private final EventRepository eventRepository;
    private final LikeRepository likeRepository;
    private final DirectorRepository directorRepository;

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
        Optional<Like> optionalLike = likeRepository.findOneByFilmIdAndUserId(id.intValue(), userId.intValue());
        optionalLike.ifPresent(like -> eventRepository.insert(userId.intValue(), Event.EventType.LIKE, Event.Operation.ADD, id.intValue()));
    }

    public void deleteLike(Long id, Long userId) {
        log.info("Удаление лайка фильму с id = {} от пользователя с id = {}", id, userId);
        userStorage.getUserById(userId);
        Optional<Like> optionalLike = likeRepository.findOneByFilmIdAndUserId(id.intValue(), userId.intValue());
        filmStorage.deleteLike(id, userId);
        optionalLike.ifPresent(like -> eventRepository.insert(userId.intValue(), Event.EventType.LIKE, Event.Operation.REMOVE, id.intValue()));
    }

    public List<Film> getCommonFilm(Long userId1, Long userId2) {
        log.info("Получение списка общих фильмов для пользователей: {}, {}", userId1, userId2);
        return filmStorage.getCommonFilms(userId1, userId2);
    }

    public List<Film> getPopularFilms(Long count, Integer genreId, Integer year) {
        log.info("Получение списка из {} популярных фильмов", count);
        return filmStorage.getPopularFilms(count, genreId, year);
    }

    public Collection<Film> getDirectorFilms(Integer directorId, String sortBy) {
        log.info("Получение фильмов режиссёра с идентификатором {}. Сортировка по полю {}", directorId, sortBy);
        return filmStorage.getDirectorFilms(directorId, sortBy);
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

    public void deleteFilm(Long filmId) {
        log.info("Удаление фильма с id = {}", filmId);
        filmStorage.getFilmById(filmId);
        filmStorage.deleteFilm(filmId);
    }

    public List<Film> searchFilms(String query, String by) {
        if (by == null) {
            return filmStorage.findByNameFilm(query);
        } else {
            String director = null;
            String title = null;
            if (by != null && by.contains(",")) {
                String[] titleDirector = by.split(",");
                director = titleDirector[0];
                title = titleDirector[1];
            } else {
                switch (by) {
                    case "title":
                        title = by;
                        break;
                    case "director":
                        director = by;
                        break;
                }
            }
            List<Film> foundFilms = filmStorage.findByNameFilmAndTitleDirector(query, director, title);
            List<Film> foundFilmWithDirectorAndOtherParam = new ArrayList<>();

            for (Film foundFilm : foundFilms) {
                foundFilm.setDirectors(directorRepository.getDirectorByFilmId(foundFilm.getId()));
                foundFilm.setLikes(new HashSet<>(likeRepository.findByFilmId(foundFilm.getId())));
                foundFilm.setMpa(mpaStorage.getMpaById(foundFilm.getMpa().getId()));
                foundFilm.setGenres(genreStorage.getGenreByFilmId(foundFilm.getId()));

                foundFilmWithDirectorAndOtherParam.add(foundFilm);
            }

            return sortFilmsByLikes(foundFilmWithDirectorAndOtherParam);
        }
    }

    public static List<Film> sortFilmsByLikes(List<Film> films) {
        return films.stream().sorted(Comparator.comparingInt(film -> film.getLikes().size())).collect(Collectors.toList());

    }
}