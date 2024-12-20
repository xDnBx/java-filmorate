package ru.yandex.practicum.filmorate.dao.mappers;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Mpa;

import java.sql.ResultSet;
import java.sql.SQLException;

@Component
public class MpaMapper {
    public static Mpa mapToMpa(ResultSet resultSet, int rowNum) throws SQLException {
        return Mpa.builder().id(resultSet.getInt("id"))
                .name(resultSet.getString("name"))
                .build();
    }
}