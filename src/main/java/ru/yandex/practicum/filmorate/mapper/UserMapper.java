package ru.yandex.practicum.filmorate.mapper;

import ru.yandex.practicum.filmorate.dto.user.CreateUserRequestDto;
import ru.yandex.practicum.filmorate.dto.user.UpdateUserRequestDto;
import ru.yandex.practicum.filmorate.dto.user.UserDto;
import ru.yandex.practicum.filmorate.model.User;

import java.util.Collection;

public final class UserMapper {
    public static User mapToUser(CreateUserRequestDto dto) {
        return User.builder()
                .login(dto.getLogin())
                .email(dto.getEmail())
                .name(dto.getName())
                .birthday(dto.getBirthday())
                .build();
    }

    public static User mapToUser(UpdateUserRequestDto dto) {
        return User.builder()
                .id(dto.getId())
                .login(dto.getLogin())
                .email(dto.getEmail())
                .name(dto.getName())
                .birthday(dto.getBirthday())
                .build();
    }

    public static UserDto mapToUserDto(User user) {
        return UserDto.builder()
                .id(user.getId())
                .login(user.getLogin())
                .email(user.getEmail())
                .name(user.getName())
                .birthday(user.getBirthday())
                .build();
    }

    public static Collection<UserDto> mapToUserCollectionDto(Collection<User> userCollection) {
        return userCollection.stream().map(UserMapper::mapToUserDto).toList();
    }
}
