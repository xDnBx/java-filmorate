package ru.yandex.practicum.filmorate.dao.mappers;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.User;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashSet;

@Component
public class UserMapper {
    public static User mapToUser(ResultSet resultSet, int rowNum) throws SQLException {
        return User.builder().id(resultSet.getLong("id"))
                .email(resultSet.getString("email"))
                .login(resultSet.getString("login"))
                .name(resultSet.getString("name"))
                .birthday(resultSet.getDate("birthday").toLocalDate())
                .friends(new HashSet<>())
                .build();
    }
}