package ru.yandex.practicum.filmorate.storage.film;

import ru.yandex.practicum.filmorate.model.Film;

import java.util.Collection;

public interface FilmStorage {


    Collection findAll();

    Film findById(int id);

    Film create(Film film);

    Film update(Film film);

    void deleteById(int id);

    boolean userExists(int userId);

    boolean filmExists(int filmId);

    boolean genreExists(int genreId);

    boolean mpaExists(int mpaId);

    void addLike(int userId, int filmId);

    void removeLike(int userId, int filmId);

    Collection<Film> findTopFilmsByLikes(int count);
}