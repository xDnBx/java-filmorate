package ru.yandex.practicum.filmorate.validation;

import jakarta.validation.Constraint;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = StringSpaceValidator.class)
public @interface StringSpace {
    String message() default "Строка не может быть пустой или содержать пробелы";
    Class<?>[] groups() default {};
    Class<?>[] payload() default {};
}