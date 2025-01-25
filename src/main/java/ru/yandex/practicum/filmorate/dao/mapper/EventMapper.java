package ru.yandex.practicum.filmorate.dao.mapper;

import ru.yandex.practicum.filmorate.model.Event;
import ru.yandex.practicum.filmorate.model.EventOperation;
import ru.yandex.practicum.filmorate.model.EventType;

import java.sql.ResultSet;
import java.sql.SQLException;

public final class EventMapper {
    public static Event mapToEvent(ResultSet resultSet, int rowNum) throws SQLException {
        return Event.builder()
                .id(resultSet.getLong("id"))
                .timestamp(resultSet.getTimestamp("timestamp").toInstant())
                .userId(resultSet.getLong("user_id"))
                .eventType(EventType.valueOf(resultSet.getString("event_type")))
                .operation(EventOperation.valueOf(resultSet.getString("operation")))
                .entityId(resultSet.getLong("entity_id"))
                .build();
    }
}