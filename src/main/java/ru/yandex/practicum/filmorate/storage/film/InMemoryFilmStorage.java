package ru.yandex.practicum.filmorate.storage.film;

import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;


public class InMemoryFilmStorage implements FilmStorage {

    private final Map<Integer, Film> films = new HashMap<>();
    private final AtomicInteger idGenerator = new AtomicInteger();

    @Override
    public Collection<Film> findAll() {
        return films.values();
    }

    @Override
    public Film findById(int id) {
        return films.get(id);
    }


    @Override
    public Film create(Film film) {
        int id = idGenerator.incrementAndGet();
        film.setId(id);
        films.put(id, film);
        return film;
    }

    @Override
    public Film update(Film film) {
        validateFilmExists(film.getId());
        films.put(film.getId(), film);
        return film;
    }

    @Override
    public void deleteById(int id) {
        validateFilmExists(id);
        films.remove(id);
    }

    @Override
    public boolean userExists(int userId) {
        return films.containsKey(userId);
    }

    @Override
    public boolean filmExists(int filmId) {
        return films.containsKey(filmId);
    }

    @Override
    public boolean genreExists(int genreId) {
        return false;
    }

    @Override
    public boolean mpaExists(int mpaId) {
        return false;
    }

    @Override
    public void addLike(int userId, int filmId) {

    }

    @Override
    public void removeLike(int userId, int filmId) {

    }

    @Override
    public Collection<Film> findTopFilmsByLikes(int count) {
        return null;
    }


    private void validateFilmExists(int filmId) {
        if (!films.containsKey(filmId)) {
            throw new NotFoundException("Фильм не найден ID: " + filmId);
        }
    }

}