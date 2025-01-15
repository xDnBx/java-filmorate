package ru.yandex.practicum.filmorate;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Mpa;

import java.time.LocalDate;
import java.time.Month;
import java.util.HashSet;
import java.util.LinkedHashSet;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class FilmControllerTest {

    @Autowired
    MockMvc mockMvc;

    @Autowired
    ObjectMapper objectMapper;

    private Film film;

    @BeforeEach
    public void beforeEach() {
        film = Film.builder()
                .name("Film 1")
                .description("Description 1")
                .releaseDate(LocalDate.of(1990, Month.MAY, 28))
                .duration(150)
                .mpa(Mpa.builder().id(1).build())
                .genres(new LinkedHashSet<>())
                .directors(new HashSet<>())
                .build();
    }

    @Test
    void shouldReturnOkWhenCreateAndUpdateFilm() throws Exception {
        Film newFilm = film.toBuilder().id(1L).name("Film 2").build();
        mockMvc.perform(post("/films")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(film)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("Film 1"))
                .andExpect(jsonPath("$.description").value("Description 1"))
                .andExpect(jsonPath("$.releaseDate").value("1990-05-28"))
                .andExpect(jsonPath("$.duration").value("150"));
        mockMvc.perform(put("/films")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(newFilm)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("Film 2"))
                .andExpect(jsonPath("$.description").value("Description 1"))
                .andExpect(jsonPath("$.releaseDate").value("1990-05-28"))
                .andExpect(jsonPath("$.duration").value("150"));
    }

    @Test
    void shouldReturnBadRequestWhenCreateFilmWithMore200Description() throws Exception {
        Film newFilm = film.toBuilder().description(film.getDescription().repeat(16)).build();
        mockMvc.perform(post("/films")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(newFilm)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void shouldReturnBadRequestWhenCreateFilmWithEmptyName() throws Exception {
        Film newFilm = film.toBuilder().name("").build();
        mockMvc.perform(post("/films")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(newFilm)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void shouldReturnBadRequestWhenCreateFilmWithNullDescription() throws Exception {
        Film newFilm = film.toBuilder().description(null).build();
        mockMvc.perform(post("/films")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(newFilm)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void shouldReturnBadRequestWhenCreateFilmWithInvalidReleaseDate() throws Exception {
        Film newFilm = film.toBuilder().releaseDate(LocalDate.of(1894, Month.MAY, 28)).build();
        mockMvc.perform(post("/films")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(newFilm)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void shouldReturnBadRequestWhenCreateFilmWithNegativeDuration() throws Exception {
        Film newFilm = film.toBuilder().duration(0).build();
        mockMvc.perform(post("/films")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(newFilm)))
                .andExpect(status().isBadRequest());
    }
}