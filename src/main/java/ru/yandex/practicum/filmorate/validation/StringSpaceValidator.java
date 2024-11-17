package ru.yandex.practicum.filmorate.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class StringSpaceValidator implements ConstraintValidator<StringSpace, String> {

    @Override
    public boolean isValid(String login, ConstraintValidatorContext constraintValidatorContext) {
        return (login != null) && !(login.contains(" ") || login.contains("\t") || login.contains("\n"));
    }
}
