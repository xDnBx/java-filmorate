package ru.yandex.practicum.filmorate.dto.event;

import lombok.Builder;
import lombok.Data;
import ru.yandex.practicum.filmorate.model.EventOperation;
import ru.yandex.practicum.filmorate.model.EventType;

/**
 * Трансферный объект для сущности "Событие".
 */
@Builder(toBuilder = true)
@Data
public class EventDto {
    /**
     * Идентификатор события.
     */
    private long eventId;

    /**
     * Дата события в UNIX-формате.
     */
    private long timestamp;

    /**
     * Идентификатор пользователя.
     */
    private long userId;

    /**
     * Тип события.
     */
    private EventType eventType;

    /**
     * Тип операции.
     */
    private EventOperation operation;

    /**
     * Идентификатор сущности, связанной с событием.
     */
    private long entityId;
}