package ru.yandex.practicum.filmorate.dao.mappers;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.User;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashSet;

@Component
public class UserMapper {
    public static User mapToUser(ResultSet resultSet, int rowNum) throws SQLException {
        return new User(resultSet.getLong("id"),
                        resultSet.getString("email"),
                        resultSet.getString("login"),
                        resultSet.getString("name"),
                        resultSet.getDate("birthday").toLocalDate(),
                        new HashSet<>());
    }
}