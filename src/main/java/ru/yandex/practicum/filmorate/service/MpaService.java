package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.storage.MpaStorage;

import java.util.Collection;
import java.util.Optional;

/**
 * Сервис для работы с оценками Ассоциации кинокомпаний.
 */
@RequiredArgsConstructor
@Service
@Slf4j
public final class MpaService {
    /**
     * Хранилища оценок Ассоциации кинокомпаний.
     */
    private final MpaStorage mpaStorage;

    /**
     * Получить список всех оценок Ассоциации кинокомпаний.
     *
     * @return список оценок Ассоциации кинокомпаний.
     */
    public Collection<Mpa> getAllMpa() {
        log.debug("Получение списка всех оценок Ассоциации кинокомпаний");
        return mpaStorage.getAllMpa();
    }

    /**
     * Получить оценку Ассоциации кинокомпаний по её идентификатору.
     *
     * @param mpaId идентификатор оценки Ассоциации кинокомпаний.
     * @return оценка Ассоциации кинокомпаний.
     */
    public Mpa getMpaById(long mpaId) {
        log.debug("Получение оценки Ассоциации кинокомпаний с id = {}", mpaId);

        Optional<Mpa> mpa = this.mpaStorage.getMpaById(mpaId);
        if (mpa.isEmpty()) {
            throw new NotFoundException(String.format("Оценка Ассоциации кинокомпаний с id = %d не найдена", mpaId));
        }

        return mpa.get();
    }
}
