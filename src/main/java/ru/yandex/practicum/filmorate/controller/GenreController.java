package ru.yandex.practicum.filmorate.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.filmorate.dto.genre.GenreDto;
import ru.yandex.practicum.filmorate.mapper.GenreMapper;
import ru.yandex.practicum.filmorate.service.GenreService;

import java.util.Collection;

/**
 * Контроллер для запросов жанров.
 */
@RequestMapping("/genres")
@RequiredArgsConstructor
@RestController
@Slf4j
public final class GenreController {
    /**
     * Сервис для работы с жанрами.
     */
    private final GenreService genreService;

    /**
     * Получить список всех жанров.
     *
     * @return список жанров.
     */
    @GetMapping
    public Collection<GenreDto> getAllGenres() {
        log.info("Запрос на получение списка всех жанров");
        return GenreMapper.mapToGenreDtoCollection(this.genreService.getAllGenres());
    }

    /**
     * Получить жанр по его идентификатору.
     *
     * @param genreId идентификатор жанра.
     * @return жанр.
     */
    @GetMapping("/{genreId}")
    public GenreDto getGenreById(@PathVariable long genreId) {
        log.info("Запрос на получение жанра с id = {}", genreId);
        return GenreMapper.mapToGenreDto(this.genreService.getGenreById(genreId));
    }
}