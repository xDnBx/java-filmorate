package ru.yandex.practicum.filmorate.model;

import lombok.Builder;
import lombok.Data;

import java.time.Instant;

/**
 * Событие.
 */
@Data
@Builder
public class Event {
    /**
     * Идентификатор события.
     */
    private long id;

    /**
     * Дата события.
     */
    private Instant timestamp;

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