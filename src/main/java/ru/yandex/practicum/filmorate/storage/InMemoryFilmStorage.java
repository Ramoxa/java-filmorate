package ru.yandex.practicum.filmorate.storage;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;

import java.util.HashMap;
import java.util.Map;


@Component
public class InMemoryFilmStorage implements FilmStorage {
    private final Map<Integer, Film> films;
    private int id;

    public InMemoryFilmStorage() {
        this.films = new HashMap<>();
        this.id = 0;
    }


    @Override
    public Map<Integer, Film> getAll() {
        return films;
    }

    @Override
    public Film findById(int id) {
        return films.get(id);
    }

    @Override
    public Film create(Film film) {
        film.setId(++id);
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

    private void validateFilmExists(int filmId) {
        if (!films.containsKey(filmId)) {
            throw new NotFoundException("Film not found with ID: " + filmId);
        }
    }
}