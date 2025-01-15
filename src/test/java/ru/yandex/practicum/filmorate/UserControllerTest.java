package ru.yandex.practicum.filmorate;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Data;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.time.LocalDate;
import java.time.Month;
import java.util.HashSet;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@Data
@AutoConfigureMockMvc
class UserControllerTest {

    @Autowired
    MockMvc mockMvc;

    @Autowired
    ObjectMapper objectMapper;

    private User user;
    private UserStorage userStorage;

    @BeforeEach
    void beforeEach() {
        user = User.builder()
                .email("mail@yandex.ru")
                .login("yandex1233")
                .name("Yandex Practicum")
                .birthday(LocalDate.of(1990, Month.MAY, 28))
                .friends(new HashSet<>())
                .build();
    }

    @Test
    void shouldReturnOkWhenCreateAndUpdateUser() throws Exception {
        User newUser = user.toBuilder().email("mail2@yandex.ru").login("yandex1531").name("Yandex Practicum 3").build();
        mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(user)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.email").value("mail@yandex.ru"))
                .andExpect(jsonPath("$.login").value("yandex1233"))
                .andExpect(jsonPath("$.name").value("Yandex Practicum 2"))
                .andExpect(jsonPath("$.birthday").value("1990-05-28"));
        mockMvc.perform(put("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(newUser)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value("mail2@yandex.ru"))
                .andExpect(jsonPath("$.login").value("yandex1531"))
                .andExpect(jsonPath("$.name").value("Yandex Practicum 3"))
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
    void shouldReturnBadRequestWhenCreateFilmWithInvalidReleaseDate() throws Exception {
        User newUser = user.toBuilder().birthday(LocalDate.of(2025, Month.MAY, 28)).build();
        mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(newUser)))
                .andExpect(status().isBadRequest());
    }
}