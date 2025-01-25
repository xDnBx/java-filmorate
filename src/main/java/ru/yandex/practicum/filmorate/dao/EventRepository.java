package ru.yandex.practicum.filmorate.dao;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.dao.mapper.EventMapper;
import ru.yandex.practicum.filmorate.dao.queries.EventQueries;
import ru.yandex.practicum.filmorate.exception.InternalServerException;
import ru.yandex.practicum.filmorate.model.Event;
import ru.yandex.practicum.filmorate.model.EventOperation;
import ru.yandex.practicum.filmorate.model.EventType;
import ru.yandex.practicum.filmorate.storage.EventStorage;

import java.util.Collection;

/**
 * Хранилище событий.
 */
@Repository
@Slf4j
public class EventRepository extends BaseRepository implements EventStorage {
    /**
     * Создать новое событие.
     *
     * @param userId    идентификатор пользователя.
     * @param eventType тип события.
     * @param operation тип операции.
     * @param entityId  идентификатор сущности, связанной с событием.
     */
    @Override
    public void createEvent(long userId, EventType eventType, EventOperation operation, long entityId) {
        try {
            long id = this.insert(EventQueries.CREATE_EVENT_QUERY, userId, eventType.toString(), operation.toString(), entityId);
            log.debug("Событие {} {} успешно добавлено с id = {}", operation, eventType, id);
        } catch (Throwable ex) {
            log.error("Ошибка при добавлении события {} {}: [{}] {}", operation, eventType, ex.getClass().getSimpleName(), ex.getMessage());
            throw new InternalServerException();
        }
    }

    /**
     * Получить список событий пользователя.
     *
     * @param userId идентификатор пользователя.
     * @return список событий.
     */
    @Override
    public Collection<Event> getUserEvents(long userId) {
        try {
            return this.findMany(EventQueries.GET_USER_EVENTS_QUERY, EventMapper::mapToEvent, userId);
        } catch (Throwable ex) {
            log.error("Ошибка при получении списка режиссёров: [{}] {}", ex.getClass().getSimpleName(), ex.getMessage());
            throw new InternalServerException();
        }
    }
}