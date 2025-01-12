package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Director;
import ru.yandex.practicum.filmorate.service.DirectorService;

import java.util.Collection;

@Slf4j
@RestController
@RequestMapping("/directors")
@RequiredArgsConstructor
public class DirectorController {
    private final DirectorService directorService;

    @PostMapping
    public Director createDirector(@RequestBody @Valid Director director) {
        log.info("Запрос на добавление нового режиссёра с именем {}", director.getName());
        return this.directorService.createDirector(director);
    }

    @GetMapping
    public Collection<Director> getAllDirectors() {
        log.info("Запрос на получение списка всех режиссёров");
        return this.directorService.getAllDirectors();
    }

    @GetMapping("/{directorId}")
    public Director getDirectorById(@PathVariable Integer directorId) {
        log.info("Запрос на получение режиссёра с идентификатором {}", directorId);
        return this.directorService.getDirectorById(directorId);
    }

    @PutMapping
    public Director updateDirector(@RequestBody @Valid Director newDirector) {
        log.info("Запрос на обновление режиссёра с идентификатором {}", newDirector.getId());
        return this.directorService.updateDirector(newDirector);
    }

    @DeleteMapping("/{directorId}")
    public void deleteDirector(@PathVariable Integer directorId) {
        log.info("Запрос на удаление режиссёра с идентификатором {}", directorId);
        this.directorService.deleteDirector(directorId);
    }
}
