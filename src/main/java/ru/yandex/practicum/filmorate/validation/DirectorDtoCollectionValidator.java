package ru.yandex.practicum.filmorate.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.springframework.beans.factory.annotation.Autowired;
import ru.yandex.practicum.filmorate.dto.director.DirectorDto;
import ru.yandex.practicum.filmorate.model.Director;
import ru.yandex.practicum.filmorate.storage.DirectorStorage;
import ru.yandex.practicum.filmorate.validation.constraint.DirectorDtoCollectionConstraint;

import java.util.Collection;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

public class DirectorDtoCollectionValidator implements ConstraintValidator<DirectorDtoCollectionConstraint, Collection<DirectorDto>> {
    @Autowired
    private DirectorStorage directorStorage;

    @Override
    public boolean isValid(Collection<DirectorDto> directorDtoCollection, ConstraintValidatorContext constraintValidatorContext) {
        Set<String> errors = new HashSet<>();

        directorDtoCollection.forEach(dto -> {
            Optional<Director> genre = this.directorStorage.getDirectorById(dto.getId());
            if (genre.isEmpty()) {
                errors.add(String.format("Режиссёр с id = %d не найден", dto.getId()));
            }
        });

        if (!errors.isEmpty()) {
            constraintValidatorContext.disableDefaultConstraintViolation();
            constraintValidatorContext.buildConstraintViolationWithTemplate(String.join(", ", errors)).addConstraintViolation();

            return false;
        }

        return true;
    }
}
