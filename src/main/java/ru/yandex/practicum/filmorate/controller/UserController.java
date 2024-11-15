package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.filmorate.exception.DuplicatedDataException;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/users")
public class UserController {

    private final Map<Long, User> users = new HashMap<>();

    @GetMapping
    public Collection<User> findUser() {
        log.info("Получение списка всех пользователей");
        return users.values();
    }

    @PostMapping
    public User createUser(@RequestBody User user) {
        log.info("Добавление нового пользователя: {}", user.getLogin());
        if (user.getEmail() == null || user.getEmail().isBlank() || !user.getEmail().contains("@")) {
            log.error("E-mail пустой");
            throw new ValidationException("E-mail должен быть указан");
        }
        if (user.getLogin() == null || user.getLogin().isBlank()) {
            log.error("Логин пустой");
            throw new ValidationException("Логин должен быть указан");
        }
        if (user.getName() == null || user.getName().isBlank()) {
            log.info("Имя пустое, в качестве имени будет использован логин пользователя");
            user.setName(user.getLogin());
        }
        if (user.getBirthday() == null || user.getBirthday().isAfter(LocalDate.now())) {
            log.error("Дата рождения пустая или указана неверно");
            throw new ValidationException("Дата рождения должна быть указана или она указана неверно");
        }
        if (users.containsKey(user.getEmail())) {
            log.error("E-mail уже присутствует у другого пользователя");
            throw new DuplicatedDataException("Этот e-mail уже используется");
        }

        user.setId(getNextId());
        users.put(user.getId(), user);
        log.info("Пользователь с id = {} успешно добавлен!", user.getId());
        return user;
    }

    @PutMapping
    public User updateUser(@RequestBody User newUser) {
        log.info("Обновление пользователя с id = {}", newUser.getId());
        if (newUser.getId() == null) {
            log.error("id пользователя не указан");
            throw new ValidationException("Id должен быть указан");
        }
        if (users.containsKey(newUser.getEmail())) {
            log.error("E-mail уже присутствует у другого пользователя");
            throw new DuplicatedDataException("Этот e-mail уже используется");
        }
        if (!users.containsKey(newUser.getId())) {
            log.error("Пользователя с данным id не существует");
            throw new NotFoundException("Пользователь с данным id не найден");
        }

        User oldUser = users.get(newUser.getId());

        if (newUser.getEmail() != null) {
            oldUser.setEmail(newUser.getEmail());
        }
        if (newUser.getLogin() != null) {
            oldUser.setLogin(newUser.getLogin());
        }
        if (newUser.getName() != null) {
            oldUser.setName(newUser.getName());
        }
        if (newUser.getBirthday() == null || newUser.getBirthday().isAfter(LocalDate.now())) {
            log.error("Дата рождения пустая или указана неверно");
            throw new ValidationException("Дата рождения должна быть указана или она указана неверно");
        } else {
            oldUser.setBirthday(newUser.getBirthday());
        }
        log.info("Обновление пользователя с id = {} прошло успешно!", newUser.getId());
        return oldUser;
    }

    private long getNextId() {
        long currentMaxId = users.keySet()
                .stream()
                .mapToLong(id -> id)
                .max()
                .orElse(0);
        return ++currentMaxId;
    }
}