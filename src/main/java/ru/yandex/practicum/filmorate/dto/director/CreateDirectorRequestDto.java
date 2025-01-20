package ru.yandex.practicum.filmorate.dto.director;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * Трансферный объект для запроса на создание нового режиссёра.
 */
@Data
public final class CreateDirectorRequestDto {
    /**
     * Имя режиссёра.
     */
    @NotBlank(message = "Имя режиссёра не может быть пустым или состоять только из пробелов")
    private String name;
}
