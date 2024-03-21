package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.User;

import javax.validation.Valid;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Validated
@RestController
@RequestMapping("/users")
public class UserController {
    private final Map<Integer, User> users = new HashMap<>();
    private Integer id = 0;

    @GetMapping
    public ResponseEntity<List<User>> findAll() {
        log.info("Вызван список пользователей.");
        return ResponseEntity.ok(new ArrayList<>(users.values()));
    }

    @GetMapping("/{id}")
    public ResponseEntity findById(@PathVariable Integer id) {
        if (users.containsKey(id)) {
            log.info("Получен запрос на пользователя с id = {}", id);
            return ResponseEntity.ok(users.get(id));
        } else {
            log.error("Пользователь с ID {} не найден.", id);
            Map<String, String> error = new HashMap<>();
            error.put("error", "Пользователь с ID " + id + " не найден.");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
        }
    }

    @PostMapping
    public ResponseEntity create(@Valid @RequestBody User user) {
        log.debug("Получен запрос на создание пользователя с телом: {}", user);
        user.setId(generateId());
        users.put(user.getId(), user);
        log.info("Добавлен пользователь: {}", user);
        return ResponseEntity.status(201).body(user);
    }

    @PutMapping
    public ResponseEntity update(@Valid @RequestBody User user) {
        Integer userId = user.getId();
        if (!users.containsKey(userId)) {
            log.error("Ошибка обновления: пользователь с ID {} не найден.", userId);
            Map<String, String> error = new HashMap<>();
            error.put("error", "Ошибка обновления: пользователь с ID " + userId + " не найден.");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
        }
        users.put(userId, user);
        log.info("Обновлен пользователь: {}", user);
        return ResponseEntity.ok(user);
    }

    private Integer generateId() {
        return ++id;
    }
}