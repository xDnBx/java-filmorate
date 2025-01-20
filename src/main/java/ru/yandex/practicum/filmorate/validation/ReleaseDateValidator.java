package ru.yandex.practicum.filmorate.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import ru.yandex.practicum.filmorate.validation.constraint.ReleaseDateConstraint;

import java.time.LocalDate;
import java.time.Month;

public class ReleaseDateValidator implements ConstraintValidator<ReleaseDateConstraint, LocalDate> {
    private final LocalDate minimumDate = LocalDate.of(1895, Month.DECEMBER, 28);

    @Override
    public boolean isValid(LocalDate localDate, ConstraintValidatorContext constraintValidatorContext) {
        return localDate == null || !localDate.isBefore(minimumDate);
    }
}
