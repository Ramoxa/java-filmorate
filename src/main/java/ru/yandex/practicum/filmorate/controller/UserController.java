package ru.yandex.practicum.filmorate.controller;


import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.UserService;

import javax.validation.Valid;
import java.util.List;
import java.util.Set;

@Slf4j
@Validated
@RestController
@RequestMapping("/users")
public class UserController {

    private final UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping
    public ResponseEntity<User> createUser(@Valid @RequestBody User user) {
        log.debug("Получен запрос на создание пользователя с телом: {}", user);
        User createdUser = userService.create(user);
        log.info("Добавлен пользователь: {}", createdUser);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdUser);
    }

    @PutMapping
    public ResponseEntity<User> updateUser(@Valid @RequestBody User user) {
        try {
            User updatedUser = userService.update(user);
            log.info("Обновлен пользователь: {}", updatedUser);
            return ResponseEntity.ok(updatedUser);
        } catch (NotFoundException e) {
            log.error("Ошибка обновления: пользователь с ID {} не найден.", user.getId());
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        }
    }

    @GetMapping
    public ResponseEntity<List<User>> findAllUsers() {
        log.info("Вызван список пользователей.");
        return ResponseEntity.ok(userService.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<User> findUserById(@PathVariable Integer id) {
        try {
            User user = userService.getUser(id);
            log.info("Получен запрос на пользователя с id = {}", id);
            return ResponseEntity.ok(user);
        } catch (NotFoundException e) {
            log.error("Пользователь с ID {} не найден.", id);
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        }
    }

    @PutMapping("/{id}/friends/{friendId}")
    public ResponseEntity<Void> addFriend(@PathVariable int id, @PathVariable int friendId) {
        try {
            userService.addFriend(id, friendId);
            log.info("Пользователь с ID: {} добавил пользователя с ID: {} в друзья", id, friendId);
            return ResponseEntity.status(HttpStatus.OK).build();
        } catch (NotFoundException e) {
            log.error("Ошибка при добавлении друга: {}", e.getMessage());
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        } catch (Exception e) {
            log.error("Ошибка при добавлении друга: {}", e);
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Внутренняя ошибка сервера");
        }
    }

    @DeleteMapping("/{id}/friends/{friendId}")
    public ResponseEntity<Void> removeFriend(@PathVariable int id, @PathVariable int friendId) {
        try {
            userService.removeFriend(id, friendId);
            log.info("Пользователь с ID: {} удалил пользователя с ID: {} из друзей", id, friendId);
            return ResponseEntity.status(HttpStatus.OK).build();
        } catch (NotFoundException e) {
            log.error("Ошибка при удалении друга: {}", e.getMessage());
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        } catch (Exception e) {
            log.error("Ошибка при удалении друга: {}", e);
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Внутренняя ошибка сервера");
        }
    }

    @GetMapping("/{id}/friends")
    public ResponseEntity<List<User>> getFriends(@PathVariable int id) {
        log.info("Получение списка друзей пользователя с ID: {}", id);
        try {
            return ResponseEntity.ok(userService.findUserFriends(id));
        } catch (NotFoundException e) {
            log.error("Ошибка при получении списка друзей: {}", e.getMessage());
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        }
    }

    @GetMapping("/{id}/friends/common/{otherId}")
    public ResponseEntity<Set<User>> getCommonFriends(@PathVariable int id, @PathVariable int otherId) {
        log.info("Получение списка общих друзей для пользователей с ID: {} и {}", id, otherId);
        try {
            Set<User> commonFriends = userService.getCommonFriends(id, otherId);
            return ResponseEntity.ok(commonFriends);
        } catch (NotFoundException e) {
            log.error("Ошибка при получении общих друзей: {}", e.getMessage());
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        }
    }
}