package ru.yandex.practicum.filmorate.dao;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.IncorrectResultSizeDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.util.CollectionUtils;
import ru.yandex.practicum.filmorate.exception.InternalServerException;

import java.sql.PreparedStatement;
import java.sql.Statement;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Function;

/**
 * Базовый класс хранилища данных.
 */
public abstract class BaseRepository {
    @Autowired
    protected JdbcTemplate jdbc;

    protected long insert(String query, Object... params) {
        GeneratedKeyHolder keyHolder = new GeneratedKeyHolder();

        this.jdbc.update(connection -> {
            PreparedStatement preparedStatement = connection.prepareStatement(query, Statement.RETURN_GENERATED_KEYS);
            for (int i = 0; i < params.length; i++) {
                preparedStatement.setObject(i + 1, params[i]);
            }
            return preparedStatement;
        }, keyHolder);

        Long id = (Long) Objects.requireNonNull(keyHolder.getKeys()).get("id");
        if (id != null) {
            return id;
        } else {
            throw new InternalServerException("Не удалось сохранить данные");
        }
    }

    protected <T> void insert(String query, Collection<T> collection, Function<T, List<Object>> paramsSelector) {
        collection.forEach(e -> {
            GeneratedKeyHolder keyHolder = new GeneratedKeyHolder();
            this.jdbc.update(connection -> {
                PreparedStatement preparedStatement = connection.prepareStatement(query, Statement.RETURN_GENERATED_KEYS);
                List<Object> params = paramsSelector.apply(e);
                for (int i = 0; i < params.size(); i++) {
                    preparedStatement.setObject(i + 1, params.get(i));
                }
                return preparedStatement;
            }, keyHolder);
        });
    }

    protected <T> Optional<T> findOne(String query, RowMapper<T> rowMapper, Object... params) {
        Collection<T> results = this.jdbc.query(query, rowMapper, params);

        if (CollectionUtils.isEmpty(results)) {
            return Optional.empty();
        } else if (results.size() > 1) {
            throw new IncorrectResultSizeDataAccessException(1, results.size());
        }

        return Optional.of(results.iterator().next());
    }

    protected <T> Collection<T> findMany(String query, RowMapper<T> rowMapper, Object... params) {
        return this.jdbc.query(query, rowMapper, params);
    }

    protected void update(String query, Object... params) {
        int rowsUpdated = this.jdbc.update(query, params);
        if (rowsUpdated == 0) {
            throw new InternalServerException("Не удалось обновить данные");
        }
    }

    protected void delete(String query, long id) {
        this.jdbc.update(query, id);
    }

    protected void delete(String query, Object... params) {
        this.jdbc.update(query, params);
    }
}
