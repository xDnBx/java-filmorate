package ru.yandex.practicum.filmorate.storage;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.*;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;

import java.util.*;

@Slf4j
@Component
public class InMemoryUserStorage implements UserStorage, FriendsStorage {
    private long id = 1;
    private final Map<Long, User> users = new HashMap<>();

    @Override
    public Collection<User> getAllUsers() {
        return users.values();
    }

    @Override
    public User createUser(User user) {
        for (User newUser : users.values()) {
            if (newUser.getEmail().equals(user.getEmail())) {
                log.error("E-mail уже присутствует у другого пользователя");
                throw new DuplicatedDataException("Этот e-mail уже используется");
            }
        }
        if (user.getName() == null) {
            user.setName(user.getLogin());
        }
        user.setId(generateNewId());
        users.put(user.getId(), user);
        log.info("Пользователь с id = {} успешно добавлен!", user.getId());
        return user;
    }

    @Override
    public User updateUser(User newUser) {
        checkUser(newUser.getId());

        for (User user : users.values()) {
            if (user.getEmail().equals(newUser.getEmail())) {
                log.error("E-mail уже присутствует у другого пользователя");
                throw new DuplicatedDataException("Этот e-mail уже используется");
            }
        }

        User oldUser = users.get(newUser.getId());
        oldUser.setEmail(newUser.getEmail());
        oldUser.setLogin(newUser.getLogin());
        if (newUser.getName() == null) {
            oldUser.setName(newUser.getLogin());
        } else {
            oldUser.setName(newUser.getName());
        }
        oldUser.setBirthday(newUser.getBirthday());

        log.info("Обновление пользователя с id = {} прошло успешно!", newUser.getId());
        return oldUser;
    }

    @Override
    public User getUserById(Long id) {
        checkUser(id);
        return users.get(id);
    }

    @Override
    public List<Film> getRecommendedFilms(Long id) {
        return List.of();
    }

    @Override
    public void addFriend(Long id, Long friendId) {
        checkUsersFriends(id, friendId);

        User user = getUserById(id);
        User friend = getUserById(friendId);

        if (user.getFriends().contains(friendId)) {
            log.error("Пользователи уже друзья");
            throw new AlreadyFriendsException("Пользователи уже являются друзьями");
        }

        user.getFriends().add(friendId);
        friend.getFriends().add(id);
        log.info("Пользователи с id = {} и id = {} теперь друзья!", id, friendId);
    }

    @Override
    public void deleteFriend(Long id, Long friendId) {
        checkUsersFriends(id, friendId);

        User user = getUserById(id);
        User friend = getUserById(friendId);

        user.getFriends().remove(friendId);
        friend.getFriends().remove(id);
        log.info("Пользователи с id = {} и id = {} теперь не друзья", id, friendId);
    }

    @Override
    public List<User> getFriends(Long id) {
        checkUser(id);

        User user = getUserById(id);
        return user.getFriends().stream()
                .map(this::getUserById)
                .toList();
    }

    @Override
    public List<User> getCommonFriends(Long id, Long otherId) {
        checkUsersFriends(id, otherId);

        Set<Long> user = getUserById(id).getFriends();
        Set<Long> friend = getUserById(otherId).getFriends();

        if (user.isEmpty() || friend.isEmpty()) {
            log.error("У пользователей нет друзей");
            throw new NotFoundException("У данных пользователей отсутствуют друзья");
        }

        return user.stream()
                .filter(friend::contains)
                .map(this::getUserById)
                .toList();
    }

    @Override
    public void deleteUser(Long userId){

    }

    private long generateNewId() {
        return id++;
    }

    private void checkUser(Long id) {
        if (id == null) {
            log.error("id пользователя не указан");
            throw new ValidationException("Id должен быть указан");
        }
        if (!users.containsKey(id)) {
            log.error("Пользователя с данным id не существует");
            throw new NotFoundException("Пользователь с данным id не найден");
        }
    }

    private void checkUsersFriends(Long id, Long friendId) {
        if (id == null || friendId == null) {
            log.error("id пользователя или его друга не указан");
            throw new ValidationException("Id пользователя или друга должен быть указан");
        }
        if (!users.containsKey(id) || !users.containsKey(friendId)) {
            log.error("Пользователя с данным id или его друга не существует");
            throw new NotFoundException("Пользователь с данным id или его друга не найден");
        }
        if (id.equals(friendId)) {
            log.error("id пользователя и друга идентичны");
            throw new FriendForHimselfException("Пользователь не может быть сам себе другом");
        }
    }
}