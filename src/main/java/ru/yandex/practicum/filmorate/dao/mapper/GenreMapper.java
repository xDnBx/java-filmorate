package ru.yandex.practicum.filmorate.dao.mapper;

import ru.yandex.practicum.filmorate.model.Genre;

import java.sql.ResultSet;
import java.sql.SQLException;

public final class GenreMapper {
    public static Genre mapToGenre(ResultSet resultSet, int rowNum) throws SQLException {
        return Genre.builder().id(resultSet.getInt("id"))
                .name(resultSet.getString("name"))
                .build();
    }
}