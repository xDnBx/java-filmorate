package ru.yandex.practicum.filmorate.dto.director;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import lombok.Data;

/**
 * Трансферный объект для запроса на обновление режиссёра.
 */
@Data
public final class UpdateDirectorRequestDto {
    /**
     * Идентификатор режиссёра.
     */
    @Positive(message = "Идентификатор режиссёра должен быть положительным числом")
    private long id;

    /**
     * Имя режиссёра.
     */
    @NotBlank(message = "Имя режиссёра не может быть пустым или состоять только из пробелов")
    private String name;
}
