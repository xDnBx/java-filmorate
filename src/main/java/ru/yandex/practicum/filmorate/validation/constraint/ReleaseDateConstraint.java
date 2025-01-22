package ru.yandex.practicum.filmorate.validation.constraint;

import jakarta.validation.Constraint;
import ru.yandex.practicum.filmorate.validation.ReleaseDateValidator;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = ReleaseDateValidator.class)
public @interface ReleaseDateConstraint {
    String message() default "Дата релиза не может быть раньше 28 декабря 1895 года";

    Class<?>[] groups() default {};

    Class<?>[] payload() default {};
}
