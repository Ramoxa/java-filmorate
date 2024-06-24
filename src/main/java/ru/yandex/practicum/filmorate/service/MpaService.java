package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.storage.mpa.MpaStorage;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class MpaService {
    @Qualifier("mpaStorage")
    private final MpaStorage mpaStorage;

    public List getAllMpa() {
        return mpaStorage.getAllMpa();
    }

    public Mpa getMpaById(int id) {
        Mpa mpa = mpaStorage.getMpaById(id);
        if (mpa == null) {
            throw new NotFoundException("MPA не найден.");
        }
        return mpa;
    }

    public boolean existsMpaById(int id) {
        return mpaStorage.existsMpaById(id);
    }
}