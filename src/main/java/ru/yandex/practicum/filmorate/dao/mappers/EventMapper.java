package ru.yandex.practicum.filmorate.dao.mappers;

import ru.yandex.practicum.filmorate.dto.review.event.ResponseEventDTO;
import ru.yandex.practicum.filmorate.model.Event;

import java.util.List;

public class EventMapper {

    public static ResponseEventDTO mapToResponseEventDTO(Event event) {
        return ResponseEventDTO.builder()
                .eventId(event.getId())
                .timestamp(event.getTimestamp().toEpochMilli())
                .userId(event.getUserId())
                .eventType(event.getEventType())
                .operation(event.getOperation())
                .entityId(event.getEntityId())
                .build();
    }

    public static List<ResponseEventDTO> mapToResponseEventDTOList(List<Event> eventList) {
        return eventList.stream().map(EventMapper::mapToResponseEventDTO).toList();
    }
}