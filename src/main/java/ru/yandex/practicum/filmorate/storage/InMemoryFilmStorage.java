package ru.yandex.practicum.filmorate.storage;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;

@Component
public class InMemoryFilmStorage implements FilmStorage {

    private final Map<Integer, Film> films = new HashMap<>();
    private final AtomicInteger idGenerator = new AtomicInteger();

    @Override
    public Collection<Film> findAll() {
        return films.values();
    }

    @Override
    public Optional<Film> findById(int id) {
        return Optional.ofNullable(films.get(id));
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

    private void validateFilmExists(int filmId) {
        if (!films.containsKey(filmId)) {
            throw new NotFoundException("Фильм не найден ID: " + filmId);
        }
    }

}