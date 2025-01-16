package ru.yandex.practicum.filmorate.model;

import lombok.Builder;
import lombok.Data;

import java.time.Instant;

@Data
@Builder
public class Event {
    private Integer id;
    private Instant timestamp;
    private Integer userId;
    private EventType eventType;
    private Operation operation;
    private Integer entityId;

    public enum Operation {
        REMOVE,
        ADD,
        UPDATE
    }

    public enum EventType {
        LIKE,
        REVIEW,
        FRIEND
    }
}