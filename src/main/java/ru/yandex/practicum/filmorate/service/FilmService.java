package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;
import ru.yandex.practicum.filmorate.storage.genre.GenreStorage;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.util.Collection;
import java.util.Comparator;
import java.util.TreeSet;

@Service
@RequiredArgsConstructor
public class FilmService {
    private final FilmStorage filmStorage;
    @Qualifier("userDbStorage")
    private final UserStorage userStorage;
    private final MpaService mpaService;
    private final GenreService genreService;
    private final GenreStorage genreStorage;

    public Collection<Film> findAll() {
        return filmStorage.findAll();
    }

    public Film findById(int id) {
        Film film = filmStorage.findById(id);
        if (film != null) {
            TreeSet<Genre> sortedGenres = new TreeSet<>(Comparator.comparingInt(Genre::getId));
            sortedGenres.addAll(genreStorage.getFilmGenres(id));
            film.setGenres(sortedGenres);
            film.setMpa(mpaService.getMpaById(film.getMpa().getId()));
        }
        return film;
    }

    public Film create(Film film) {
        return filmStorage.create(film);
    }

    public Film update(Film film) {
        return filmStorage.update(film);
    }

    public void addLike(int filmId, int userId) {
        filmStorage.addLike(userId, filmId);
    }

    public void removeLike(int filmId, int userId) {
        filmStorage.removeLike(userId, filmId);
    }

    public Collection<Film> getPopularFilms(int count) {
        return filmStorage.findTopFilmsByLikes(count);
    }

    public boolean existsGenreById(int id) {
        return genreStorage.existsGenreById(id);
    }

    public boolean existsMpaById(int id) {
        return mpaService.existsMpaById(id);
    }
}
