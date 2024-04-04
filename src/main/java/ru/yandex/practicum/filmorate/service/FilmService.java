package ru.yandex.practicum.filmorate.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.FilmStorage;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class FilmService {
    private final FilmStorage filmStorage;

    @Autowired
    public FilmService(FilmStorage filmStorage) {
        this.filmStorage = filmStorage;
    }

    public Map<Integer, Film> findAll() {
        return filmStorage.getAll();
    }

    public List<Film> getPopularFilms(int amount) {
        Collection<Film> films = filmStorage.getAll().values();

        return films.stream().sorted(Film::compareTo).limit(amount).collect(Collectors.toList());
    }

    public Film create(Film film) {
        return filmStorage.create(film);
    }

    public Film update(Film film) {
        return filmStorage.update(film);
    }

    public void addLike(int userId, int filmId) {
        boolean userDoesntExist = filmStorage.getAll().get(userId) == null;
        boolean filmDoesntExist = filmStorage.getAll().get(filmId) == null;

        if (userDoesntExist) throw new ValidationException("Данного пользователя не существует");

        if (filmDoesntExist) throw new ValidationException("Данного фильма не существует");

        filmStorage.findById(filmId).getLikes().add(userId);
    }


    public void removeLike(int userId, int filmId) {
        boolean userDoesntExist = filmStorage.getAll().get(userId) == null;
        boolean filmDoesntExist = filmStorage.getAll().get(filmId) == null;
        boolean storageIsEmpty = filmStorage.getAll().isEmpty();

        if (userDoesntExist || storageIsEmpty) throw new ValidationException("Данного пользователя не существует");

        if (filmDoesntExist) throw new ValidationException("Данного фильма не существует");

        filmStorage.findById(filmId).getLikes().remove(userId);
    }

}