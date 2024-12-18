package ru.yandex.practicum.filmorate.dao;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.dao.mappers.MpaMapper;
import ru.yandex.practicum.filmorate.exception.InternalServerException;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.storage.MpaStorage;

import java.util.Collection;

@Repository
@Slf4j
@RequiredArgsConstructor
public class MpaRepository implements MpaStorage {
    private static final String GET_ALL_QUERY = "SELECT * FROM mpa";
    private static final String FIND_BY_ID_QUERY = "SELECT * FROM mpa WHERE id = ?";
    private final JdbcTemplate jdbc;

    @Override
    public Collection<Mpa> getAllMpa() {
        try {
            return jdbc.query(GET_ALL_QUERY, MpaMapper::mapToMpa);
        } catch (Exception e) {
            log.error("Ошибка при получении списка рейтингов");
            throw new InternalServerException("Ошибка при получении списка рейтингов");
        }
    }

    @Override
    public Mpa getMpaById(Integer id) {
        try {
            return jdbc.queryForObject(FIND_BY_ID_QUERY, MpaMapper::mapToMpa, id);
        } catch (EmptyResultDataAccessException e) {
            log.error("Ошибка при поиске рейтинга");
            throw new NotFoundException("Рейтинг с данным id не найден");
        }
    }
}