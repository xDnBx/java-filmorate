package ru.yandex.practicum.filmorate.mapper;

import ru.yandex.practicum.filmorate.dto.event.EventDto;
import ru.yandex.practicum.filmorate.model.Event;

import java.util.Collection;

public final class EventMapper {
    public static EventDto mapToEventDto(Event event) {
        return EventDto.builder()
                .eventId(event.getId())
                .timestamp(event.getTimestamp().toEpochMilli())
                .userId(event.getUserId())
                .eventType(event.getEventType())
                .operation(event.getOperation())
                .entityId(event.getEntityId())
                .build();
    }

    public static Collection<EventDto> mapToEventDtoCollection(Collection<Event> eventCollection) {
        return eventCollection.stream().map(EventMapper::mapToEventDto).toList();
    }
}
