package ru.yandex.practicum.filmorate;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;
import java.time.Month;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class UserControllerTest {

    @Autowired
    MockMvc mockMvc;

    @Autowired
    ObjectMapper objectMapper;

    private User user;

    @BeforeEach
    public void beforeEach() {
        user = User.builder()
                .id(2L)
                .email("mail@yandex.ru")
                .login("yandex")
                .name("Yandex Practicum")
                .birthday(LocalDate.of(1990, Month.MAY, 28))
                .build();
    }

    @Test
    void shouldReturnOkWhenCreateAndUpdateUser() throws Exception {
        User newUser = user.toBuilder().email("mail1@yandex.ru").name("Yandex Practicum 2").build();
        mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(user)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(2))
                .andExpect(jsonPath("$.email").value("mail@yandex.ru"))
                .andExpect(jsonPath("$.login").value("yandex"))
                .andExpect(jsonPath("$.name").value("Yandex Practicum"))
                .andExpect(jsonPath("$.birthday").value("1990-05-28"));
        mockMvc.perform(put("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(newUser)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(2))
                .andExpect(jsonPath("$.email").value("mail1@yandex.ru"))
                .andExpect(jsonPath("$.login").value("yandex"))
                .andExpect(jsonPath("$.name").value("Yandex Practicum 2"))
                .andExpect(jsonPath("$.birthday").value("1990-05-28"));
    }

    @Test
    void shouldReturnBadRequestWhenCreateUserWithInvalidEmail() throws Exception {
        User newUser = user.toBuilder().email("mail.yandex").build();
        mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(newUser)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void shouldReturnBadRequestWhenCreateUserWithInvalidLogin() throws Exception {
        User newUser = user.toBuilder().login("yan dex").build();
        mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(newUser)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void shouldWriteLoginToNameWhenNameIsEmpty() throws Exception {
        User newUser = user.toBuilder().name(null).build();
        mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(newUser)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.email").value("mail@yandex.ru"))
                .andExpect(jsonPath("$.login").value("yandex"))
                .andExpect(jsonPath("$.name").value("yandex"))
                .andExpect(jsonPath("$.birthday").value("1990-05-28"));
    }

    @Test
    void shouldReturnBadRequestWhenCreateFilmWithInvalidReleaseDate() throws Exception {
        User newUser = user.toBuilder().birthday(LocalDate.of(2025, Month.MAY, 28)).build();
        mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(newUser)))
                .andExpect(status().isBadRequest());
    }
}