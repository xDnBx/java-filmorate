package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.Event;
import ru.yandex.practicum.filmorate.model.EventOperation;
import ru.yandex.practicum.filmorate.model.EventType;

import java.util.Collection;

/**
 * Контракт хранилища событий.
 */
public interface EventStorage {
    /**
     * Создать новое событие.
     *
     * @param userId    идентификатор пользователя.
     * @param eventType тип события.
     * @param operation тип операции.
     * @param entityId  идентификатор сущности, связанной с событием.
     */
    void createEvent(long userId, EventType eventType, EventOperation operation, long entityId);

    /**
     * Получить список событий пользователя.
     *
     * @param userId идентификатор пользователя.
     * @return список событий.
     */
    Collection<Event> getUserEvents(long userId);
}
