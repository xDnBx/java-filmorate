package ru.yandex.practicum.filmorate.dao.mapper;

import java.sql.ResultSet;
import java.sql.SQLException;

public final class BooleanMapper {
    public static boolean mapToBoolean(ResultSet resultSet, int rowNum) throws SQLException {
        return resultSet.getBoolean(1);
    }
}
