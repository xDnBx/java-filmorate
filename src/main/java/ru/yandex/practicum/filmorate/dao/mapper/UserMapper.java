package ru.yandex.practicum.filmorate.dao.mapper;

import ru.yandex.practicum.filmorate.model.User;

import java.sql.ResultSet;
import java.sql.SQLException;

public final class UserMapper {
    public static User mapToUser(ResultSet resultSet, int rowNum) throws SQLException {
        return User.builder().id(resultSet.getLong("id"))
                .email(resultSet.getString("email"))
                .login(resultSet.getString("login"))
                .name(resultSet.getString("name"))
                .birthday(resultSet.getDate("birthday").toLocalDate())
                .build();
    }
}