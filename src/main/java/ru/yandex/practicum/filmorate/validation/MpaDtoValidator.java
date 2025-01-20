package ru.yandex.practicum.filmorate.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.springframework.beans.factory.annotation.Autowired;
import ru.yandex.practicum.filmorate.dto.mpa.MpaDto;
import ru.yandex.practicum.filmorate.storage.MpaStorage;
import ru.yandex.practicum.filmorate.validation.constraint.MpaDtoConstraint;

public class MpaDtoValidator implements ConstraintValidator<MpaDtoConstraint, MpaDto> {
    @Autowired
    private MpaStorage mpaStorage;

    @Override
    public boolean isValid(MpaDto mpaDto, ConstraintValidatorContext constraintValidatorContext) {
        if (this.mpaStorage.getMpaById(mpaDto.getId()).isEmpty()) {
            constraintValidatorContext.disableDefaultConstraintViolation();
            constraintValidatorContext.buildConstraintViolationWithTemplate(String.format("Оценка Ассоциации кинокомпаний с id = %d не найдена", mpaDto.getId())).addConstraintViolation();

            return false;
        }

        return true;
    }
}
