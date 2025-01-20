package ru.yandex.practicum.filmorate.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.filmorate.dto.mpa.MpaDto;
import ru.yandex.practicum.filmorate.mapper.MpaMapper;
import ru.yandex.practicum.filmorate.service.MpaService;

import java.util.Collection;

/**
 * Контроллер для запросов оценок Ассоциации кинокомпаний.
 */
@RequestMapping("/mpa")
@RequiredArgsConstructor
@RestController
@Slf4j
public final class MpaController {
    /**
     * Сервис для работы с оценками Ассоциации кинокомпаний.
     */
    private final MpaService mpaService;

    /**
     * Получить список всех оценок Ассоциации кинокомпаний.
     *
     * @return список оценок Ассоциации кинокомпаний.
     */
    @GetMapping
    public Collection<MpaDto> getAllMpa() {
        log.info("Запрос на получение списка всех рейтингов");
        return MpaMapper.mapToMpaDtoCollection(mpaService.getAllMpa());
    }

    /**
     * Получить оценку Ассоциации кинокомпаний по её идентификатору.
     *
     * @param mpaId идентификатор оценки Ассоциации кинокомпаний.
     * @return оценка Ассоциации кинокомпаний.
     */
    @GetMapping("/{mpaId}")
    public MpaDto getMpaById(@PathVariable long mpaId) {
        log.info("Запрос на получение оценки Ассоциации кинокомпаний с id = {}", mpaId);
        return MpaMapper.mapToMpaDto(mpaService.getMpaById(mpaId));
    }
}