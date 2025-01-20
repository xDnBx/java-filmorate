package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Event;
import ru.yandex.practicum.filmorate.storage.EventStorage;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.util.Collection;

/**
 * Сервис для работы с событиями.
 */
@RequiredArgsConstructor
@Service
@Slf4j
public final class EventService {

    /**
     * Хранилище событий.
     */
    private final EventStorage eventStorage;

    /**
     * Хранилище пользователй.
     */
    private final UserStorage userStorage;

    /**
     * Получить список событий пользователя.
     *
     * @param userId идентификатор пользователя.
     * @return список событий.
     */
    public Collection<Event> getUserEvents(long userId) {
        throwIfUserNotFound(userId);
        log.debug("Получение списка событий для пользователя с id = {}", userId);

        return this.eventStorage.getUserEvents(userId);
    }

    //region Facilities

    /**
     * Выбросить исключение, если пользователей не найден.
     *
     * @param userId идентификатор пользователя.
     */
    private void throwIfUserNotFound(long userId) {
        if (this.userStorage.getUserById(userId).isEmpty()) {
            throw new NotFoundException(String.format("Пользователь с id = %d не найден", userId));
        }
    }

    //endregion
}
