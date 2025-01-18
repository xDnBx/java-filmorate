package ru.yandex.practicum.filmorate.storage;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.NegativeCountException;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Component
@RequiredArgsConstructor
public class InMemoryFilmStorage implements FilmStorage {

    private long id = 1;
    private final Map<Long, Film> films = new HashMap<>();
    private final UserStorage userStorage;

    @Override
    public Collection<Film> getAllFilms() {
        return films.values();
    }

    @Override
    public Film createFilm(Film film) {
        film.setId(generateNewId());
        films.put(film.getId(), film);
        log.info("Фильм с id = {} успешно добавлен!", film.getId());
        return film;
    }

    @Override
    public Film updateFilm(Film newFilm) {
        checkFilm(newFilm.getId());

        Film oldFilm = films.get(newFilm.getId());
        oldFilm.setName(newFilm.getName());
        oldFilm.setDescription(newFilm.getDescription());
        oldFilm.setReleaseDate(newFilm.getReleaseDate());
        oldFilm.setDuration(newFilm.getDuration());

        log.info("Обновление фильма с id = {} прошло успешно!", newFilm.getId());
        return oldFilm;
    }

    @Override
    public Film getFilmById(Long id) {
        checkFilm(id);
        return films.get(id);
    }

    @Override
    public void addLike(Long id, Long userId) {
        userStorage.getUserById(userId);

        Film film = getFilmById(id);
        film.getLikes().add(userId);
    }

    @Override
    public void deleteLike(Long id, Long userId) {
        userStorage.getUserById(userId);

        Film film = getFilmById(id);

        if (!film.getLikes().contains(userId)) {
            log.error("Пользователь не ставил лайк этому фильму");
            throw new NotFoundException("Данный пользователь не ставил лайк этому фильму");
        }

        film.getLikes().remove(userId);
    }

    @Override
    public List<Film> getCommonFilms(Long userId1, Long userId2) {
        return List.of();
    }

    @Override
    public List<Film> findByNameFilmAndTitleDirector(String requestedMovieTitleToSearch, String title, String director) {
        return List.of();
    }

    @Override
    public List<Film> getPopularFilms(Long count, Integer genreId, Integer year) {
        if (count <= 0) {
            log.error("Число фильмов для показа меньше или равно нулю");
            throw new NegativeCountException("Число фильмов для показа должно быть больше нуля");
        }
        return films.values().stream()
                .sorted((film1, film2) -> (film2.getLikes().size()) - film1.getLikes().size())
                .limit(count)
                .toList();
    }

    @Override
    public Collection<Film> getDirectorFilms(Integer directorId, String sortBy) {
        throw new UnsupportedOperationException();
    }

    @Override
    public List<Film> findByNameFilm(String requestedMovieTitleToSearch) {
        return List.of();
    }

    private long generateNewId() {
        return id++;
    }

    private void checkFilm(Long id) {
        if (id == null) {
            log.error("id фильма не указан");
            throw new ValidationException("id должен быть указан");
        }
        if (!films.containsKey(id)) {
            log.error("Фильм с id = {} не найден", id);
            throw new NotFoundException("Фильма с таким id не найдено");
        }
    }
}