package ru.yandex.practicum.filmorate.storage;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

@Component
public class InMemoryFilmStorage implements FilmStorage {

    private final Map<Integer, Film> films = new HashMap<>();
    private final AtomicInteger filmIdCounter = new AtomicInteger();

    private final Map<Integer, User> users = new HashMap<>();

    @Override
    public Film create(Film film) {
        film.setId(filmIdCounter.incrementAndGet());
        films.put(film.getId(), film);
        return film;
    }

    @Override
    public Film update(Film film) {

        films.put(film.getId(), film);
        return film;
    }

    @Override
    public void addLike(int filmId, int userId) {
        validateUserExists(userId);
        validateFilmExists(filmId);
        Film film = films.get(filmId);
        film.getLikes().add(userId);
    }

    @Override
    public void removeLike(int filmId, int userId) {
        validateUserExists(userId);
        validateFilmExists(filmId);
        Film film = films.get(filmId);
        film.getLikes().remove(userId);
    }

    @Override
    public List<Film> findAll() {
        return new ArrayList<>(films.values());
    }

    @Override
    public List<Film> getMostLikedFilms(int count) {
        return films.values().stream().sorted(Comparator.comparingInt(f -> -f.getLikes().size())).limit(count).collect(Collectors.toList());
    }

    private void validateFilmExists(int filmId) {
        if (!films.containsKey(filmId)) {
            throw new NotFoundException("Film not found with ID: " + filmId);
        }
    }

    private void validateUserExists(int userId) {
        if (!users.containsKey(userId)) {
            throw new NotFoundException("User not found with ID: " + userId);
        }
    }
}