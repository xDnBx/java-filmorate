package ru.yandex.practicum.filmorate.model;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;

/**
 * Пользователь.
 */
@Builder(toBuilder = true)
@Data
public class User {
    /**
     * Идентификатор пользователя.
     */
    private long id;

    /**
     * Логин пользователя.
     */
    private String login;

    /**
     * Адрес электронной почты пользователя.
     */
    private String email;

    /**
     * Имя пользователя.
     */
    private String name;

    /**
     * Дата рождения пользователя.
     */
    private LocalDate birthday;
}