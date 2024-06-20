package ru.yandex.practicum.filmorate.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.exception.CustomBadRequestException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.service.FilmService;

import javax.validation.Valid;
import java.util.*;


@RestController
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/films")
public class FilmController {
    private final FilmService filmService;

    @GetMapping
    public ResponseEntity <Collection<Film>> findAll() {
        log.info("Вызван список фильмов.");
        return ResponseEntity.ok(filmService.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Film> findById(@PathVariable int id) {
        log.info("Вызван фильм с ID: {}", id);
        Film film = filmService.findById(id);
        if (film == null) {
            throw new NotFoundException("Фильм не найден.");
        }
        return ResponseEntity.ok(film);
    }

    @PostMapping
    public ResponseEntity<Film> create(@Valid @RequestBody Film film) {
        log.info("Добавлен фильм: {}", film);

        if (!isValidMpa(film)) {
            log.error("Ошибка добавления фильма: некорректная MPA.");
            throw new CustomBadRequestException("Некорректная MPA.");
        }

        if (!areValidGenres(film)) {
            log.error("Ошибка добавления фильма: некорректные жанры.");
            throw new CustomBadRequestException("Некорректные жанры.");
        }

        sortGenresById(film);

        Film createdFilm = filmService.create(film);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdFilm);
    }

    @PutMapping
    public ResponseEntity<Film> update(@Valid @RequestBody Film film) {
        log.info("Обновлен фильм: {}", film);
        try {
            Film updatedFilm = filmService.update(film);
            return ResponseEntity.ok(updatedFilm);
        } catch (ValidationException e) {
            throw new CustomBadRequestException(e.getMessage());
        } catch (NotFoundException e) {
            throw new NotFoundException(e.getMessage());
        }
    }

    @PutMapping("/{id}/like/{userId}")
    public ResponseEntity<Void> addLike(@PathVariable int id, @PathVariable int userId) {
        try {
            filmService.addLike(id, userId);
            log.info("Юзер с ID: {} поставил лайк фильму с ID: {}", userId, id);
            return ResponseEntity.ok().build();
        } catch (NotFoundException e) {
            throw new NotFoundException(e.getMessage());
        }
    }

    @DeleteMapping("/{id}/like/{userId}")
    public ResponseEntity<Void> removeLike(@PathVariable int id, @PathVariable int userId) {
        try {
            filmService.removeLike(id, userId);
            log.info("Юзер с ID: {} удалил лайк фильма с ID: {}", userId, id);
            return ResponseEntity.ok().build();
        } catch (NotFoundException e) {
            throw new NotFoundException(e.getMessage());
        }
    }

    @GetMapping("/popular")
    public ResponseEntity<List<Film>> getPopularFilms(@RequestParam(defaultValue = "10") int count) {
        log.info("Вызыван список {} самых популярных фильмов", count);
        return ResponseEntity.ok((List<Film>) filmService.getPopularFilms(count));
    }

    private boolean isValidMpa(Film film) {
        return film.getMpa() != null && filmService.existsMpaById(film.getMpa().getId());
    }

    private boolean areValidGenres(Film film) {
        if (film.getGenres() == null) {
            return true;
        }
        for (Genre genre : film.getGenres()) {
            if (!filmService.existsGenreById(genre.getId())) {
                return false;
            }
        }
        return true;
    }

    private void sortGenresById(Film film) {
        if (film.getGenres() != null) {
            Set<Genre> sortedGenres = new TreeSet<>(Comparator.comparingInt(Genre::getId));
            sortedGenres.addAll(film.getGenres());
            film.setGenres(sortedGenres);
        }
    }
}
