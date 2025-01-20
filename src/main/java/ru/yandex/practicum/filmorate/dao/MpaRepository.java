package ru.yandex.practicum.filmorate.dao;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.dao.mapper.MpaMapper;
import ru.yandex.practicum.filmorate.dao.queries.MpaQueries;
import ru.yandex.practicum.filmorate.exception.InternalServerException;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.storage.MpaStorage;

import java.util.Collection;
import java.util.Optional;

/**
 * Хранилище оценок Ассоциации кинокомпаний.
 */
@Repository
@Slf4j
public class MpaRepository extends BaseRepository implements MpaStorage {
    /**
     * Получить список оценок Ассоциации кинокомпаний.
     *
     * @return список оценок Ассоциации кинокомпаний.
     */
    @Override
    public Collection<Mpa> getAllMpa() {
        try {
            return this.findMany(MpaQueries.GET_ALL_MPA_QUERY, MpaMapper::mapToMpa);
        } catch (Throwable ex) {
            log.error("Ошибка при получении списка рейтингов: [{}] {}", ex.getClass().getSimpleName(), ex.getMessage());
            throw new InternalServerException();
        }
    }

    /**
     * Получить оценку Ассоциации кинокомпаний по её идентификатору.
     *
     * @param mpaId идентификатор оценки Ассоциации кинокомпаний.
     * @return оценка Ассоциации кинокомпаний.
     */
    @Override
    public Optional<Mpa> getMpaById(long mpaId) {
        try {
            return this.findOne(MpaQueries.GET_MPA_BY_ID_QUERY, MpaMapper::mapToMpa, mpaId);
        } catch (Throwable ex) {
            log.error("Ошибка при получении оценки Ассоциации кинокомпаний с id = {}: [{}] {}", mpaId, ex.getClass().getSimpleName(), ex.getMessage());
            throw new InternalServerException();
        }
    }
}