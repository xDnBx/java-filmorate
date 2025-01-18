package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.Film;

import java.util.Collection;
import java.util.List;

public interface FilmStorage {

    Collection<Film> getAllFilms();

    Film createFilm(Film film);

    Film updateFilm(Film newFilm);

    Film getFilmById(Long id);

    void addLike(Long id, Long userId);

    void deleteLike(Long id, Long userId);

    List<Film> findByNameFilm(String requestedMovieTitleToSearch);

    Collection<Film> getDirectorFilms(Integer directorId, String sortBy);

    List<Film> getPopularFilms(Long count, Integer genreId, Integer year);

    List<Film> getCommonFilms(Long userId1, Long userId2);

    List<Film> findByNameFilmAndTitleDirector(String requestedMovieTitleToSearch, String title, String director);

}