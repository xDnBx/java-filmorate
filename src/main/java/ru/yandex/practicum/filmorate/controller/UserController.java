package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.dto.event.EventDto;
import ru.yandex.practicum.filmorate.dto.film.FilmDto;
import ru.yandex.practicum.filmorate.dto.user.CreateUserRequestDto;
import ru.yandex.practicum.filmorate.dto.user.UpdateUserRequestDto;
import ru.yandex.practicum.filmorate.dto.user.UserDto;
import ru.yandex.practicum.filmorate.mapper.EventMapper;
import ru.yandex.practicum.filmorate.mapper.FilmMapper;
import ru.yandex.practicum.filmorate.mapper.UserMapper;
import ru.yandex.practicum.filmorate.service.EventService;
import ru.yandex.practicum.filmorate.service.FilmService;
import ru.yandex.practicum.filmorate.service.UserService;

import java.util.Collection;

/**
 * Контроллер для запросов пользователей.
 */
@RequestMapping("/users")
@RequiredArgsConstructor
@RestController
@Slf4j
public class UserController {
    /**
     * Сервис для работы с событиями.
     */
    private final EventService eventService;

    /**
     * Сервис для работы с фильмами
     */
    private final FilmService filmService;

    /**
     * Сервис для работы с пользователями.
     */
    private final UserService userService;

    //region Пользователи

    /**
     * Создать нового пользователя.
     *
     * @param dto трансферный объект запроса на создание пользователя.
     * @return пользователь.
     */
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public UserDto createUser(@Valid @RequestBody CreateUserRequestDto dto) {
        log.info("Запрос на добавление нового пользователя: {}", dto.getLogin());
        return UserMapper.mapToUserDto(this.userService.createUser(UserMapper.mapToUser(dto)));
    }

    /**
     * Получить список всех пользователей.
     *
     * @return список пользователей.
     */
    @GetMapping
    public Collection<UserDto> getAllUsers() {
        log.info("Запрос на получение списка всех пользователей");
        return UserMapper.mapToUserCollectionDto(this.userService.getAllUsers());
    }

    /**
     * Получить пользователя по его идентификатору.
     *
     * @param userId идентификатор пользователя.
     * @return пользователь.
     */
    @GetMapping("/{userId}")
    public UserDto getUserById(@PathVariable long userId) {
        log.info("Запрос на получение пользователя с id = {}", userId);
        return UserMapper.mapToUserDto(this.userService.getUserById(userId));
    }

    /**
     * Обновить пользователя.
     *
     * @param dto трансферный объект запроса на обновление пользователя.
     * @return пользователь.
     */
    @PutMapping
    public UserDto updateUser(@Valid @RequestBody UpdateUserRequestDto dto) {
        log.info("Запрос на обновление пользователя с id = {}", dto.getId());
        return UserMapper.mapToUserDto(this.userService.updateUser(UserMapper.mapToUser(dto)));
    }

    /**
     * Удалить пользователя.
     *
     * @param userId идентификатор пользователя.
     */
    @DeleteMapping("/{userId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteUser(@PathVariable long userId) {
        log.info("Запрос на удаление пользователя с id = {}", userId);
        this.userService.deleteUser(userId);
    }

    //endregion

    //region Друзья

    /**
     * Добавить пользователя в друзья.
     *
     * @param userId   идентификатор пользователя.
     * @param friendId идентификатор друга.
     */
    @PutMapping("/{userId}/friends/{friendId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void addFriend(@PathVariable long userId, @PathVariable long friendId) {
        log.info("Запрос на добавление пользователя с userId = {} в друзья к пользователю с userId = {}", friendId, userId);
        this.userService.addFriend(userId, friendId);
    }

    /**
     * Получить список друзей пользователя по идентификатору пользователя.
     *
     * @param userId идентификатор пользователя.
     * @return список пользователей.
     */
    @GetMapping("/{userId}/friends")
    public Collection<UserDto> getFriends(@PathVariable long userId) {
        log.info("Запрос на получение списка друзей пользователя с id = {}", userId);
        return UserMapper.mapToUserCollectionDto(this.userService.getFriends(userId));
    }

    /**
     * Получить список общих друзей двух пользователей.
     *
     * @param firstUserId  идентификатор первого пользователя.
     * @param secondUserId идентификатор второго пользователя.
     * @return список пользователей.
     */
    @GetMapping("/{firstUserId}/friends/common/{secondUserId}")
    public Collection<UserDto> getCommonFriends(@PathVariable long firstUserId, @PathVariable long secondUserId) {
        log.info("Запрос на получение списка общих друзей пользователей с id = {} и id = {}", firstUserId, secondUserId);
        return UserMapper.mapToUserCollectionDto(this.userService.getCommonFriends(firstUserId, secondUserId));
    }

    /**
     * Удалить пользователя из друзей.
     *
     * @param userId   идентификатор пользователя.
     * @param friendId идентификатор друга.
     */
    @DeleteMapping("/{userId}/friends/{friendId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteFriend(@PathVariable long userId, @PathVariable long friendId) {
        log.info("Запрос на удаление пользователя с id = {} из друзей пользователя с id = {}", friendId, userId);
        this.userService.deleteFriend(userId, friendId);
    }

    //endregion

    //region Фильмы

    /**
     * Получить список фильмов, рекомендуемых для пользователя.
     *
     * @param userId идентификатор пользователя.
     * @return список фильмов.
     */
    @GetMapping("/{userId}/recommendations")
    public Collection<FilmDto> getRecommendedFilms(@PathVariable Long userId) {
        log.info("Запрос на получение списка рекомендуемых фильмов для пользователя с userId = {}", userId);
        return FilmMapper.mapToFilmDtoCollection(this.filmService.getRecommendedFilms(userId));
    }

    //endregion

    //region События

    /**
     * Получить список событий пользователя.
     *
     * @param userId идентификатор пользователя.
     * @return список событий.
     */
    @GetMapping("/{userId}/feed")
    public Collection<EventDto> getEvents(@PathVariable long userId) {
        log.info("Запрос на получение событий пользователя с id = {}", userId);
        return EventMapper.mapToEventDtoCollection(this.eventService.getUserEvents(userId));
    }

    //endregion
}