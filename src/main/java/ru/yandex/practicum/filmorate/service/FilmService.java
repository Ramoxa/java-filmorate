package ru.yandex.practicum.filmorate.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.FilmStorage;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class FilmService {
    private final FilmStorage filmStorage;

    @Autowired
    public FilmService(FilmStorage filmStorage) {
        this.filmStorage = filmStorage;
    }

    public List<Film> findAll() {
        return new ArrayList<>(filmStorage.getAll().values());
    }

    public List<Film> getPopularFilms(int count) {
        List<Film> allFilms = (List<Film>) filmStorage.getAll();
        allFilms.sort((f1, f2) -> Integer.compare(f2.getLikes().size(), f1.getLikes().size()));
        return allFilms.subList(0, Math.min(allFilms.size(), count));
    }

    public Film create(Film film) {
        return filmStorage.create(film);
    }

    public Film update(Film film) {
        return filmStorage.update(film);
    }

    public void addLike(int userId, int filmId) {
        if (!filmStorage.userExists(userId)) {
            throw new NotFoundException("Данного пользователя не существует");
        }
        if (!filmStorage.filmExists(filmId)) {
            throw new NotFoundException("Данного фильма не существует");
        }
        filmStorage.findById(filmId).getLikes().add(userId);
    }

    public void removeLike(int userId, int filmId) {
        if (!filmStorage.userExists(userId)) {
            throw new NotFoundException("Данного пользователя не существует");
        }
        if (!filmStorage.filmExists(filmId)) {
            throw new NotFoundException("Данного фильма не существует");
        }
        filmStorage.findById(filmId).getLikes().remove(userId);
    }

}