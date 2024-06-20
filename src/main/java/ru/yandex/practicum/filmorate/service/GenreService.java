package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.storage.genre.GenreStorage;

import java.util.List;

@Service
@RequiredArgsConstructor
public class GenreService {
    @Qualifier("genreStorage")
    private final GenreStorage genreStorage;

    public List getAllGenres() {
        return genreStorage.getAllGenres();
    }

    public Genre getGenreById(int id) {
        if (!genreStorage.existsGenreById(id)) {
            throw new NotFoundException("Жанр не найден.");
        }
        return genreStorage.getGenreById(id);
    }

    public boolean existsGenreById(int id) {
        return genreStorage.existsGenreById(id);
    }

}