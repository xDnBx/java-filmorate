package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.dto.film.CreateFilmRequestDto;
import ru.yandex.practicum.filmorate.dto.film.FilmDto;
import ru.yandex.practicum.filmorate.dto.film.UpdateFilmRequestDto;
import ru.yandex.practicum.filmorate.mapper.FilmMapper;
import ru.yandex.practicum.filmorate.service.FilmService;

import java.util.Collection;

/**
 * Контроллер для запросов фильмов.
 */
@RequestMapping("/films")
@RequiredArgsConstructor
@RestController
@Slf4j
public final class FilmController {
    /**
     * Сервис для работы с фильмами.
     */
    private final FilmService filmService;

    //region Фильмы

    /**
     * Создать новый фильм.
     *
     * @param dto трансферный объект запроса на создание фильма.
     * @return фильм.
     */
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public FilmDto createFilm(@Valid @RequestBody CreateFilmRequestDto dto) {
        log.info("Запрос на добавление нового фильма: {}", dto.getName());
        return FilmMapper.mapToFilmDto(this.filmService.createFilm(FilmMapper.mapToFilm(dto)));
    }

    /**
     * Получить список всех фильмов.
     *
     * @return список фильмов.
     */
    @GetMapping
    public Collection<FilmDto> getAllFilms() {
        log.info("Запрос на получение списка всех фильмов");
        return FilmMapper.mapToFilmDtoCollection(this.filmService.getAllFilms());
    }

    /**
     * Получить список популярных фильмов, отсортированный по количеству лайков.
     *
     * @param count   количество фильмов, которые необходимо получить.
     * @param genreId идентификатор жанра, по которому необходимо произвести фильтрацию, если предоставлен.
     * @param year    год выпуска фильма, по которому необходимо произвести фильтрацию, если предоставлен.
     * @return список фильмов.
     */
    @GetMapping("/popular")
    public Collection<FilmDto> getPopularFilms(@RequestParam(defaultValue = "10") long count,
                                               @RequestParam(required = false) Long genreId,
                                               @RequestParam(required = false) Integer year) {
        log.info("Запрос на получение списка из {} популярных фильмов", count);
        return FilmMapper.mapToFilmDtoCollection(this.filmService.getPopularFilms(count, genreId, year));
    }

    /**
     * Получить фильм по его идентификатору.
     *
     * @param filmId идентификатор фильма.
     * @return фильм.
     */
    @GetMapping("/{filmId}")
    public FilmDto getFilmById(@PathVariable long filmId) {
        log.info("Запрос на получение фильма с id = {}", filmId);
        return FilmMapper.mapToFilmDto(this.filmService.getFilmById(filmId));
    }

    /**
     * Получить список фильмов режиссёра.
     *
     * @param directorId идентификатор режиссёра.
     * @param sortBy     поле, по которому необходимо отсортировать список фильмов.
     * @return список фильмов.
     */
    @GetMapping("/director/{directorId}")
    public Collection<FilmDto> getDirectorFilms(@PathVariable long directorId, @RequestParam String sortBy) {
        log.info("Запрос на получение фильмов режиссёра с id = {}. Сортировка по полю {}", directorId, sortBy);
        return FilmMapper.mapToFilmDtoCollection(this.filmService.getDirectorFilms(directorId, sortBy));
    }

    /**
     * Получить список общих с другом фильмов.
     *
     * @param userId   идентификатор пользователя.
     * @param friendId идентификатор друга.
     * @return список фильмов.
     */
    @GetMapping("/common")
    public Collection<FilmDto> getCommonFilms(@RequestParam long userId, @RequestParam long friendId) {
        log.info("Запрос на получение общих фильмов для пользователей с id = {} и id = {}", userId, friendId);
        return FilmMapper.mapToFilmDtoCollection(this.filmService.getCommonFilm(userId, friendId));
    }

    /**
     * Получить список фильмов, подходящих под условие поиска.
     *
     * @param query поисковая строка.
     * @param by    список полей, по которым необходимо произвести поиск.
     * @return список фильмов.
     */
    @GetMapping("/search")
    public Collection<FilmDto> searchFilms(@NotNull(message = "query не может быть пустым") @RequestParam String query, @RequestParam(required = false) String by) {
        return FilmMapper.mapToFilmDtoCollection(filmService.searchFilms(query, by));
    }

    /**
     * Обновить фильм.
     *
     * @param dto трансферный объект запроса на обновление фильма.
     * @return фильм.
     */
    @PutMapping
    public FilmDto updateFilm(@Valid @RequestBody UpdateFilmRequestDto dto) {
        log.info("Запрос на обновление фильма с id = {}", dto.getId());
        return FilmMapper.mapToFilmDto(this.filmService.updateFilm(FilmMapper.mapToFilm(dto)));
    }

    /**
     * Удалить фильм.
     *
     * @param filmId идентификатор фильма.
     */
    @DeleteMapping("/{filmId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteFilm(@PathVariable long filmId) {
        log.info("Запрос на удаление фильма с id = {}", filmId);
        filmService.deleteFilm(filmId);
    }

    //endregion

    //region Лайки

    /**
     * Добавить фильма пользовательский лайк.
     *
     * @param filmId идентификатор фильма.
     * @param userId идентификатор пользователя.
     */
    @PutMapping("/{filmId}/like/{userId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void addLike(@PathVariable long filmId, @PathVariable long userId) {
        log.info("Запрос на добавление лайка фильму с id = {} от пользователя с id = {}", filmId, userId);
        this.filmService.addLikeToFilm(filmId, userId);
    }

    /**
     * Удалить у фильма пользовательский лайк.
     *
     * @param filmId идентификатор фильма.
     * @param userId идентификатор пользователя.
     */
    @DeleteMapping("/{filmId}/like/{userId}")
    public void deleteLike(@PathVariable long filmId, @PathVariable long userId) {
        log.info("Запрос на удаление лайка фильму с id = {} от пользователя с id = {}", filmId, userId);
        this.filmService.removeLikeFromFilm(filmId, userId);
    }

    //endregion
}