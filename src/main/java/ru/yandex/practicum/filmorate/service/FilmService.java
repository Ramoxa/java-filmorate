package ru.yandex.practicum.filmorate.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.FilmStorage;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class FilmService {
    private final FilmStorage filmStorage;

    private final UserStorage userStorage;

    @Autowired
    public FilmService(FilmStorage filmStorage, UserStorage userStorage) {
        this.filmStorage = filmStorage;
        this.userStorage = userStorage;
    }

    public Collection<Film> findAll() {
        return filmStorage.findAll();
    }

    public List<Film> getPopularFilms(int count) {
        return filmStorage.findAll().stream().sorted((f1, f2) -> f2.getLikes().size() - f1.getLikes().size()).limit(count).collect(Collectors.toList());
    }

    public Film create(Film film) {
        return filmStorage.create(film);
    }

    public Film update(Film film) {
        return filmStorage.update(film);
    }

    public void addLike(int filmId, int userId) {
        if (!userStorage.findId(userId)) {
            throw new ValidationException("User not found with ID: " + userId);
        }
        Film film = filmStorage.findById(filmId).orElseThrow(() -> new NotFoundException("Film not found with ID: " + filmId));
        film.getLikes().add(userId);
    }

    public void removeLike(int filmId, int userId) {
        if (!userStorage.findId(userId)) {
            throw new ValidationException("User not found with ID: " + userId);
        }
        Film film = filmStorage.findById(filmId).orElseThrow(() -> new NotFoundException("Film not found with ID: " + filmId));
        film.getLikes().remove(userId);
    }
}