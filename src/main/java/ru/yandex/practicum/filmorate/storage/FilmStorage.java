package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.Film;

import java.util.Collection;
import java.util.Map;

public interface FilmStorage {

    Map<Integer, Film> getAll();

    Film findById(int id);

    Film create(Film film);

    Film update(Film film);

    void deleteById(int id);
}