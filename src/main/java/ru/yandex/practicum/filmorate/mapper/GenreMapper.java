package ru.yandex.practicum.filmorate.mapper;

import ru.yandex.practicum.filmorate.dto.genre.GenreDto;
import ru.yandex.practicum.filmorate.model.Genre;

import java.util.Collection;

public final class GenreMapper {
    public static Genre mapToGenre(GenreDto dto) {
        return Genre.builder()
                .id(dto.getId())
                .name(dto.getName())
                .build();
    }

    public static Collection<Genre> mapToGenreCollection(Collection<GenreDto> dtoCollection) {
        return dtoCollection.stream().map(GenreMapper::mapToGenre).toList();
    }

    public static GenreDto mapToGenreDto(Genre genre) {
        GenreDto dto = new GenreDto();

        dto.setId(genre.getId());
        dto.setName(genre.getName());

        return dto;
    }

    public static Collection<GenreDto> mapToGenreDtoCollection(Collection<Genre> genreCollection) {
        return genreCollection.stream().map(GenreMapper::mapToGenreDto).toList();
    }
}
