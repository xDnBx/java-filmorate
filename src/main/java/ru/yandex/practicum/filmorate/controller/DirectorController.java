package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.dto.director.CreateDirectorRequestDto;
import ru.yandex.practicum.filmorate.dto.director.DirectorDto;
import ru.yandex.practicum.filmorate.dto.director.UpdateDirectorRequestDto;
import ru.yandex.practicum.filmorate.mapper.DirectorMapper;
import ru.yandex.practicum.filmorate.service.DirectorService;

import java.util.Collection;

/**
 * Контроллер для запросов режиссёров.
 */
@RequestMapping("/directors")
@RequiredArgsConstructor
@RestController
@Slf4j
public final class DirectorController {
    /**
     * Сервис для работы с режиссёрами.
     */
    private final DirectorService directorService;

    /**
     * Создать нового режиссёра.
     *
     * @param dto трансферный объект на запрос для создания режиссёра.
     * @return режиссёр.
     */
    @PostMapping
    public DirectorDto createDirector(@RequestBody @Valid CreateDirectorRequestDto dto) {
        log.info("Запрос на добавление нового режиссёра с именем {}", dto.getName());
        return DirectorMapper.mapToDirectorDto(this.directorService.createDirector(DirectorMapper.mapToDirector(dto)));
    }

    /**
     * Получить список всех режиссёров.
     *
     * @return список режиссёров.
     */
    @GetMapping
    public Collection<DirectorDto> getAllDirectors() {
        log.info("Запрос на получение списка всех режиссёров");
        return DirectorMapper.mapToDirectorDtoCollection(this.directorService.getAllDirectors());
    }

    /**
     * Получить режиссёра по его идентификатору.
     *
     * @param directorId идентификатор режиссёра.
     * @return режиссёр.
     */
    @GetMapping("/{directorId}")
    public DirectorDto getDirectorById(@PathVariable Integer directorId) {
        log.info("Запрос на получение режиссёра с id = {}", directorId);
        return DirectorMapper.mapToDirectorDto(this.directorService.getDirectorById(directorId));
    }

    /**
     * Обновить режиссёра.
     *
     * @param dto трансферный объект на запрос для обновления режиссёра.
     * @return режиссёр.
     */
    @PutMapping
    public DirectorDto updateDirector(@RequestBody @Valid UpdateDirectorRequestDto dto) {
        log.info("Запрос на обновление режиссёра с id = {}", dto.getId());
        return DirectorMapper.mapToDirectorDto(this.directorService.updateDirector(DirectorMapper.mapToDirector(dto)));
    }

    /**
     * Удалить режиссёра.
     *
     * @param directorId идентификатор режиссёра.
     */
    @DeleteMapping("/{directorId}")
    public void deleteDirector(@PathVariable Integer directorId) {
        log.info("Запрос на удаление режиссёра с id = {}", directorId);
        this.directorService.deleteDirector(directorId);
    }
}
