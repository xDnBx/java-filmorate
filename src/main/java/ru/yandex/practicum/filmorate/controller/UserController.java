package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
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

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/users")
@Validated
public class UserController {

    private long id = 1;
    private final Map<Long, User> users = new HashMap<>();

    @GetMapping
    public Collection<User> findUser() {
        log.info("Получение списка всех пользователей");
        return users.values();
    }

    @PostMapping
    public User createUser(@Valid @RequestBody User user) {
        log.info("Добавление нового пользователя: {}", user.getLogin());
        for (Map.Entry<Long, User> mapEntries : users.entrySet()) {
            if (mapEntries.getValue().getEmail().equals(user.getEmail())) {
                log.error("E-mail уже присутствует у другого пользователя");
                throw new DuplicatedDataException("Этот e-mail уже используется");
            }
        }
        if (user.getName() == null) {
            user.setName(user.getLogin());
        }
        user.setId(generateNewId());
        users.put(user.getId(), user);
        log.info("Пользователь с id = {} успешно добавлен!", user.getId());
        return user;
    }

    @PutMapping
    public User updateUser(@Valid @RequestBody User newUser) {
        log.info("Обновление пользователя с id = {}", newUser.getId());
        if (newUser.getId() == null) {
            log.error("id пользователя не указан");
            throw new ValidationException("Id должен быть указан");
        }
        for (Map.Entry<Long, User> mapEntries : users.entrySet()) {
            if (mapEntries.getValue().getEmail().equals(newUser.getEmail())) {
                log.error("E-mail уже присутствует у другого пользователя");
                throw new DuplicatedDataException("Этот e-mail уже используется");
            }
        }
        if (!users.containsKey(newUser.getId())) {
            log.error("Пользователя с данным id не существует");
            throw new NotFoundException("Пользователь с данным id не найден");
        }

        User oldUser = users.get(newUser.getId());
        oldUser.setEmail(newUser.getEmail());
        oldUser.setLogin(newUser.getLogin());
        if (newUser.getName() == null) {
            oldUser.setName(newUser.getLogin());
        } else {
            oldUser.setName(newUser.getName());
        }
        oldUser.setBirthday(newUser.getBirthday());

        log.info("Обновление пользователя с id = {} прошло успешно!", newUser.getId());
        return oldUser;
    }

    private long generateNewId() {
        return id++;
    }
}