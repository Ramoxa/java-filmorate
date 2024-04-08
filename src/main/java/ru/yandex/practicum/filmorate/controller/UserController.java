package ru.yandex.practicum.filmorate.controller;


import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
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

    @GetMapping
    public ResponseEntity<List<User>> findAll() {
        log.info("Вызван список пользователей.");
        return ResponseEntity.ok(userService.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<User> findById(@PathVariable Integer id) {
        User user = userService.getUser(id);
        if (user != null) {
            log.info("Получен запрос на пользователя с id = {}", id);
            return ResponseEntity.ok(user);
        } else {
            log.error("Пользователь с ID {} не найден.", id);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }

    @PostMapping
    public ResponseEntity<User> create(@Valid @RequestBody User user) {
        log.debug("Получен запрос на создание пользователя с телом: {}", user);
        User createdUser = userService.create(user);
        log.info("Добавлен пользователь: {}", createdUser);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdUser);
    }

    @PutMapping
    public ResponseEntity<User> update(@Valid @RequestBody User user) {
        if (userService.getUser(user.getId()) == null) {
            log.error("Ошибка обновления: пользователь с ID {} не найден.", user.getId());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
        User updatedUser = userService.update(user);
        log.info("Обновлен пользователь: {}", updatedUser);
        return ResponseEntity.ok(updatedUser);
    }

    @PutMapping("/{id}/friends/{friendId}")
    public ResponseEntity<?> addFriend(@PathVariable int id, @PathVariable int friendId) {
        log.info("Юзер с ID: {} добавил в друзья юзера с ID: {}", id, friendId);
        userService.addFriend(id, friendId);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{id}/friends/{friendId}")
    public ResponseEntity<?> removeFriend(@PathVariable int id, @PathVariable int friendId) {
        log.info("Юзер с ID: {} удалил из друзей юзера с ID: {}", id, friendId);
        userService.removeFriend(id, friendId);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/{id}/friends")
    public ResponseEntity<List<User>> getFriends(@PathVariable int id) {
        log.info("Вызван список общих друзей");
        return ResponseEntity.ok(userService.findUserFriends(id));
    }

    @GetMapping("/{id}/friends/common/{otherId}")
    public ResponseEntity<Set<User>> getCommonFriends(@PathVariable int id, @PathVariable int otherId) {
        log.info("Вызван список друзей");
        Set<User> commonFriends = userService.getCommonFriends(id, otherId);
        return ResponseEntity.ok(commonFriends);
    }
}