package ru.yandex.practicum.filmorate.converter;

import org.springframework.core.convert.converter.Converter;
import ru.yandex.practicum.filmorate.model.SortBy;

public final class SortByConverter implements Converter<String, SortBy> {
    @Override
    public SortBy convert(String source) {
        return SortBy.valueOf(source.toUpperCase());
    }
}
