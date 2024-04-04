package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.service.FilmService;

import javax.validation.Valid;
import java.util.List;

@Slf4j
@Validated
@RestController
@RequestMapping("/films")
public class FilmController {

    public static final String COUNT_OF_POPULAR_FILM = "10";
    private final FilmService filmService;

    @Autowired
    public FilmController(FilmService filmService) {
        this.filmService = filmService;
    }

    @GetMapping
    public ResponseEntity findAll() {
        log.info("Вызван список фильмов.");
        return ResponseEntity.ok(filmService.findAll());
    }

    @PostMapping
    public ResponseEntity create(@Valid @RequestBody Film film) {
        log.info("Добавлен фильм: {}", film);
        return new ResponseEntity(filmService.create(film), HttpStatus.CREATED);
    }

    @PutMapping
    public ResponseEntity update(@Valid @RequestBody Film film) {
        log.info("Обновлен фильм: {}", film);
        return ResponseEntity.ok(filmService.update(film));
    }

    @PutMapping("/{id}/like/{userId}")
    public ResponseEntity addLike(@PathVariable int id, @PathVariable int userId) {
        try {
            filmService.addLike(id, userId);
            return ResponseEntity.ok().build();
        } catch (NotFoundException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{id}/like/{userId}")
    public ResponseEntity removeLike(@PathVariable int id, @PathVariable int userId) {
        try {
            filmService.removeLike(id, userId);
            return ResponseEntity.ok().build();
        } catch (NotFoundException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/popular")
    public List getPopularFilms(@RequestParam(defaultValue = "10") int count) {
        return filmService.getPopularFilms(count);
    }

}