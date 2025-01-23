package ru.yandex.practicum.filmorate.dto.user;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.PastOrPresent;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

import java.time.LocalDate;

/**
 * Трансферный объект для запроса на создание пользователя.
 */
@Data
public class CreateUserRequestDto {
    /**
     * Логин пользователя.
     */
    @NotBlank(message = "Логин пользователя не может быть пустым")
    @Pattern(regexp = "^\\S+$", message = "Логин пользователя не может содержать пробелы")
    private String login;

    /**
     * Адрес электронной почты пользователя.
     */
    @Email(message = "Адрес электронной почты должен содержать символ '@'")
    @NotBlank(message = "Адрес электронной почты пользователя не может быть пустым")
    private String email;

    /**
     * Имя пользователя.
     */
    private String name;

    /**
     * Дата рождения пользователя.
     */
    @PastOrPresent(message = "Дата рождения пользователя не может быть больше текущей даты")
    private LocalDate birthday;
}