package ru.yandex.practicum.filmorate.dao.mapper;

import ru.yandex.practicum.filmorate.model.Mpa;

import java.sql.ResultSet;
import java.sql.SQLException;

public final class MpaMapper {
    public static Mpa mapToMpa(ResultSet resultSet, int rowNum) throws SQLException {
        return Mpa.builder().id(resultSet.getInt("id"))
                .name(resultSet.getString("name"))
                .build();
    }
}