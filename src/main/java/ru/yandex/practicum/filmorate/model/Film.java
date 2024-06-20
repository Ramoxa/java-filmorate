package ru.yandex.practicum.filmorate.model;

import lombok.Builder;
import lombok.Data;
import ru.yandex.practicum.filmorate.exception.ValidationException;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;


@Data
public class Film {
    private int id;
    private String name;
    private String description;
    private LocalDate releaseDate;
    private int duration;
    private Mpa mpa;
    private Set<Genre> genres = new HashSet<>();
    private Set<Integer> likes = new HashSet<>();

    public void setName(String name) {
        if (name == null || name.trim().isEmpty()) {
            throw new ValidationException("Название фильма не может быть пустым");
        }
        this.name = name;
    }

    public void setDescription(String description) {
        if (description != null && description.length() > 200) {
            throw new ValidationException("Максимальная длина описания - 200 символов");
        }
        this.description = description;
    }

    public void setReleaseDate(LocalDate releaseDate) {
        if (releaseDate.isBefore(LocalDate.of(1895, 12, 28))) {
            throw new ValidationException("Дата релиза не может быть ранее 28 декабря 1895 года");
        }
        this.releaseDate = releaseDate;
    }

    public void setDuration(int duration) {
        if (duration < 0) {
            throw new ValidationException("Продолжительность фильма должна быть положительной");
        }
        this.duration = duration;
    }

    public void setGenres(Set<Genre> genres) {
        if (genres == null) {
            throw new ValidationException("Список жанров не может быть пустым");
        }
        this.genres = genres;
    }
}