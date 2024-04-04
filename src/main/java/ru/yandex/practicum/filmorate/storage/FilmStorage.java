package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.Film;

import java.util.Collection;
import java.util.Optional;

public interface FilmStorage {
    Collection<Film> findAll();

    Optional<Film> findById(int id);

    Film create(Film film);

    Film update(Film film);

    void deleteById(int id);

    boolean userExists(int userId);

    boolean filmExists(int filmId);
}