package ru.yandex.practicum.filmorate.mapper;

import ru.yandex.practicum.filmorate.dto.mpa.MpaDto;
import ru.yandex.practicum.filmorate.model.Mpa;

import java.util.Collection;

public final class MpaMapper {
    public static Mpa mapToMpa(MpaDto dto) {
        return Mpa.builder()
                .id(dto.getId())
                .name(dto.getName())
                .build();
    }

    public static MpaDto mapToMpaDto(Mpa mpa) {
        return MpaDto.builder()
                .id(mpa.getId())
                .name(mpa.getName())
                .build();
    }

    public static Collection<MpaDto> mapToMpaDtoCollection(Collection<Mpa> mpaCollection) {
        return mpaCollection.stream().map(MpaMapper::mapToMpaDto).toList();
    }
}
