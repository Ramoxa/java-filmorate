package ru.yandex.practicum.filmorate.storage.genre;

import ru.yandex.practicum.filmorate.model.Genre;

import java.util.List;
import java.util.Set;

public interface GenreStorage {
    Genre getGenreById(int id);

    List<Genre> getAllGenres();

    Set getFilmGenres(int filmId);

    boolean existsGenreById(int id);
}