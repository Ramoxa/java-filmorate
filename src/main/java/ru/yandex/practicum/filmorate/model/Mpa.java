package ru.yandex.practicum.filmorate.model;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class Mpa {
    public Mpa(int id, String name) {
        this.id = id;
        this.name = name;
    }

    private int id;
    private String name;
}