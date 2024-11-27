package ru.yandex.practicum.filmorate.model;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.PastOrPresent;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.yandex.practicum.filmorate.validation.StringSpace;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@Data
@Builder(toBuilder = true)
@AllArgsConstructor
@NoArgsConstructor
public class User {
    private Long id;

    @NotBlank(message = "E-mail не может быть пустым")
    @Email(message = "Поле должно содержать символ @")
    private String email;

    @NotBlank
    @StringSpace
    private String login;

    private String name;

    @PastOrPresent(message = "Дата рождения указана неверно")
    private LocalDate birthday;

    private Set<Long> friends = new HashSet<>();
}