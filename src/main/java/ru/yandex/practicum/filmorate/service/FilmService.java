package ru.yandex.practicum.filmorate.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
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
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Юзер не найден: " + userId);
        }
        Film film = filmStorage.findById(filmId).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Фильм не найден: " + filmId));
        film.getLikes().add(userId);
    }

    public void removeLike(int filmId, int userId) {
        if (!userStorage.findId(userId)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Юзер не найден: " + userId);
        }
        Film film = filmStorage.findById(filmId).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Фильм не найден: " + filmId));
        film.getLikes().remove(userId);
    }
}