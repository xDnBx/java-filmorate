package ru.yandex.practicum.filmorate.mapper;

import ru.yandex.practicum.filmorate.dto.film.CreateFilmRequestDto;
import ru.yandex.practicum.filmorate.dto.film.FilmDto;
import ru.yandex.practicum.filmorate.dto.film.UpdateFilmRequestDto;
import ru.yandex.practicum.filmorate.model.Film;

import java.util.Collection;
import java.util.HashSet;

public final class FilmMapper {
    public static Film mapToFilm(CreateFilmRequestDto dto) {
        return Film.builder()
                .name(dto.getName())
                .description(dto.getDescription())
                .releaseDate(dto.getReleaseDate())
                .duration(dto.getDuration())
                .mpa(MpaMapper.mapToMpa(dto.getMpa()))
                .directors(DirectorMapper.mapToDirectorCollection(dto.getDirectors()))
                .genres(new HashSet<>(GenreMapper.mapToGenreCollection(dto.getGenres())))
                .build();
    }

    public static Film mapToFilm(UpdateFilmRequestDto dto) {
        return Film.builder()
                .id(dto.getId())
                .name(dto.getName())
                .description(dto.getDescription())
                .releaseDate(dto.getReleaseDate())
                .duration(dto.getDuration())
                .mpa(MpaMapper.mapToMpa(dto.getMpa()))
                .directors(DirectorMapper.mapToDirectorCollection(dto.getDirectors()))
                .genres(new HashSet<>(GenreMapper.mapToGenreCollection(dto.getGenres())))
                .build();
    }

    public static FilmDto mapToFilmDto(Film film) {
        return FilmDto.builder()
                .id(film.getId())
                .name(film.getName())
                .description(film.getDescription())
                .releaseDate(film.getReleaseDate())
                .duration(film.getDuration())
                .mpa(MpaMapper.mapToMpaDto(film.getMpa()))
                .directors(DirectorMapper.mapToDirectorDtoCollection(film.getDirectors()))
                .genres(GenreMapper.mapToGenreDtoCollection(film.getGenres()))
                .build();
    }

    public static Collection<FilmDto> mapToFilmDtoCollection(Collection<Film> filmCollection) {
        return filmCollection.stream().map(FilmMapper::mapToFilmDto).toList();
    }
}
