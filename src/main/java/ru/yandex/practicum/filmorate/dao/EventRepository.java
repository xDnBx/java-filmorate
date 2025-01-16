package ru.yandex.practicum.filmorate.dao;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.exception.InternalServerException;
import ru.yandex.practicum.filmorate.model.Event;

import java.util.List;

@Slf4j
@Repository
@RequiredArgsConstructor
public class EventRepository {
    private static final String INSERT_QUERY = """
            INSERT INTO events (
            	user_id,
            	event_type,
            	operation,
            	entity_id)
            VALUES (?, ?, ?, ?)
            """;
    private static final String FIND_ALL_BY_USER_ID_QUERY = """
            SELECT
            	*
            FROM
            	events
            WHERE user_id = ?
            """;
    private final JdbcTemplate jdbc;
    private final RowMapper<Event> eventRowMapper;

    public void insert(Integer userId, Event.EventType eventType, Event.Operation operation, Integer entityId) {
        int rowsUpdated = jdbc.update(INSERT_QUERY,
                userId,
                eventType.toString(),
                operation.toString(),
                entityId);
        if (rowsUpdated == 0) {
            log.error("Произошла ошибка при создании события.");
            throw new InternalServerException("Произошла ошибка при создании события.");
        }
    }

    public List<Event> findAllByUserId(Integer userId) {
        return jdbc.query(FIND_ALL_BY_USER_ID_QUERY, eventRowMapper, userId);
    }
}