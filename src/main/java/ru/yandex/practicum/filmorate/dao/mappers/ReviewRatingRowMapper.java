package ru.yandex.practicum.filmorate.dao.mappers;

import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.ReviewRating;

import java.sql.ResultSet;
import java.sql.SQLException;

@Component
public class ReviewRatingRowMapper implements RowMapper<ReviewRating> {
    @Override
    public ReviewRating mapRow(ResultSet rs, int rowNum) throws SQLException {
        return ReviewRating.builder()
                .id(rs.getInt("id"))
                .reviewId(rs.getInt("review_id"))
                .rating(rs.getInt("rating"))
                .build();
    }
}
