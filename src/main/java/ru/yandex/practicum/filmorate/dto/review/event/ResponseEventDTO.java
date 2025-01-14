package ru.yandex.practicum.filmorate.dto.review.event;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import ru.yandex.practicum.filmorate.model.Event;

@Getter
@Setter
@Builder
public class ResponseEventDTO {
    private Integer eventId;
    private Long timestamp;
    private Integer userId;
    private Event.EventType eventType;
    private Event.Operation operation;
    private Integer entityId;
}