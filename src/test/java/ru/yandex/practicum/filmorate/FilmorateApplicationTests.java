package ru.yandex.practicum.filmorate;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.context.annotation.Import;
import ru.yandex.practicum.filmorate.dao.UserRepository;
import ru.yandex.practicum.filmorate.dao.mapper.UserMapper;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;
import java.time.Month;
import java.util.Collection;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;

@JdbcTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@Import({UserRepository.class, UserMapper.class})
class FilmorateApplicationTests {
    private final UserRepository userRepository;
    User user;

    @BeforeEach
    void beforeEach() {
        user = User.builder()
                .email("mail@yandex.ru")
                .login("yandex")
                .name("Yandex Practicum")
                .birthday(LocalDate.of(1990, Month.MAY, 28))
                .build();
    }

    @Test
    public void testCreateUser() {
        User newUser = userRepository.createUser(user);

        assertThat(newUser.getId()).isNotNull();
        assertThat(newUser.getEmail()).isEqualTo("mail@yandex.ru");
        assertThat(newUser.getLogin()).isEqualTo("yandex");
        assertThat(newUser.getName()).isEqualTo("Yandex Practicum");
        assertThat(newUser.getBirthday()).isEqualTo("1990-05-28");
    }

    @Test
    public void testGetAllUsers() {
        User newUser = user.toBuilder().email("mail1@yandex.ru").login("yan").name("Yandex Practicum 2").build();
        User anotherUser = user.toBuilder().email("mail2@yandex.ru").login("ya").name("Yandex Practicum 3").build();
        userRepository.createUser(user);
        userRepository.createUser(newUser);
        userRepository.createUser(anotherUser);

        Collection<User> users = userRepository.getAllUsers();

        assertEquals(users.size(), 3, "Пользователи не добавлены");
    }
}