package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.Film;

import java.util.List;

public interface FilmStorage {
    Film create(Film film);
    Film update(Film film);
    void addLike(int filmId, int userId);
    void removeLike(int filmId, int userId);
    List<Film> findAll();
    List<Film> getMostLikedFilms(int count);

}