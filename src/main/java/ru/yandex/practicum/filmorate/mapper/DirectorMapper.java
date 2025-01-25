package ru.yandex.practicum.filmorate.mapper;

import ru.yandex.practicum.filmorate.dto.director.CreateDirectorRequestDto;
import ru.yandex.practicum.filmorate.dto.director.DirectorDto;
import ru.yandex.practicum.filmorate.dto.director.UpdateDirectorRequestDto;
import ru.yandex.practicum.filmorate.model.Director;

import java.util.Collection;

public final class DirectorMapper {
    public static Director mapToDirector(CreateDirectorRequestDto dto) {
        return Director.builder()
                .name(dto.getName())
                .build();
    }

    public static Director mapToDirector(DirectorDto dto) {
        return Director.builder()
                .id(dto.getId())
                .name(dto.getName())
                .build();
    }

    public static Director mapToDirector(UpdateDirectorRequestDto dto) {
        return Director.builder()
                .id(dto.getId())
                .name(dto.getName())
                .build();
    }

    public static Collection<Director> mapToDirectorCollection(Collection<DirectorDto> dtoCollection) {
        return dtoCollection.stream().map(DirectorMapper::mapToDirector).toList();
    }

    public static DirectorDto mapToDirectorDto(Director director) {
        return DirectorDto.builder()
                .id(director.getId())
                .name(director.getName())
                .build();
    }

    public static Collection<DirectorDto> mapToDirectorDtoCollection(Collection<Director> directorCollection) {
        return directorCollection.stream().map(DirectorMapper::mapToDirectorDto).toList();
    }
}
