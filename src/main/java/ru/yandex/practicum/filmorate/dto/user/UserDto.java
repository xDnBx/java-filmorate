package ru.yandex.practicum.filmorate.dto.user;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;

/**
 * Трансферный объект для сущности "Пользователь".
 */
@Builder(toBuilder = true)
@Data
public final class UserDto {
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
