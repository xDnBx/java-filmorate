package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.Director;
import ru.yandex.practicum.filmorate.storage.DirectorStorage;

import java.util.Collection;

@Slf4j
@Service
@RequiredArgsConstructor
public class DirectorService {
    private final DirectorStorage directorStorage;

    public Director createDirector(Director director) {
        log.info("Добавление нового режиссёра с именем {}", director.getName());
        return this.directorStorage.createDirector(director);
    }

    public Collection<Director> getAllDirectors() {
        log.info("Получение списка всех режиссёров");
        return this.directorStorage.getAllDirectors();
    }

    public Director getDirectorById(Integer directorId) {
        log.info("Получение режиссёра с идентификатором {}", directorId);
        return this.directorStorage.getDirectorById(directorId);
    }

    public Director updateDirector(Director newDirector) {
        log.info("Обновление режиссёра с идентификатором {}", newDirector.getId());
        return this.directorStorage.updateDirector(newDirector);
    }

    public void deleteDirector(Integer directorId) {
        log.info("Удаление режиссёра с идентификатором {}", directorId);
        this.directorStorage.deleteDirector(directorId);
    }
}
