package ru.yandex.practicum.filmorate;

import org.junit.jupiter.api.BeforeEach;
import org.springframework.boot.test.context.SpringBootTest;
import ru.yandex.practicum.filmorate.model.Film;

import java.time.LocalDate;
import java.time.Month;

@SpringBootTest
public class FilmControllerTest {
    private Film film;

    @BeforeEach
    public void beforeEach() {
        film = Film.builder()
                .name("Film 1")
                .description("Description 1")
                .releaseDate(LocalDate.of(1990, Month.MAY, 28))
                .duration(150)
                .build();
    }
}
